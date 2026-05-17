package com.platform.module.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.module.example.entity.Example;
import com.platform.module.example.mapper.ExampleMapper;
import com.platform.module.example.service.IExampleService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl extends ServiceImpl<ExampleMapper, Example> implements IExampleService {
}