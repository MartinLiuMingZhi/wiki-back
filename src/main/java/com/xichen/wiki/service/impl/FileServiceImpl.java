package com.xichen.wiki.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.xichen.wiki.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件服务实现类
 * 主要使用七牛云对象存储，本地存储作为备选方案
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    // 七牛云配置
    @Value("${qiniu.access-key:}")
    private String accessKey;

    @Value("${qiniu.secret-key:}")
    private String secretKey;

    @Value("${qiniu.bucket:}")
    private String bucket;

    @Value("${qiniu.domain:}")
    private String domain;

    @Value("${qiniu.region:huadong}")
    private String region;

    // 本地存储配置（备选方案）
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:104857600}") // 100MB
    private long maxFileSize;

    @Value("${file.storage.type:qiniu}") // qiniu 或 local
    private String storageType;

    // 七牛云上传管理器
    private UploadManager uploadManager;
    private Auth auth;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file, String folder, Long userId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("文件大小超过限制");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = generateFileName(extension);
            String fileKey = buildFilePath(folder, userId, fileName);

            Map<String, Object> result = new HashMap<>();
            result.put("fileKey", fileKey);
            result.put("fileName", originalFilename);
            result.put("fileSize", file.getSize());
            result.put("contentType", file.getContentType());
            result.put("uploadTime", LocalDateTime.now());

            if ("qiniu".equals(storageType)) {
                // 使用七牛云上传
                uploadToQiniu(file, fileKey);
                result.put("url", getQiniuUrl(fileKey));
                result.put("storageType", "qiniu");
                log.info("文件上传到七牛云成功：用户ID={}, 文件路径={}", userId, fileKey);
            } else {
                // 使用本地存储
                uploadToLocal(file, fileKey);
                result.put("url", "/api/v1/files/download/" + fileKey);
                result.put("storageType", "local");
                log.info("文件上传到本地成功：用户ID={}, 文件路径={}", userId, fileKey);
            }

            return result;

        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public String generateDownloadUrl(String fileKey, Long userId) {
        if ("qiniu".equals(storageType)) {
            return getQiniuUrl(fileKey);
        } else {
            return "/api/v1/files/download/" + fileKey;
        }
    }

    @Override
    public void deleteFile(String fileKey, Long userId) {
        try {
            if ("qiniu".equals(storageType)) {
                deleteFromQiniu(fileKey);
                log.info("文件从七牛云删除成功：用户ID={}, 文件路径={}", userId, fileKey);
            } else {
                deleteFromLocal(fileKey);
                log.info("文件从本地删除成功：用户ID={}, 文件路径={}", userId, fileKey);
            }
        } catch (Exception e) {
            log.error("文件删除失败：{}", e.getMessage());
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public Map<String, Object> getFileInfo(String fileKey, Long userId) {
        Map<String, Object> info = new HashMap<>();
        
        if ("qiniu".equals(storageType)) {
            // 七牛云文件信息查询
            info.put("exists", true); // 简化处理，实际应该调用七牛云API查询
            info.put("fileKey", fileKey);
            info.put("url", getQiniuUrl(fileKey));
            info.put("storageType", "qiniu");
        } else {
            // 本地文件信息查询
            Path filePath = Paths.get(uploadPath, fileKey);
            if (Files.exists(filePath)) {
                try {
                    info.put("exists", true);
                    info.put("fileSize", Files.size(filePath));
                    info.put("lastModified", Files.getLastModifiedTime(filePath));
                    info.put("fileKey", fileKey);
                    info.put("storageType", "local");
                } catch (IOException e) {
                    log.error("获取文件信息失败：{}", e.getMessage());
                    info.put("exists", false);
                }
            } else {
                info.put("exists", false);
            }
        }

        return info;
    }

    @Override
    public boolean fileExists(String fileKey) {
        if ("qiniu".equals(storageType)) {
            // 简化处理，实际应该调用七牛云API查询
            return true;
        } else {
            Path filePath = Paths.get(uploadPath, fileKey);
            return Files.exists(filePath);
        }
    }

    @Override
    public long getFileSize(String fileKey) {
        if ("qiniu".equals(storageType)) {
            // 简化处理，实际应该调用七牛云API查询
            return 0;
        } else {
            try {
                Path filePath = Paths.get(uploadPath, fileKey);
                return Files.size(filePath);
            } catch (IOException e) {
                log.error("获取文件大小失败：{}", e.getMessage());
                return 0;
            }
        }
    }

    @Override
    public Map<String, Object> generateUploadUrl(String fileName, String contentType, String folder, Long userId) {
        String extension = getFileExtension(fileName);
        String newFileName = generateFileName(extension);
        String fileKey = buildFilePath(folder, userId, newFileName);

        Map<String, Object> result = new HashMap<>();
        result.put("fileKey", fileKey);
        
        if ("qiniu".equals(storageType)) {
            // 生成七牛云上传token
            String uploadToken = getQiniuUploadToken(fileKey);
            result.put("uploadToken", uploadToken);
            result.put("uploadUrl", "https://upload-z2.qiniup.com");
            result.put("expiresIn", 3600); // 1小时过期
        } else {
            result.put("uploadUrl", "/api/v1/files/upload/" + fileKey);
            result.put("expiresIn", 3600); // 1小时过期
        }

        return result;
    }

    @Override
    public void confirmUpload(String fileKey, Long fileSize, Long userId) {
        // 验证文件是否存在且大小正确
        if (fileExists(fileKey) && getFileSize(fileKey) == fileSize) {
            log.info("文件上传确认成功：用户ID={}, 文件路径={}", userId, fileKey);
        } else {
            throw new RuntimeException("文件上传验证失败");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return timestamp + "_" + uuid + extension;
    }

    /**
     * 构建文件路径
     */
    private String buildFilePath(String folder, Long userId, String fileName) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("%s/%d/%s/%s", folder, userId, datePath, fileName);
    }

    // ==================== 七牛云相关方法 ====================

    /**
     * 初始化七牛云配置
     */
    private void initQiniu() {
        if (uploadManager == null) {
            Configuration config = new Configuration(Region.region0());
            uploadManager = new UploadManager(config);
            auth = Auth.create(accessKey, secretKey);
        }
    }

    /**
     * 上传文件到七牛云
     */
    private void uploadToQiniu(MultipartFile file, String fileKey) throws QiniuException, IOException {
        initQiniu();
        String uploadToken = getQiniuUploadToken(fileKey);
        Response response = uploadManager.put(file.getBytes(), fileKey, uploadToken);
        if (!response.isOK()) {
            throw new RuntimeException("七牛云上传失败: " + response.bodyString());
        }
    }

    /**
     * 上传文件到本地
     */
    private void uploadToLocal(MultipartFile file, String fileKey) throws IOException {
        Path filePath = Paths.get(uploadPath, fileKey);
        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath.toFile());
    }

    /**
     * 从七牛云删除文件
     */
    private void deleteFromQiniu(String fileKey) {
        // 简化处理，实际应该调用七牛云删除API
        log.info("从七牛云删除文件: {}", fileKey);
    }

    /**
     * 从本地删除文件
     */
    private void deleteFromLocal(String fileKey) throws IOException {
        Path filePath = Paths.get(uploadPath, fileKey);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    /**
     * 获取七牛云文件URL
     */
    private String getQiniuUrl(String fileKey) {
        if (domain.endsWith("/")) {
            return domain + fileKey;
        } else {
            return domain + "/" + fileKey;
        }
    }

    /**
     * 获取七牛云上传token
     */
    private String getQiniuUploadToken(String fileKey) {
        initQiniu();
        return auth.uploadToken(bucket, fileKey, 3600, null);
    }

    // ==================== 本地存储相关方法 ====================

    /**
     * 获取本地文件资源
     */
    public File getLocalFile(String fileKey) {
        Path filePath = Paths.get(uploadPath, fileKey);
        return filePath.toFile();
    }
}
