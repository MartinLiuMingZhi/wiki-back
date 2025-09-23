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
     * 上传电子书
     */
    Ebook uploadEbook(Long userId, MultipartFile file, String title, String author, 
                     String description, Long categoryId);
    
    /**
     * 更新电子书信息
     */
    Ebook updateEbook(Long ebookId, Long userId, String title, String author, 
                     String description, Long categoryId);
    
    /**
     * 获取用户电子书列表
     */
    Page<Ebook> getUserEbooks(Long userId, Integer page, Integer size, Long categoryId, String keyword);
    
    /**
     * 获取电子书详情
     */
    Ebook getEbookDetail(Long ebookId, Long userId);
    
    /**
     * 删除电子书
     */
    void deleteEbook(Long ebookId, Long userId);
    
    /**
     * 收藏电子书
     */
    void favoriteEbook(Long ebookId, Long userId);
    
    /**
     * 取消收藏电子书
     */
    void unfavoriteEbook(Long ebookId, Long userId);
    
    /**
     * 获取收藏的电子书列表
     */
    Page<Ebook> getFavoriteEbooks(Long userId, Integer page, Integer size);
    
    /**
     * 更新阅读进度
     */
    void updateReadingProgress(Long ebookId, Long userId, Integer progress, Integer pageNumber);
    
    /**
     * 获取阅读进度
     */
    Map<String, Object> getReadingProgress(Long ebookId, Long userId);
    
    /**
     * 搜索电子书
     */
    Page<Ebook> searchEbooks(String keyword, Long userId, Integer page, Integer size);
    
    /**
     * 增加查看次数
     */
    void incrementViewCount(Long ebookId);
    
    /**
     * 获取热门电子书
     */
    Page<Ebook> getPopularEbooks(Integer page, Integer size);
    
    /**
     * 获取最近阅读的电子书
     */
    Page<Ebook> getRecentReadEbooks(Long userId, Integer page, Integer size);
}
