package com.xichen.wiki.service;

/**
 * 验证码服务接口
 * 
 * @author xichen
 * @since 2024-09-25
 */
public interface IVerificationCodeService {
    
    /**
     * 生成验证码
     * @return 验证码字符串
     */
    String generateVerificationCode();
    
    /**
     * 发送验证码
     * @param email 邮箱地址
     * @param type 验证码类型
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String email, String type);
    
    /**
     * 验证验证码
     * @param email 邮箱地址
     * @param code 验证码
     * @param type 验证码类型
     * @return 是否验证成功
     */
    boolean verifyCode(String email, String code, String type);
    
    /**
     * 清理过期的验证码
     */
    void cleanExpiredCodes();
    
    /**
     * 获取验证码统计信息
     * @param email 邮箱地址
     * @return 统计信息
     */
    Object getVerificationStats(String email);
}
