package com.xichen.wiki;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

/**
 * 加密邮箱测试
 */
public class EncryptEmailTest {
    
    @Test
    public void encryptEmail() {
        // 配置加密器
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        
        config.setPassword("WikiSecretKey2024!@#");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        
        encryptor.setConfig(config);
        
        // 你的真实邮箱和授权码
        String realEmail = "3108531642@qq.com";
        String realPassword = "utpxkodjemyrdged";
        
        // 加密
        String encryptedEmail = encryptor.encrypt(realEmail);
        String encryptedPassword = encryptor.encrypt(realPassword);
        
        System.out.println("=== 加密结果 ===");
        System.out.println("原始邮箱: " + realEmail);
        System.out.println("加密邮箱: " + encryptedEmail);
        System.out.println();
        System.out.println("原始授权码: " + realPassword);
        System.out.println("加密授权码: " + encryptedPassword);
        System.out.println();
        
        // 验证解密
        String decryptedEmail = encryptor.decrypt(encryptedEmail);
        String decryptedPassword = encryptor.decrypt(encryptedPassword);
        
        System.out.println("=== 解密验证 ===");
        System.out.println("邮箱解密: " + decryptedEmail + " (正确: " + realEmail.equals(decryptedEmail) + ")");
        System.out.println("授权码解密: " + decryptedPassword + " (正确: " + realPassword.equals(decryptedPassword) + ")");
        System.out.println();
        
        System.out.println("=== 配置文件更新 ===");
        System.out.println("spring.mail.username=ENC(" + encryptedEmail + ")");
        System.out.println("spring.mail.password=ENC(" + encryptedPassword + ")");
    }
}
