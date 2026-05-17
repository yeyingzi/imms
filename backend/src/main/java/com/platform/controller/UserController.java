package com.platform.controller;

import com.platform.common.result.PageResult;
import com.platform.common.result.Result;
import com.platform.entity.User;
import com.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<PageResult<User>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        var page = userService.getUserList(username, status, pageNum, pageSize);
        PageResult<User> pageResult = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                pageNum,
                pageSize
        );
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @PostMapping
    public Result<?> create(@RequestBody User user) {
        userService.createUser(user);
        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        userService.updateUserStatus(id, request.getStatus());
        return Result.success("状态更新成功", null);
    }

    @GetMapping("/{id}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = userService.getUserRoles(id);
        return Result.success(roleIds);
    }

    @PutMapping("/{id}/roles")
    public Result<?> assignRoles(@PathVariable Long id, @RequestBody RoleAssignRequest request) {
        userService.assignRoles(id, request.getRoles());
        return Result.success("角色分配成功", null);
    }

    public static class StatusRequest {
        private Integer status;
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }

    public static class RoleAssignRequest {
        private List<Long> roles;
        public List<Long> getRoles() { return roles; }
        public void setRoles(List<Long> roles) { this.roles = roles; }
    }
}
