package com.xichen.wiki.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.BatchDeleteRequest;
import com.xichen.wiki.dto.CreateTagRequest;
import com.xichen.wiki.dto.UpdateTagRequest;
import com.xichen.wiki.entity.Tag;
import com.xichen.wiki.service.TagService;
import com.xichen.wiki.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 标签控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@Validated
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "标签创建、管理、搜索相关接口")
public class TagController {

    @Autowired
    private TagService tagService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "创建标签", description = "创建新的标签", 
               security = @SecurityRequirement(name = "Authorization"))
    @PostMapping
    public Result<Tag> createTag(@Valid @RequestBody CreateTagRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Tag tag = tagService.createTag(userId, request.getName(), request.getDescription());
            return Result.success("标签创建成功", tag);
        } catch (Exception e) {
            log.error("标签创建失败：{}", e.getMessage());
            return Result.error("标签创建失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新标签", description = "更新标签信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/{id}")
    public Result<Tag> updateTag(
            @Parameter(description = "标签ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateTagRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Tag tag = tagService.updateTag(id, userId, request.getName(), request.getDescription());
            return Result.success("标签更新成功", tag);
        } catch (Exception e) {
            log.error("标签更新失败：{}", e.getMessage());
            return Result.error("标签更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取标签详情", description = "获取指定标签的详细信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getTagDetail(
            @Parameter(description = "标签ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Map<String, Object> tagDetail = tagService.getTagDetail(id, userId);
            if (tagDetail == null) {
                return Result.error(404, "标签不存在");
            }
            return Result.success(tagDetail);
        } catch (Exception e) {
            log.error("获取标签详情失败：{}", e.getMessage());
            return Result.error("获取标签详情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取用户标签列表", description = "分页获取用户的所有标签", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping
    public Result<Page<Tag>> getUserTags(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Page<Tag> tags = tagService.getUserTags(userId, page, size, keyword);
            return Result.success(tags);
        } catch (Exception e) {
            log.error("获取标签列表失败：{}", e.getMessage());
            return Result.error("获取标签列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取公共标签列表", description = "获取所有用户都可以使用的公共标签")
    @GetMapping("/public")
    public Result<Page<Tag>> getPublicTags(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        
        try {
            Page<Tag> tags = tagService.getPublicTags(page, size, keyword);
            return Result.success(tags);
        } catch (Exception e) {
            log.error("获取公共标签列表失败：{}", e.getMessage());
            return Result.error("获取公共标签列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取热门标签", description = "获取使用频率最高的标签", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/popular")
    public Result<List<Tag>> getPopularTags(
            @Parameter(description = "数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        
        try {
            List<Tag> tags = tagService.getPopularTags(limit);
            return Result.success(tags);
        } catch (Exception e) {
            log.error("获取热门标签失败：{}", e.getMessage());
            return Result.error("获取热门标签失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索标签", description = "根据关键词搜索标签", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/search")
    public Result<List<Tag>> searchTags(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            List<Tag> tags = tagService.searchTags(keyword, userId, limit);
            return Result.success(tags);
        } catch (Exception e) {
            log.error("搜索标签失败：{}", e.getMessage());
            return Result.error("搜索标签失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除标签", description = "删除指定的标签", 
               security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/{id}")
    public Result<Object> deleteTag(
            @Parameter(description = "标签ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            tagService.deleteTag(id, userId);
            return Result.success("标签删除成功");
        } catch (Exception e) {
            log.error("标签删除失败：{}", e.getMessage());
            return Result.error("标签删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量删除标签", description = "批量删除多个标签", 
               security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/batch")
    public Result<Object> deleteTags(@Valid @RequestBody BatchDeleteRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            tagService.deleteTags(request.getTagIds(), userId);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            log.error("批量删除标签失败：{}", e.getMessage());
            return Result.error("批量删除标签失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取标签使用统计", description = "获取标签的使用次数统计", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/{id}/usage")
    public Result<Map<String, Object>> getTagUsage(
            @Parameter(description = "标签ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Integer usageCount = tagService.getTagUsageCount(id);
            Map<String, Object> result = Map.of(
                    "tagId", id,
                    "usageCount", usageCount
            );
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取标签使用统计失败：{}", e.getMessage());
            return Result.error("获取标签使用统计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取标签统计", description = "获取用户标签统计信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getTagStatistics(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            // 这里可以实现标签统计功能
            // 暂时返回基本统计
            Map<String, Object> statistics = Map.of(
                    "totalTags", 0,
                    "publicTags", 0,
                    "privateTags", 0
            );
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取标签统计失败：{}", e.getMessage());
            return Result.error("获取标签统计失败：" + e.getMessage());
        }
    }

}