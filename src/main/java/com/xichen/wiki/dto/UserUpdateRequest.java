package com.xichen.wiki.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateRequest {
    
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String avatarUrl;
}

