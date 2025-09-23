package com.xichen.wiki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xichen.wiki.entity.*;
import com.xichen.wiki.mapper.*;
import com.xichen.wiki.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final DocumentMapper documentMapper;
    private final EbookMapper ebookMapper;
    private final CategoryMapper categoryMapper;
    private final BookmarkMapper bookmarkMapper;
    private final UserActivityMapper userActivityMapper;

    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 文档统计
        LambdaQueryWrapper<Document> docWrapper = new LambdaQueryWrapper<>();
        docWrapper.eq(Document::getUserId, userId);
        long documentCount = documentMapper.selectCount(docWrapper);
        
        // 电子书统计
        LambdaQueryWrapper<Ebook> ebookWrapper = new LambdaQueryWrapper<>();
        ebookWrapper.eq(Ebook::getUserId, userId);
        long ebookCount = ebookMapper.selectCount(ebookWrapper);
        
        // 书签统计
        LambdaQueryWrapper<Bookmark> bookmarkWrapper = new LambdaQueryWrapper<>();
        bookmarkWrapper.eq(Bookmark::getUserId, userId);
        long bookmarkCount = bookmarkMapper.selectCount(bookmarkWrapper);
        
        // 分类统计
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(Category::getUserId, userId);
        long categoryCount = categoryMapper.selectCount(categoryWrapper);
        
        statistics.put("documentCount", documentCount);
        statistics.put("ebookCount", ebookCount);
        statistics.put("bookmarkCount", bookmarkCount);
        statistics.put("categoryCount", categoryCount);
        statistics.put("totalCount", documentCount + ebookCount);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getDocumentStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getUserId, userId);
        
        // 总文档数
        long totalCount = documentMapper.selectCount(wrapper);
        
        // 收藏文档数
        wrapper.eq(Document::getIsFavorite, true);
        long favoriteCount = documentMapper.selectCount(wrapper);
        
        // 重置条件
        wrapper.clear();
        wrapper.eq(Document::getUserId, userId);
        
        // 本月新增文档数
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        wrapper.ge(Document::getCreatedAt, startOfMonth);
        long monthlyNewCount = documentMapper.selectCount(wrapper);
        
        statistics.put("totalCount", totalCount);
        statistics.put("favoriteCount", favoriteCount);
        statistics.put("monthlyNewCount", monthlyNewCount);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getEbookStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ebook::getUserId, userId);
        
        // 总电子书数
        long totalCount = ebookMapper.selectCount(wrapper);
        
        // 收藏电子书数
        wrapper.eq(Ebook::getIsFavorite, true);
        long favoriteCount = ebookMapper.selectCount(wrapper);
        
        // 重置条件
        wrapper.clear();
        wrapper.eq(Ebook::getUserId, userId);
        
        // 总文件大小
        List<Ebook> ebooks = ebookMapper.selectList(wrapper);
        long totalFileSize = ebooks.stream().mapToLong(Ebook::getFileSize).sum();
        
        // 总页数
        int totalPages = ebooks.stream().mapToInt(Ebook::getPageCount).sum();
        
        // 本月新增电子书数
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        wrapper.ge(Ebook::getCreatedAt, startOfMonth);
        long monthlyNewCount = ebookMapper.selectCount(wrapper);
        
        statistics.put("totalCount", totalCount);
        statistics.put("favoriteCount", favoriteCount);
        statistics.put("totalFileSize", totalFileSize);
        statistics.put("totalPages", totalPages);
        statistics.put("monthlyNewCount", monthlyNewCount);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getReadingStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ebook::getUserId, userId)
                .isNotNull(Ebook::getLastReadDate);
        
        if (startDate != null) {
            wrapper.ge(Ebook::getLastReadDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Ebook::getLastReadDate, endDate);
        }
        
        List<Ebook> readEbooks = ebookMapper.selectList(wrapper);
        
        // 阅读过的电子书数量
        long readCount = readEbooks.size();
        
        // 总阅读页数
        int totalReadPages = readEbooks.stream().mapToInt(Ebook::getLastReadPage).sum();
        
        // 平均阅读进度
        double avgProgress = readEbooks.stream()
                .filter(ebook -> ebook.getPageCount() > 0)
                .mapToDouble(ebook -> (double) ebook.getLastReadPage() / ebook.getPageCount())
                .average()
                .orElse(0.0);
        
        statistics.put("readCount", readCount);
        statistics.put("totalReadPages", totalReadPages);
        statistics.put("avgProgress", Math.round(avgProgress * 100) / 100.0);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getCategoryStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 文档分类统计
        LambdaQueryWrapper<Category> docCategoryWrapper = new LambdaQueryWrapper<>();
        docCategoryWrapper.eq(Category::getUserId, userId)
                .eq(Category::getType, "document");
        long documentCategoryCount = categoryMapper.selectCount(docCategoryWrapper);
        
        // 电子书分类统计
        LambdaQueryWrapper<Category> ebookCategoryWrapper = new LambdaQueryWrapper<>();
        ebookCategoryWrapper.eq(Category::getUserId, userId)
                .eq(Category::getType, "ebook");
        long ebookCategoryCount = categoryMapper.selectCount(ebookCategoryWrapper);
        
        statistics.put("documentCategoryCount", documentCategoryCount);
        statistics.put("ebookCategoryCount", ebookCategoryCount);
        statistics.put("totalCategoryCount", documentCategoryCount + ebookCategoryCount);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getStorageStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 电子书文件大小统计
        LambdaQueryWrapper<Ebook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ebook::getUserId, userId);
        List<Ebook> ebooks = ebookMapper.selectList(wrapper);
        
        long totalFileSize = ebooks.stream().mapToLong(Ebook::getFileSize).sum();
        
        // 转换为更易读的格式
        String fileSizeFormatted = formatFileSize(totalFileSize);
        
        statistics.put("totalFileSize", totalFileSize);
        statistics.put("fileSizeFormatted", fileSizeFormatted);
        statistics.put("ebookCount", ebooks.size());
        
        return statistics;
    }

    @Override
    public Map<String, Object> getActivityStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        LambdaQueryWrapper<UserActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserActivity::getUserId, userId);
        
        if (startDate != null) {
            wrapper.ge(UserActivity::getActivityTime, startDate);
        }
        if (endDate != null) {
            wrapper.le(UserActivity::getActivityTime, endDate);
        }
        
        List<UserActivity> activities = userActivityMapper.selectList(wrapper);
        
        // 按活动类型统计
        Map<String, Long> activityCounts = activities.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        UserActivity::getActivityType,
                        java.util.stream.Collectors.counting()
                ));
        
        statistics.put("totalActivities", activities.size());
        statistics.put("activityCounts", activityCounts);
        
        return statistics;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
