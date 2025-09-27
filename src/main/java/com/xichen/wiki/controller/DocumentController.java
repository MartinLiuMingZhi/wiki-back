package com.xichen.wiki.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xichen.wiki.common.Result;
import com.xichen.wiki.dto.CreateDocumentRequest;
import com.xichen.wiki.dto.UpdateDocumentRequest;
import com.xichen.wiki.entity.Document;
import com.xichen.wiki.service.DocumentService;
import com.xichen.wiki.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

    @Operation(summary = "创建文档", description = "创建新的文档", 
               security = @SecurityRequirement(name = "Authorization"))
    @PostMapping
    public Result<Document> createDocument(
            @Valid @RequestBody CreateDocumentRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
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

    @Operation(summary = "获取文档列表", description = "分页获取用户的文档列表", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping
    public Result<Page<Document>> getDocuments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Page<Document> documents = documentService.getUserDocuments(userId, page, size, keyword);
        return Result.success(documents);
    }

    @Operation(summary = "获取文档详情", description = "根据ID获取文档详细信息", 
               security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/{id}")
    public Result<Document> getDocumentDetail(
            @Parameter(description = "文档ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        Document document = documentService.getDocumentDetail(id, userId);
        return Result.success(document);
    }

    @Operation(summary = "更新文档", description = "更新文档内容", 
               security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/{id}")
    public Result<Document> updateDocument(
            @Parameter(description = "文档ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody UpdateDocumentRequest request,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
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

    @Operation(summary = "删除文档", description = "删除指定文档", 
               security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/{id}")
    public Result<String> deleteDocument(
            @Parameter(description = "文档ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        documentService.deleteDocument(id, userId);
        return Result.success("删除成功");
    }

    @Operation(summary = "切换收藏状态", description = "切换文档的收藏状态", 
               security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/{id}/favorite")
    public Result<String> toggleFavorite(
            @Parameter(description = "文档ID") @PathVariable @NotNull Long id,
            HttpServletRequest httpRequest) {
        
        String token = httpRequest.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromAuthorizationHeader(token);
        
        documentService.toggleFavorite(id, userId);
        return Result.success("操作成功");
    }

}

