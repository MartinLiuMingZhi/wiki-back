package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xichen.wiki.entity.Category;
import com.xichen.wiki.exception.BusinessException;
import com.xichen.wiki.mapper.CategoryMapper;
import com.xichen.wiki.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public Category createCategory(Long userId, String name, Long parentId) {
        // 检查同级分类名称是否重复
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, name)
                .eq(Category::getParentId, parentId)
                .eq(Category::getUserId, userId);
        
        if (count(wrapper) > 0) {
            throw new BusinessException("同级分类名称不能重复");
        }
        
        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId);
        category.setUserId(userId);
        
        save(category);
        log.info("分类创建成功：{}", name);
        return category;
    }

    @Override
    public Category updateCategory(Long categoryId, Long userId, String name, Long parentId) {
        Category category = getById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        if (!category.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作此分类");
        }
        
        // 检查同级分类名称是否重复
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, name)
                .eq(Category::getParentId, parentId)
                .eq(Category::getType, category.getType())
                .eq(Category::getUserId, userId)
                .ne(Category::getId, categoryId);
        
        if (count(wrapper) > 0) {
            throw new BusinessException("同级分类名称不能重复");
        }
        
        category.setName(name);
        category.setParentId(parentId);
        
        updateById(category);
        log.info("分类更新成功：{}", name);
        return category;
    }

    @Override
    public List<Category> getCategoryTree(Long userId, String type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getUserId, userId)
                .eq(Category::getType, type)
                .orderByAsc(Category::getName);
        
        List<Category> categories = list(wrapper);
        return buildCategoryTree(categories, null);
    }

    @Override
    public List<Category> getPublicCategoryTree(String type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(Category::getUserId)
                .eq(Category::getType, type)
                .orderByAsc(Category::getName);
        
        List<Category> categories = list(wrapper);
        return buildCategoryTree(categories, null);
    }

    @Override
    public boolean deleteCategory(Long categoryId, Long userId) {
        Category category = getById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        if (!category.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此分类");
        }
        
        // 检查是否有子分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, categoryId);
        if (count(wrapper) > 0) {
            throw new BusinessException("该分类下还有子分类，无法删除");
        }
        
        removeById(categoryId);
        log.info("分类删除成功：{}", category.getName());
        return true;
    }

    @Override
    public Category getCategoryDetail(Long categoryId, Long userId) {
        Category category = getById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        if (!category.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此分类");
        }
        
        return category;
    }

    @Override
    public List<Category> getChildCategories(Long parentId, Long userId, String type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, parentId)
                .eq(Category::getUserId, userId)
                .eq(Category::getType, type)
                .orderByAsc(Category::getName);
        
        return list(wrapper);
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Category> getUserCategories(Long userId, Integer page, Integer size, String keyword) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Category> pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getUserId, userId);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Category::getName, keyword);
        }
        
        wrapper.orderByDesc(Category::getCreatedAt);
        
        return page(pageParam, wrapper);
    }

    /**
     * 构建分类树
     */
    private List<Category> buildCategoryTree(List<Category> categories, Long parentId) {
        List<Category> result = new ArrayList<>();
        
        for (Category category : categories) {
            if ((parentId == null && category.getParentId() == null) ||
                (parentId != null && parentId.equals(category.getParentId()))) {
                
                List<Category> children = buildCategoryTree(categories, category.getId());
                category.setChildren(children);
                result.add(category);
            }
        }
        
        return result;
    }
}
