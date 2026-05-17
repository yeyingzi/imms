package com.platform.service;

import com.platform.dto.UserSession;
import com.platform.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizedUserSessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_KEY_PREFIX = "user:session:";
    private static final long REDIS_EXPIRE_HOURS = 24;

    public void saveUserSession(UserSession session) {
        String key = buildKey(session.getUserId());
        try {
            String json = JsonUtil.toJson(session);
            redisTemplate.opsForValue().set(key, json, REDIS_EXPIRE_HOURS, TimeUnit.HOURS);
            log.info("[Session] 会话已保存到Redis: userId={}", session.getUserId());
        } catch (Exception e) {
            log.error("[Session] 保存会话到Redis失败: userId={}, 错误: {}", 
                     session.getUserId(), e.getMessage());
            throw new RuntimeException("Redis保存会话失败", e);
        }
    }

    public UserSession getUserSession(Long userId) {
        String key = buildKey(userId);
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj == null) {
                log.warn("[Session] Redis中未找到会话: userId={}", userId);
                return null;
            }
            
            String json = obj.toString();
            UserSession session = JsonUtil.fromJson(json, UserSession.class);
            log.info("[Session] 从Redis获取会话成功: userId={}", userId);
            return session;
        } catch (Exception e) {
            log.error("[Session] 从Redis获取会话失败: userId={}, 错误: {}", 
                     userId, e.getMessage());
            return null;
        }
    }

    public void deleteUserSession(Long userId) {
        try {
            String key = buildKey(userId);
            redisTemplate.delete(key);
            log.info("[Session] 会话已删除: userId={}", userId);
        } catch (Exception e) {
            log.error("[Session] 删除会话失败: userId={}", userId, e);
        }
    }

    public boolean existsUserSession(Long userId) {
        String key = buildKey(userId);
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("[Session] 检查会话存在性失败: userId={}", userId, e);
            return false;
        }
    }

    private String buildKey(Long userId) {
        return SESSION_KEY_PREFIX + userId;
    }
}
