package com.platform.module.bookmark.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.module.bookmark.entity.Bookmark;
import com.platform.module.bookmark.mapper.BookmarkMapper;
import com.platform.module.bookmark.service.BookmarkService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {

    @Override
    public Page<Bookmark> getBookmarkList(Page<Bookmark> page, String keyword, String currentUser, Boolean mineOnly, Integer isPrivate, String sortBy, String sortOrder) {
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Bookmark::getTitle, keyword);
        }

        if (isPrivate != null) {
            wrapper.eq(Bookmark::getIsPrivate, isPrivate);
        } else if (Boolean.TRUE.equals(mineOnly) && StringUtils.hasText(currentUser)) {
            wrapper.eq(Bookmark::getCreatedBy, currentUser);
        } else if (StringUtils.hasText(currentUser)) {
            wrapper.apply("is_private = 0 OR (is_private = 1 AND created_by = {0})", currentUser);
        } else {
            wrapper.eq(Bookmark::getIsPrivate, 0);
        }

        if ("asc".equals(sortOrder)) {
            wrapper.orderByAsc(Bookmark::getCreatedAt);
        } else {
            wrapper.orderByDesc(Bookmark::getCreatedAt);
        }

        return this.page(page, wrapper);
    }

    @Override
    public Bookmark getBookmarkById(Long id) {
        return this.getById(id);
    }

    @Override
    public void createBookmark(Bookmark bookmark) {
        if (!StringUtils.hasText(bookmark.getCreatedBy())) {
            bookmark.setCreatedBy("anonymous");
        }
        if (bookmark.getIsPrivate() == null) {
            bookmark.setIsPrivate(0);
        }
        this.save(bookmark);
    }

    @Override
    public void updateBookmark(Bookmark bookmark) {
        this.updateById(bookmark);
    }

    @Override
    public void deleteBookmark(Long id) {
        this.removeById(id);
    }

    @Override
    public void togglePrivacy(Long id) {
        Bookmark bookmark = this.getById(id);
        if (bookmark != null) {
            bookmark.setIsPrivate(bookmark.getIsPrivate() == 0 ? 1 : 0);
            this.updateById(bookmark);
        }
    }
}
