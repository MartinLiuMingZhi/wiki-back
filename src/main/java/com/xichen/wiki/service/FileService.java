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
     * 生成文件下载URL
     */
    String generateDownloadUrl(String fileKey, Long userId);
    
    /**
     * 删除文件
     */
    void deleteFile(String fileKey, Long userId);
    
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
    long getFileSize(String fileKey);
    
    /**
     * 生成预签名上传URL
     */
    Map<String, Object> generateUploadUrl(String fileName, String contentType, String folder, Long userId);
    
    /**
     * 确认文件上传完成
     */
    void confirmUpload(String fileKey, Long fileSize, Long userId);
}
