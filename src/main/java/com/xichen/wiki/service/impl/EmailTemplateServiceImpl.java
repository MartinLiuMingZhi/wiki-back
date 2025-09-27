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
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <title>验证码邮件</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n");
        html.append("        .container { max-width: 600px; margin: 0 auto; padding: 20px; }\n");
        html.append("        .header { background: #667eea; color: white; padding: 20px; text-align: center; }\n");
        html.append("        .content { padding: 20px; background: #f9f9f9; }\n");
        html.append("        .code { font-size: 24px; font-weight: bold; color: #667eea; text-align: center; margin: 20px 0; }\n");
        html.append("        .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <div class=\"header\">\n");
        html.append("            <h1>🔐 验证码邮件</h1>\n");
        html.append("            <p>Wiki知识管理系统</p>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"content\">\n");
        html.append("            <h2>您的验证码</h2>\n");
        html.append("            <p>您正在进行").append(getTypeDisplayName(type)).append("操作，请使用以下验证码完成验证：</p>\n");
        html.append("            <div class=\"code\">").append(code).append("</div>\n");
        html.append("            <p><strong>验证码信息：</strong></p>\n");
        html.append("            <ul>\n");
        html.append("                <li>验证码有效期：").append(expireMinutes).append("分钟</li>\n");
        html.append("                <li>验证码类型：").append(getTypeDisplayName(type)).append("</li>\n");
        html.append("                <li>接收邮箱：").append(email).append("</li>\n");
        html.append("                <li>发送时间：").append(LocalDateTime.now().format(TIME_FORMATTER)).append("</li>\n");
        html.append("            </ul>\n");
        html.append("            <p style=\"color: #e74c3c; font-weight: bold;\">⚠️ 请勿将验证码告知他人，如非本人操作，请忽略此邮件。</p>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"footer\">\n");
        html.append("            <p>Wiki知识管理系统 - 让知识管理更简单、更高效</p>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
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
