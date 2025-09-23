package com.xichen.wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xichen.wiki.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper接口
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}

