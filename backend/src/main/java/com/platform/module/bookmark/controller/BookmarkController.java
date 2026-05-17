package com.platform.module.bookmark.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.result.Result;
import com.platform.module.bookmark.entity.Bookmark;
import com.platform.module.bookmark.service.BookmarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkController.class);

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
        try {
            bookmarkService.createBookmark(bookmark);
            return Result.success();
        } catch (DuplicateKeyException e) {
            logger.error("创建书签失败 - URL已存在: {}", bookmark.getUrl(), e);
            return Result.error(409, "该网址已被收藏");
        } catch (Exception e) {
            logger.error("创建书签失败", e);
            return Result.error(500, "创建失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateBookmark(@PathVariable Long id, @RequestBody Bookmark bookmark) {
        try {
            bookmark.setId(id);
            bookmarkService.updateBookmark(bookmark);
            return Result.success();
        } catch (Exception e) {
            logger.error("更新书签失败", e);
            return Result.error(500, "更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBookmark(@PathVariable Long id) {
        try {
            bookmarkService.deleteBookmark(id);
            return Result.success();
        } catch (Exception e) {
            logger.error("删除书签失败", e);
            return Result.error(500, "删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/privacy")
    public Result<Void> togglePrivacy(@PathVariable Long id) {
        try {
            bookmarkService.togglePrivacy(id);
            return Result.success();
        } catch (Exception e) {
            logger.error("切换隐私状态失败", e);
            return Result.error(500, "操作失败");
        }
    }
}
