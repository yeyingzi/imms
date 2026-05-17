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
            wrapper.and(w -> w
                .like(Bookmark::getTitle, keyword)
                .or()
                .like(Bookmark::getDescription, keyword)
                .or()
                .like(Bookmark::getUrl, keyword)
            );
        }

        if (Boolean.TRUE.equals(mineOnly) && StringUtils.hasText(currentUser)) {
            wrapper.eq(Bookmark::getCreatedBy, currentUser);
        }

        if (isPrivate != null) {
            wrapper.eq(Bookmark::getIsPrivate, isPrivate);
        } else {
            wrapper.ne(Bookmark::getIsPrivate, 1);
        }

        if ("clickCount".equals(sortBy)) {
            if ("asc".equals(sortOrder)) {
                wrapper.orderByAsc(Bookmark::getClickCount);
            } else {
                wrapper.orderByDesc(Bookmark::getClickCount);
            }
        } else {
            if ("asc".equals(sortOrder)) {
                wrapper.orderByAsc(Bookmark::getCreatedAt);
            } else {
                wrapper.orderByDesc(Bookmark::getCreatedAt);
            }
        }

        return this.page(page, wrapper);
    }

    @Override
    public Bookmark getBookmarkById(Long id) {
        return this.getById(id);
    }

    @Override
    public void createBookmark(Bookmark bookmark) {
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

    @Override
    public void incrementClickCount(Long id) {
        Bookmark bookmark = this.getById(id);
        if (bookmark != null) {
            bookmark.setClickCount(bookmark.getClickCount() + 1);
            this.updateById(bookmark);
        }
    }
}
