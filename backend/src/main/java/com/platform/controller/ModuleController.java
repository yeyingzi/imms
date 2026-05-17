package com.platform.controller;

import com.platform.common.result.Result;
import com.platform.entity.Module;
import com.platform.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping
    public Result<List<Module>> list() {
        List<Module> modules = moduleService.getModuleList();
        return Result.success(modules);
    }

    @GetMapping("/{id}")
    public Result<Module> getById(@PathVariable("id") Long id) {
        Module module = moduleService.getModuleById(id);
        return Result.success(module);
    }

    @PutMapping("/{id}/toggle")
    public Result<Map<String, Object>> toggle(@PathVariable("id") Long id) {
        Map<String, Object> result = moduleService.toggleModule(id);
        return Result.success(result);
    }
}
