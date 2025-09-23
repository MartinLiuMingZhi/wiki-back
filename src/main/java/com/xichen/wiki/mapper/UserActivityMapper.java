package com.xichen.wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xichen.wiki.entity.UserActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户活动Mapper接口
 */
@Mapper
public interface UserActivityMapper extends BaseMapper<UserActivity> {
}
