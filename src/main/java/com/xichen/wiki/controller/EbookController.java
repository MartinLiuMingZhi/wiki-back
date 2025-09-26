package com.xichen.wiki.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.ReadingProgressRequest;
import com.xichen.wiki.dto.UpdateEbookRequest;
import com.xichen.wiki.entity.Ebook;
import com.xichen.wiki.service.EbookService;
import com.xichen.wiki.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * 电子书控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ebooks")
@Validated
@Tag(name = "电子书管理", description = "电子书上传、管理、阅读相关接口")
public class EbookController {

    @Autowired
    private EbookService ebookService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "上传电子书", description = "上传PDF电子书文件")
    @PostMapping("/upload")
    public Result<Ebook> uploadEbook(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "电子书文件") @RequestParam("file") @NotNull MultipartFile file,
            @Parameter(description = "电子书标题") @RequestParam @NotBlank String title,
            @Parameter(description = "作者") @RequestParam(required = false) String author,
            @Parameter(description = "描述") @RequestParam(required = false) String description,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Ebook ebook = ebookService.uploadEbook(userId, file, title, description, author, "未分类", categoryId);
            return Result.success("电子书上传成功", ebook);
        } catch (Exception e) {
            log.error("电子书上传失败：{}", e.getMessage());
            return Result.error("电子书上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取电子书列表", description = "分页获取用户的电子书列表")
    @GetMapping
    public Result<Page<Ebook>> getEbooks(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Page<Ebook> ebooks = ebookService.getUserEbooks(userId, page, size, keyword);
        return Result.success(ebooks);
    }

    @Operation(summary = "获取电子书详情", description = "获取指定电子书的详细信息")
    @GetMapping("/{id}")
    public Result<Ebook> getEbookDetail(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "电子书ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Ebook ebook = ebookService.getEbookDetail(id, userId);
        if (ebook == null) {
            return Result.error(404, "电子书不存在");
        }

        return Result.success(ebook);
    }

    @Operation(summary = "更新电子书信息", description = "更新电子书的基本信息")
    @PutMapping("/{id}")
    public Result<Ebook> updateEbook(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "电子书ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateEbookRequest request) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Ebook ebook = ebookService.updateEbook(id, userId, request.getTitle(), 
                    request.getAuthor(), request.getDescription(), request.getCategoryId());
            return Result.success("更新成功", ebook);
        } catch (Exception e) {
            log.error("电子书更新失败：{}", e.getMessage());
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除电子书", description = "删除指定的电子书")
    @DeleteMapping("/{id}")
    public Result<Object> deleteEbook(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "电子书ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            ebookService.deleteEbook(id, userId);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("电子书删除失败：{}", e.getMessage());
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新阅读进度", description = "更新电子书的阅读进度")
    @PutMapping("/{id}/progress")
    public Result<Object> updateReadingProgress(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "电子书ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody ReadingProgressRequest request) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            ebookService.updateReadingProgress(id, userId, request.getProgress(), request.getPageNumber());
            return Result.success("阅读进度更新成功");
        } catch (Exception e) {
            log.error("阅读进度更新失败：{}", e.getMessage());
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取阅读进度", description = "获取电子书的阅读进度")
    @GetMapping("/{id}/progress")
    public Result<Map<String, Object>> getReadingProgress(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "电子书ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Map<String, Object> progress = ebookService.getReadingProgress(id, userId);
        return Result.success(progress);
    }

    @Operation(summary = "收藏电子书", description = "收藏或取消收藏电子书")
    @PostMapping("/{id}/favorite")
    public Result<Object> favoriteEbook(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "电子书ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            ebookService.favoriteEbook(id, userId);
            return Result.success("收藏成功");
        } catch (Exception e) {
            log.error("收藏失败：{}", e.getMessage());
            return Result.error("收藏失败：" + e.getMessage());
        }
    }

    @Operation(summary = "取消收藏电子书", description = "取消收藏电子书")
    @DeleteMapping("/{id}/favorite")
    public Result<Object> unfavoriteEbook(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "电子书ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            ebookService.unfavoriteEbook(id, userId);
            return Result.success("取消收藏成功");
        } catch (Exception e) {
            log.error("取消收藏失败：{}", e.getMessage());
            return Result.error("取消收藏失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取收藏的电子书", description = "获取用户收藏的电子书列表")
    @GetMapping("/favorites")
    public Result<Page<Ebook>> getFavoriteEbooks(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Page<Ebook> ebooks = ebookService.getFavoriteEbooks(userId, page, size);
        return Result.success(ebooks);
    }

    @Operation(summary = "搜索电子书", description = "搜索电子书")
    @GetMapping("/search")
    public Result<Page<Ebook>> searchEbooks(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Page<Ebook> ebooks = ebookService.searchEbooks(keyword, userId, page, size);
        return Result.success(ebooks);
    }

}