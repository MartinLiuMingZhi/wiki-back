package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新头像请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class UpdateAvatarRequest {
    
    /**
     * 头像URL（必填）
     */
    @NotBlank(message = "头像URL不能为空")
    private String avatarUrl;
}
