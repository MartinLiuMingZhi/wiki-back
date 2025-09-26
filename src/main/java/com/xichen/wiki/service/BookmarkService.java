package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.Bookmark;


/**
 * 书签服务接口
 */
public interface BookmarkService extends IService<Bookmark> {
    
    /**
     * 创建书签
     */
    Bookmark createBookmark(Long userId, Long ebookId, Integer pageNumber, String note);
    
    /**
     * 删除书签
     */
    boolean deleteBookmark(Long userId, Long bookmarkId);
    
    /**
     * 批量删除书签
     */
    boolean batchDeleteBookmarks(Long userId, Long[] bookmarkIds);
    
    /**
     * 获取用户书签列表
     */
    Page<Bookmark> getUserBookmarks(Long userId, Integer page, Integer size);
    
    /**
     * 检查书签是否存在
     */
    boolean isBookmarked(Long userId, Long documentId);
    
    /**
     * 更新书签
     */
    Bookmark updateBookmark(Long bookmarkId, Long userId, String note);
    
    /**
     * 获取书签详情
     */
    Bookmark getBookmarkDetail(Long bookmarkId, Long userId);
    
    /**
     * 获取电子书书签列表
     */
    java.util.List<Bookmark> getEbookBookmarks(Long ebookId, Long userId);
    
    /**
     * 获取用户书签列表（带电子书ID过滤）
     */
    Page<Bookmark> getUserBookmarks(Long userId, Integer page, Integer size, Long ebookId);
    
    /**
     * 批量删除书签（List版本）
     */
    void deleteBookmarks(java.util.List<Long> bookmarkIds, Long userId);
}
