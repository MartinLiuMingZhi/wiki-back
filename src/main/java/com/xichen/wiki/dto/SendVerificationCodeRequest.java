package com.xichen.wiki.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 发送验证码请求DTO
 * 
 * @author xichen
 * @since 2024-09-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationCodeRequest {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String type; // 验证码类型：register, login
}
