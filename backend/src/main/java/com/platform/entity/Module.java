package com.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_module")
public class Module {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String moduleKey;

    private String name;

    private String version;

    private String description;

    private String author;

    private String icon;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
