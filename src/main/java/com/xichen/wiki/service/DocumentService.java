package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.Document;

import java.util.Map;

/**
 * 文档服务接口
 */
public interface DocumentService extends IService<Document> {
    
    /**
     * 创建文档
     */
    Document createDocument(Long userId, String title, String content, Long categoryId, Long[] tagIds);
    
    /**
     * 更新文档
     */
    Document updateDocument(Long documentId, Long userId, String title, String content, Long categoryId, Long[] tagIds);
    
    /**
     * 删除文档
     */
    boolean deleteDocument(Long documentId, Long userId);
    
    /**
     * 获取文档详情
     */
    Document getDocumentById(Long documentId, Long userId);
    
    /**
     * 获取用户文档列表
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<Document> getUserDocuments(Long userId, Integer page, Integer size, String keyword);
    
    /**
     * 搜索文档
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<Document> searchDocuments(String keyword, Long userId, Integer page, Integer size);
    
    /**
     * 获取文档统计信息
     */
    Map<String, Object> getDocumentStatistics(Long documentId, Long userId);
    
    /**
     * 获取文档详情（简化版本）
     */
    Document getDocumentDetail(Long documentId, Long userId);
    
    /**
     * 切换文档收藏状态
     */
    boolean toggleFavorite(Long documentId, Long userId);
}
