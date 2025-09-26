package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xichen.wiki.entity.Ebook;
import com.xichen.wiki.exception.BusinessException;
import com.xichen.wiki.mapper.EbookMapper;
import com.xichen.wiki.service.EbookService;
import com.xichen.wiki.service.FileService;
import com.xichen.wiki.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 电子书服务实现类
 */
@Slf4j
@Service
public class EbookServiceImpl extends ServiceImpl<EbookMapper, Ebook> implements EbookService {

    @Autowired
    private FileService fileService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis键常量已移至RedisKeyUtil统一管理

    @Override
    public Ebook createEbook(Long userId, String title, String description, String coverUrl) {
        Ebook ebook = new Ebook();
        ebook.setTitle(title);
        ebook.setDescription(description);
        ebook.setCoverKey(coverUrl);
        ebook.setUserId(userId);
        ebook.setIsFavorite(false);
        ebook.setDownloadCount(0);
        ebook.setViewCount(0);
        ebook.setLastReadPage(0);
        ebook.setCreatedAt(LocalDateTime.now());
        ebook.setUpdatedAt(LocalDateTime.now());
        
        save(ebook);
        log.info("电子书创建成功：用户ID={}, 标题={}", userId, title);
        return ebook;
    }


    @Override
    @Transactional
    public Ebook updateEbook(Long ebookId, Long userId, String title, String description, String coverUrl, Long categoryId) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            throw new BusinessException("电子书不存在");
        }

        if (!ebook.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此电子书");
        }

        ebook.setTitle(title);
        ebook.setDescription(description);
        ebook.setCoverKey(coverUrl);
        ebook.setUpdatedAt(LocalDateTime.now());

        updateById(ebook);
        log.info("电子书更新成功：ID={}, 标题={}", ebookId, title);
        return ebook;
    }

    @Override
    public Page<Ebook> getUserEbooks(Long userId, Integer page, Integer size, String keyword) {
        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ebook::getUserId, userId);
        
        // 移除categoryId过滤，因为接口中没有这个参数
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Ebook::getTitle, keyword)
                    .or().like(Ebook::getAuthor, keyword)
                    .or().like(Ebook::getDescription, keyword));
        }
        
        wrapper.orderByDesc(Ebook::getCreatedAt);
        return page(pageParam, wrapper);
    }

    @Override
    public Ebook getEbookDetail(Long ebookId, Long userId) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            return null;
        }

        // 检查权限（用户只能查看自己的电子书，或者公开的电子书）
        if (!ebook.getUserId().equals(userId) && !ebook.getIsPublic()) {
            throw new BusinessException("无权限访问此电子书");
        }

        // 增加查看次数
        incrementViewCount(ebookId);
        
        return ebook;
    }

    @Override
    @Transactional
    public boolean deleteEbook(Long ebookId, Long userId) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            throw new BusinessException("电子书不存在");
        }

        if (!ebook.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此电子书");
        }

        // 删除文件
        try {
            fileService.deleteFile(ebook.getFileKey(), userId);
        } catch (Exception e) {
            log.warn("删除电子书文件失败：{}", e.getMessage());
        }

        // 删除电子书记录
        removeById(ebookId);
        
        // 清理相关缓存
        redisTemplate.delete(RedisKeyUtil.getEbookFavoriteKey(userId));
        redisTemplate.delete(RedisKeyUtil.getEbookReadingProgressKey(userId, ebookId));
        
        log.info("电子书删除成功：ID={}, 用户ID={}", ebookId, userId);
        return true;
    }

    @Override
    public boolean updateReadingProgress(Long userId, Long ebookId, Integer currentPage, Integer totalPages) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            throw new BusinessException("电子书不存在");
        }
        
        if (!ebook.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作此电子书");
        }
        
        ebook.setLastReadPage(currentPage);
        ebook.setLastReadDate(LocalDateTime.now());
        updateById(ebook);
        
        // 更新Redis缓存
        String key = RedisKeyUtil.getEbookReadingProgressKey(userId, ebookId);
        Map<String, Object> progressData = new HashMap<>();
        progressData.put("currentPage", currentPage);
        progressData.put("totalPages", totalPages);
        redisTemplate.opsForValue().set(key, progressData, 30, TimeUnit.DAYS);
        
        log.info("阅读进度更新成功：用户ID={}, 电子书ID={}, 当前页={}, 总页数={}", userId, ebookId, currentPage, totalPages);
        return true;
    }


    @Override
    public Page<Ebook> searchEbooks(String keyword, Long userId, Integer page, Integer size) {
        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        
        // 搜索条件：标题、作者、描述
        wrapper.and(w -> w.like(Ebook::getTitle, keyword)
                .or().like(Ebook::getAuthor, keyword)
                .or().like(Ebook::getDescription, keyword));
        
        // 只搜索用户自己的电子书
        wrapper.eq(Ebook::getUserId, userId);
        wrapper.orderByDesc(Ebook::getCreatedAt);
        
        return page(pageParam, wrapper);
    }

    public void incrementViewCount(Long ebookId) {
        String key = RedisKeyUtil.getEbookViewCountKey(ebookId);
        redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
        
        // 异步更新数据库
        // 这里可以添加定时任务来同步Redis数据到数据库
        log.debug("电子书查看次数增加：ID={}", ebookId);
    }

    public Page<Ebook> getPopularEbooks(Integer page, Integer size) {
        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Ebook::getViewCount, Ebook::getDownloadCount)
               .last("LIMIT " + size);
        return page(pageParam, wrapper);
    }

    public Page<Ebook> getRecentReadEbooks(Long userId, Integer page, Integer size) {
        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ebook::getUserId, userId)
               .gt(Ebook::getLastReadPage, 0) // 有阅读记录的
               .orderByDesc(Ebook::getUpdatedAt);
        return page(pageParam, wrapper);
    }
    
    @Override
    public boolean toggleFavorite(Long ebookId, Long userId) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            throw new BusinessException("电子书不存在");
        }
        
        if (!ebook.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作此电子书");
        }
        
        ebook.setIsFavorite(!ebook.getIsFavorite());
        updateById(ebook);
        log.info("电子书收藏状态切换成功：{}", ebook.getTitle());
        return ebook.getIsFavorite();
    }
    
    @Override
    public Ebook getEbookById(Long ebookId, Long userId) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            throw new BusinessException("电子书不存在");
        }
        
        if (!ebook.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此电子书");
        }
        
        return ebook;
    }
    
    @Override
    public Map<String, Object> getEbookStatistics(Long ebookId, Long userId) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            throw new BusinessException("电子书不存在");
        }
        
        if (!ebook.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此电子书");
        }
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalPages", ebook.getTotalPages());
        statistics.put("currentPage", ebook.getCurrentPage());
        statistics.put("readingProgress", ebook.getReadingProgress());
        statistics.put("isFavorite", ebook.getIsFavorite());
        statistics.put("createdAt", ebook.getCreatedAt());
        statistics.put("updatedAt", ebook.getUpdatedAt());
        
        return statistics;
    }
    
    @Override
    public Ebook uploadEbook(Long userId, MultipartFile file, String title, String description, String author, String category, Long categoryId) {
        // 上传文件
        Map<String, Object> uploadResult = fileService.uploadFile(file, "ebooks", userId);
        String fileKey = (String) uploadResult.get("key");
        String fileUrl = (String) uploadResult.get("url");
        
        // 创建电子书记录
        Ebook ebook = new Ebook();
        ebook.setTitle(title);
        ebook.setDescription(description);
        ebook.setAuthor(author);
        ebook.setCategory(category);
        ebook.setCategoryId(categoryId);
        ebook.setFileKey(fileKey);
        ebook.setFileUrl(fileUrl);
        ebook.setUserId(userId);
        ebook.setIsPublic(false);
        ebook.setIsFavorite(false);
        ebook.setDownloadCount(0);
        ebook.setViewCount(0);
        ebook.setLastReadPage(0);
        ebook.setTotalPages(0);
        ebook.setCreatedAt(LocalDateTime.now());
        ebook.setUpdatedAt(LocalDateTime.now());
        
        save(ebook);
        log.info("电子书上传成功：ID={}, 用户ID={}, 标题={}", ebook.getId(), userId, title);
        return ebook;
    }
    
    @Override
    public Map<String, Object> getReadingProgress(Long userId, Long ebookId) {
        String key = RedisKeyUtil.getEbookReadingProgressKey(userId, ebookId);
        Object progressData = redisTemplate.opsForValue().get(key);
        
        if (progressData == null) {
            Map<String, Object> defaultProgress = new HashMap<>();
            defaultProgress.put("currentPage", 0);
            defaultProgress.put("totalPages", 0);
            defaultProgress.put("progress", 0.0);
            return defaultProgress;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) progressData;
        return result;
    }
    
    @Override
    public boolean favoriteEbook(Long userId, Long ebookId) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            throw new BusinessException("电子书不存在");
        }
        
        String key = RedisKeyUtil.getEbookFavoriteKey(userId);
        redisTemplate.opsForSet().add(key, ebookId);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        
        log.info("电子书收藏成功：用户ID={}, 电子书ID={}", userId, ebookId);
        return true;
    }
    
    @Override
    public boolean unfavoriteEbook(Long userId, Long ebookId) {
        String key = RedisKeyUtil.getEbookFavoriteKey(userId);
        redisTemplate.opsForSet().remove(key, ebookId);
        
        log.info("电子书取消收藏成功：用户ID={}, 电子书ID={}", userId, ebookId);
        return true;
    }
    
    @Override
    public Page<Ebook> getFavoriteEbooks(Long userId, Integer page, Integer size) {
        String key = RedisKeyUtil.getEbookFavoriteKey(userId);
        Set<Object> favoriteIds = redisTemplate.opsForSet().members(key);
        
        if (favoriteIds == null || favoriteIds.isEmpty()) {
            return new Page<>(page, size);
        }
        
        List<Long> ebookIds = favoriteIds.stream()
                .map(id -> Long.valueOf(id.toString()))
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Ebook::getId, ebookIds)
                .orderByDesc(Ebook::getUpdatedAt);
        
        return page(new Page<>(page, size), wrapper);
    }
}