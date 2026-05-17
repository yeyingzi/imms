package com.platform.service;

import com.platform.entity.Permission;
import java.util.List;

public interface PermissionService {
    
    List<Permission> getPermissionList();
    
    Permission getPermissionById(Long id);
    
    void createPermission(Permission permission);
    
    void updatePermission(Long id, Permission permission);
    
    void deletePermission(Long id);
}
