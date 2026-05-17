package com.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.platform.entity.Role;
import java.util.List;

public interface RoleService {
    
    IPage<Role> getRoleList(Integer pageNum, Integer pageSize);
    
    Role getRoleById(Long id);
    
    void createRole(Role role);
    
    void updateRole(Long id, Role role);
    
    void deleteRole(Long id);
    
    List<Long> getRolePermissions(Long roleId);
    
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
