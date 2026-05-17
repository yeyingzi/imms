package com.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private Integer type;

    private String path;

    private Long parentId;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<Permission> children;
}
