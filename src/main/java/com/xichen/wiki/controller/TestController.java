package com.xichen.wiki.controller;

import com.xichen.wiki.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 系统监控控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "系统监控", description = "系统健康检查和监控接口")
public class TestController {

    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        return Result.success(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "message", "Wiki知识管理系统运行正常"
        ));
    }

    @Operation(summary = "系统信息", description = "获取系统基本信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        return Result.success(Map.of(
            "application", "Wiki知识管理系统",
            "version", "1.0.0",
            "javaVersion", System.getProperty("java.version"),
            "springBootVersion", org.springframework.boot.SpringBootVersion.getVersion(),
            "timestamp", LocalDateTime.now()
        ));
    }
}
