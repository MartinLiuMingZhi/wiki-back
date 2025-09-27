package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新文档请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class UpdateDocumentRequest {
    
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
    
    /**
     * 获取标签ID数组的不可变副本
     */
    public Long[] getTagIds() {
        return tagIds != null ? tagIds.clone() : new Long[0];
    }
    
    /**
     * 设置标签ID数组
     */
    public void setTagIds(Long[] tagIds) {
        this.tagIds = tagIds != null ? tagIds.clone() : new Long[0];
    }
}
