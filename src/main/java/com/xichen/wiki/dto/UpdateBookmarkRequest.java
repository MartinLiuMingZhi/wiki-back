package com.xichen.wiki.dto;

import lombok.Data;

/**
 * 更新书签请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class UpdateBookmarkRequest {
    
    /**
     * 书签备注
     */
    private String note;
}
