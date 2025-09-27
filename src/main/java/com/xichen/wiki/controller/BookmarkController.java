package com.xichen.wiki.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.BatchDeleteBookmarkRequest;
import com.xichen.wiki.dto.CreateBookmarkRequest;
import com.xichen.wiki.dto.UpdateBookmarkRequest;
import com.xichen.wiki.entity.Bookmark;
import com.xichen.wiki.service.BookmarkService;
import com.xichen.wiki.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 书签控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bookmarks")
@Validated
@Tag(name = "书签管理", description = "电子书书签创建、管理相关接口")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "创建书签", description = "为电子书创建书签", 
               security = @SecurityRequirement(name = "Authorization"))
    @PostMapping
    public Result<Bookmark> createBookmark(@Valid @RequestBody CreateBookmarkRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Bookmark bookmark = bookmarkService.createBookmark(
                    userId,
                    request.getEbookId(),
                    request.getPageNumber(),
                    request.getNote()
            );
            return Result.success("书签创建成功", bookmark);
        } catch (Exception e) {
            log.error("书签创建失败：{}", e.getMessage());
            return Result.error("书签创建失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新书签", description = "更新书签内容", 
               security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/{id}")
    public Result<Bookmark> updateBookmark(
            @Parameter(description = "书签ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateBookmarkRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Bookmark bookmark = bookmarkService.updateBookmark(id, userId, request.getNote());
            return Result.success("书签更新成功", bookmark);
        } catch (Exception e) {
            log.error("书签更新失败：{}", e.getMessage());
            return Result.error("书签更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取书签详情", description = "获取指定书签的详细信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/{id}")
    public Result<Bookmark> getBookmarkDetail(
            @Parameter(description = "书签ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Bookmark bookmark = bookmarkService.getBookmarkDetail(id, userId);
            if (bookmark == null) {
                return Result.error(404, "书签不存在");
            }
            return Result.success(bookmark);
        } catch (Exception e) {
            log.error("获取书签详情失败：{}", e.getMessage());
            return Result.error("获取书签详情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取用户书签列表", description = "分页获取用户的所有书签", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping
    public Result<Page<Bookmark>> getUserBookmarks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "电子书ID") @RequestParam(required = false) Long ebookId,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Page<Bookmark> bookmarks = bookmarkService.getUserBookmarks(userId, page, size, ebookId);
            return Result.success(bookmarks);
        } catch (Exception e) {
            log.error("获取书签列表失败：{}", e.getMessage());
            return Result.error("获取书签列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取电子书书签", description = "获取指定电子书的所有书签", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/ebook/{ebookId}")
    public Result<List<Bookmark>> getEbookBookmarks(
            @Parameter(description = "电子书ID") @PathVariable @NotNull Long ebookId,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            List<Bookmark> bookmarks = bookmarkService.getEbookBookmarks(ebookId, userId);
            return Result.success(bookmarks);
        } catch (Exception e) {
            log.error("获取电子书书签失败：{}", e.getMessage());
            return Result.error("获取电子书书签失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除书签", description = "删除指定的书签", 
               security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/{id}")
    public Result<Object> deleteBookmark(
            @Parameter(description = "书签ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            bookmarkService.deleteBookmark(id, userId);
            return Result.success("书签删除成功");
        } catch (Exception e) {
            log.error("书签删除失败：{}", e.getMessage());
            return Result.error("书签删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量删除书签", description = "批量删除多个书签", 
               security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/batch")
    public Result<Object> deleteBookmarks(@Valid @RequestBody BatchDeleteBookmarkRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            bookmarkService.deleteBookmarks(request.getBookmarkIds(), userId);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            log.error("批量删除书签失败：{}", e.getMessage());
            return Result.error("批量删除书签失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索书签", description = "根据关键词搜索书签", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/search")
    public Result<Page<Bookmark>> searchBookmarks(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            // 这里可以实现书签搜索功能
            // 暂时返回空结果
            Page<Bookmark> bookmarks = new Page<>(page, size);
            return Result.success(bookmarks);
        } catch (Exception e) {
            log.error("搜索书签失败：{}", e.getMessage());
            return Result.error("搜索书签失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取书签统计", description = "获取用户书签统计信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/statistics")
    public Result<Object> getBookmarkStatistics(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            // 这里可以实现书签统计功能
            // 暂时返回基本统计
            return Result.success("书签统计功能待实现");
        } catch (Exception e) {
            log.error("获取书签统计失败：{}", e.getMessage());
            return Result.error("获取书签统计失败：" + e.getMessage());
        }
    }

}