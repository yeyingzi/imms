package com.platform.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.entity.OperationLog;
import com.platform.mapper.OperationLogMapper;
import com.platform.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Pointcut("execution(* com.platform.controller.*.*(..)) && " +
              "!execution(* com.platform.controller.AuthController.*(..)) && " +
              "!execution(* com.platform.controller.LogController.*(..)) && " +
              "!execution(* com.platform.controller.DashboardController.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        if (!"POST".equals(method) && !"PUT".equals(method) && !"DELETE".equals(method)) {
            return joinPoint.proceed();
        }

        String username = "anonymous";
        Long userId = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                username = jwtUtil.getUsernameFromToken(token);
                userId = jwtUtil.getUserIdFromToken(token);
            } catch (Exception ignored) {}
        }

        String moduleName = extractModuleName(uri);
        String actionName = method + " " + getActionDescription(uri, method);
        String description = buildDescription(joinPoint, method);

        long startTime = System.currentTimeMillis();
        boolean success = false;

        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(userId);
            operationLog.setUsername(username);
            operationLog.setModule(moduleName);
            operationLog.setAction(actionName);
            operationLog.setDescription(description);
            operationLog.setIpAddress(getClientIp(request));
            operationLog.setCreatedAt(LocalDateTime.now());

            if (success) {
                operationLog.setDescription(operationLog.getDescription() + " [耗时: " + duration + "ms]");
            } else {
                operationLog.setDescription(operationLog.getDescription() + " [执行失败]");
            }

            try {
                operationLogMapper.insert(operationLog);
            } catch (Exception e) {
                log.error("[OperationLog] 记录操作日志失败: {}", e.getMessage());
            }
        }
    }

    private String extractModuleName(String uri) {
        if (uri.contains("/users")) return "用户管理";
        if (uri.contains("/roles")) return "角色管理";
        if (uri.contains("/permissions")) return "权限管理";
        if (uri.contains("/modules")) return "模块管理";
        if (uri.contains("/configs")) return "系统配置";
        if (uri.contains("/logs")) return "日志管理";
        return "未知模块";
    }

    private String getActionDescription(String uri, String method) {
        return switch (method) {
            case "POST" -> uri.contains("/login") || uri.contains("/logout") ? "登录/登出" : "新增";
            case "PUT" -> "修改";
            case "DELETE" -> "删除";
            default -> "查询";
        };
    }

    private String buildDescription(ProceedingJoinPoint joinPoint, String method) {
        StringBuilder desc = new StringBuilder();
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg == null || arg instanceof MultipartFile ||
                arg instanceof HttpServletRequest || arg instanceof jakarta.servlet.http.HttpServletResponse) {
                continue;
            }

            try {
                String json = objectMapper.writeValueAsString(arg);
                if (json.length() > 200) {
                    json = json.substring(0, 200) + "...";
                }
                desc.append(json).append("; ");
            } catch (Exception ignored) {}
        }

        return desc.length() == 0 ? "无参数" : desc.toString().trim();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
