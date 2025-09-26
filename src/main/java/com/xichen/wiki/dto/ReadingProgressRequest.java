package com.xichen.wiki.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 阅读进度请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class ReadingProgressRequest {
    
    /**
     * 阅读进度（必填，0-100）
     */
    @NotNull(message = "阅读进度不能为空")
    @Min(value = 0, message = "阅读进度不能小于0")
    private Integer progress;
    
    /**
     * 当前页码
     */
    private Integer pageNumber;
}
