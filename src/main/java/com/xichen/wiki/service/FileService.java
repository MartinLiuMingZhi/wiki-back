package com.xichen.wiki.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     */
    Map<String, Object> uploadFile(MultipartFile file, String folder, Long userId);
    
    /**
     * 生成下载URL
     */
    String generateDownloadUrl(String fileKey, Long userId);
    
    /**
     * 删除文件
     */
    boolean deleteFile(String fileKey, Long userId);
    
    /**
     * 获取文件信息
     */
    Map<String, Object> getFileInfo(String fileKey, Long userId);
    
    /**
     * 检查文件是否存在
     */
    boolean fileExists(String fileKey);
    
    /**
     * 获取文件大小
     */
    Long getFileSize(String fileKey);
    
    /**
     * 生成上传URL
     */
    Map<String, Object> generateUploadUrl(String fileName, String fileType, String folder, Long userId);
    
    /**
     * 确认上传
     */
    String confirmUpload(String key, Long userId, Long fileSize);
}
