package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.Category;

import java.util.List;
import java.util.Map;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {
    
    /**
     * 创建分类
     */
    Category createCategory(Long userId, String name, Long parentId);
    
    /**
     * 更新分类
     */
    Category updateCategory(Long categoryId, Long userId, String name, Long parentId);
    
    /**
     * 删除分类
     */
    boolean deleteCategory(Long categoryId, Long userId);
    
    /**
     * 获取用户分类列表
     */
    Page<Category> getUserCategories(Long userId, Integer page, Integer size, String type, String keyword);
    
    /**
     * 获取分类树
     */
    List<Category> getCategoryTree(Long userId, String type);
    
    /**
     * 获取分类详情
     */
    Category getCategoryDetail(Long categoryId, Long userId);
    
    /**
     * 获取公共分类树
     */
    List<Category> getPublicCategoryTree(String type);
    
    /**
     * 获取子分类列表
     */
    List<Category> getChildCategories(Long parentId, Long userId, String type);
    
    /**
     * 获取分类统计信息
     */
    Map<String, Object> getCategoryStatistics(Long userId, String type);
}
