package com.platform.service;

import com.platform.dto.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_KEY_PREFIX = "user:session:";
    private static final long DEFAULT_EXPIRE_HOURS = 24;

    public void saveUserSession(UserSession session) {
        saveUserSession(session, DEFAULT_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    public void saveUserSession(UserSession session, long timeout, TimeUnit timeUnit) {
        String key = buildKey(session.getUserId());
        try {
            redisTemplate.opsForValue().set(key, session, timeout, timeUnit);
            log.debug("[UserSession] 用户会话已保存: userId={}, key={}", session.getUserId(), key);
        } catch (Exception e) {
            log.error("[UserSession] 保存用户会话失败: userId={}", session.getUserId(), e);
        }
    }

    public UserSession getUserSession(Long userId) {
        String key = buildKey(userId);
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj instanceof UserSession) {
                log.debug("[UserSession] 从缓存获取用户会话成功: userId={}", userId);
                return (UserSession) obj;
            }
        } catch (Exception e) {
            log.error("[UserSession] 获取用户会话失败: userId={}", userId, e);
        }
        return null;
    }

    public void updateUserSession(UserSession session) {
        String key = buildKey(session.getUserId());
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (expire != null && expire > 0) {
            try {
                redisTemplate.opsForValue().set(key, session, expire, TimeUnit.SECONDS);
                log.debug("[UserSession] 用户会话已更新: userId={}", session.getUserId());
            } catch (Exception e) {
                log.error("[UserSession] 更新用户会话失败: userId={}", session.getUserId(), e);
            }
        } else {
            saveUserSession(session);
        }
    }

    public void deleteUserSession(Long userId) {
        String key = buildKey(userId);
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("[UserSession] 用户会话已删除: userId={}, result={}", userId, deleted);
        } catch (Exception e) {
            log.error("[UserSession] 删除用户会话失败: userId={}", userId, e);
        }
    }

    public boolean existsUserSession(Long userId) {
        String key = buildKey(userId);
        try {
            Boolean hasKey = redisTemplate.hasKey(key);
            return hasKey != null && hasKey;
        } catch (Exception e) {
            log.error("[UserSession] 检查用户会话存在性失败: userId={}", userId, e);
            return false;
        }
    }

    public void refreshExpire(Long userId) {
        refreshExpire(userId, DEFAULT_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    public void refreshExpire(Long userId, long timeout, TimeUnit timeUnit) {
        String key = buildKey(userId);
        try {
            Boolean result = redisTemplate.expire(key, timeout, timeUnit);
            if (result != null && result) {
                log.debug("[UserSession] 用户会话过期时间已刷新: userId={}", userId);
            }
        } catch (Exception e) {
            log.error("[UserSession] 刷新用户会话过期时间失败: userId={}", userId, e);
        }
    }

    private String buildKey(Long userId) {
        return SESSION_KEY_PREFIX + userId;
    }
}
