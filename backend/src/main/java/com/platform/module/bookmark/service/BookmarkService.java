package com.platform.module.bookmark.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.module.bookmark.entity.Bookmark;

public interface BookmarkService extends IService<Bookmark> {
    Page<Bookmark> getBookmarkList(Page<Bookmark> page, String keyword, String currentUser, Boolean mineOnly);
    Bookmark getBookmarkById(Long id);
    void createBookmark(Bookmark bookmark);
    void updateBookmark(Long id, String currentUser, Bookmark bookmark);
    void deleteBookmark(Long id, String currentUser);
    void togglePrivacy(Long id, String currentUser);
}
