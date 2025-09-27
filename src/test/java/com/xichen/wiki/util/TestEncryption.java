package com.xichen.wiki.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * 测试加密解密
 */
public class TestEncryption {
    
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
        
        // 测试解密现有的加密值
        try {
            String encryptedEmail = "3qzFkg8ZRD+r09ytK3Rf7ojNnGdjAEz5";
            String decryptedEmail = encryptor.decrypt(encryptedEmail);
            System.out.println("解密邮箱地址: " + decryptedEmail);
            
            String encryptedPassword = "7ex0STiNqn5meD9O5J5aAU4TuwoGdvAR";
            String decryptedPassword = encryptor.decrypt(encryptedPassword);
            System.out.println("解密邮箱密码: " + decryptedPassword);
            
            // 重新加密新的值
            String newEmail = "3108531642@qq.com";
            String newPassword = "utpxkodjemyrdged";
            
            String newEncryptedEmail = encryptor.encrypt(newEmail);
            String newEncryptedPassword = encryptor.encrypt(newPassword);
            
            System.out.println("新加密邮箱地址: " + newEncryptedEmail);
            System.out.println("新加密邮箱密码: " + newEncryptedPassword);
            
        } catch (Exception e) {
            System.err.println("加密/解密失败: " + e.getMessage());
        }
    }
}
