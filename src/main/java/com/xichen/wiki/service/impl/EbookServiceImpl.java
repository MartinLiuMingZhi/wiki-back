package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xichen.wiki.entity.Ebook;
import com.xichen.wiki.exception.BusinessException;
import com.xichen.wiki.mapper.EbookMapper;
import com.xichen.wiki.service.EbookService;
import com.xichen.wiki.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 电子书服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EbookServiceImpl extends ServiceImpl<EbookMapper, Ebook> implements EbookService {

    private final FileService fileService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String EBOOK_FAVORITE_KEY = "ebook:favorite:"; // userId -> Set<ebookId>
    private static final String EBOOK_READING_PROGRESS_KEY = "ebook:reading_progress:"; // userId:ebookId -> progress
    private static final String EBOOK_VIEW_COUNT_KEY = "ebook:view_count:";

    @Override
    @Transactional
    public Ebook uploadEbook(Long userId, MultipartFile file, String title, String author, 
                           String description, Long categoryId) {
        try {
            // 上传文件
            Map<String, Object> uploadResult = fileService.uploadFile(file, "ebooks", userId);
            String fileKey = (String) uploadResult.get("fileKey");
            String fileUrl = (String) uploadResult.get("url");
            Long fileSize = (Long) uploadResult.get("fileSize");

            // 创建电子书记录
            Ebook ebook = new Ebook();
            ebook.setTitle(title);
            ebook.setAuthor(author);
            ebook.setDescription(description);
            ebook.setUserId(userId);
            ebook.setCategoryId(categoryId);
            ebook.setFileKey(fileKey);
            ebook.setFileUrl(fileUrl);
            ebook.setFileSize(fileSize);
            ebook.setIsFavorite(false);
            ebook.setDownloadCount(0);
            ebook.setViewCount(0);
            ebook.setLastReadPage(0);
            ebook.setCreatedAt(LocalDateTime.now());
            ebook.setUpdatedAt(LocalDateTime.now());

            save(ebook);
            log.info("电子书上传成功：用户ID={}, 标题={}", userId, title);
            return ebook;

        } catch (Exception e) {
            log.error("电子书上传失败：{}", e.getMessage());
            throw new BusinessException("电子书上传失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Ebook updateEbook(Long ebookId, Long userId, String title, String author, 
                           String description, Long categoryId) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            throw new BusinessException("电子书不存在");
        }

        if (!ebook.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此电子书");
        }

        ebook.setTitle(title);
        ebook.setAuthor(author);
        ebook.setDescription(description);
        ebook.setCategoryId(categoryId);
        ebook.setUpdatedAt(LocalDateTime.now());

        updateById(ebook);
        log.info("电子书更新成功：ID={}, 标题={}", ebookId, title);
        return ebook;
    }

    @Override
    public Page<Ebook> getUserEbooks(Long userId, Integer page, Integer size, Long categoryId, String keyword) {
        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ebook::getUserId, userId);
        
        if (categoryId != null) {
            wrapper.eq(Ebook::getCategoryId, categoryId);
        }
        
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
    public void deleteEbook(Long ebookId, Long userId) {
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
        redisTemplate.delete(EBOOK_FAVORITE_KEY + userId);
        redisTemplate.delete(EBOOK_READING_PROGRESS_KEY + userId + ":" + ebookId);
        
        log.info("电子书删除成功：ID={}, 用户ID={}", ebookId, userId);
    }

    @Override
    public void favoriteEbook(Long ebookId, Long userId) {
        Ebook ebook = getById(ebookId);
        if (ebook == null) {
            throw new BusinessException("电子书不存在");
        }

        String key = EBOOK_FAVORITE_KEY + userId;
        redisTemplate.opsForSet().add(key, ebookId);
        
        // 更新数据库
        ebook.setIsFavorite(true);
        updateById(ebook);
        
        log.info("电子书收藏成功：用户ID={}, 电子书ID={}", userId, ebookId);
    }

    @Override
    public void unfavoriteEbook(Long ebookId, Long userId) {
        String key = EBOOK_FAVORITE_KEY + userId;
        redisTemplate.opsForSet().remove(key, ebookId);
        
        // 更新数据库
        Ebook ebook = getById(ebookId);
        if (ebook != null) {
            ebook.setIsFavorite(false);
            updateById(ebook);
        }
        
        log.info("电子书取消收藏成功：用户ID={}, 电子书ID={}", userId, ebookId);
    }

    @Override
    public Page<Ebook> getFavoriteEbooks(Long userId, Integer page, Integer size) {
        String key = EBOOK_FAVORITE_KEY + userId;
        // 获取收藏的电子书ID列表
        java.util.Set<Object> ebookIds = redisTemplate.opsForSet().members(key);
        
        if (ebookIds == null || ebookIds.isEmpty()) {
            return new Page<>(page, size);
        }

        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Ebook::getId, ebookIds)
               .eq(Ebook::getIsFavorite, true)
               .orderByDesc(Ebook::getUpdatedAt);
        
        return page(pageParam, wrapper);
    }

    @Override
    public void updateReadingProgress(Long ebookId, Long userId, Integer progress, Integer pageNumber) {
        String key = EBOOK_READING_PROGRESS_KEY + userId + ":" + ebookId;
        
        Map<String, Object> progressData = new HashMap<>();
        progressData.put("progress", progress);
        progressData.put("pageNumber", pageNumber);
        progressData.put("lastReadTime", LocalDateTime.now());
        
        redisTemplate.opsForHash().putAll(key, progressData);
        redisTemplate.expire(key, 30, TimeUnit.DAYS); // 30天过期
        
        // 更新数据库
        Ebook ebook = getById(ebookId);
        if (ebook != null) {
            ebook.setLastReadPage(pageNumber);
            ebook.setUpdatedAt(LocalDateTime.now());
            updateById(ebook);
        }
        
        log.info("阅读进度更新成功：用户ID={}, 电子书ID={}, 进度={}%, 页码={}", 
                userId, ebookId, progress, pageNumber);
    }

    @Override
    public Map<String, Object> getReadingProgress(Long ebookId, Long userId) {
        String key = EBOOK_READING_PROGRESS_KEY + userId + ":" + ebookId;
        Map<Object, Object> progressData = redisTemplate.opsForHash().entries(key);
        
        Map<String, Object> result = new HashMap<>();
        if (!progressData.isEmpty()) {
            result.put("progress", progressData.get("progress"));
            result.put("pageNumber", progressData.get("pageNumber"));
            result.put("lastReadTime", progressData.get("lastReadTime"));
        } else {
            // 从数据库获取
            Ebook ebook = getById(ebookId);
            if (ebook != null) {
                result.put("progress", 0);
                result.put("pageNumber", ebook.getLastReadPage());
                result.put("lastReadTime", ebook.getUpdatedAt());
            }
        }
        
        return result;
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

    @Override
    public void incrementViewCount(Long ebookId) {
        String key = EBOOK_VIEW_COUNT_KEY + ebookId;
        redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
        
        // 异步更新数据库
        // 这里可以添加定时任务来同步Redis数据到数据库
        log.debug("电子书查看次数增加：ID={}", ebookId);
    }

    @Override
    public Page<Ebook> getPopularEbooks(Integer page, Integer size) {
        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Ebook::getViewCount, Ebook::getDownloadCount)
               .last("LIMIT " + size);
        return page(pageParam, wrapper);
    }

    @Override
    public Page<Ebook> getRecentReadEbooks(Long userId, Integer page, Integer size) {
        Page<Ebook> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ebook::getUserId, userId)
               .gt(Ebook::getLastReadPage, 0) // 有阅读记录的
               .orderByDesc(Ebook::getUpdatedAt);
        return page(pageParam, wrapper);
    }
}