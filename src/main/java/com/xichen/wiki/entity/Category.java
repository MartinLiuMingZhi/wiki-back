package com.xichen.wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = false, exclude = {"children"})
@TableName("categories")
public class Category {

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    @TableField("name")
    private String name;

    /**
     * 父分类ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 分类类型：document-文档分类，ebook-电子书分类
     */
    @TableField("type")
    private String type;

    /**
     * 用户ID，NULL表示公共分类
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

    /**
     * 子分类列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Category> children = new ArrayList<>();
    
    /**
     * 获取子分类列表的不可变副本
     */
    public List<Category> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    /**
     * 设置子分类列表
     */
    public void setChildren(List<Category> children) {
        this.children = children != null ? new ArrayList<>(children) : new ArrayList<>();
    }
}


