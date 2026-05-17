package com.platform.controller;

import com.platform.common.result.Result;
import com.platform.dto.LoginRequest;
import com.platform.dto.LoginVO;
import com.platform.dto.PasswordChangeRequest;
import com.platform.dto.UserVO;
import com.platform.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        LoginVO loginVO = authService.login(request.getUsername(), request.getPassword(), ipAddress, userAgent);
        return Result.success("登录成功", loginVO);
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return Result.success("退出成功", null);
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestHeader("Authorization") String authHeader,
                                    @RequestBody PasswordChangeRequest request) {
        String token = authHeader.substring(7);
        authService.changePassword(token, request.getOldPassword(), request.getNewPassword());
        return Result.success("密码修改成功", null);
    }

    @GetMapping("/userinfo")
    public Result<UserVO> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        UserVO userVO = authService.getUserInfo(token);
        return Result.success(userVO);
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
