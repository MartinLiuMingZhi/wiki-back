package com.xichen.wiki.util;

import com.xichen.wiki.constant.VerificationCodeConstants;

/**
 * Redis键工具类
 * 
 * @author xichen
 * @since 2024-09-25
 */
public final class RedisKeyUtil {
    
    private RedisKeyUtil() {
        // 工具类，禁止实例化
    }
    
    /**
     * 生成验证码Redis键
     * @param email 邮箱地址
     * @param type 验证码类型
     * @return Redis键
     */
    public static String getVerificationCodeKey(String email, String type) {
        return VerificationCodeConstants.REDIS_PREFIX + email + ":" + type;
    }
    
    /**
     * 生成频率限制Redis键
     * @param email 邮箱地址
     * @return Redis键
     */
    public static String getRateLimitKey(String email) {
        return VerificationCodeConstants.REDIS_PREFIX + VerificationCodeConstants.RATE_LIMIT_PREFIX + email;
    }
    
    /**
     * 生成用户会话Redis键
     * @param userId 用户ID
     * @return Redis键
     */
    public static String getUserSessionKey(Long userId) {
        return "user_session:" + userId;
    }
    
    /**
     * 生成用户登录尝试Redis键
     * @param email 邮箱地址
     * @return Redis键
     */
    public static String getLoginAttemptKey(String email) {
        return "login_attempt:" + email;
    }
}
