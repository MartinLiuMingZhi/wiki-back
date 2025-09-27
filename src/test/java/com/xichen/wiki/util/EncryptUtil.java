package com.xichen.wiki.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * Jasypt加密工具类
 */
public class EncryptUtil {
    
    public static void main(String[] args) {
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
        
        // 加密邮箱地址
        String email = "3108531642@qq.com";
        String encryptedEmail = encryptor.encrypt(email);
        System.out.println("加密后的邮箱地址: " + encryptedEmail);
        
        // 加密邮箱密码
        String password = "utpxkodjemyrdged";
        String encryptedPassword = encryptor.encrypt(password);
        System.out.println("加密后的邮箱密码: " + encryptedPassword);
        
        // 验证解密
        System.out.println("解密邮箱地址: " + encryptor.decrypt(encryptedEmail));
        System.out.println("解密邮箱密码: " + encryptor.decrypt(encryptedPassword));
    }
}
