package com.xichen.wiki.constant;

/**
 * 验证码相关常量
 * 
 * @author xichen
 * @since 2024-09-25
 */
public final class VerificationCodeConstants {
    
    private VerificationCodeConstants() {
        // 工具类，禁止实例化
    }
    
    // ==================== Redis键前缀 ====================
    
    /**
     * 验证码Redis键前缀
     */
    public static final String REDIS_PREFIX = "verification_code:";
    
    /**
     * 频率限制Redis键前缀
     */
    public static final String RATE_LIMIT_PREFIX = "rate_limit:";
    
    // ==================== 验证码类型 ====================
    
    /**
     * 注册验证码类型
     */
    public static final String TYPE_REGISTER = "register";
    
    /**
     * 登录验证码类型
     */
    public static final String TYPE_LOGIN = "login";
    
    /**
     * 重置密码验证码类型
     */
    public static final String TYPE_RESET_PASSWORD = "reset_password";
    
    // ==================== 默认配置 ====================
    
    /**
     * 默认验证码长度
     */
    public static final int DEFAULT_CODE_LENGTH = 6;
    
    /**
     * 默认过期时间（分钟）
     */
    public static final int DEFAULT_EXPIRE_MINUTES = 5;
    
    /**
     * 默认频率限制时间（分钟）
     */
    public static final int DEFAULT_RATE_LIMIT_MINUTES = 1;
    
    // ==================== 错误消息 ====================
    
    /**
     * 发送频率限制错误消息
     */
    public static final String RATE_LIMIT_MESSAGE = "发送过于频繁，请{0}分钟后再试";
    
    /**
     * 验证码无效错误消息
     */
    public static final String INVALID_CODE_MESSAGE = "验证码无效或已过期";
    
    /**
     * 验证码发送失败错误消息
     */
    public static final String SEND_FAILED_MESSAGE = "验证码发送失败";
    
    /**
     * 验证码验证失败错误消息
     */
    public static final String VERIFY_FAILED_MESSAGE = "验证码验证失败";
}
