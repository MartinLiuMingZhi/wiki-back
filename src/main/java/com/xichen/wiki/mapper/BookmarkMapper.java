package com.xichen.wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xichen.wiki.entity.Bookmark;
import org.apache.ibatis.annotations.Mapper;

/**
 * 书签Mapper接口
 */
@Mapper
public interface BookmarkMapper extends BaseMapper<Bookmark> {
}

