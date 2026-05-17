package com.platform.service;

import com.platform.dto.LoginVO;
import com.platform.dto.UserVO;

public interface AuthService {
    
    LoginVO login(String username, String password, String ipAddress, String userAgent);
    
    void logout(String token);
    
    UserVO getUserInfo(String token);
    
    void changePassword(String token, String oldPassword, String newPassword);
}
