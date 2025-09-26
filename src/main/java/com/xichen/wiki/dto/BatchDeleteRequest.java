package com.xichen.wiki.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
    private List<Long> tagIds;
}
