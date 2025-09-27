package com.xichen.wiki;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 邮件配置测试类
 * 用于验证QQ邮箱SMTP配置是否正确
 * 
 * @author xichen
 * @since 2024-09-27
 */
@SpringBootTest
public class EmailConfigTest {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * 测试邮件连接和发送
     * 注意：此测试会实际发送邮件，请确保配置正确
     */
    @Test
    public void testEmailConnection() {
        try {
            System.out.println("开始测试邮件配置...");
            
            // 创建测试邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("你的QQ邮箱@qq.com"); // 请替换为你的实际邮箱
            message.setTo("测试邮箱@qq.com"); // 请替换为测试邮箱
            message.setSubject("Wiki系统邮件配置测试");
            message.setText("""
                这是一封来自Wiki知识管理系统的测试邮件。
                
                如果您收到此邮件，说明邮件配置已正确！
                
                测试时间：%s
                
                ---
                Wiki知识管理系统
                """.formatted(java.time.LocalDateTime.now()));
            
            // 发送邮件
            mailSender.send(message);
            System.out.println("✅ 邮件发送成功！请检查收件箱。");
            
        } catch (Exception e) {
            System.err.println("❌ 邮件发送失败：" + e.getMessage());
            e.printStackTrace();
            
            // 提供详细的错误分析
            analyzeEmailError(e);
        }
    }
    
    /**
     * 分析邮件错误并提供解决建议
     */
    private void analyzeEmailError(Exception e) {
        String errorMessage = e.getMessage().toLowerCase();
        
        System.out.println("\n🔍 错误分析：");
        
        if (errorMessage.contains("authentication failed") || errorMessage.contains("535")) {
            System.out.println("❌ 认证失败 - 可能的原因：");
            System.out.println("   1. 邮箱地址或授权码错误");
            System.out.println("   2. 未使用授权码（使用了QQ密码）");
            System.out.println("   3. 授权码已过期");
            System.out.println("   4. QQ邮箱SMTP服务未开启");
            System.out.println("\n💡 解决方案：");
            System.out.println("   1. 登录QQ邮箱，开启SMTP服务");
            System.out.println("   2. 获取新的授权码");
            System.out.println("   3. 更新配置文件中的密码");
        } else if (errorMessage.contains("connection") || errorMessage.contains("timeout")) {
            System.out.println("❌ 连接问题 - 可能的原因：");
            System.out.println("   1. 网络连接问题");
            System.out.println("   2. 防火墙阻止");
            System.out.println("   3. SMTP服务器地址或端口错误");
            System.out.println("\n💡 解决方案：");
            System.out.println("   1. 检查网络连接");
            System.out.println("   2. 尝试使用端口465（SSL）或587（TLS）");
            System.out.println("   3. 检查防火墙设置");
        } else if (errorMessage.contains("ssl") || errorMessage.contains("tls")) {
            System.out.println("❌ SSL/TLS问题 - 可能的原因：");
            System.out.println("   1. SSL配置不正确");
            System.out.println("   2. 证书验证失败");
            System.out.println("\n💡 解决方案：");
            System.out.println("   1. 检查SSL配置");
            System.out.println("   2. 尝试禁用SSL验证（仅测试环境）");
        } else {
            System.out.println("❌ 其他错误：" + e.getMessage());
            System.out.println("\n💡 建议：");
            System.out.println("   1. 检查配置文件格式");
            System.out.println("   2. 确认所有必需属性都已设置");
            System.out.println("   3. 查看详细错误日志");
        }
        
        System.out.println("\n📖 详细配置指南请查看：QQ_EMAIL_SETUP_GUIDE.md");
    }
}
