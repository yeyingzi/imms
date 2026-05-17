package com.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.platform.dto.LoginVO;
import com.platform.dto.UserVO;
import com.platform.entity.LoginLog;
import com.platform.entity.Permission;
import com.platform.entity.User;
import com.platform.mapper.LoginLogMapper;
import com.platform.mapper.PermissionMapper;
import com.platform.mapper.UserMapper;
import com.platform.mapper.UserRoleMapper;
import com.platform.service.AuthService;
import com.platform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;
    private final LoginLogMapper loginLogMapper;
    private final UserRoleMapper userRoleMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public LoginVO login(String username, String password, String ipAddress, String userAgent) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            saveLoginLog(null, username, 1, 0, "用户不存在", ipAddress, userAgent);
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            saveLoginLog(user.getId(), username, 1, 0, "账号已禁用", ipAddress, userAgent);
            throw new RuntimeException("账号已禁用");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            saveLoginLog(user.getId(), username, 1, 0, "密码错误", ipAddress, userAgent);
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        List<String> permissionCodes = permissionMapper.selectPermissionsByUserId(user.getId())
                .stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());

        List<String> roleCodes = userRoleMapper.selectRoleCodesByUserId(user.getId());

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setRealName(user.getRealName());
        userVO.setPermissions(permissionCodes);
        userVO.setRoles(roleCodes);
        userVO.setAvatar(user.getAvatar());

        saveLoginLog(user.getId(), username, 1, 1, "登录成功", ipAddress, userAgent);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setUser(userVO);

        return loginVO;
    }

    @Override
    public void logout(String token) {
    }

    @Override
    public UserVO getUserInfo(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<String> permissionCodes = permissionMapper.selectPermissionsByUserId(userId)
                .stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());

        List<String> roleCodes = userRoleMapper.selectRoleCodesByUserId(userId);

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setRealName(user.getRealName());
        userVO.setPermissions(permissionCodes);
        userVO.setRoles(roleCodes);
        userVO.setAvatar(user.getAvatar());

        return userVO;
    }

    @Override
    @Transactional
    public void changePassword(String token, String oldPassword, String newPassword) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码不正确");
        }

        if (newPassword.length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private void saveLoginLog(Long userId, String username, Integer loginType,
                             Integer status, String errorMsg, String ipAddress, String userAgent) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setLoginType(loginType);
        log.setStatus(status);
        log.setErrorMsg(errorMsg);
        log.setIpAddress(ipAddress != null ? ipAddress : "unknown");
        log.setUserAgent(userAgent != null ? userAgent : "unknown");
        log.setCreatedAt(LocalDateTime.now());
        loginLogMapper.insert(log);
    }
}
