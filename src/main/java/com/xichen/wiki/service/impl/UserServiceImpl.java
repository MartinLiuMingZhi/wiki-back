package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xichen.wiki.entity.User;
import com.xichen.wiki.entity.UserActivity;
import com.xichen.wiki.exception.BusinessException;
import com.xichen.wiki.mapper.UserMapper;
import com.xichen.wiki.mapper.UserActivityMapper;
import com.xichen.wiki.service.UserService;
import com.xichen.wiki.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserActivityMapper userActivityMapper;

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

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException("原密码错误");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        updateById(user);
        log.info("用户密码修改成功：{}", user.getUsername());
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        User user = findByEmail(email);
        if (user == null) {
            throw new BusinessException("邮箱不存在");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        updateById(user);
        log.info("用户密码重置成功：{}", user.getUsername());
    }

    @Override
    public User updateAvatar(Long userId, String avatarUrl) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setAvatarUrl(avatarUrl);
        updateById(user);
        log.info("用户头像更新成功：{}", user.getUsername());
        return user;
    }

    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 这里可以添加更多统计信息
        User user = getById(userId);
        if (user != null) {
            statistics.put("userId", user.getId());
            statistics.put("username", user.getUsername());
            statistics.put("email", user.getEmail());
            statistics.put("status", user.getStatus());
            statistics.put("createdAt", user.getCreatedAt());
            statistics.put("updatedAt", user.getUpdatedAt());
        }
        
        return statistics;
    }

    @Override
    public Page<Map<String, Object>> getUserActivities(Long userId, Integer page, Integer size) {
        Page<UserActivity> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserActivity::getUserId, userId)
                .orderByDesc(UserActivity::getActivityTime);
        
        Page<UserActivity> activities = userActivityMapper.selectPage(pageParam, wrapper);
        
        // 转换为Map格式
        Page<Map<String, Object>> result = new Page<>(page, size);
        result.setTotal(activities.getTotal());
        result.setPages(activities.getPages());
        
        return result;
    }

    @Override
    public void recordUserActivity(Long userId, String activityType, String resourceType, Long resourceId) {
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setActivityType(activityType);
        activity.setResourceType(resourceType);
        activity.setResourceId(resourceId);
        activity.setActivityTime(LocalDateTime.now());
        
        userActivityMapper.insert(activity);
        log.info("用户活动记录成功：用户ID={}, 活动类型={}", userId, activityType);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return findByUsername(username) == null;
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return findByEmail(email) == null;
    }
}

