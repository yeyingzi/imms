package com.platform.module.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.result.PageResult;
import com.platform.common.result.Result;
import com.platform.module.example.entity.Example;
import com.platform.module.example.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/example-module")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExampleController {

    @Autowired
    private ExampleService exampleService;

    @GetMapping("/list")
    public Result<PageResult<Example>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Example> page = new Page<>(pageNum, pageSize);
        Page<Example> result = exampleService.selectPage(page);

        PageResult<Example> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                (int) result.getCurrent(),
                (int) result.getSize()
        );

        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<Example> getById(@PathVariable Long id) {
        Example example = exampleService.getById(id);
        if (example == null) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(example);
    }

    @PostMapping
    public Result<?> create(@RequestBody Example example) {
        exampleService.create(example);
        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Example example) {
        example.setId(id);
        exampleService.update(example);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        exampleService.delete(id);
        return Result.success("删除成功", null);
    }
}
