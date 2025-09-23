package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.Document;

/**
 * 文档服务接口
 */
public interface DocumentService extends IService<Document> {
    
    /**
     * 创建文档
     */
    Document createDocument(Long userId, String title, String content, Long categoryId);
    
    /**
     * 更新文档
     */
    Document updateDocument(Long documentId, Long userId, String title, String content, Long categoryId);
    
    /**
     * 获取用户文档列表
     */
    Page<Document> getUserDocuments(Long userId, Integer page, Integer size, Long categoryId, String keyword);
    
    /**
     * 获取文档详情
     */
    Document getDocumentDetail(Long documentId, Long userId);
    
    /**
     * 删除文档
     */
    void deleteDocument(Long documentId, Long userId);
    
    /**
     * 切换收藏状态
     */
    void toggleFavorite(Long documentId, Long userId);
}

