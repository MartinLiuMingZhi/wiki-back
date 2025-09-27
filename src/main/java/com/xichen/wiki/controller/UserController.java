package com.xichen.wiki.controller;

import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.ChangePasswordRequest;
import com.xichen.wiki.dto.ResetPasswordRequest;
import com.xichen.wiki.dto.UpdateAvatarRequest;
import com.xichen.wiki.dto.UserUpdateRequest;
import com.xichen.wiki.entity.User;
import com.xichen.wiki.service.UserService;
import com.xichen.wiki.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "用户管理", description = "用户信息管理相关接口")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        // 从请求头中获取JWT token
        String token = request.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("status", user.getStatus());
        data.put("createdAt", user.getCreatedAt());
        data.put("updatedAt", user.getUpdatedAt());

        return Result.success(data);
    }

    @Operation(summary = "更新用户信息", description = "更新当前用户的基本信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/me")
    public Result<Map<String, Object>> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request, HttpServletRequest httpRequest) {
        // 从请求头中获取JWT token
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        
        User user = userService.updateUserInfo(
                userId,
                request.getUsername(),
                request.getEmail(),
                request.getAvatarUrl()
        );

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("status", user.getStatus());
        data.put("createdAt", user.getCreatedAt());
        data.put("updatedAt", user.getUpdatedAt());


        return Result.success("更新成功", data);
    }

    @Operation(summary = "修改密码", description = "修改用户密码", 
               security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/me/password")
    public Result<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success("密码修改成功");
    }

    @Operation(summary = "重置密码", description = "通过邮箱重置密码")
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getEmail(), request.getNewPassword(), request.getVerificationCode());
        return Result.success("密码重置成功");
    }

    @Operation(summary = "更新头像", description = "更新用户头像", 
               security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/me/avatar")
    public Result<Map<String, Object>> updateAvatar(
            @Valid @RequestBody UpdateAvatarRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        User user = userService.updateAvatar(userId, request.getAvatarUrl());

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("status", user.getStatus());
        data.put("createdAt", user.getCreatedAt());
        data.put("updatedAt", user.getUpdatedAt());

        return Result.success("头像更新成功", data);
    }

    @Operation(summary = "获取用户统计", description = "获取用户统计信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/me/statistics")
    public Result<Map<String, Object>> getUserStatistics(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Map<String, Object> statistics = userService.getUserStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取用户活动", description = "获取用户活动记录", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/me/activities")
    public Result<Page<Map<String, Object>>> getUserActivities(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Page<Map<String, Object>> activities = userService.getUserActivities(userId, page, size);
        return Result.success(activities);
    }

    @Operation(summary = "检查用户名", description = "检查用户名是否可用")
    @GetMapping("/check-username")
    public Result<Map<String, Object>> checkUsername(
            @Parameter(description = "用户名") @RequestParam @NotBlank String username) {
        
        boolean available = userService.isUsernameAvailable(username);
        return Result.success(Map.of("available", available));
    }

    @Operation(summary = "检查邮箱", description = "检查邮箱是否可用")
    @GetMapping("/check-email")
    public Result<Map<String, Object>> checkEmail(
            @Parameter(description = "邮箱") @RequestParam @NotBlank String email) {
        
        boolean available = userService.isEmailAvailable(email);
        return Result.success(Map.of("available", available));
    }

}

