package com.xichen.wiki.service;

import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics(Long userId);
    
    /**
     * 获取文档统计信息
     */
    Map<String, Object> getDocumentStatistics(Long userId);
    
    /**
     * 获取分类统计信息
     */
    Map<String, Object> getCategoryStatistics(Long userId);
    
    /**
     * 获取标签统计信息
     */
    Map<String, Object> getTagStatistics(Long userId);
    
    /**
     * 获取电子书统计信息
     */
    Map<String, Object> getEbookStatistics(Long userId);
    
    /**
     * 获取阅读统计信息
     */
    Map<String, Object> getReadingStatistics(Long userId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    
    /**
     * 获取存储统计信息
     */
    Map<String, Object> getStorageStatistics(Long userId);
    
    /**
     * 获取活动统计信息
     */
    Map<String, Object> getActivityStatistics(Long userId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
}
