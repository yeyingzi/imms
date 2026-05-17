package com.platform.util;

import com.platform.dto.UserSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserContext {

    private static final ThreadLocal<UserSession> USER_SESSION_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(UserSession userSession) {
        if (userSession != null) {
            USER_SESSION_THREAD_LOCAL.set(userSession);
            log.debug("[UserContext] 用户上下文已设置: userId={}, username={}", 
                     userSession.getUserId(), userSession.getUsername());
        }
    }

    public static UserSession getUser() {
        return USER_SESSION_THREAD_LOCAL.get();
    }

    public static Long getCurrentUserId() {
        UserSession session = getUser();
        if (session != null) {
            return session.getUserId();
        }
        log.warn("[UserContext] 获取当前用户ID失败: 用户上下文为空");
        return null;
    }

    public static String getCurrentUsername() {
        UserSession session = getUser();
        if (session != null) {
            return session.getUsername();
        }
        log.warn("[UserContext] 获取当前用户名失败: 用户上下文为空");
        return null;
    }

    public static String getCurrentRealName() {
        UserSession session = getUser();
        return session != null ? session.getRealName() : null;
    }

    public static boolean isLogin() {
        return getUser() != null;
    }

    public static boolean hasPermission(String permission) {
        UserSession session = getUser();
        if (session == null || session.getPermissions() == null) {
            return false;
        }
        return session.getPermissions().contains(permission);
    }

    public static boolean hasRole(String role) {
        UserSession session = getUser();
        if (session == null || session.getRoles() == null) {
            return false;
        }
        return session.getRoles().contains(role);
    }

    public static void clear() {
        USER_SESSION_THREAD_LOCAL.remove();
        log.debug("[UserContext] 用户上下文已清除");
    }
}
