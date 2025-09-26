package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新电子书请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class UpdateEbookRequest {
    
    /**
     * 电子书标题（必填）
     */
    @NotBlank(message = "标题不能为空")
    private String title;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 分类ID
     */
    private Long categoryId;
}
