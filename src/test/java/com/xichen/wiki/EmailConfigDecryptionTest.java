package com.xichen.wiki;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 邮件配置解密测试
 * 验证Jasypt是否正确解密了邮件配置
 * 
 * @author xichen
 * @since 2024-09-27
 */
@SpringBootTest
public class EmailConfigDecryptionTest {
    
    @Value("${spring.mail.username}")
    private String mailUsername;
    
    @Value("${spring.mail.password}")
    private String mailPassword;
    
    @Value("${spring.mail.host}")
    private String mailHost;
    
    @Value("${spring.mail.port}")
    private int mailPort;
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * 测试配置解密
     */
    @Test
    public void testEmailConfigDecryption() {
        System.out.println("=== 邮件配置解密测试 ===");
        System.out.println("SMTP Host: " + mailHost);
        System.out.println("SMTP Port: " + mailPort);
        System.out.println("Username: " + mailUsername);
        System.out.println("Password: " + mailPassword);
        System.out.println("Password Length: " + mailPassword.length());
        
        // 验证解密是否成功
        boolean isDecrypted = mailUsername != null && 
                             mailPassword != null && 
                             !mailUsername.isEmpty() && 
                             !mailPassword.isEmpty();
        
        System.out.println("解密状态: " + (isDecrypted ? "✅ 成功" : "❌ 失败"));
        
        // 验证邮件发送器是否正常创建
        boolean mailSenderReady = mailSender != null;
        System.out.println("邮件发送器: " + (mailSenderReady ? "✅ 已创建" : "❌ 创建失败"));
        
        // 输出解密后的配置（用于调试）
        System.out.println("\n=== 解密后的配置 ===");
        System.out.println("邮箱地址: " + mailUsername);
        System.out.println("授权码: " + mailPassword);
        System.out.println("授权码长度: " + mailPassword.length() + " 位");
        
        // 验证授权码格式（QQ邮箱授权码通常是16位）
        boolean isValidAuthCode = mailPassword.length() == 16 && 
                                 mailPassword.matches("[a-zA-Z0-9]+");
        System.out.println("授权码格式: " + (isValidAuthCode ? "✅ 正确" : "❌ 可能不正确"));
    }
}
