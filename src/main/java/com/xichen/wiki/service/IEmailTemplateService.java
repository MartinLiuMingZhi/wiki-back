package com.xichen.wiki.service;

/**
 * 邮件模板服务接口
 * 
 * @author xichen
 * @since 2024-09-25
 */
public interface IEmailTemplateService {
    
    /**
     * 生成验证码HTML邮件内容
     * 
     * @param code 验证码
     * @param type 验证码类型
     * @param email 邮箱地址
     * @param expireMinutes 过期时间（分钟）
     * @return HTML邮件内容
     */
    String generateVerificationCodeHtml(String code, String type, String email, int expireMinutes);
}
