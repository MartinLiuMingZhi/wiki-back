package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.entity.Document;
import com.xichen.wiki.entity.Ebook;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 */
public interface SearchService {
    
    /**
     * 全局搜索
     */
    Map<String, Object> globalSearch(String keyword, String type, Long userId, Integer page, Integer size);
    
    /**
     * 搜索文档
     */
    Page<Document> searchDocuments(String keyword, Long userId, Integer page, Integer size);
    
    /**
     * 搜索电子书
     */
    Page<Ebook> searchEbooks(String keyword, Long userId, Integer page, Integer size);
    
    /**
     * 获取搜索建议
     */
    List<String> getSearchSuggestions(String keyword, Integer limit);
    
    /**
     * 获取热门搜索词
     */
    List<String> getPopularSearchTerms(Integer limit);
    
    /**
     * 记录搜索历史
     */
    void recordSearchHistory(Long userId, String keyword, String type);
    
    /**
     * 获取用户搜索历史
     */
    List<Map<String, Object>> getUserSearchHistory(Long userId, Integer limit);
    
    /**
     * 清除用户搜索历史
     */
    void clearUserSearchHistory(Long userId);
    
    /**
     * 高级搜索
     */
    Map<String, Object> advancedSearch(Object request, Long userId);
}
