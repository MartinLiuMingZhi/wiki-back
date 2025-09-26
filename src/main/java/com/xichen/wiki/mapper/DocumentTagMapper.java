package com.xichen.wiki.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xichen.wiki.entity.DocumentTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档标签关联Mapper接口
 */
@Mapper
public interface DocumentTagMapper extends BaseMapper<DocumentTag> {
    
    /**
     * 根据文档ID删除所有关联的标签
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);
    
    /**
     * 根据标签ID删除所有关联的文档
     */
    int deleteByTagId(@Param("tagId") Long tagId);
    
    /**
     * 批量插入文档标签关联
     */
    int batchInsert(@Param("documentTags") List<DocumentTag> documentTags);
}
