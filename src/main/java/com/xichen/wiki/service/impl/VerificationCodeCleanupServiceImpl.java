package com.xichen.wiki.service.impl;

import com.xichen.wiki.mapper.VerificationCodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 验证码清理服务
 */
@Slf4j
@Service
public class VerificationCodeCleanupServiceImpl {
    
    @Autowired
    private VerificationCodeMapper verificationCodeMapper;
    
    /**
     * 每小时清理一次过期的验证码
     * Redis会自动清理，这里主要清理数据库中的过期数据
     */
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void cleanExpiredCodes() {
        try {
            log.info("开始清理过期的验证码...");
            verificationCodeMapper.cleanExpiredCodes();
            log.info("过期验证码清理完成");
        } catch (Exception e) {
            log.error("清理过期验证码失败", e);
        }
    }
    
    /**
     * 每天凌晨2点执行深度清理
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void deepCleanup() {
        try {
            log.info("开始执行深度清理...");
            // 清理7天前的所有验证码记录
            verificationCodeMapper.cleanExpiredCodes();
            log.info("深度清理完成");
        } catch (Exception e) {
            log.error("深度清理失败", e);
        }
    }
}
