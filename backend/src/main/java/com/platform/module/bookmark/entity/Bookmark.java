package com.platform.module.bookmark.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bm_bookmark")
public class Bookmark {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String url;

    private String description;

    private String icon;

    private String createdBy;

    private Integer isPrivate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
