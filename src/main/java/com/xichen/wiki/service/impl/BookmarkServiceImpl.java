package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xichen.wiki.entity.Bookmark;
import com.xichen.wiki.exception.BusinessException;
import com.xichen.wiki.mapper.BookmarkMapper;
import com.xichen.wiki.service.BookmarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 书签服务实现类
 */
@Slf4j
@Service
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {

    @Override
    public Bookmark createBookmark(Long userId, Long ebookId, Integer pageNumber, String note) {
        // 检查是否已存在书签
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookmark::getEbookId, ebookId)
                .eq(Bookmark::getUserId, userId);
        
        if (count(wrapper) > 0) {
            throw new BusinessException("该电子书已存在书签");
        }
        
        Bookmark bookmark = new Bookmark();
        bookmark.setEbookId(ebookId);
        bookmark.setUserId(userId);
        bookmark.setPageNumber(pageNumber);
        bookmark.setNote(note);
        
        save(bookmark);
        log.info("书签创建成功：电子书ID={}", ebookId);
        return bookmark;
    }

    @Override
    public Bookmark updateBookmark(Long bookmarkId, Long userId, String note) {
        Bookmark bookmark = getById(bookmarkId);
        if (bookmark == null) {
            throw new BusinessException("书签不存在");
        }
        
        if (!bookmark.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作此书签");
        }
        
        bookmark.setNote(note);
        updateById(bookmark);
        log.info("书签更新成功：ID={}", bookmarkId);
        return bookmark;
    }

    @Override
    public List<Bookmark> getEbookBookmarks(Long ebookId, Long userId) {
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookmark::getEbookId, ebookId)
                .eq(Bookmark::getUserId, userId)
                .orderByAsc(Bookmark::getPageNumber);
        
        return list(wrapper);
    }

    @Override
    public Page<Bookmark> getUserBookmarks(Long userId, Integer page, Integer size, Long ebookId) {
        Page<Bookmark> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Bookmark::getUserId, userId);
        
        if (ebookId != null) {
            wrapper.eq(Bookmark::getEbookId, ebookId);
        }
        
        wrapper.orderByDesc(Bookmark::getCreatedAt);
        
        return page(pageParam, wrapper);
    }

    @Override
    public boolean deleteBookmark(Long bookmarkId, Long userId) {
        Bookmark bookmark = getById(bookmarkId);
        if (bookmark == null) {
            throw new BusinessException("书签不存在");
        }
        
        if (!bookmark.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此书签");
        }
        
        removeById(bookmarkId);
        log.info("书签删除成功：ID={}", bookmarkId);
        return true;
    }

    @Override
    public Bookmark getBookmarkDetail(Long bookmarkId, Long userId) {
        Bookmark bookmark = getById(bookmarkId);
        if (bookmark == null) {
            throw new BusinessException("书签不存在");
        }
        
        if (!bookmark.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看此书签");
        }
        
        return bookmark;
    }

    @Override
    public void deleteBookmarks(List<Long> bookmarkIds, Long userId) {
        if (bookmarkIds == null || bookmarkIds.isEmpty()) {
            return;
        }
        
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Bookmark::getId, bookmarkIds)
                .eq(Bookmark::getUserId, userId);
        
        remove(wrapper);
        log.info("批量删除书签成功：数量={}", bookmarkIds.size());
    }

    @Override
    public boolean isBookmarked(Long userId, Long documentId) {
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookmark::getUserId, userId)
                .eq(Bookmark::getEbookId, documentId);
        return count(wrapper) > 0;
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Bookmark> getUserBookmarks(Long userId, Integer page, Integer size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Bookmark> pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookmark::getUserId, userId)
                .orderByDesc(Bookmark::getCreatedAt);
        
        return page(pageParam, wrapper);
    }

    @Override
    public boolean batchDeleteBookmarks(Long userId, Long[] bookmarkIds) {
        if (bookmarkIds == null || bookmarkIds.length == 0) {
            return false;
        }
        
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Bookmark::getId, bookmarkIds)
                .eq(Bookmark::getUserId, userId);
        
        remove(wrapper);
        log.info("批量删除书签成功：数量={}", bookmarkIds.length);
        return true;
    }
}
