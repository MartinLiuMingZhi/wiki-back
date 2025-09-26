package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量删除书签请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class BatchDeleteBookmarkRequest {
    
    /**
     * 书签ID列表（必填）
     */
    @NotNull(message = "书签ID列表不能为空")
    private List<Long> bookmarkIds;
}
