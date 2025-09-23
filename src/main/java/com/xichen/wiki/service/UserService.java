package com.xichen.wiki.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xichen.wiki.entity.User;

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
}

