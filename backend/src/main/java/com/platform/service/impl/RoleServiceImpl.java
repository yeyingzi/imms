package com.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.entity.Role;
import com.platform.entity.RolePermission;
import com.platform.mapper.PermissionMapper;
import com.platform.mapper.RoleMapper;
import com.platform.mapper.RolePermissionMapper;
import com.platform.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public Page<Role> getRoleList(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Role::getCreatedAt);
        Page<Role> page = new Page<>(pageNum, pageSize);
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public Role getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        return role;
    }

    @Override
    @Transactional
    public void createRole(Role role) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, role.getCode());
        if (roleMapper.selectOne(wrapper) != null) {
            throw new RuntimeException("角色编码已存在");
        }
        roleMapper.insert(role);
    }

    @Override
    @Transactional
    public void updateRole(Long id, Role role) {
        Role existingRole = roleMapper.selectById(id);
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }
        
        if (role.getName() != null) {
            existingRole.setName(role.getName());
        }
        if (role.getDescription() != null) {
            existingRole.setDescription(role.getDescription());
        }
        
        roleMapper.updateById(existingRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (roleMapper.selectById(id) == null) {
            throw new RuntimeException("角色不存在");
        }
        roleMapper.deleteById(id);
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        if (roleMapper.selectById(roleId) == null) {
            throw new RuntimeException("角色不存在");
        }
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        if (roleMapper.selectById(roleId) == null) {
            throw new RuntimeException("角色不存在");
        }
        
        LambdaUpdateWrapper<RolePermission> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                if (permissionMapper.selectById(permissionId) != null) {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setPermissionId(permissionId);
                    rolePermissionMapper.insert(rolePermission);
                }
            }
        }
    }
}
