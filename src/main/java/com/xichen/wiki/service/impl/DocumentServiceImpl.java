package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xichen.wiki.entity.Document;
import com.xichen.wiki.entity.DocumentTag;
import com.xichen.wiki.exception.BusinessException;
import com.xichen.wiki.mapper.DocumentMapper;
import com.xichen.wiki.mapper.DocumentTagMapper;
import com.xichen.wiki.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档服务实现类
 * 
 * 提供文档的CRUD操作，包括：
 * - 文档创建、更新、删除
 * - 文档标签关联管理
 * - 文档搜索和分页查询
 * - 文档收藏状态管理
 * - 文档统计信息获取
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Slf4j
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Autowired
    private DocumentTagMapper documentTagMapper;

    /**
     * 创建文档
     * 
     * 业务逻辑：
     * 1. 创建文档基础信息（标题、内容、用户ID、分类ID等）
     * 2. 设置文档初始状态（版本号1，非收藏状态）
     * 3. 计算文档字数统计
     * 4. 保存文档到数据库
     * 5. 如果提供了标签ID数组，则建立文档-标签关联关系
     * 
     * @param userId 用户ID
     * @param title 文档标题
     * @param content 文档内容
     * @param categoryId 分类ID
     * @param tagIds 标签ID数组
     * @return 创建的文档对象
     */
    @Override
    @Transactional
    public Document createDocument(Long userId, String title, String content, Long categoryId, Long[] tagIds) {
        // 创建文档对象并设置基础属性
        Document document = new Document();
        document.setTitle(title);
        document.setContent(content);
        document.setUserId(userId);
        document.setCategoryId(categoryId);
        document.setIsFavorite(false);  // 初始状态为非收藏
        document.setVersion(1);          // 初始版本号
        document.setWordCount(content != null ? content.length() : 0);  // 计算字数
        
        // 保存文档到数据库
        save(document);
        
        // 处理标签关联：如果提供了标签ID，则建立文档-标签的多对多关系
        if (tagIds != null && tagIds.length > 0) {
            saveDocumentTags(document.getId(), tagIds);
        }
        
        log.info("文档创建成功：用户ID={}, 标题={}, 字数={}", userId, title, document.getWordCount());
        return document;
    }

    /**
     * 更新文档
     * 
     * 业务逻辑：
     * 1. 验证文档是否存在
     * 2. 验证用户是否有权限操作该文档
     * 3. 更新文档内容（标题、内容、分类等）
     * 4. 版本号自增，重新计算字数
     * 5. 更新标签关联关系（先删除旧关联，再建立新关联）
     * 
     * @param documentId 文档ID
     * @param userId 用户ID
     * @param title 新标题
     * @param content 新内容
     * @param categoryId 新分类ID
     * @param tagIds 新标签ID数组
     * @return 更新后的文档对象
     * @throws BusinessException 当文档不存在或无权限时抛出
     */
    @Override
    @Transactional
    public Document updateDocument(Long documentId, Long userId, String title, String content, Long categoryId, Long[] tagIds) {
        // 1. 验证文档是否存在
        Document document = getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }
        
        // 2. 验证用户权限：只有文档创建者才能修改
        if (!document.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作此文档");
        }
        
        // 3. 更新文档内容
        document.setTitle(title);
        document.setContent(content);
        document.setCategoryId(categoryId);
        document.setVersion(document.getVersion() + 1);  // 版本号自增
        document.setWordCount(content != null ? content.length() : 0);  // 重新计算字数
        
        // 4. 保存文档更新
        updateById(document);
        
        // 5. 更新标签关联关系
        // 先删除所有旧的标签关联
        documentTagMapper.deleteByDocumentId(documentId);
        // 再建立新的标签关联
        if (tagIds != null && tagIds.length > 0) {
            saveDocumentTags(documentId, tagIds);
        }
        
        log.info("文档更新成功：用户ID={}, 文档ID={}, 标题={}, 版本={}", userId, documentId, title, document.getVersion());
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
    @Transactional
    public boolean deleteDocument(Long documentId, Long userId) {
        Document document = getById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }
        
        if (!document.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此文档");
        }
        
        // 删除文档标签关联
        documentTagMapper.deleteByDocumentId(documentId);
        
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
    
    /**
     * 保存文档标签关联
     * 
     * 业务逻辑：
     * 1. 遍历标签ID数组，为每个标签创建文档-标签关联对象
     * 2. 设置关联对象的基础属性（文档ID、标签ID、创建时间）
     * 3. 批量插入到数据库，建立多对多关系
     * 
     * 注意：此方法不进行重复性检查，调用方需要确保标签ID的有效性
     * 
     * @param documentId 文档ID
     * @param tagIds 标签ID数组
     */
    private void saveDocumentTags(Long documentId, Long[] tagIds) {
        // 创建文档-标签关联对象列表
        List<DocumentTag> documentTags = new ArrayList<>();
        
        // 遍历标签ID数组，为每个标签创建关联对象
        for (Long tagId : tagIds) {
            DocumentTag documentTag = new DocumentTag();
            documentTag.setDocumentId(documentId);
            documentTag.setTagId(tagId);
            documentTag.setCreatedAt(LocalDateTime.now());
            documentTags.add(documentTag);
        }
        
        // 批量插入到数据库，建立文档-标签的多对多关系
        documentTagMapper.batchInsert(documentTags);
        
        log.debug("文档标签关联保存成功：文档ID={}, 标签数量={}", documentId, tagIds.length);
    }
    
}

