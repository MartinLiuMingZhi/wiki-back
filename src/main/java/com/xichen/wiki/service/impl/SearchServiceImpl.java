package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.entity.Document;
import com.xichen.wiki.entity.Ebook;
import com.xichen.wiki.service.DocumentService;
import com.xichen.wiki.service.EbookService;
import com.xichen.wiki.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final DocumentService documentService;
    private final EbookService ebookService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SEARCH_HISTORY_KEY = "search:history:user:";
    private static final String POPULAR_SEARCH_KEY = "search:popular";
    private static final String SEARCH_SUGGESTION_KEY = "search:suggestion:";

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

    @Override
    public Page<Document> searchDocuments(String keyword, Long userId, Integer page, Integer size) {
        Page<Document> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Document::getUserId, userId)
               .and(w -> w.like(Document::getTitle, keyword)
                       .or().like(Document::getContent, keyword))
               .orderByDesc(Document::getUpdatedAt);
        
        return documentService.page(pageParam, wrapper);
    }

    @Override
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

    @Override
    public List<String> getSearchSuggestions(String keyword, Integer limit) {
        if (StringUtils.isBlank(keyword)) {
            return new ArrayList<>();
        }
        
        String key = SEARCH_SUGGESTION_KEY + keyword.toLowerCase();
        Set<Object> suggestions = redisTemplate.opsForZSet()
                .reverseRange(key, 0, limit - 1);
        
        return suggestions.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPopularSearchTerms(Integer limit) {
        Set<Object> popularTerms = redisTemplate.opsForZSet()
                .reverseRange(POPULAR_SEARCH_KEY, 0, limit - 1);

        return popularTerms.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public void recordSearchHistory(Long userId, String keyword, String type) {
        if (StringUtils.isBlank(keyword)) {
            return;
        }
        
        String key = SEARCH_HISTORY_KEY + userId;
        
        // 记录搜索历史
        Map<String, Object> historyItem = new HashMap<>();
        historyItem.put("keyword", keyword);
        historyItem.put("type", type);
        historyItem.put("timestamp", LocalDateTime.now());
        
        redisTemplate.opsForList().leftPush(key, historyItem);
        redisTemplate.opsForList().trim(key, 0, 99); // 只保留最近100条
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        
        // 更新热门搜索词
        redisTemplate.opsForZSet().incrementScore(POPULAR_SEARCH_KEY, keyword, 1);
        redisTemplate.expire(POPULAR_SEARCH_KEY, 30, TimeUnit.DAYS);
        
        // 更新搜索建议
        String suggestionKey = SEARCH_SUGGESTION_KEY + keyword.toLowerCase();
        redisTemplate.opsForZSet().incrementScore(suggestionKey, keyword, 1);
        redisTemplate.expire(suggestionKey, 7, TimeUnit.DAYS);
        
        log.info("搜索历史记录成功：用户ID={}, 关键词={}, 类型={}", userId, keyword, type);
    }

    @Override
    public List<Map<String, Object>> getUserSearchHistory(Long userId, Integer limit) {
        String key = SEARCH_HISTORY_KEY + userId;
        List<Object> history = redisTemplate.opsForList().range(key, 0, limit - 1);
        
        if (history == null) {
            return new ArrayList<>();
        }
        
        return history.stream()
                .map(item -> (Map<String, Object>) item)
                .collect(Collectors.toList());
    }

    @Override
    public void clearUserSearchHistory(Long userId) {
        String key = SEARCH_HISTORY_KEY + userId;
        redisTemplate.delete(key);
        log.info("用户搜索历史已清空：用户ID={}", userId);
    }

    @Override
    public Map<String, Object> advancedSearch(Object request, Long userId) {
        // 这里可以实现更复杂的搜索逻辑
        // 暂时返回基本搜索结果
        Map<String, Object> result = new HashMap<>();
        result.put("message", "高级搜索功能待实现");
        result.put("userId", userId);
        return result;
    }
}