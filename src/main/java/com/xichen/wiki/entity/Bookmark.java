package com.xichen.wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 书签实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bookmarks")
public class Bookmark {

    /**
     * 书签ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 电子书ID
     */
    @TableField("ebook_id")
    private Long ebookId;

    /**
     * 页码
     */
    @TableField("page_number")
    private Integer pageNumber;

    /**
     * 备注
     */
    @TableField("note")
    private String note;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}