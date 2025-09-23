package com.xichen.wiki.controller;

import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.UserUpdateRequest;
import com.xichen.wiki.entity.User;
import com.xichen.wiki.service.UserService;
import com.xichen.wiki.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户信息管理相关接口")
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser() {
        // 从JWT token中解析用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "avatarUrl", user.getAvatarUrl(),
                "status", user.getStatus(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt()
        );
        
        return Result.success(data);
    }

    @Operation(summary = "更新用户信息", description = "更新当前用户的基本信息")
    @PutMapping("/me")
    public Result<Map<String, Object>> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        // 从JWT token中解析用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        
        User user = userService.updateUserInfo(
                userId,
                request.getUsername(),
                request.getEmail(),
                request.getAvatarUrl()
        );
        
        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "avatarUrl", user.getAvatarUrl(),
                "status", user.getStatus(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt()
        );
        
        return Result.success("更新成功", data);
    }

    @Operation(summary = "修改密码", description = "修改用户密码")
    @PutMapping("/me/password")
    public Result<String> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest request) {
        
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success("密码修改成功");
    }

    @Operation(summary = "重置密码", description = "通过邮箱重置密码")
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getEmail(), request.getNewPassword());
        return Result.success("密码重置成功");
    }

    @Operation(summary = "更新头像", description = "更新用户头像")
    @PutMapping("/me/avatar")
    public Result<Map<String, Object>> updateAvatar(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateAvatarRequest request) {
        
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        User user = userService.updateAvatar(userId, request.getAvatarUrl());
        
        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "avatarUrl", user.getAvatarUrl(),
                "status", user.getStatus(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt()
        );
        
        return Result.success("头像更新成功", data);
    }

    @Operation(summary = "获取用户统计", description = "获取用户统计信息")
    @GetMapping("/me/statistics")
    public Result<Map<String, Object>> getUserStatistics(@RequestHeader("Authorization") String token) {
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
        Map<String, Object> statistics = userService.getUserStatistics(userId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取用户活动", description = "获取用户活动记录")
    @GetMapping("/me/activities")
    public Result<Page<Map<String, Object>>> getUserActivities(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
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

    /**
     * 修改密码请求DTO
     */
    public static class ChangePasswordRequest {
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;
        
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
        private String newPassword;
        
        // Getters and Setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    /**
     * 重置密码请求DTO
     */
    public static class ResetPasswordRequest {
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;
        
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
        private String newPassword;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    /**
     * 更新头像请求DTO
     */
    public static class UpdateAvatarRequest {
        @NotBlank(message = "头像URL不能为空")
        private String avatarUrl;
        
        // Getters and Setters
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
}

