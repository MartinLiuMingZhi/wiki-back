package com.xichen.wiki.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.common.Result;
import com.xichen.wiki.entity.Document;
import com.xichen.wiki.service.DocumentService;
import com.xichen.wiki.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 文档控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@Validated
@Tag(name = "文档管理", description = "文档CRUD操作相关接口")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "创建文档", description = "创建新的文档")
    @PostMapping
    public Result<Document> createDocument(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateDocumentRequest request) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Document document = documentService.createDocument(
                userId,
                request.getTitle(),
                request.getContent(),
                request.getCategoryId(),
                request.getTagIds()
        );
        
        return Result.success("创建成功", document);
    }

    @Operation(summary = "获取文档列表", description = "分页获取用户的文档列表")
    @GetMapping
    public Result<Page<Document>> getDocuments(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Page<Document> documents = documentService.getUserDocuments(userId, page, size, keyword);
        return Result.success(documents);
    }

    @Operation(summary = "获取文档详情", description = "根据ID获取文档详细信息")
    @GetMapping("/{id}")
    public Result<Document> getDocumentDetail(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "文档ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Document document = documentService.getDocumentDetail(id, userId);
        return Result.success(document);
    }

    @Operation(summary = "更新文档", description = "更新文档内容")
    @PutMapping("/{id}")
    public Result<Document> updateDocument(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "文档ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateDocumentRequest request) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Document document = documentService.updateDocument(
                id,
                userId,
                request.getTitle(),
                request.getContent(),
                request.getCategoryId(),
                request.getTagIds()
        );
        
        return Result.success("更新成功", document);
    }

    @Operation(summary = "删除文档", description = "删除指定文档")
    @DeleteMapping("/{id}")
    public Result<String> deleteDocument(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "文档ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        documentService.deleteDocument(id, userId);
        return Result.success("删除成功");
    }

    @Operation(summary = "切换收藏状态", description = "切换文档的收藏状态")
    @PostMapping("/{id}/favorite")
    public Result<String> toggleFavorite(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "文档ID") @PathVariable @NotNull Long id) {
        
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        documentService.toggleFavorite(id, userId);
        return Result.success("操作成功");
    }

    /**
     * 创建文档请求DTO
     */
    public static class CreateDocumentRequest {
        @NotBlank(message = "标题不能为空")
        private String title;
        
        private String content;
        
        private Long categoryId;
        
        private Long[] tagIds;
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public Long[] getTagIds() { return tagIds; }
        public void setTagIds(Long[] tagIds) { this.tagIds = tagIds; }
    }

    /**
     * 更新文档请求DTO
     */
    public static class UpdateDocumentRequest {
        @NotBlank(message = "标题不能为空")
        private String title;
        
        private String content;
        
        private Long categoryId;
        
        private Long[] tagIds;
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public Long[] getTagIds() { return tagIds; }
        public void setTagIds(Long[] tagIds) { this.tagIds = tagIds; }
    }
}

