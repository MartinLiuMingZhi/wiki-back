package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xichen.wiki.entity.User;
import com.xichen.wiki.exception.BusinessException;
import com.xichen.wiki.mapper.UserMapper;
import com.xichen.wiki.service.UserService;
import com.xichen.wiki.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public User register(String username, String email, String password) {
        // 检查用户名是否已存在
        if (findByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (findByEmail(email) != null) {
            throw new BusinessException("邮箱已被注册");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus(1);
        
        save(user);
        log.info("用户注册成功：{}", username);
        return user;
    }

    @Override
    public String login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账户已被禁用");
        }
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("用户登录成功：{}", username);
        return token;
    }

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public User findByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return getOne(wrapper);
    }

    @Override
    public User updateUserInfo(Long userId, String username, String email, String avatarUrl) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查用户名是否被其他用户使用
        if (username != null && !username.equals(user.getUsername())) {
            User existingUser = findByUsername(username);
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new BusinessException("用户名已被使用");
            }
            user.setUsername(username);
        }
        
        // 检查邮箱是否被其他用户使用
        if (email != null && !email.equals(user.getEmail())) {
            User existingUser = findByEmail(email);
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new BusinessException("邮箱已被使用");
            }
            user.setEmail(email);
        }
        
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        
        updateById(user);
        log.info("用户信息更新成功：{}", user.getUsername());
        return user;
    }
}

