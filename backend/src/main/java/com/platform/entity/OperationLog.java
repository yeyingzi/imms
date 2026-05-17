package com.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String module;

    private String action;

    private String description;

    private String ipAddress;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
