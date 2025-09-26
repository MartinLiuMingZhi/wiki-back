package com.xichen.wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文档标签关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("document_tags")
public class DocumentTag {

    /**
     * 文档ID
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
