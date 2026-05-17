package com.platform.module.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.module.example.entity.Example;

public interface ExampleService extends IService<Example> {
    Page<Example> selectPage(Page<Example> page);
    Example getById(Long id);
    void create(Example example);
    void update(Example example);
    void delete(Long id);
}
