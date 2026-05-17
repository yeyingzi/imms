package com.platform.module.example.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.module.example.entity.Example;
import com.platform.module.example.mapper.ExampleMapper;
import com.platform.module.example.service.ExampleService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl extends ServiceImpl<ExampleMapper, Example> implements ExampleService {

    @Override
    public Page<Example> selectPage(Page<Example> page) {
        return this.page(page);
    }
}
