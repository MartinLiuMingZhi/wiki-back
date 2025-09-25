package com.xichen.wiki.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "verification.code")
public class VerificationCodeProperties {
    
    /**
     * 验证码长度
     */
    private int length = 6;
    
    /**
     * 验证码过期时间（分钟）
     */
    private int expireMinutes = 5;
    
    /**
     * 发送频率限制（分钟）
     */
    private int rateLimitMinutes = 1;
}
