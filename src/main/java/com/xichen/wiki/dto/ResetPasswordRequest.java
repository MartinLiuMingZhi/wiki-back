package com.xichen.wiki.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class ResetPasswordRequest {
    
    /**
     * 邮箱地址（必填）
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 新密码（必填，6-20个字符）
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    private String newPassword;
    
    /**
     * 验证码（必填）
     */
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;
}
