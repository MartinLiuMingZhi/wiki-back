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
     * 文件URL
     */
    @TableField("file_url")
    private String fileUrl;

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
     * 分类名称
     */
    @TableField("category")
    private String category;

    /**
     * 是否公开
     */
    @TableField("is_public")
    private Boolean isPublic;

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
     * 总页数（已废弃，使用pageCount代替）
     * @deprecated 使用 {@link #getPageCount()} 代替
     */
    @Deprecated
    @TableField("total_pages")
    private Integer totalPages;

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

    /**
     * 获取总页数
     */
    public Integer getTotalPages() {
        return pageCount != null ? pageCount : 0;
    }

    /**
     * 获取当前页数
     */
    public Integer getCurrentPage() {
        return lastReadPage != null ? lastReadPage : 0;
    }

    /**
     * 获取阅读进度百分比
     */
    public Double getReadingProgress() {
        if (pageCount == null || pageCount == 0) {
            return 0.0;
        }
        if (lastReadPage == null) {
            return 0.0;
        }
        return Math.min(100.0, (double) lastReadPage / pageCount * 100);
    }
}

