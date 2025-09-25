package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xichen.wiki.entity.Tag;
import com.xichen.wiki.exception.BusinessException;
import com.xichen.wiki.mapper.TagMapper;
import com.xichen.wiki.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 标签服务实现类
 */
@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TAG_USAGE_COUNT_KEY = "tag:usage_count:";

    @Override
    public Tag createTag(Long userId, String name, String description) {
        // 检查标签名称是否已存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, name)
                .eq(Tag::getUserId, userId);
        
        if (count(wrapper) > 0) {
            throw new BusinessException("标签名称已存在");
        }
        
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        tag.setUserId(userId);
        tag.setUsageCount(0L);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        
        save(tag);
        log.info("标签创建成功：用户ID={}, 标签名={}", userId, name);
        return tag;
    }

    @Override
    public Tag updateTag(Long tagId, Long userId, String name, String description) {
        Tag tag = getById(tagId);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        
        if (!tag.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此标签");
        }
        
        // 检查新名称是否与其他标签冲突
        if (!name.equals(tag.getName())) {
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getName, name)
                    .eq(Tag::getUserId, userId)
                    .ne(Tag::getId, tagId);
            
            if (count(wrapper) > 0) {
                throw new BusinessException("标签名称已存在");
            }
        }
        
        tag.setName(name);
        tag.setDescription(description);
        tag.setUpdatedAt(LocalDateTime.now());
        
        updateById(tag);
        log.info("标签更新成功：ID={}, 标签名={}", tagId, name);
        return tag;
    }

    @Override
    public Page<Tag> getUserTags(Long userId, Integer page, Integer size, String keyword) {
        Page<Tag> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Tag::getUserId, userId);
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Tag::getName, keyword)
                    .or().like(Tag::getDescription, keyword));
        }
        
        wrapper.orderByDesc(Tag::getUsageCount, Tag::getCreatedAt);
        return page(pageParam, wrapper);
    }

    @Override
    public Page<Tag> getPublicTags(Integer page, Integer size, String keyword) {
        Page<Tag> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        
        // 公共标签：使用次数大于0的标签
        wrapper.gt(Tag::getUsageCount, 0);
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Tag::getName, keyword)
                    .or().like(Tag::getDescription, keyword));
        }
        
        wrapper.orderByDesc(Tag::getUsageCount, Tag::getCreatedAt);
        return page(pageParam, wrapper);
    }

    @Override
    public List<Tag> getPopularTags(Integer limit) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Tag::getUsageCount, 0)
               .orderByDesc(Tag::getUsageCount)
               .last("LIMIT " + limit);
        
        return list(wrapper);
    }

    @Override
    public boolean deleteTag(Long tagId, Long userId) {
        Tag tag = getById(tagId);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        
        if (!tag.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此标签");
        }
        
        // 检查标签是否被使用
        if (tag.getUsageCount() > 0) {
            throw new BusinessException("标签正在被使用，无法删除");
        }
        
        removeById(tagId);
        
        // 清理相关缓存
        redisTemplate.delete(TAG_USAGE_COUNT_KEY + tagId);
        
        log.info("标签删除成功：ID={}, 用户ID={}", tagId, userId);
        return true;
    }

    @Override
    public Map<String, Object> getTagDetail(Long tagId, Long userId) {
        Tag tag = getById(tagId);
        if (tag == null) {
            return null;
        }
        
        // 检查权限：用户只能查看自己的标签或公共标签
        if (!tag.getUserId().equals(userId) && tag.getUsageCount() == 0) {
            throw new BusinessException("无权限访问此标签");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", tag.getId());
        result.put("name", tag.getName());
        result.put("description", tag.getDescription());
        result.put("usageCount", tag.getUsageCount());
        result.put("userId", tag.getUserId());
        result.put("createdAt", tag.getCreatedAt());
        result.put("updatedAt", tag.getUpdatedAt());
        return result;
    }

    @Override
    public List<Tag> searchTags(String keyword, Long userId, Integer limit) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        
        // 搜索用户自己的标签和公共标签
        wrapper.and(w -> w.eq(Tag::getUserId, userId)
                .or().gt(Tag::getUsageCount, 0));
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Tag::getName, keyword)
                    .or().like(Tag::getDescription, keyword));
        }
        
        wrapper.orderByDesc(Tag::getUsageCount, Tag::getCreatedAt)
               .last("LIMIT " + limit);
        
        return list(wrapper);
    }

    @Override
    public Integer getTagUsageCount(Long tagId) {
        // 先从Redis获取
        String key = TAG_USAGE_COUNT_KEY + tagId;
        Object count = redisTemplate.opsForValue().get(key);
        
        if (count != null) {
            return Integer.valueOf(count.toString());
        }
        
        // 从数据库获取
        Tag tag = getById(tagId);
        if (tag != null) {
            Long usageCount = tag.getUsageCount();
            // 缓存到Redis
            redisTemplate.opsForValue().set(key, usageCount, 1, TimeUnit.HOURS);
            return usageCount.intValue();
        }
        
        return 0;
    }

    @Override
    public boolean deleteTags(List<Long> tagIds, Long userId) {
        for (Long tagId : tagIds) {
            deleteTag(tagId, userId);
        }
        log.info("批量删除标签成功：用户ID={}, 标签IDs={}", userId, tagIds);
        return true;
    }

    /**
     * 增加标签使用次数
     */
    public void incrementTagUsage(Long tagId) {
        String key = TAG_USAGE_COUNT_KEY + tagId;
        redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
        
        // 异步更新数据库
        // 这里可以添加定时任务来同步Redis数据到数据库
        log.debug("标签使用次数增加：ID={}", tagId);
    }

    /**
     * 减少标签使用次数
     */
    public void decrementTagUsage(Long tagId) {
        String key = TAG_USAGE_COUNT_KEY + tagId;
        redisTemplate.opsForValue().increment(key, -1);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
        
        log.debug("标签使用次数减少：ID={}", tagId);
    }
}