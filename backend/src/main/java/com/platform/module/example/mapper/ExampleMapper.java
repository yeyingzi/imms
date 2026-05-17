package com.platform.module.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.module.example.entity.Example;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExampleMapper extends BaseMapper<Example> {
}
