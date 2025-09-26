package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.entity.Document;
import com.xichen.wiki.entity.DocumentTag;
import com.xichen.wiki.entity.Ebook;
import com.xichen.wiki.mapper.DocumentTagMapper;
import com.xichen.wiki.service.DocumentService;
import com.xichen.wiki.service.EbookService;
import com.xichen.wiki.service.SearchService;
import com.xichen.wiki.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private EbookService ebookService;
    
    @Autowired
    private DocumentTagMapper documentTagMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis键常量已移至RedisKeyUtil统一管理

    @Override
    public Map<String, Object> globalSearch(String keyword, String type, Long userId, Integer page, Integer size) {
        Map<String, Object> result = new HashMap<>();
        
        if (StringUtils.isBlank(keyword)) {
            result.put("documents", new Page<Document>(page, size));
            result.put("ebooks", new Page<Ebook>(page, size));
            result.put("total", 0);
            return result;
        }
        
        // 记录搜索历史
        recordSearchHistory(userId, keyword, type);
        
        Page<Document> documents = new Page<>(page, size);
        Page<Ebook> ebooks = new Page<>(page, size);
        
        if ("all".equals(type) || "document".equals(type)) {
            documents = searchDocuments(keyword, userId, page, size);
        }
        
        if ("all".equals(type) || "ebook".equals(type)) {
            ebooks = searchEbooks(keyword, userId, page, size);
        }
        
        result.put("documents", documents);
        result.put("ebooks", ebooks);
        result.put("total", documents.getTotal() + ebooks.getTotal());
        result.put("keyword", keyword);
        result.put("type", type);
        
        return result;
    }

    public Page<Document> searchDocuments(String keyword, Long userId, Integer page, Integer size) {
        Page<Document> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Document::getUserId, userId)
               .and(w -> w.like(Document::getTitle, keyword)
                       .or().like(Document::getContent, keyword))
               .orderByDesc(Document::getUpdatedAt);
        
        return documentService.page(pageParam, wrapper);
    }

    public Page<Ebook> searchEbooks(String keyword, Long userId, Integer page, Integer size) {
        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Ebook::getUserId, userId)
               .and(w -> w.like(Ebook::getTitle, keyword)
                       .or().like(Ebook::getAuthor, keyword)
                       .or().like(Ebook::getDescription, keyword))
               .orderByDesc(Ebook::getCreatedAt);
        
        return ebookService.page(pageParam, wrapper);
    }


    public List<String> getPopularSearchTerms(Integer limit) {
        Set<Object> popularTerms = redisTemplate.opsForZSet()
                .reverseRange(RedisKeyUtil.getPopularSearchTermsKey(), 0, limit - 1);

        return popularTerms.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public void recordSearchHistory(Long userId, String keyword, String type) {
        if (StringUtils.isBlank(keyword)) {
            return;
        }
        
        String key = RedisKeyUtil.getSearchHistoryKey(userId);
        
        // 记录搜索历史
        Map<String, Object> historyItem = new HashMap<>();
        historyItem.put("keyword", keyword);
        historyItem.put("type", type);
        historyItem.put("timestamp", LocalDateTime.now());
        
        redisTemplate.opsForList().leftPush(key, historyItem);
        redisTemplate.opsForList().trim(key, 0, 99); // 只保留最近100条
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        
        // 更新热门搜索词
        String popularKey = RedisKeyUtil.getPopularSearchTermsKey();
        redisTemplate.opsForZSet().incrementScore(popularKey, keyword, 1);
        redisTemplate.expire(popularKey, 30, TimeUnit.DAYS);
        
        log.info("搜索历史记录成功：用户ID={}, 关键词={}, 类型={}", userId, keyword, type);
    }

    public List<Map<String, Object>> getUserSearchHistory(Long userId, Integer limit) {
        String key = RedisKeyUtil.getSearchHistoryKey(userId);
        List<Object> history = redisTemplate.opsForList().range(key, 0, limit - 1);
        
        if (history == null) {
            return new ArrayList<>();
        }
        
        return history.stream()
                .map(item -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) item;
                    return map;
                })
                .collect(Collectors.toList());
    }

    public void clearUserSearchHistory(Long userId) {
        String key = RedisKeyUtil.getSearchHistoryKey(userId);
        redisTemplate.delete(key);
        log.info("用户搜索历史已清空：用户ID={}", userId);
    }

    @Override
    public Map<String, Object> advancedSearch(String keyword, String type, Long categoryId, Long[] tagIds, 
                                              String sortBy, String sortOrder, Long userId, Integer page, Integer size) {
        Map<String, Object> result = new HashMap<>();
        
        // 记录搜索历史
        recordSearchHistory(userId, keyword, type);
        
        // 根据类型进行高级搜索
        if ("document".equals(type)) {
            Page<Document> documents = advancedSearchDocuments(keyword, categoryId, tagIds, sortBy, sortOrder, userId, page, size);
            result.put("documents", documents);
            result.put("total", documents.getTotal());
        } else if ("ebook".equals(type)) {
            Page<Ebook> ebooks = advancedSearchEbooks(keyword, categoryId, tagIds, sortBy, sortOrder, userId, page, size);
            result.put("ebooks", ebooks);
            result.put("total", ebooks.getTotal());
        } else {
            // 全局高级搜索
            Page<Document> documents = advancedSearchDocuments(keyword, categoryId, tagIds, sortBy, sortOrder, userId, page, size);
            Page<Ebook> ebooks = advancedSearchEbooks(keyword, categoryId, tagIds, sortBy, sortOrder, userId, page, size);
            
            result.put("documents", documents);
            result.put("ebooks", ebooks);
            result.put("total", documents.getTotal() + ebooks.getTotal());
        }
        
        result.put("keyword", keyword);
        result.put("type", type);
        result.put("categoryId", categoryId);
        result.put("tagIds", tagIds);
        result.put("sortBy", sortBy);
        result.put("sortOrder", sortOrder);
        
        log.info("高级搜索完成：用户ID={}, 关键词={}, 类型={}, 分类ID={}, 标签IDs={}", 
                userId, keyword, type, categoryId, Arrays.toString(tagIds));
        
        return result;
    }
    
    /**
     * 高级文档搜索
     */
    private Page<Document> advancedSearchDocuments(String keyword, Long categoryId, Long[] tagIds, 
                                                  String sortBy, String sortOrder, Long userId, Integer page, Integer size) {
        Page<Document> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        
        // 基础条件：用户ID
        wrapper.eq(Document::getUserId, userId);
        
        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Document::getTitle, keyword)
                    .or().like(Document::getContent, keyword));
        }
        
        // 分类筛选
        if (categoryId != null) {
            wrapper.eq(Document::getCategoryId, categoryId);
        }
        
        // 标签筛选（需要关联查询）
        if (tagIds != null && tagIds.length > 0) {
            // 这里需要根据document_tags表进行关联查询
            // 暂时简化处理，实际应该使用JOIN查询
            wrapper.in(Document::getId, getDocumentIdsByTags(tagIds));
        }
        
        // 排序
        applyDocumentSorting(wrapper, sortBy, sortOrder);
        
        return documentService.page(pageParam, wrapper);
    }
    
    /**
     * 高级电子书搜索
     */
    private Page<Ebook> advancedSearchEbooks(String keyword, Long categoryId, Long[] tagIds, String sortBy, String sortOrder, 
                                             Long userId, Integer page, Integer size) {
        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        
        // 基础条件：用户ID
        wrapper.eq(Ebook::getUserId, userId);
        
        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Ebook::getTitle, keyword)
                    .or().like(Ebook::getAuthor, keyword)
                    .or().like(Ebook::getDescription, keyword));
        }
        
        // 分类筛选
        if (categoryId != null) {
            wrapper.eq(Ebook::getCategoryId, categoryId);
        }
        
        // 标签筛选（电子书暂时不支持标签，但保留接口一致性）
        // 如果将来需要支持电子书标签，可以在这里添加相关逻辑
        
        // 排序
        applyEbookSorting(wrapper, sortBy, sortOrder);
        
        return ebookService.page(pageParam, wrapper);
    }
    
    /**
     * 应用文档排序规则
     */
    private void applyDocumentSorting(LambdaQueryWrapper<Document> wrapper, String sortBy, String sortOrder) {
        if (StringUtils.isBlank(sortBy)) {
            sortBy = "created_at";
        }
        if (StringUtils.isBlank(sortOrder)) {
            sortOrder = "desc";
        }
        
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
        
        switch (sortBy.toLowerCase()) {
            case "title":
                if (isAsc) {
                    wrapper.orderByAsc(Document::getTitle);
                } else {
                    wrapper.orderByDesc(Document::getTitle);
                }
                break;
            case "updated_at":
                if (isAsc) {
                    wrapper.orderByAsc(Document::getUpdatedAt);
                } else {
                    wrapper.orderByDesc(Document::getUpdatedAt);
                }
                break;
            case "created_at":
            default:
                if (isAsc) {
                    wrapper.orderByAsc(Document::getCreatedAt);
                } else {
                    wrapper.orderByDesc(Document::getCreatedAt);
                }
                break;
        }
    }
    
    /**
     * 应用电子书排序规则
     */
    private void applyEbookSorting(LambdaQueryWrapper<Ebook> wrapper, String sortBy, String sortOrder) {
        if (StringUtils.isBlank(sortBy)) {
            sortBy = "created_at";
        }
        if (StringUtils.isBlank(sortOrder)) {
            sortOrder = "desc";
        }
        
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
        
        switch (sortBy.toLowerCase()) {
            case "title":
                if (isAsc) {
                    wrapper.orderByAsc(Ebook::getTitle);
                } else {
                    wrapper.orderByDesc(Ebook::getTitle);
                }
                break;
            case "updated_at":
                if (isAsc) {
                    wrapper.orderByAsc(Ebook::getUpdatedAt);
                } else {
                    wrapper.orderByDesc(Ebook::getUpdatedAt);
                }
                break;
            case "created_at":
            default:
                if (isAsc) {
                    wrapper.orderByAsc(Ebook::getCreatedAt);
                } else {
                    wrapper.orderByDesc(Ebook::getCreatedAt);
                }
                break;
        }
    }
    
    /**
     * 根据标签获取文档ID列表
     */
    private List<Long> getDocumentIdsByTags(Long[] tagIds) {
        if (tagIds == null || tagIds.length == 0) {
            return new ArrayList<>();
        }
        
        // 查询document_tags表获取关联的文档ID
        LambdaQueryWrapper<DocumentTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DocumentTag::getTagId, Arrays.asList(tagIds));
        wrapper.select(DocumentTag::getDocumentId);
        
        List<DocumentTag> documentTags = documentTagMapper.selectList(wrapper);
        return documentTags.stream()
                .map(DocumentTag::getDocumentId)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public String[] getSearchSuggestions(String keyword, Long userId) {
        if (StringUtils.isBlank(keyword)) {
            return new String[0];
        }
        
        Set<String> suggestions = new LinkedHashSet<>();
        
        // 1. 获取用户历史搜索建议（优先级最高）
        List<String> userHistorySuggestions = getUserHistorySuggestions(keyword, userId);
        suggestions.addAll(userHistorySuggestions);
        
        // 2. 获取热门搜索建议
        List<String> popularSuggestions = getPopularSearchSuggestions(keyword);
        suggestions.addAll(popularSuggestions);
        
        // 3. 从数据库获取内容匹配建议
        List<String> contentSuggestions = getContentBasedSuggestions(keyword, userId);
        suggestions.addAll(contentSuggestions);
        
        // 限制建议数量，避免过多
        List<String> result = suggestions.stream()
                .filter(StringUtils::isNotBlank)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
        
        return result.toArray(new String[0]);
    }
    
    /**
     * 获取用户历史搜索建议
     */
    private List<String> getUserHistorySuggestions(String keyword, Long userId) {
        try {
            String historyKey = RedisKeyUtil.getSearchHistoryKey(userId);
            Set<Object> historyObj = redisTemplate.opsForZSet().reverseRange(historyKey, 0, 9);
            
            if (historyObj != null) {
                return historyObj.stream()
                        .map(Object::toString)
                        .filter(historyKeyword -> historyKeyword.toLowerCase().contains(keyword.toLowerCase()))
                        .limit(3)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("获取用户历史搜索建议失败: {}", e.getMessage());
        }
        return new ArrayList<>();
    }
    
    /**
     * 获取热门搜索建议
     */
    private List<String> getPopularSearchSuggestions(String keyword) {
        try {
            String popularKey = RedisKeyUtil.getPopularSearchTermsKey();
            Set<Object> popularObj = redisTemplate.opsForZSet().reverseRange(popularKey, 0, 19);
            
            if (popularObj != null) {
                return popularObj.stream()
                        .map(Object::toString)
                        .filter(popularKeyword -> popularKeyword.toLowerCase().contains(keyword.toLowerCase()))
                        .limit(3)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("获取热门搜索建议失败: {}", e.getMessage());
        }
        return new ArrayList<>();
    }
    
    /**
     * 获取基于内容的搜索建议
     */
    private List<String> getContentBasedSuggestions(String keyword, Long userId) {
        Set<String> suggestions = new HashSet<>();
        
        try {
            // 从用户文档标题中获取建议
            LambdaQueryWrapper<Document> docWrapper = new LambdaQueryWrapper<>();
            docWrapper.eq(Document::getUserId, userId)
                    .like(Document::getTitle, keyword)
                    .orderByDesc(Document::getCreatedAt)
                    .last("LIMIT 3");
            
            List<Document> documents = documentService.list(docWrapper);
            for (Document doc : documents) {
                suggestions.add(doc.getTitle());
            }
            
            // 从用户电子书标题中获取建议
            LambdaQueryWrapper<Ebook> ebookWrapper = new LambdaQueryWrapper<>();
            ebookWrapper.eq(Ebook::getUserId, userId)
                    .like(Ebook::getTitle, keyword)
                    .orderByDesc(Ebook::getCreatedAt)
                    .last("LIMIT 3");
            
            List<Ebook> ebooks = ebookService.list(ebookWrapper);
            for (Ebook ebook : ebooks) {
                suggestions.add(ebook.getTitle());
            }
            
        } catch (Exception e) {
            log.warn("获取内容搜索建议失败: {}", e.getMessage());
        }
        
        return new ArrayList<>(suggestions);
    }
}