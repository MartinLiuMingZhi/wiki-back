package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建分类请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class CreateCategoryRequest {
    
    /**
     * 分类名称（必填）
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 分类类型（必填）
     */
    @NotBlank(message = "分类类型不能为空")
    private String type;
}
