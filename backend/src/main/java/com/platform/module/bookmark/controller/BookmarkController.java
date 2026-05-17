package com.platform.module.bookmark.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.result.Result;
import com.platform.module.bookmark.entity.Bookmark;
import com.platform.module.bookmark.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @GetMapping
    public Result<Page<Bookmark>> getBookmarkList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String currentUser,
            @RequestParam(required = false) Boolean mineOnly,
            @RequestParam(required = false) Integer isPrivate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        Page<Bookmark> pageParam = new Page<>(page, pageSize);
        Page<Bookmark> result = bookmarkService.getBookmarkList(pageParam, keyword, currentUser, mineOnly, isPrivate, sortBy, sortOrder);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> createBookmark(@RequestBody Bookmark bookmark) {
        bookmarkService.createBookmark(bookmark);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateBookmark(@PathVariable Long id, @RequestBody Bookmark bookmark) {
        bookmark.setId(id);
        bookmarkService.updateBookmark(bookmark);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBookmark(@PathVariable Long id) {
        bookmarkService.deleteBookmark(id);
        return Result.success();
    }

    @PutMapping("/{id}/privacy")
    public Result<Void> togglePrivacy(@PathVariable Long id) {
        bookmarkService.togglePrivacy(id);
        return Result.success();
    }

    @PutMapping("/{id}/click")
    public Result<Void> incrementClickCount(@PathVariable Long id) {
        bookmarkService.incrementClickCount(id);
        return Result.success();
    }
}
