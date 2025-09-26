package com.xichen.wiki.controller;

import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.CreateCategoryRequest;
import com.xichen.wiki.dto.UpdateCategoryRequest;
import com.xichen.wiki.entity.Category;
import com.xichen.wiki.service.CategoryService;
import com.xichen.wiki.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@Validated
@Tag(name = "分类管理", description = "分类CRUD操作相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "创建分类", description = "创建新的分类")
    @PostMapping
    public Result<Category> createCategory(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateCategoryRequest request) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Category category = categoryService.createCategory(
                userId,
                request.getName(),
                request.getParentId()
        );
        
        return Result.success("创建成功", category);
    }

    @Operation(summary = "更新分类", description = "更新分类信息")
    @PutMapping("/{id}")
    public Result<Category> updateCategory(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "分类ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Category category = categoryService.updateCategory(
                id,
                userId,
                request.getName(),
                request.getParentId()
        );
        
        return Result.success("更新成功", category);
    }

    @Operation(summary = "获取用户分类树", description = "获取用户的分类树结构")
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "分类类型") @RequestParam @NotBlank String type) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        List<Category> categories = categoryService.getCategoryTree(userId, type);
        return Result.success(categories);
    }

    @Operation(summary = "获取公共分类树", description = "获取公共分类树结构")
    @GetMapping("/public/tree")
    public Result<List<Category>> getPublicCategoryTree(
            @Parameter(description = "分类类型") @RequestParam @NotBlank String type) {
        
        List<Category> categories = categoryService.getPublicCategoryTree(type);
        return Result.success(categories);
    }

    @Operation(summary = "获取子分类", description = "获取指定分类的子分类列表")
    @GetMapping("/{id}/children")
    public Result<List<Category>> getChildCategories(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "分类ID") @PathVariable @NotNull Long id,
            @Parameter(description = "分类类型") @RequestParam @NotBlank String type) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        List<Category> categories = categoryService.getChildCategories(id, userId, type);
        return Result.success(categories);
    }

    @Operation(summary = "获取分类详情", description = "根据ID获取分类详细信息")
    @GetMapping("/{id}")
    public Result<Category> getCategoryDetail(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "分类ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Category category = categoryService.getCategoryDetail(id, userId);
        return Result.success(category);
    }

    @Operation(summary = "删除分类", description = "删除指定分类")
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "分类ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        categoryService.deleteCategory(id, userId);
        return Result.success("删除成功");
    }

}
