package com.platform.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.platform.entity.Role;
import com.platform.entity.User;
import com.platform.entity.UserRole;
import com.platform.mapper.RoleMapper;
import com.platform.mapper.UserMapper;
import com.platform.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public void run(ApplicationArguments args) {
        User admin = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, "admin")
        );

        if (admin != null) {
            LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRole::getUserId, admin.getId());
            long roleCount = userRoleMapper.selectCount(wrapper);

            if (roleCount == 0) {
                Role adminRole = roleMapper.selectOne(
                    new LambdaQueryWrapper<Role>().eq(Role::getCode, "SUPER_ADMIN")
                );

                if (adminRole != null) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(admin.getId());
                    userRole.setRoleId(adminRole.getId());
                    userRoleMapper.insert(userRole);
                    log.info("管理员已分配超级管理员角色");
                }
            }
        }
    }
}
