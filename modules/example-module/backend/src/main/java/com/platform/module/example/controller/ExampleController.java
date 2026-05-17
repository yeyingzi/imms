package com.platform.module.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.result.Result;
import com.platform.module.example.entity.Example;
import com.platform.module.example.service.IExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/example-module")
public class ExampleController {

    @Autowired
    private IExampleService exampleService;

    @GetMapping("/list")
    public Result<Page<Example>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        Page<Example> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Example> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Example::getName, keyword.trim());
        }
        
        wrapper.orderByDesc(Example::getCreatedAt);
        
        return Result.success(exampleService.page(page, wrapper));
    }

    @GetMapping("/{id}")
    public Result<Example> getById(@PathVariable Long id) {
        return Result.success(exampleService.getById(id));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody Example example) {
        return Result.success(exampleService.save(example));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Example example) {
        example.setId(id);
        return Result.success(exampleService.updateById(example));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(exampleService.removeById(id));
    }
}