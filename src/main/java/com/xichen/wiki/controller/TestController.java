package com.xichen.wiki.controller;

import com.xichen.wiki.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "测试接口", description = "用于测试系统功能的接口")
public class TestController {

    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "Wiki知识库后端服务运行正常");
        
        log.info("健康检查请求");
        return Result.success("系统运行正常", data);
    }

    @Operation(summary = "系统信息", description = "获取系统基本信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("application", "Wiki知识库后端服务");
        data.put("version", "1.0.0");
        data.put("javaVersion", System.getProperty("java.version"));
        data.put("springBootVersion", org.springframework.boot.SpringBootVersion.getVersion());
        data.put("timestamp", LocalDateTime.now());
        
        return Result.success(data);
    }
}
