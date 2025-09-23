package com.xichen.wiki.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户活动实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_activity")
public class UserActivity {

    /**
     * 活动ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 活动类型
     */
    @TableField("activity_type")
    private String activityType;

    /**
     * 资源类型
     */
    @TableField("resource_type")
    private String resourceType;

    /**
     * 资源ID
     */
    @TableField("resource_id")
    private Long resourceId;

    /**
     * 活动时间
     */
    @TableField(value = "activity_time", fill = FieldFill.INSERT)
    private LocalDateTime activityTime;
}
