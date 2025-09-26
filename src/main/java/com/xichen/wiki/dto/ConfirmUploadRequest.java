package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 确认上传请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class ConfirmUploadRequest {
    
    /**
     * 文件键（必填）
     */
    @NotBlank(message = "文件键不能为空")
    private String fileKey;
    
    /**
     * 文件大小（必填）
     */
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;
}
