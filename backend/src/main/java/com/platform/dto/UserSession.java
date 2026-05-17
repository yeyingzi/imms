package com.platform.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserSession implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String username;

    private String realName;

    private String avatar;

    private List<String> roles;

    private List<String> permissions;

    private Long loginTime;

    private String ipAddress;

    private String token;

    public static UserSession from(Long userId, String username, String realName, 
                                   String avatar, List<String> roles, List<String> permissions,
                                   String token, String ipAddress) {
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setUsername(username);
        session.setRealName(realName);
        session.setAvatar(avatar);
        session.setRoles(roles);
        session.setPermissions(permissions);
        session.setLoginTime(System.currentTimeMillis());
        session.setToken(token);
        session.setIpAddress(ipAddress);
        return session;
    }
}
