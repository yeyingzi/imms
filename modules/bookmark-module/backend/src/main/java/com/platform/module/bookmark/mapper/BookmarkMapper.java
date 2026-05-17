package com.platform.module.bookmark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.module.bookmark.entity.Bookmark;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookmarkMapper extends BaseMapper<Bookmark> {
}
