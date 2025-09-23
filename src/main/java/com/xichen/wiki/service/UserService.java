package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.User;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    User register(String username, String email, String password);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);
    
    /**
     * 更新用户信息
     */
    User updateUserInfo(Long userId, String username, String email, String avatarUrl);
    
    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    void resetPassword(String email, String newPassword);
    
    /**
     * 更新用户头像
     */
    User updateAvatar(Long userId, String avatarUrl);
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics(Long userId);
    
    /**
     * 获取用户活动记录
     */
    Page<Map<String, Object>> getUserActivities(Long userId, Integer page, Integer size);
    
    /**
     * 记录用户活动
     */
    void recordUserActivity(Long userId, String activityType, String resourceType, Long resourceId);
    
    /**
     * 检查用户名是否可用
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * 检查邮箱是否可用
     */
    boolean isEmailAvailable(String email);
}

