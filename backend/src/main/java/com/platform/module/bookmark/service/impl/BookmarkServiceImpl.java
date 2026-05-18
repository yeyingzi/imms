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
    public Page<Bookmark> getBookmarkList(Page<Bookmark> page, String keyword, String currentUser, Boolean mineOnly) {
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Bookmark::getTitle, keyword);
        }

        if (Boolean.TRUE.equals(mineOnly) && StringUtils.hasText(currentUser)) {
            wrapper.eq(Bookmark::getCreatedBy, currentUser);
        } else if (StringUtils.hasText(currentUser)) {
            wrapper.apply("is_private = 0 OR (is_private = 1 AND created_by = {0})", currentUser);
        } else {
            wrapper.eq(Bookmark::getIsPrivate, 0);
        }

        wrapper.orderByDesc(Bookmark::getCreatedAt);

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
    public void updateBookmark(Long id, String currentUser, Bookmark bookmark) {
        Bookmark existing = this.getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("书签不存在");
        }

        if (!existing.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("无权限修改他人的书签");
        }

        bookmark.setId(id);
        bookmark.setCreatedBy(existing.getCreatedBy());
        this.updateById(bookmark);
    }

    @Override
    public void deleteBookmark(Long id, String currentUser) {
        Bookmark existing = this.getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("书签不存在");
        }

        if (!existing.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("无权限删除他人的书签");
        }

        this.removeById(id);
    }

    @Override
    public void togglePrivacy(Long id, String currentUser) {
        Bookmark bookmark = this.getById(id);
        if (bookmark == null) {
            throw new IllegalArgumentException("书签不存在");
        }

        if (!bookmark.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("无权限修改他人的书签");
        }

        bookmark.setIsPrivate(bookmark.getIsPrivate() == 0 ? 1 : 0);
        this.updateById(bookmark);
    }
}
