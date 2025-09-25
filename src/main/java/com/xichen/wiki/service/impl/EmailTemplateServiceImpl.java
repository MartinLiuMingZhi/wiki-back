package com.xichen.wiki.service.impl;

import com.xichen.wiki.service.IEmailTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 邮件模板服务实现类
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Slf4j
@Service
public class EmailTemplateServiceImpl implements IEmailTemplateService {
    
    private static final String TEMPLATE_PATH = "templates/verification-code-email.html";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 生成验证码HTML邮件内容
     * 
     * @param code 验证码
     * @param type 验证码类型
     * @param email 邮箱地址
     * @param expireMinutes 过期时间（分钟）
     * @return HTML邮件内容
     */
    public String generateVerificationCodeHtml(String code, String type, String email, int expireMinutes) {
        try {
            // 读取HTML模板
            ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
            String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            
            // 替换模板变量
            String html = template
                .replace("{code}", code)
                .replace("{type}", getTypeDisplayName(type))
                .replace("{email}", email)
                .replace("{expireMinutes}", String.valueOf(expireMinutes))
                .replace("{sendTime}", LocalDateTime.now().format(TIME_FORMATTER));
            
            log.debug("生成验证码HTML邮件成功，邮箱: {}, 类型: {}", email, type);
            return html;
            
        } catch (IOException e) {
            log.error("读取邮件模板失败: {}", e.getMessage(), e);
            // 返回简单的HTML作为备用
            return generateSimpleHtml(code, type, email, expireMinutes);
        }
    }
    
    /**
     * 生成简单的HTML邮件内容（备用方案）
     */
    private String generateSimpleHtml(String code, String type, String email, int expireMinutes) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>验证码邮件</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #667eea; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background: #f9f9f9; }
                    .code { font-size: 24px; font-weight: bold; color: #667eea; text-align: center; margin: 20px 0; }
                    .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 验证码邮件</h1>
                        <p>Wiki知识管理系统</p>
                    </div>
                    <div class="content">
                        <h2>您的验证码</h2>
                        <p>您正在进行%s操作，请使用以下验证码完成验证：</p>
                        <div class="code">%s</div>
                        <p><strong>验证码信息：</strong></p>
                        <ul>
                            <li>验证码有效期：%d分钟</li>
                            <li>验证码类型：%s</li>
                            <li>接收邮箱：%s</li>
                            <li>发送时间：%s</li>
                        </ul>
                        <p style="color: #e74c3c; font-weight: bold;">⚠️ 请勿将验证码告知他人，如非本人操作，请忽略此邮件。</p>
                    </div>
                    <div class="footer">
                        <p>Wiki知识管理系统 - 让知识管理更简单、更高效</p>
                    </div>
                </div>
            </body>
            </html>
            """, 
            getTypeDisplayName(type), 
            code, 
            expireMinutes, 
            getTypeDisplayName(type), 
            email, 
            LocalDateTime.now().format(TIME_FORMATTER)
        );
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
