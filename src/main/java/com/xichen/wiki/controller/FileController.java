package com.xichen.wiki.controller;

import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.ConfirmUploadRequest;
import com.xichen.wiki.dto.GenerateUploadUrlRequest;
import com.xichen.wiki.service.FileService;
import com.xichen.wiki.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 文件控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@Validated
@Tag(name = "文件管理", description = "文件上传下载相关接口")
public class FileController {

    @Autowired
    private FileService fileService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "上传文件", description = "上传文件到服务器", 
               security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(
            @Parameter(description = "文件") @RequestParam("file") @NotNull MultipartFile file,
            @Parameter(description = "文件夹") @RequestParam(defaultValue = "general") String folder,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Map<String, Object> result = fileService.uploadFile(file, folder, userId);
        return Result.success(result);
    }

    @Operation(summary = "生成上传URL", description = "生成预签名上传URL", 
               security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/upload-url")
    public Result<Map<String, Object>> generateUploadUrl(
            @Valid @RequestBody GenerateUploadUrlRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Map<String, Object> result = fileService.generateUploadUrl(
                request.getFileName(),
                request.getContentType(),
                request.getFolder(),
                userId
        );
        return Result.success(result);
    }

    @Operation(summary = "确认上传", description = "确认文件上传完成", 
               security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/confirm-upload")
    public Result<String> confirmUpload(
            @Valid @RequestBody ConfirmUploadRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        fileService.confirmUpload(request.getFileKey(), request.getFileSize(), userId);
        return Result.success("上传确认成功");
    }

    @Operation(summary = "下载文件", description = "下载文件", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/download/{fileKey}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "文件键") @PathVariable @NotBlank String fileKey,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        try {
            // 获取文件信息
            Map<String, Object> fileInfo = fileService.getFileInfo(fileKey, userId);
            if (!(Boolean) fileInfo.get("exists")) {
                return ResponseEntity.notFound().build();
            }

            String storageType = (String) fileInfo.get("storageType");
            
            if ("qiniu".equals(storageType)) {
                // 七牛云文件，返回重定向到七牛云URL
                String qiniuUrl = (String) fileInfo.get("url");
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(java.net.URI.create(qiniuUrl));
                return ResponseEntity.status(302).headers(headers).build();
            } else {
                // 本地文件
                File file = getLocalFile(fileKey);
                
                if (!file.exists()) {
                    return ResponseEntity.notFound().build();
                }

                Resource resource = new FileSystemResource(file);
                
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
                
                // 设置内容类型
                String contentType = Files.probeContentType(file.toPath());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(file.length())
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            }

        } catch (Exception e) {
            log.error("文件下载失败：{}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "获取文件信息", description = "获取文件详细信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/info/{fileKey}")
    public Result<Map<String, Object>> getFileInfo(
            @Parameter(description = "文件键") @PathVariable @NotBlank String fileKey,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Map<String, Object> fileInfo = fileService.getFileInfo(fileKey, userId);
        return Result.success(fileInfo);
    }

    @Operation(summary = "删除文件", description = "删除指定文件", 
               security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/{fileKey}")
    public Result<String> deleteFile(
            @Parameter(description = "文件键") @PathVariable @NotBlank String fileKey,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        fileService.deleteFile(fileKey, userId);
        return Result.success("删除成功");
    }

    @Operation(summary = "检查文件存在", description = "检查文件是否存在")
    @GetMapping("/exists/{fileKey}")
    public Result<Map<String, Object>> checkFileExists(
            @Parameter(description = "文件键") @PathVariable @NotBlank String fileKey) {
        
        boolean exists = fileService.fileExists(fileKey);
        return Result.success(Map.of("exists", exists));
    }


    /**
     * 获取本地文件
     */
    private File getLocalFile(String fileKey) {
        // 这里应该从配置中获取本地存储路径
        String uploadPath = "uploads"; // 从配置中读取
        Path filePath = Paths.get(uploadPath, fileKey);
        return filePath.toFile();
    }
}
