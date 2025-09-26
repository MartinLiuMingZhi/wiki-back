package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 高级搜索请求DTO
 * 
 * 支持多条件组合的复杂搜索功能，包括：
 * - 关键词搜索
 * - 类型筛选（文档/电子书/全部）
 * - 分类筛选
 * - 时间范围筛选
 * - 排序方式设置
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class AdvancedSearchRequest {
    
    /**
     * 搜索关键词（必填）
     */
    @NotBlank(message = "搜索关键词不能为空")
    private String keyword;
    
    /**
     * 搜索类型：document-仅文档, ebook-仅电子书, all-全部
     */
    private String type;
    
    /**
     * 分类ID，用于按分类筛选
     */
    private Long categoryId;
    
    /**
     * 开始日期，格式：yyyy-MM-dd
     */
    private String startDate;
    
    /**
     * 结束日期，格式：yyyy-MM-dd
     */
    private String endDate;
    
    /**
     * 排序字段：title-标题, created_at-创建时间, updated_at-更新时间
     */
    private String sortBy;
    
    /**
     * 排序方向：asc-升序, desc-降序
     */
    private String sortOrder;
}
