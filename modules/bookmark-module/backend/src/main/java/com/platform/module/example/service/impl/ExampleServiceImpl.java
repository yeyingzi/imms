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

    @Override
    public Example getById(Long id) {
        return this.getById(id);
    }

    @Override
    public void create(Example example) {
        this.save(example);
    }

    @Override
    public void update(Example example) {
        this.updateById(example);
    }

    @Override
    public void delete(Long id) {
        this.removeById(id);
    }
}
