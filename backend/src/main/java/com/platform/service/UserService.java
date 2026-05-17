package com.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.platform.entity.User;
import java.util.List;

public interface UserService {
    
    IPage<User> getUserList(String username, Integer status, Integer pageNum, Integer pageSize);
    
    User getUserById(Long id);
    
    void createUser(User user);
    
    void updateUser(Long id, User user);
    
    void deleteUser(Long id);
    
    void updateUserStatus(Long id, Integer status);
    
    List<Long> getUserRoles(Long userId);
    
    void assignRoles(Long userId, List<Long> roleIds);
}
