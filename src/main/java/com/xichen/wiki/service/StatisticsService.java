package com.xichen.wiki.service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取用户统计概览
     */
    Map<String, Object> getUserStatistics(Long userId);
    
    /**
     * 获取文档统计
     */
    Map<String, Object> getDocumentStatistics(Long userId);
    
    /**
     * 获取电子书统计
     */
    Map<String, Object> getEbookStatistics(Long userId);
    
    /**
     * 获取阅读统计
     */
    Map<String, Object> getReadingStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取分类统计
     */
    Map<String, Object> getCategoryStatistics(Long userId);
    
    /**
     * 获取存储空间统计
     */
    Map<String, Object> getStorageStatistics(Long userId);
    
    /**
     * 获取活动统计
     */
    Map<String, Object> getActivityStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
