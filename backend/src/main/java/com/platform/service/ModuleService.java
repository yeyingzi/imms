package com.platform.service;

import com.platform.entity.Module;
import java.util.List;
import java.util.Map;

public interface ModuleService {
    
    List<Module> getModuleList();
    
    Module getModuleById(Long id);
    
    Map<String, Object> toggleModule(Long id);
}
