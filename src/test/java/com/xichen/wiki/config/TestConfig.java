package com.xichen.wiki.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * 测试配置类
 * 用于解决测试环境中的配置问题
 */
@TestConfiguration
@Profile("test")
public class TestConfig {
    
    /**
     * 测试用的验证码配置
     */
    @Bean
    @Primary
    public VerificationCodeProperties testVerificationCodeProperties() {
        VerificationCodeProperties properties = new VerificationCodeProperties();
        properties.setLength(6);
        properties.setExpireMinutes(5);
        properties.setRateLimitMinutes(1);
        return properties;
    }
}
