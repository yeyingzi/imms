package com.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.result.PageResult;
import com.platform.common.result.Result;
import com.platform.entity.LoginLog;
import com.platform.entity.OperationLog;
import com.platform.mapper.LoginLogMapper;
import com.platform.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class LogController {

    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    @GetMapping("/operation")
    public Result<PageResult<OperationLog>> listOperationLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(OperationLog::getUsername, username);
        }
        if (module != null && !module.isEmpty()) {
            wrapper.eq(OperationLog::getModule, module);
        }
        wrapper.orderByDesc(OperationLog::getCreatedAt);

        Page<OperationLog> page = new Page<>(pageNum, pageSize);
        Page<OperationLog> result = operationLogMapper.selectPage(page, wrapper);

        PageResult<OperationLog> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                pageNum,
                pageSize
        );

        return Result.success(pageResult);
    }

    @GetMapping("/login")
    public Result<PageResult<LoginLog>> listLoginLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(LoginLog::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(LoginLog::getStatus, status);
        }
        wrapper.orderByDesc(LoginLog::getCreatedAt);

        Page<LoginLog> page = new Page<>(pageNum, pageSize);
        Page<LoginLog> result = loginLogMapper.selectPage(page, wrapper);

        PageResult<LoginLog> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                pageNum,
                pageSize
        );

        return Result.success(pageResult);
    }
}
