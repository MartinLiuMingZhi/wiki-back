package com.xichen.wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 电子书实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ebooks")
public class Ebook {

    /**
     * 电子书ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 电子书标题
     */
    @TableField("title")
    private String title;

    /**
     * 作者
     */
    @TableField("author")
    private String author;

    /**
     * 文件存储键
     */
    @TableField("file_key")
    private String fileKey;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 封面图片存储键
     */
    @TableField("cover_key")
    private String coverKey;

    /**
     * 页数
     */
    @TableField("page_count")
    private Integer pageCount;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 是否收藏
     */
    @TableField("is_favorite")
    private Boolean isFavorite;

    /**
     * 下载次数
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * 查看次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 上传日期
     */
    @TableField(value = "upload_date", fill = FieldFill.INSERT)
    private LocalDateTime uploadDate;

    /**
     * 最后阅读日期
     */
    @TableField("last_read_date")
    private LocalDateTime lastReadDate;

    /**
     * 最后阅读页数
     */
    @TableField("last_read_page")
    private Integer lastReadPage;

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

