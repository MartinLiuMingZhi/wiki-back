package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 生成上传URL请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class GenerateUploadUrlRequest {
    
    /**
     * 文件名（必填）
     */
    @NotBlank(message = "文件名不能为空")
    private String fileName;
    
    /**
     * 文件内容类型
     */
    private String contentType;
    
    /**
     * 存储文件夹
     */
    private String folder;
}
