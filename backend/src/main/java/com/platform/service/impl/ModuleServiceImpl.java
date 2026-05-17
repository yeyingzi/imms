package com.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.platform.entity.Module;
import com.platform.mapper.ModuleMapper;
import com.platform.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleMapper moduleMapper;

    @Override
    public List<Module> getModuleList() {
        LambdaQueryWrapper<Module> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Module::getId);
        return moduleMapper.selectList(wrapper);
    }

    @Override
    public Module getModuleById(Long id) {
        Module module = moduleMapper.selectById(id);
        if (module == null) {
            throw new RuntimeException("模块不存在");
        }
        return module;
    }

    @Override
    @Transactional
    public Map<String, Object> toggleModule(Long id) {
        Module module = moduleMapper.selectById(id);
        if (module == null) {
            throw new RuntimeException("模块不存在");
        }
        
        Integer newStatus = module.getStatus() == 1 ? 0 : 1;
        module.setStatus(newStatus);
        moduleMapper.updateById(module);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", newStatus);
        result.put("message", newStatus == 1 ? "模块已启用" : "模块已停用");
        
        return result;
    }
}
