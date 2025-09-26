package com.xichen.wiki.service.impl;

import com.xichen.wiki.service.IEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 邮件服务实现类
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Slf4j
@Service
public class EmailServiceImpl implements IEmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailTemplateServiceImpl emailTemplateService;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 发送验证码邮件（HTML格式）
     * @param toEmail 收件人邮箱
     * @param verificationCode 验证码
     * @param type 验证码类型（注册/登录）
     */
    public void sendVerificationCode(String toEmail, String verificationCode, String type) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🔐 Wiki知识管理系统 - " + getTypeDisplayName(type) + "验证码");
            
            // 生成HTML内容
            String htmlContent = emailTemplateService.generateVerificationCodeHtml(
                verificationCode, type, toEmail, 5
            );
            
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            log.info("HTML验证码邮件发送成功，邮箱: {}, 类型: {}", toEmail, type);
            
        } catch (MailException | MessagingException e) {
            log.error("HTML验证码邮件发送失败，邮箱: {}, 类型: {}, 错误: {}", toEmail, type, e.getMessage(), e);
            // 降级到简单文本邮件
            sendSimpleVerificationCode(toEmail, verificationCode, type);
        }
    }
    
    /**
     * 发送简单文本验证码邮件（备用方案）
     */
    private void sendSimpleVerificationCode(String toEmail, String verificationCode, String type) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Wiki知识管理系统 - " + getTypeDisplayName(type) + "验证码");
            
            String content = buildVerificationEmailContent(verificationCode, type);
            message.setText(content);
            
            mailSender.send(message);
            log.info("简单文本验证码邮件发送成功，邮箱: {}, 类型: {}", toEmail, type);
            
        } catch (MailException e) {
            log.error("简单文本验证码邮件发送失败，邮箱: {}, 类型: {}, 错误: {}", toEmail, type, e.getMessage(), e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建验证码邮件内容
     */
    private String buildVerificationEmailContent(String verificationCode, String type) {
        return String.format("""
            亲爱的用户，
            
            您正在进行%s操作，验证码为：%s
            
            验证码有效期为5分钟，请及时使用。
            
            如果这不是您的操作，请忽略此邮件。
            
            此邮件由系统自动发送，请勿回复。
            
            ---
            Wiki知识管理系统
            """, type, verificationCode);
    }
    
    /**
     * 发送密码重置邮件
     * @param toEmail 收件人邮箱
     * @param resetToken 重置令牌
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Wiki知识管理系统 - 密码重置");
        
        String content = buildPasswordResetEmailContent(resetToken);
        message.setText(content);
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建密码重置邮件内容
     */
    private String buildPasswordResetEmailContent(String resetToken) {
        return String.format("""
            亲爱的用户，
            
            您申请了密码重置，重置令牌为：%s
            
            请点击以下链接完成密码重置：
            http://localhost:8080/api/auth/reset-password?token=%s
            
            重置令牌有效期为30分钟，请及时使用。
            
            如果这不是您的操作，请忽略此邮件。
            
            此邮件由系统自动发送，请勿回复。
            
            ---
            Wiki知识管理系统
            """, resetToken, resetToken);
    }
    
    /**
     * 获取验证码类型的显示名称
     */
    private String getTypeDisplayName(String type) {
        return switch (type) {
            case "register" -> "用户注册";
            case "login" -> "用户登录";
            case "reset_password" -> "重置密码";
            default -> "身份验证";
        };
    }
}
