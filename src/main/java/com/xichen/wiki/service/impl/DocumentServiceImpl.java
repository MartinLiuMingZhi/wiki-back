package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xichen.wiki.entity.Document;
import com.xichen.wiki.exception.BusinessException;
import com.xichen.wiki.mapper.DocumentMapper;
import com.xichen.wiki.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 文档服务实现类
 */
@Slf4j
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Override
    public Document createDocument(Long userId, String title, String content, Long categoryId, Long[] tagIds) {
        Document document = new Document();
        document.setTitle(title);
        document.setContent(content);
        document.setUserId(userId);
        document.setCategoryId(categoryId);
        document.setIsFavorite(false);
        document.setVersion(1);
        document.setWordCount(content != null ? content.length() : 0);
        
        save(document);
        log.info("文档创建成功：{}", title);
        return document;
    }

    @Override
    public Document updateDocument(Long documentId, Long userId, String title, String content, Long categoryId, Long[] tagIds) {
        Document document = getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }
        
        if (!document.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作此文档");
        }
        
        document.setTitle(title);
        document.setContent(content);
        document.setCategoryId(categoryId);
        document.setVersion(document.getVersion() + 1);
        document.setWordCount(content != null ? content.length() : 0);
        
        updateById(document);
        log.info("文档更新成功：{}", title);
        return document;
    }

    @Override
    public Page<Document> getUserDocuments(Long userId, Integer page, Integer size, String keyword) {
        Page<Document> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Document::getUserId, userId);
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Document::getTitle, keyword)
                    .or()
                    .like(Document::getContent, keyword));
        }
        
        wrapper.orderByDesc(Document::getUpdatedAt);
        
        return page(pageParam, wrapper);
    }

    @Override
    public Document getDocumentById(Long documentId, Long userId) {
        Document document = getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }
        
        if (!document.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此文档");
        }
        
        return document;
    }

    @Override
    public Page<Document> searchDocuments(String keyword, Long userId, Integer page, Integer size) {
        Page<Document> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Document::getUserId, userId);
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Document::getTitle, keyword)
                    .or()
                    .like(Document::getContent, keyword));
        }
        
        wrapper.orderByDesc(Document::getUpdatedAt);
        
        return page(pageParam, wrapper);
    }

    @Override
    public Document getDocumentDetail(Long documentId, Long userId) {
        Document document = getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }
        
        if (!document.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此文档");
        }
        
        return document;
    }

    @Override
    public boolean deleteDocument(Long documentId, Long userId) {
        Document document = getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }
        
        if (!document.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此文档");
        }
        
        removeById(documentId);
        log.info("文档删除成功：{}", document.getTitle());
        return true;
    }

    @Override
    public boolean toggleFavorite(Long documentId, Long userId) {
        Document document = getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }
        
        if (!document.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作此文档");
        }
        
        document.setIsFavorite(!document.getIsFavorite());
        updateById(document);
        log.info("文档收藏状态切换成功：{}", document.getTitle());
        return document.getIsFavorite();
    }
    
    @Override
    public Map<String, Object> getDocumentStatistics(Long documentId, Long userId) {
        Document document = getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }
        
        if (!document.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此文档");
        }
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("documentId", document.getId());
        statistics.put("title", document.getTitle());
        statistics.put("isFavorite", document.getIsFavorite());
        statistics.put("createdAt", document.getCreatedAt());
        statistics.put("updatedAt", document.getUpdatedAt());
        
        return statistics;
    }
    
}

