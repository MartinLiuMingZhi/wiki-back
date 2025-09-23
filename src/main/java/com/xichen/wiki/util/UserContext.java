package com.xichen.wiki.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用户上下文工具类
 */
@Slf4j
public class UserContext {

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                Object userId = request.getAttribute("currentUserId");
                if (userId instanceof Long) {
                    return (Long) userId;
                }
            }
        } catch (Exception e) {
            log.warn("获取当前用户ID失败：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                Object username = request.getAttribute("currentUsername");
                if (username instanceof String) {
                    return (String) username;
                }
            }
        } catch (Exception e) {
            log.warn("获取当前用户名失败：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前请求
     */
    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            log.warn("获取当前请求失败：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 检查用户是否已登录
     */
    public static boolean isUserLoggedIn() {
        return getCurrentUserId() != null;
    }
}
