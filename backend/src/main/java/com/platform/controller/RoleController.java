package com.platform.controller;

import com.platform.common.result.PageResult;
import com.platform.common.result.Result;
import com.platform.entity.Role;
import com.platform.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public Result<PageResult<Role>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        var page = roleService.getRoleList(pageNum, pageSize);
        PageResult<Role> pageResult = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                pageNum,
                pageSize
        );
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return Result.success(role);
    }

    @PostMapping
    public Result<?> create(@RequestBody Role role) {
        roleService.createRole(role);
        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Role role) {
        roleService.updateRole(id, role);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissions(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getRolePermissions(id);
        return Result.success(permissionIds);
    }

    @PutMapping("/{id}/permissions")
    public Result<?> assignPermissions(@PathVariable Long id, @RequestBody PermissionAssignRequest request) {
        roleService.assignPermissions(id, request.getPermissions());
        return Result.success("权限分配成功", null);
    }

    public static class PermissionAssignRequest {
        private List<Long> permissions;
        public List<Long> getPermissions() { return permissions; }
        public void setPermissions(List<Long> permissions) { this.permissions = permissions; }
    }
}
