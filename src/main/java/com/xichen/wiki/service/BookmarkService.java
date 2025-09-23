package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.Bookmark;

import java.util.List;

/**
 * 书签服务接口
 */
public interface BookmarkService extends IService<Bookmark> {
    
    /**
     * 创建书签
     */
    Bookmark createBookmark(Long userId, Long ebookId, Integer pageNumber, String note);
    
    /**
     * 更新书签
     */
    Bookmark updateBookmark(Long bookmarkId, Long userId, String note);
    
    /**
     * 获取电子书书签列表
     */
    List<Bookmark> getEbookBookmarks(Long ebookId, Long userId);
    
    /**
     * 获取用户所有书签
     */
    Page<Bookmark> getUserBookmarks(Long userId, Integer page, Integer size, Long ebookId);
    
    /**
     * 删除书签
     */
    void deleteBookmark(Long bookmarkId, Long userId);
    
    /**
     * 获取书签详情
     */
    Bookmark getBookmarkDetail(Long bookmarkId, Long userId);
    
    /**
     * 批量删除书签
     */
    void deleteBookmarks(List<Long> bookmarkIds, Long userId);
}
