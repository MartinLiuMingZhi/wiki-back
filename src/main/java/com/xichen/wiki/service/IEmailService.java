package com.xichen.wiki.service;

/**
 * 邮件服务接口
 * 
 * @author xichen
 * @since 2024-09-25
 */
public interface IEmailService {
    
    /**
     * 发送验证码邮件（HTML格式）
     * @param toEmail 收件人邮箱
     * @param verificationCode 验证码
     * @param type 验证码类型（注册/登录）
     */
    void sendVerificationCode(String toEmail, String verificationCode, String type);
    
    /**
     * 发送密码重置邮件
     * @param toEmail 收件人邮箱
     * @param resetToken 重置令牌
     */
    void sendPasswordResetEmail(String toEmail, String resetToken);
}
