package com.platform.interceptor;

import com.platform.dto.UserSession;
import com.platform.entity.Permission;
import com.platform.entity.User;
import com.platform.mapper.PermissionMapper;
import com.platform.mapper.UserMapper;
import com.platform.mapper.UserRoleMapper;
import com.platform.service.OptimizedUserSessionService;
import com.platform.util.JwtUtil;
import com.platform.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final OptimizedUserSessionService userSessionService;
    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        
        log.info("[JwtAuth] 收到请求: {} {}, Authorization: {}", 
                request.getMethod(), request.getRequestURI(), 
                authHeader != null ? (authHeader.length() > 20 ? authHeader.substring(0, 20) + "..." : authHeader) : "null");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("[JwtAuth] 无Bearer Token，跳过");
            return true;
        }

        try {
            String token = authHeader.substring(7);
            log.info("[JwtAuth] 解析Token...");

            if (!jwtUtil.validateToken(token)) {
                log.warn("[JwtAuth] Token已失效");
                return true;
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            log.info("[JwtAuth] 从Token解析到userId: {}", userId);
            
            if (userId == null) {
                log.warn("[JwtAuth] 无法从Token中解析userId");
                return true;
            }

            // 1. 先尝试从Redis获取
            log.info("[JwtAuth] 尝试从Redis获取会话: userId={}", userId);
            UserSession session = userSessionService.getUserSession(userId);

            // 2. 如果Redis没有，从数据库加载
            if (session == null) {
                log.info("[JwtAuth] Redis未命中，从数据库加载: userId={}", userId);
                session = loadFromDatabase(userId, token, request);
                
                if (session != null) {
                    log.info("[JwtAuth] 从数据库加载成功，保存到Redis: userId={}", userId);
                    userSessionService.saveUserSession(session);
                }
            }

            // 3. 设置UserContext
            if (session != null) {
                UserContext.setUser(session);
                log.info("[JwtAuth] ✅ 用户上下文设置成功: userId={}, username={}", 
                        session.getUserId(), session.getUsername());
            } else {
                log.error("[JwtAuth] ❌ 用户会话加载失败（Redis和数据库都失败）: userId={}", userId);
            }

        } catch (Exception e) {
            log.error("[JwtAuth] ❌ Token处理异常: {}", e.getMessage(), e);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

    private UserSession loadFromDatabase(Long userId, String token, HttpServletRequest request) {
        try {
            log.info("[JwtAuth] 查询数据库: userId={}", userId);
            User user = userMapper.selectById(userId);
            
            if (user == null) {
                log.error("[JwtAuth] 数据库中不存在该用户: userId={}", userId);
                return null;
            }
            log.info("[JwtAuth] 找到用户: username={}", user.getUsername());

            log.info("[JwtAuth] 查询权限...");
            List<String> permissionCodes = permissionMapper.selectPermissionsByUserId(userId)
                    .stream()
                    .map(Permission::getCode)
                    .collect(Collectors.toList());
            log.info("[JwtAuth] 权限数量: {}", permissionCodes.size());

            log.info("[JwtAuth] 查询角色...");
            List<String> roleCodes = userRoleMapper.selectRoleCodesByUserId(userId);
            log.info("[JwtAuth] 角色数量: {}", roleCodes.size());

            String ipAddress = getClientIp(request);

            return UserSession.from(
                    user.getId(),
                    user.getUsername(),
                    user.getRealName(),
                    user.getAvatar(),
                    roleCodes,
                    permissionCodes,
                    token,
                    ipAddress
            );
        } catch (Exception e) {
            log.error("[JwtAuth] 从数据库加载用户信息失败: userId={}, 错误: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
