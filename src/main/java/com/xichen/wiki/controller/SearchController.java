package com.xichen.wiki.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.common.Result;
import com.xichen.wiki.entity.Document;
import com.xichen.wiki.entity.Ebook;
import com.xichen.wiki.service.SearchService;
import com.xichen.wiki.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@Validated
@Tag(name = "搜索服务", description = "全局搜索、搜索建议、搜索历史相关接口")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Operation(summary = "全局搜索", description = "搜索文档和电子书")
    @GetMapping
    public Result<Map<String, Object>> globalSearch(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "搜索类型") @RequestParam(defaultValue = "all") String type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Map<String, Object> result = searchService.globalSearch(keyword, type, userId, page, size);
            
            // 记录搜索历史
            searchService.recordSearchHistory(userId, keyword, type);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索失败：{}", e.getMessage());
            return Result.error("搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索文档", description = "只搜索文档")
    @GetMapping("/documents")
    public Result<Page<Document>> searchDocuments(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Page<Document> documents = searchService.searchDocuments(keyword, userId, page, size);
            return Result.success(documents);
        } catch (Exception e) {
            log.error("文档搜索失败：{}", e.getMessage());
            return Result.error("文档搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索电子书", description = "只搜索电子书")
    @GetMapping("/ebooks")
    public Result<Page<Ebook>> searchEbooks(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Page<Ebook> ebooks = searchService.searchEbooks(keyword, userId, page, size);
            return Result.success(ebooks);
        } catch (Exception e) {
            log.error("电子书搜索失败：{}", e.getMessage());
            return Result.error("电子书搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取搜索建议", description = "根据输入获取搜索建议")
    @GetMapping("/suggestions")
    public Result<List<String>> getSearchSuggestions(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "建议数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            String[] suggestionsArray = searchService.getSearchSuggestions(keyword, userId);
            List<String> suggestions = suggestionsArray != null ? Arrays.asList(suggestionsArray) : Collections.emptyList();
            return Result.success(suggestions);
        } catch (Exception e) {
            log.error("获取搜索建议失败：{}", e.getMessage());
            return Result.error("获取搜索建议失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取热门搜索词", description = "获取热门搜索关键词")
    @GetMapping("/popular")
    public Result<List<String>> getPopularSearchTerms(
            @Parameter(description = "数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        
        try {
            List<String> popularTerms = searchService.getPopularSearchTerms(limit);
            return Result.success(popularTerms);
        } catch (Exception e) {
            log.error("获取热门搜索词失败：{}", e.getMessage());
            return Result.error("获取热门搜索词失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取搜索历史", description = "获取用户的搜索历史")
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getSearchHistory(
            @Parameter(description = "数量") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            List<Map<String, Object>> history = searchService.getUserSearchHistory(userId, limit);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取搜索历史失败：{}", e.getMessage());
            return Result.error("获取搜索历史失败：" + e.getMessage());
        }
    }

    @Operation(summary = "清空搜索历史", description = "清空用户的搜索历史")
    @DeleteMapping("/history")
    public Result<Object> clearSearchHistory() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            searchService.clearUserSearchHistory(userId);
            return Result.success("搜索历史已清空");
        } catch (Exception e) {
            log.error("清空搜索历史失败：{}", e.getMessage());
            return Result.error("清空搜索历史失败：" + e.getMessage());
        }
    }

    @Operation(summary = "高级搜索", description = "支持多条件的高级搜索")
    @PostMapping("/advanced")
    public Result<Map<String, Object>> advancedSearch(@Valid @RequestBody AdvancedSearchRequest request) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            Map<String, Object> result = searchService.advancedSearch(
                request.getKeyword(),
                request.getType(),
                request.getCategoryId(),
                null, // tagIds - 暂时设为null，因为AdvancedSearchRequest中没有tagIds字段
                request.getSortBy(),
                request.getSortOrder(),
                userId,
                1, // page - 默认第1页
                10 // size - 默认每页10条
            );
            return Result.success(result);
        } catch (Exception e) {
            log.error("高级搜索失败：{}", e.getMessage());
            return Result.error("高级搜索失败：" + e.getMessage());
        }
    }

    // ==================== 请求DTO类 ====================

    /**
     * 高级搜索请求
     */
    public static class AdvancedSearchRequest {
        @NotBlank(message = "搜索关键词不能为空")
        private String keyword;
        
        private String type; // document, ebook, all
        private Long categoryId;
        private String startDate;
        private String endDate;
        private String sortBy; // title, created_at, updated_at
        private String sortOrder; // asc, desc
        
        // Getters and Setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
    }
}