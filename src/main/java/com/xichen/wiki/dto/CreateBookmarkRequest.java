package com.xichen.wiki.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建书签请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class CreateBookmarkRequest {
    
    /**
     * 电子书ID（必填）
     */
    @NotNull(message = "电子书ID不能为空")
    private Long ebookId;
    
    /**
     * 页码（必填，必须大于0）
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNumber;
    
    /**
     * 书签备注
     */
    private String note;
}
