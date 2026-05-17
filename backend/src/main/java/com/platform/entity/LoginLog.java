package com.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_login_log")
public class LoginLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private Integer loginType;

    private Integer status;

    private String errorMsg;

    private String ipAddress;

    private String userAgent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
