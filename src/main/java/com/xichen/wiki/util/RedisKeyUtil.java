package com.xichen.wiki.util;

import com.xichen.wiki.constant.VerificationCodeConstants;

/**
 * Redis键工具类
 * 
 * @author xichen
 * @since 2024-09-25
 */
public final class RedisKeyUtil {
    
    private RedisKeyUtil() {
        // 工具类，禁止实例化
    }
    
    /**
     * 生成验证码Redis键
     * @param email 邮箱地址
     * @param type 验证码类型
     * @return Redis键
     */
    public static String getVerificationCodeKey(String email, String type) {
        return VerificationCodeConstants.REDIS_PREFIX + email + ":" + type;
    }
    
    /**
     * 生成频率限制Redis键
     * @param email 邮箱地址
     * @return Redis键
     */
    public static String getRateLimitKey(String email) {
        return VerificationCodeConstants.REDIS_PREFIX + VerificationCodeConstants.RATE_LIMIT_PREFIX + email;
    }
    
    /**
     * 生成用户会话Redis键
     * @param userId 用户ID
     * @return Redis键
     */
    public static String getUserSessionKey(Long userId) {
        return "user_session:" + userId;
    }
    
    /**
     * 生成用户登录尝试Redis键
     * @param email 邮箱地址
     * @return Redis键
     */
    public static String getLoginAttemptKey(String email) {
        return "login_attempt:" + email;
    }
    
    // ==================== 电子书相关Redis键 ====================
    
    /**
     * 生成电子书收藏Redis键
     * @param userId 用户ID
     * @return Redis键
     */
    public static String getEbookFavoriteKey(Long userId) {
        return "ebook:favorite:" + userId;
    }
    
    /**
     * 生成电子书阅读进度Redis键
     * @param userId 用户ID
     * @param ebookId 电子书ID
     * @return Redis键
     */
    public static String getEbookReadingProgressKey(Long userId, Long ebookId) {
        return "ebook:reading_progress:" + userId + ":" + ebookId;
    }
    
    /**
     * 生成电子书查看次数Redis键
     * @param ebookId 电子书ID
     * @return Redis键
     */
    public static String getEbookViewCountKey(Long ebookId) {
        return "ebook:view_count:" + ebookId;
    }
    
    // ==================== 搜索相关Redis键 ====================
    
    /**
     * 生成搜索历史Redis键
     * @param userId 用户ID
     * @return Redis键
     */
    public static String getSearchHistoryKey(Long userId) {
        return "search:history:" + userId;
    }
    
    /**
     * 生成热门搜索词Redis键
     * @return Redis键
     */
    public static String getPopularSearchTermsKey() {
        return "search:popular_terms";
    }
    
    // ==================== 标签相关Redis键 ====================
    
    /**
     * 生成标签使用次数Redis键
     * @param tagId 标签ID
     * @return Redis键
     */
    public static String getTagUsageCountKey(Long tagId) {
        return "tag:usage_count:" + tagId;
    }
    
    /**
     * 生成标签统计Redis键
     * @param userId 用户ID
     * @return Redis键
     */
    public static String getTagStatisticsKey(Long userId) {
        return "tag:statistics:" + userId;
    }
    
    /**
     * 生成热门标签Redis键
     * @return Redis键
     */
    public static String getPopularTagsKey() {
        return "tag:popular";
    }
    
    // ==================== 书签相关Redis键 ====================
    
    /**
     * 生成书签统计Redis键
     * @param userId 用户ID
     * @return Redis键
     */
    public static String getBookmarkStatisticsKey(Long userId) {
        return "bookmark:statistics:" + userId;
    }
    
    // ==================== 分类相关Redis键 ====================
    
    /**
     * 生成分类统计Redis键
     * @param userId 用户ID
     * @return Redis键
     */
    public static String getCategoryStatisticsKey(Long userId) {
        return "category:statistics:" + userId;
    }
}
