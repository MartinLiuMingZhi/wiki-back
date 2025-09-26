package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建标签请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class CreateTagRequest {
    
    /**
     * 标签名称（必填）
     */
    @NotBlank(message = "标签名称不能为空")
    private String name;
    
    /**
     * 标签描述
     */
    private String description;
}
