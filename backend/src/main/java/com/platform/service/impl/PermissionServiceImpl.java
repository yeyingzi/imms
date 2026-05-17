package com.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.platform.entity.Permission;
import com.platform.mapper.PermissionMapper;
import com.platform.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public List<Permission> getPermissionList() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSortOrder);
        return permissionMapper.selectList(wrapper);
    }

    @Override
    public Permission getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        return permission;
    }

    @Override
    @Transactional
    public void createPermission(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    @Transactional
    public void updatePermission(Long id, Permission permission) {
        Permission existing = permissionMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("权限不存在");
        }
        
        if (permission.getName() != null) {
            existing.setName(permission.getName());
        }
        if (permission.getCode() != null) {
            existing.setCode(permission.getCode());
        }
        if (permission.getType() != null) {
            existing.setType(permission.getType());
        }
        if (permission.getPath() != null) {
            existing.setPath(permission.getPath());
        }
        if (permission.getParentId() != null) {
            existing.setParentId(permission.getParentId());
        }
        if (permission.getSortOrder() != null) {
            existing.setSortOrder(permission.getSortOrder());
        }
        
        permissionMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        if (permissionMapper.selectById(id) == null) {
            throw new RuntimeException("权限不存在");
        }
        permissionMapper.deleteById(id);
    }
}
