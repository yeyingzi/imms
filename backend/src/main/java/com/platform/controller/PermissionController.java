package com.platform.controller;

import com.platform.common.result.Result;
import com.platform.entity.Permission;
import com.platform.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public Result<List<Permission>> list() {
        List<Permission> permissions = permissionService.getPermissionList();
        return Result.success(permissions);
    }

    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getPermissionById(id);
        return Result.success(permission);
    }

    @PostMapping
    public Result<?> create(@RequestBody Permission permission) {
        permissionService.createPermission(permission);
        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Permission permission) {
        permissionService.updatePermission(id, permission);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success("删除成功", null);
    }
}
