package com.xichen.wiki.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.AdvancedSearchRequest;
import com.xichen.wiki.entity.Document;
import com.xichen.wiki.entity.Ebook;
import com.xichen.wiki.service.SearchService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 * 
 * 提供完整的搜索功能，包括：
 * - 全局搜索：支持文档和电子书的统一搜索
 * - 分类搜索：分别搜索文档或电子书
 * - 搜索建议：根据用户输入提供智能搜索建议
 * - 搜索历史：记录和管理用户的搜索历史
 * - 热门搜索：获取系统热门搜索词
 * - 高级搜索：支持多条件组合的复杂搜索
 * 
 * 安全机制：
 * - 所有接口都需要JWT token认证
 * - 自动记录用户搜索行为用于个性化推荐
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@Validated
@Tag(name = "搜索服务", description = "全局搜索、搜索建议、搜索历史相关接口")
public class SearchController {

    @Autowired
    private SearchService searchService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 全局搜索
     * 
     * 业务逻辑：
     * 1. 从JWT token中解析用户ID
     * 2. 调用搜索服务执行全局搜索（文档+电子书）
     * 3. 自动记录用户搜索历史用于个性化推荐
     * 4. 返回搜索结果和分页信息
     * 
     * 搜索范围：
     * - type="all": 搜索文档和电子书
     * - type="document": 仅搜索文档
     * - type="ebook": 仅搜索电子书
     * 
     * @param token JWT认证token
     * @param keyword 搜索关键词
     * @param type 搜索类型（all/document/ebook）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 搜索结果，包含文档列表、电子书列表和分页信息
     */
    @Operation(summary = "全局搜索", description = "搜索文档和电子书", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping
    public Result<Map<String, Object>> globalSearch(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "搜索类型") @RequestParam(defaultValue = "all") String type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            HttpServletRequest httpRequest) {
        
        // 从请求头中获取JWT token
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);

        try {
            // 执行全局搜索
            Map<String, Object> result = searchService.globalSearch(keyword, type, userId, page, size);
            
            // 记录搜索历史，用于个性化推荐和搜索分析
            searchService.recordSearchHistory(userId, keyword, type);
            
            log.info("全局搜索成功：用户ID={}, 关键词={}, 类型={}, 页码={}", userId, keyword, type, page);
            return Result.success(result);
        } catch (Exception e) {
            log.error("全局搜索失败：用户ID={}, 关键词={}, 错误={}", userId, keyword, e.getMessage(), e);
            return Result.error("搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索文档", description = "只搜索文档", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/documents")
    public Result<Page<Document>> searchDocuments(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);

        try {
            Page<Document> documents = searchService.searchDocuments(keyword, userId, page, size);
            return Result.success(documents);
        } catch (Exception e) {
            log.error("文档搜索失败：{}", e.getMessage());
            return Result.error("文档搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索电子书", description = "只搜索电子书", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/ebooks")
    public Result<Page<Ebook>> searchEbooks(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);

        try {
            Page<Ebook> ebooks = searchService.searchEbooks(keyword, userId, page, size);
            return Result.success(ebooks);
        } catch (Exception e) {
            log.error("电子书搜索失败：{}", e.getMessage());
            return Result.error("电子书搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取搜索建议", description = "根据输入获取搜索建议", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/suggestions")
    public Result<List<String>> getSearchSuggestions(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "建议数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);

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

    @Operation(summary = "获取搜索历史", description = "获取用户的搜索历史", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getSearchHistory(
            HttpServletRequest httpRequest,
            @Parameter(description = "数量") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);

        try {
            List<Map<String, Object>> history = searchService.getUserSearchHistory(userId, limit);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取搜索历史失败：{}", e.getMessage());
            return Result.error("获取搜索历史失败：" + e.getMessage());
        }
    }

    @Operation(summary = "清空搜索历史", description = "清空用户的搜索历史", 
               security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/history")
    public Result<Object> clearSearchHistory(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);

        try {
            searchService.clearUserSearchHistory(userId);
            return Result.success("搜索历史已清空");
        } catch (Exception e) {
            log.error("清空搜索历史失败：{}", e.getMessage());
            return Result.error("清空搜索历史失败：" + e.getMessage());
        }
    }

    @Operation(summary = "高级搜索", description = "支持多条件的高级搜索", 
               security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/advanced")
    public Result<Map<String, Object>> advancedSearch(
            @Valid @RequestBody AdvancedSearchRequest request,
            HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);

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

}