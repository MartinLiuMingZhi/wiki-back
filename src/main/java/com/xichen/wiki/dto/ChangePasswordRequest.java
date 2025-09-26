package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class ChangePasswordRequest {
    
    /**
     * 原密码（必填）
     */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    /**
     * 新密码（必填，6-20个字符）
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    private String newPassword;
}
