package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {
    
    /**
     * 创建分类
     */
    Category createCategory(Long userId, String name, Long parentId, String type);
    
    /**
     * 更新分类
     */
    Category updateCategory(Long categoryId, Long userId, String name, Long parentId);
    
    /**
     * 获取用户分类树
     */
    List<Category> getCategoryTree(Long userId, String type);
    
    /**
     * 获取公共分类树
     */
    List<Category> getPublicCategoryTree(String type);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId, Long userId);
    
    /**
     * 获取分类详情
     */
    Category getCategoryDetail(Long categoryId, Long userId);
    
    /**
     * 获取子分类列表
     */
    List<Category> getChildCategories(Long parentId, Long userId, String type);
}
