package com.xichen.wiki.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.CreateCategoryRequest;
import com.xichen.wiki.dto.UpdateCategoryRequest;
import com.xichen.wiki.entity.Category;
import com.xichen.wiki.service.CategoryService;
import com.xichen.wiki.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

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

    @Operation(summary = "创建分类", description = "创建新的分类", 
               security = @SecurityRequirement(name = "Authorization"))
    @PostMapping
    public Result<Category> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Category category = categoryService.createCategory(
                userId,
                request.getName(),
                request.getParentId()
        );
        
        return Result.success("创建成功", category);
    }

    @Operation(summary = "更新分类", description = "更新分类信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/{id}")
    public Result<Category> updateCategory(
            @Parameter(description = "分类ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateCategoryRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Category category = categoryService.updateCategory(
                id,
                userId,
                request.getName(),
                request.getParentId()
        );
        
        return Result.success("更新成功", category);
    }

    @Operation(summary = "获取用户分类列表", description = "分页获取用户的分类列表", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping
    public Result<Page<Category>> getCategories(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "分类类型") @RequestParam @NotBlank String type,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Page<Category> categories = categoryService.getUserCategories(userId, page, size, type, keyword);
        return Result.success(categories);
    }

    @Operation(summary = "获取用户分类树", description = "获取用户的分类树结构", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree(
            @Parameter(description = "分类类型") @RequestParam @NotBlank String type,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
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

    @Operation(summary = "获取子分类", description = "获取指定分类的子分类列表", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/{id}/children")
    public Result<List<Category>> getChildCategories(
            @Parameter(description = "分类ID") @PathVariable @NotNull Long id,
            @Parameter(description = "分类类型") @RequestParam @NotBlank String type,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        List<Category> categories = categoryService.getChildCategories(id, userId, type);
        return Result.success(categories);
    }

    @Operation(summary = "获取分类详情", description = "根据ID获取分类详细信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/{id}")
    public Result<Category> getCategoryDetail(
            @Parameter(description = "分类ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Category category = categoryService.getCategoryDetail(id, userId);
        return Result.success(category);
    }

    @Operation(summary = "删除分类", description = "删除指定分类", 
               security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        categoryService.deleteCategory(id, userId);
        return Result.success("删除成功");
    }

    @Operation(summary = "获取分类统计", description = "获取用户分类统计信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getCategoryStatistics(
            @Parameter(description = "分类类型") @RequestParam @NotBlank String type,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Map<String, Object> statistics = categoryService.getCategoryStatistics(userId, type);
        return Result.success(statistics);
    }

}
