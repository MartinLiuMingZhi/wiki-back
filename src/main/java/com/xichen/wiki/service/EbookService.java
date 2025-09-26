package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.Ebook;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 电子书服务接口
 */
public interface EbookService extends IService<Ebook> {
    
    /**
     * 创建电子书
     */
    Ebook createEbook(Long userId, String title, String description, String coverUrl);
    
    /**
     * 更新电子书
     */
    Ebook updateEbook(Long ebookId, Long userId, String title, String description, String coverUrl, Long categoryId);
    
    /**
     * 删除电子书
     */
    boolean deleteEbook(Long ebookId, Long userId);
    
    /**
     * 更新阅读进度
     */
    boolean updateReadingProgress(Long userId, Long ebookId, Integer currentPage, Integer totalPages);
    
    /**
     * 获取用户电子书列表
     */
    Page<Ebook> getUserEbooks(Long userId, Integer page, Integer size, String keyword);
    
    /**
     * 获取电子书详情
     */
    Ebook getEbookDetail(Long ebookId, Long userId);
    
    /**
     * 搜索电子书
     */
    Page<Ebook> searchEbooks(String keyword, Long userId, Integer page, Integer size);
    
    /**
     * 获取电子书统计信息
     */
    Map<String, Object> getEbookStatistics(Long ebookId, Long userId);
    
    /**
     * 切换电子书收藏状态
     */
    boolean toggleFavorite(Long ebookId, Long userId);
    
    /**
     * 获取电子书详情（简化版本）
     */
    Ebook getEbookById(Long ebookId, Long userId);
    
    /**
     * 上传电子书文件
     */
    Ebook uploadEbook(Long userId, MultipartFile file, String title, String description, String author, String category, Long categoryId);
    
    /**
     * 获取阅读进度
     */
    Map<String, Object> getReadingProgress(Long userId, Long ebookId);
    
    /**
     * 收藏电子书
     */
    boolean favoriteEbook(Long userId, Long ebookId);
    
    /**
     * 取消收藏电子书
     */
    boolean unfavoriteEbook(Long userId, Long ebookId);
    
    /**
     * 获取收藏的电子书列表
     */
    Page<Ebook> getFavoriteEbooks(Long userId, Integer page, Integer size);
}
