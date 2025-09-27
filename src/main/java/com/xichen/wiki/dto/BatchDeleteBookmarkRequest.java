package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
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
    private List<Long> bookmarkIds = new ArrayList<>();
    
    /**
     * 获取书签ID列表的不可变副本
     */
    public List<Long> getBookmarkIds() {
        return Collections.unmodifiableList(bookmarkIds);
    }
    
    /**
     * 设置书签ID列表
     */
    public void setBookmarkIds(List<Long> bookmarkIds) {
        this.bookmarkIds = bookmarkIds != null ? new ArrayList<>(bookmarkIds) : new ArrayList<>();
    }
}
