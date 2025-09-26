package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新分类请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class UpdateCategoryRequest {
    
    /**
     * 分类名称（必填）
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    /**
     * 父分类ID
     */
    private Long parentId;
}
