package com.xichen.wiki.controller;

import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.UserUpdateRequest;
import com.xichen.wiki.entity.User;
import com.xichen.wiki.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
    public Result<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
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
    public Result<Map<String, Object>> updateCurrentUser(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserUpdateRequest request) {
        
        // 这里应该从token中解析用户ID，暂时使用固定值
        Long userId = 1L; // TODO: 从JWT token中解析用户ID
        
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
}

