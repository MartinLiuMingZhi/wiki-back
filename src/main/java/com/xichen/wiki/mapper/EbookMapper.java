package com.xichen.wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xichen.wiki.entity.Ebook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电子书Mapper接口
 */
@Mapper
public interface EbookMapper extends BaseMapper<Ebook> {
}

