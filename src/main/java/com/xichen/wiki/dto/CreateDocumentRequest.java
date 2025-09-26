package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建文档请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class CreateDocumentRequest {
    
    /**
     * 文档标题（必填）
     */
    @NotBlank(message = "标题不能为空")
    private String title;
    
    /**
     * 文档内容
     */
    private String content;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 标签ID数组
     */
    private Long[] tagIds;
}
