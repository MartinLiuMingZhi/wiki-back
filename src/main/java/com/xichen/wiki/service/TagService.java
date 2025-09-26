package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.Tag;

import java.util.List;
import java.util.Map;

/**
 * 标签服务接口
 */
public interface TagService extends IService<Tag> {
    
    /**
     * 创建标签
     */
    Tag createTag(Long userId, String name, String description);
    
    /**
     * 更新标签
     */
    Tag updateTag(Long tagId, Long userId, String name, String description);
    
    /**
     * 获取用户标签列表
     */
    Page<Tag> getUserTags(Long userId, Integer page, Integer size, String keyword);
    
    /**
     * 获取公共标签
     */
    Page<Tag> getPublicTags(Integer page, Integer size, String keyword);
    
    /**
     * 获取热门标签
     */
    List<Tag> getPopularTags(Integer limit);
    
    /**
     * 删除标签
     */
    boolean deleteTag(Long tagId, Long userId);
    
    /**
     * 获取标签详情
     */
    Map<String, Object> getTagDetail(Long tagId, Long userId);
    
    /**
     * 搜索标签
     */
    List<Tag> searchTags(String keyword, Long userId, Integer limit);
    
    /**
     * 获取标签使用次数
     */
    Integer getTagUsageCount(Long tagId);
    
    /**
     * 批量删除标签
     */
    boolean deleteTags(List<Long> tagIds, Long userId);
}
