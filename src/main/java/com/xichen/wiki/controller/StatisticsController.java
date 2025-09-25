package com.xichen.wiki.controller;

import com.xichen.wiki.common.Result;
import com.xichen.wiki.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/statistics")
@Validated
@Tag(name = "统计分析", description = "统计和报表相关接口")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Operation(summary = "获取用户统计概览", description = "获取用户整体统计信息")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getUserStatistics(@RequestHeader("Authorization") String token) {
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        Map<String, Object> statistics = statisticsService.getUserStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取文档统计", description = "获取文档相关统计信息")
    @GetMapping("/documents")
    public Result<Map<String, Object>> getDocumentStatistics(@RequestHeader("Authorization") String token) {
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        Map<String, Object> statistics = statisticsService.getDocumentStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取电子书统计", description = "获取电子书相关统计信息")
    @GetMapping("/ebooks")
    public Result<Map<String, Object>> getEbookStatistics(@RequestHeader("Authorization") String token) {
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        Map<String, Object> statistics = statisticsService.getEbookStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取阅读统计", description = "获取阅读相关统计信息")
    @GetMapping("/reading")
    public Result<Map<String, Object>> getReadingStatistics(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "开始日期") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        Map<String, Object> statistics = statisticsService.getReadingStatistics(userId, startDate, endDate);
        return Result.success(statistics);
    }

    @Operation(summary = "获取分类统计", description = "获取分类相关统计信息")
    @GetMapping("/categories")
    public Result<Map<String, Object>> getCategoryStatistics(@RequestHeader("Authorization") String token) {
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        Map<String, Object> statistics = statisticsService.getCategoryStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取存储统计", description = "获取存储空间相关统计信息")
    @GetMapping("/storage")
    public Result<Map<String, Object>> getStorageStatistics(@RequestHeader("Authorization") String token) {
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        Map<String, Object> statistics = statisticsService.getStorageStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取活动统计", description = "获取用户活动相关统计信息")
    @GetMapping("/activities")
    public Result<Map<String, Object>> getActivityStatistics(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "开始日期") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        Map<String, Object> statistics = statisticsService.getActivityStatistics(userId, startDate, endDate);
        return Result.success(statistics);
    }
}
