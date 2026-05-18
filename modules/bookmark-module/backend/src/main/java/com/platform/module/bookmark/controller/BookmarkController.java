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
            @RequestParam(required = false) Boolean mineOnly) {

        Page<Bookmark> pageParam = new Page<>(page, pageSize);
        Page<Bookmark> result = bookmarkService.getBookmarkList(pageParam, keyword, currentUser, mineOnly);
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
    public Result<Void> updateBookmark(
            @PathVariable Long id,
            @RequestParam String currentUser,
            @RequestBody Bookmark bookmark) {
        try {
            bookmark.setId(id);
            bookmarkService.updateBookmark(id, currentUser, bookmark);
            return Result.success();
        } catch (SecurityException e) {
            logger.warn("更新书签权限不足: id={}, currentUser={}", id, currentUser);
            return Result.error(403, "无权限修改他人的书签");
        } catch (Exception e) {
            logger.error("更新书签失败", e);
            return Result.error(500, "更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBookmark(
            @PathVariable Long id,
            @RequestParam String currentUser) {
        try {
            bookmarkService.deleteBookmark(id, currentUser);
            return Result.success();
        } catch (SecurityException e) {
            logger.warn("删除书签权限不足: id={}, currentUser={}", id, currentUser);
            return Result.error(403, "无权限删除他人的书签");
        } catch (Exception e) {
            logger.error("删除书签失败", e);
            return Result.error(500, "删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/privacy")
    public Result<Void> togglePrivacy(
            @PathVariable Long id,
            @RequestParam String currentUser) {
        try {
            bookmarkService.togglePrivacy(id, currentUser);
            return Result.success();
        } catch (SecurityException e) {
            logger.warn("切换隐私状态权限不足: id={}, currentUser={}", id, currentUser);
            return Result.error(403, "无权限修改他人的书签");
        } catch (Exception e) {
            logger.error("切换隐私状态失败", e);
            return Result.error(500, "操作失败");
        }
    }
}
