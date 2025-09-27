package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 批量删除请求DTO
 * 
 * @author xichen
 * @since 2024-09-25
 */
@Data
public class BatchDeleteRequest {
    
    /**
     * ID列表（必填）
     */
    @NotNull(message = "ID列表不能为空")
    private List<Long> tagIds = new ArrayList<>();
    
    /**
     * 获取标签ID列表的不可变副本
     */
    public List<Long> getTagIds() {
        return Collections.unmodifiableList(tagIds);
    }
    
    /**
     * 设置标签ID列表
     */
    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds != null ? new ArrayList<>(tagIds) : new ArrayList<>();
    }
}
