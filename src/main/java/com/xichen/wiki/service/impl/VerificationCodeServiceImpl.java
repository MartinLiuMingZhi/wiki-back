package com.xichen.wiki.service.impl;

import com.xichen.wiki.config.VerificationCodeProperties;
import com.xichen.wiki.constant.VerificationCodeConstants;
import com.xichen.wiki.entity.VerificationCode;
import com.xichen.wiki.exception.VerificationCodeException;
import com.xichen.wiki.mapper.VerificationCodeMapper;
import com.xichen.wiki.service.IVerificationCodeService;
import com.xichen.wiki.service.IEmailService;
import com.xichen.wiki.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务类 - 使用Redis + 数据库混合存储
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Slf4j
@Service
public class VerificationCodeServiceImpl implements IVerificationCodeService {
    
    @Autowired
    private VerificationCodeMapper verificationCodeMapper;
    
    @Autowired
    private IEmailService emailService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private VerificationCodeProperties properties;
    
    /**
     * 生成验证码
     */
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < properties.getLength(); i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 发送验证码 - 使用Redis存储
     * @param email 邮箱地址
     * @param type 验证码类型
     * @return 是否发送成功
     */
    public boolean sendVerificationCode(String email, String type) {
        try {
            log.info("开始发送验证码，邮箱: {}, 类型: {}", email, type);
            
            // 检查发送频率限制
            if (isRateLimited(email)) {
                log.warn("发送频率限制，邮箱: {}", email);
                throw new VerificationCodeException("RATE_LIMIT", 
                    String.format(VerificationCodeConstants.RATE_LIMIT_MESSAGE, properties.getRateLimitMinutes()));
            }
            
            // 生成验证码
            String code = generateVerificationCode();
            log.debug("生成验证码成功，邮箱: {}", email);
            
            // 存储到Redis
            storeCodeInRedis(email, type, code);
            
            // 设置发送频率限制
            setRateLimit(email);
            
            // 异步保存到数据库
            saveToDatabaseAsync(email, code, type);
            
            // 发送邮件
            emailService.sendVerificationCode(email, code, type);
            log.info("验证码发送成功，邮箱: {}", email);
            
            return true;
        } catch (Exception e) {
            log.error("发送验证码失败，邮箱: {}, 类型: {}, 错误: {}", email, type, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 验证验证码 - 优先从Redis验证
     * @param email 邮箱地址
     * @param code 验证码
     * @param type 验证码类型
     * @return 是否验证成功
     */
    public boolean verifyCode(String email, String code, String type) {
        try {
            log.info("开始验证验证码，邮箱: {}, 类型: {}", email, type);
            
            // 优先从Redis验证
            if (verifyFromRedis(email, code, type)) {
                log.info("Redis验证成功，邮箱: {}", email);
                return true;
            }
            
            // Redis中没有，尝试从数据库验证（兼容性）
            if (verifyFromDatabase(email, code, type)) {
                log.info("数据库验证成功，邮箱: {}", email);
                return true;
            }
            
            log.warn("验证码验证失败，邮箱: {}, 类型: {}", email, type);
            return false;
            
        } catch (Exception e) {
            log.error("验证验证码异常，邮箱: {}, 类型: {}, 错误: {}", email, type, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 异步保存到数据库
     */
    private void saveToDatabaseAsync(String email, String code, String type) {
        try {
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setEmail(email);
            verificationCode.setCode(code);
            verificationCode.setType(type);
            verificationCode.setUsed(false);
            verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(properties.getExpireMinutes()));
            verificationCode.setCreateTime(LocalDateTime.now());
            verificationCode.setUpdateTime(LocalDateTime.now());
            
            verificationCodeMapper.insert(verificationCode);
        } catch (Exception e) {
            // 数据库保存失败不影响主流程
            log.error("保存验证码到数据库失败，邮箱: {}, 类型: {}, 错误: {}", email, type, e.getMessage(), e);
        }
    }
    
    /**
     * 从数据库验证（兼容性方法）
     */
    private boolean verifyFromDatabase(String email, String code, String type) {
        try {
            VerificationCode verificationCode = verificationCodeMapper.findValidCodeByEmailAndType(email, type);
            
            if (verificationCode == null || !verificationCode.getCode().equals(code)) {
                return false;
            }
            
            // 标记为已使用
            verificationCode.setUsed(true);
            verificationCode.setUpdateTime(LocalDateTime.now());
            verificationCodeMapper.updateById(verificationCode);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 更新数据库状态
     */
    private void updateDatabaseStatus(String email, String code, String type, boolean used) {
        try {
            VerificationCode verificationCode = verificationCodeMapper.findValidCodeByEmailAndType(email, type);
            if (verificationCode != null && verificationCode.getCode().equals(code)) {
                verificationCode.setUsed(used);
                verificationCode.setUpdateTime(LocalDateTime.now());
                verificationCodeMapper.updateById(verificationCode);
            }
        } catch (Exception e) {
            // 数据库更新失败不影响主流程
            e.printStackTrace();
        }
    }
    
    /**
     * 清理过期的验证码（Redis自动清理，数据库手动清理）
     */
    public void cleanExpiredCodes() {
        try {
            verificationCodeMapper.cleanExpiredCodes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取验证码统计信息
     */
    public Object getVerificationStats(String email) {
        try {
            // 从数据库获取统计信息
            return verificationCodeMapper.selectCount(null);
        } catch (Exception e) {
            log.error("获取验证码统计信息失败，邮箱: {}, 错误: {}", email, e.getMessage(), e);
            return null;
        }
    }
    
    // ==================== 私有方法 ====================
    
    /**
     * 检查是否被频率限制
     */
    private boolean isRateLimited(String email) {
        String rateLimitKey = RedisKeyUtil.getRateLimitKey(email);
        return Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey));
    }
    
    /**
     * 存储验证码到Redis
     */
    private void storeCodeInRedis(String email, String type, String code) {
        String redisKey = RedisKeyUtil.getVerificationCodeKey(email, type);
        redisTemplate.opsForValue().set(redisKey, code, properties.getExpireMinutes(), TimeUnit.MINUTES);
        log.debug("验证码已存储到Redis，邮箱: {}, 类型: {}", email, type);
    }
    
    /**
     * 设置发送频率限制
     */
    private void setRateLimit(String email) {
        String rateLimitKey = RedisKeyUtil.getRateLimitKey(email);
        redisTemplate.opsForValue().set(rateLimitKey, "1", properties.getRateLimitMinutes(), TimeUnit.MINUTES);
        log.debug("设置频率限制，邮箱: {}, 限制时间: {}分钟", email, properties.getRateLimitMinutes());
    }
    
    /**
     * 从Redis验证验证码
     */
    private boolean verifyFromRedis(String email, String code, String type) {
        String redisKey = RedisKeyUtil.getVerificationCodeKey(email, type);
        String storedCode = (String) redisTemplate.opsForValue().get(redisKey);
        
        if (storedCode != null && storedCode.equals(code)) {
            // 验证成功，删除Redis中的验证码
            redisTemplate.delete(redisKey);
            log.debug("Redis验证成功，删除验证码，邮箱: {}", email);
            
            // 更新数据库中的验证码状态
            updateDatabaseStatus(email, code, type, true);
            
            return true;
        }
        
        return false;
    }
}