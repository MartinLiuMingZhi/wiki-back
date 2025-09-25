package com.xichen.wiki.controller;

import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.*;
import com.xichen.wiki.entity.User;
import com.xichen.wiki.service.UserService;
import com.xichen.wiki.service.IVerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private IVerificationCodeService verificationCodeService;

    @Operation(summary = "用户注册", description = "用户注册接口")
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getUsername(), request.getEmail(), request.getPassword());
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("avatarUrl", user.getAvatarUrl());
        
        return Result.success("注册成功", data);
    }

    @Operation(summary = "用户登录", description = "用户登录接口")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> data = userService.login(request.getUsername(), request.getPassword());
        
        return Result.success("登录成功", data);
    }
    
    @Operation(summary = "发送验证码", description = "发送邮箱验证码")
    @PostMapping("/send-verification-code")
    public Result<String> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        try {
            boolean success = verificationCodeService.sendVerificationCode(request.getEmail(), request.getType());
            if (success) {
                return Result.success("验证码发送成功");
            } else {
                return Result.error("验证码发送失败");
            }
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return Result.error("验证码发送失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "验证邮箱验证码", description = "验证邮箱验证码")
    @PostMapping("/verify-email-code")
    public Result<String> verifyEmailCode(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            boolean valid = verificationCodeService.verifyCode(request.getEmail(), request.getVerificationCode(), "register");
            if (valid) {
                return Result.success("验证码验证成功");
            } else {
                return Result.error("验证码无效或已过期");
            }
        } catch (Exception e) {
            log.error("验证码验证失败", e);
            return Result.error("验证码验证失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "邮箱注册", description = "使用邮箱验证码注册")
    @PostMapping("/register-with-email")
    public Result<Map<String, Object>> registerWithEmail(@Valid @RequestBody RegisterRequest request) {
        try {
            // 验证邮箱验证码
            boolean valid = verificationCodeService.verifyCode(request.getEmail(), request.getVerificationCode(), "register");
            if (!valid) {
                return Result.error("验证码无效或已过期");
            }
            
            // 注册用户
            User user = userService.register(request.getUsername(), request.getEmail(), request.getPassword());
            
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("message", "注册成功，请登录");
            
            return Result.success("注册成功", data);
        } catch (Exception e) {
            log.error("邮箱注册失败", e);
            return Result.error("注册失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "邮箱登录", description = "使用邮箱验证码登录")
    @PostMapping("/login-with-email")
    public Result<Map<String, Object>> loginWithEmail(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            // 验证邮箱验证码
            boolean valid = verificationCodeService.verifyCode(request.getEmail(), request.getVerificationCode(), "login");
            if (!valid) {
                return Result.error("验证码无效或已过期");
            }
            
            // 查找用户
            User user = userService.findByEmail(request.getEmail());
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 生成JWT token
            String token = userService.generateToken(user);
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            
            return Result.success("登录成功", data);
        } catch (Exception e) {
            log.error("邮箱登录失败", e);
            return Result.error("登录失败: " + e.getMessage());
        }
    }
}

