# CI/CD 流程说明

## 概述

本项目使用GitHub Actions实现完整的CI/CD流程，包括代码质量检查、自动化测试、安全扫描、Docker镜像构建和部署。

## 工作流文件

### 1. 主要CI/CD流程 (`.github/workflows/ci.yml`)

**触发条件：**
- 推送到 `main` 或 `develop` 分支
- 创建Pull Request到 `main` 或 `develop` 分支

**包含的作业：**

#### 代码质量检查 (code-quality)
- 使用SpotBugs进行静态代码分析
- 使用Checkstyle检查代码风格
- 使用PMD检查代码质量
- 生成代码质量报告

#### 单元测试 (unit-tests)
- 启动MySQL和Redis测试服务
- 运行所有单元测试
- 生成测试覆盖率报告
- 上传测试结果

#### 集成测试 (integration-tests)
- 运行集成测试套件
- 验证系统组件间的交互

#### 安全扫描 (security-scan)
- 使用OWASP Dependency Check扫描依赖漏洞
- 生成安全报告

#### Docker镜像构建 (build-docker)
- 构建多架构Docker镜像 (linux/amd64, linux/arm64)
- 推送到GitHub Container Registry
- 使用Docker Buildx和缓存优化

#### 部署 (deploy-staging / deploy-production)
- 自动部署到测试环境 (develop分支)
- 自动部署到生产环境 (main分支)

### 2. 发布流程 (`.github/workflows/release.yml`)

**触发条件：**
- 推送版本标签 (v*)
- 手动触发发布

**功能：**
- 创建GitHub Release
- 构建发布版本
- 构建Docker镜像
- 部署到生产环境

### 3. 依赖更新 (`.github/workflows/dependabot.yml`)

**功能：**
- 自动合并Dependabot的依赖更新PR
- 支持minor版本更新

## 代码质量工具

### 1. SpotBugs
- 静态代码分析工具
- 检测潜在的bug和性能问题
- 配置：最大努力级别，低阈值

### 2. Checkstyle
- 代码风格检查
- 基于Google Java Style Guide
- 自定义规则集

### 3. PMD
- 代码质量检查
- 包含最佳实践、代码风格、设计、错误倾向、性能、安全规则

### 4. OWASP Dependency Check
- 依赖漏洞扫描
- CVSS评分阈值：7
- 支持抑制文件

### 5. JaCoCo
- 代码覆盖率检查
- 最低覆盖率要求：80%
- 生成HTML报告

## 部署配置

### 1. Docker配置

**基础镜像：** OpenJDK 17
**多阶段构建：** 优化镜像大小
**安全配置：** 非root用户运行

### 2. Kubernetes部署

**测试环境：**
- 命名空间：wiki-staging
- 副本数：2
- 资源配置：512Mi内存，250m CPU

**生产环境：**
- 命名空间：wiki-production
- 副本数：3
- 资源配置：1Gi内存，500m CPU
- 负载均衡器

### 3. Docker Compose

**生产环境配置：**
- 3个应用副本
- Nginx反向代理
- SSL/TLS支持
- 资源限制和重启策略

## 环境变量

### 必需的环境变量

```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_USER=your_username
MYSQL_PASSWORD=your_password

# 应用配置
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC
```

## 监控和日志

### 1. 健康检查
- 端点：`/actuator/health`
- 存活探针：60秒初始延迟，30秒间隔
- 就绪探针：30秒初始延迟，10秒间隔

### 2. 日志配置
- 结构化日志输出
- 日志级别：INFO
- 日志轮转：按大小和时间

## 安全配置

### 1. 容器安全
- 非root用户运行
- 最小权限原则
- 资源限制

### 2. 网络安全
- HTTPS强制重定向
- 安全头设置
- SSL/TLS配置

### 3. 依赖安全
- 定期依赖更新
- 漏洞扫描
- 安全补丁管理

## 使用指南

### 1. 本地开发

```bash
# 启动开发环境
docker-compose up -d

# 运行测试
./mvnw test

# 代码质量检查
./mvnw spotbugs:check checkstyle:check pmd:check
```

### 2. 部署到测试环境

```bash
# 推送代码到develop分支
git push origin develop

# 自动触发CI/CD流程
# 部署到测试环境
```

### 3. 部署到生产环境

```bash
# 创建发布标签
git tag v1.0.0
git push origin v1.0.0

# 或推送到main分支
git push origin main
```

### 4. 手动部署

```bash
# 使用Docker Compose
docker-compose -f docker-compose.production.yml up -d

# 使用Kubernetes
kubectl apply -f k8s/production/
```

## 故障排除

### 1. 构建失败
- 检查代码质量报告
- 修复测试失败
- 更新依赖版本

### 2. 部署失败
- 检查环境变量配置
- 验证镜像构建
- 检查资源限制

### 3. 性能问题
- 调整JVM参数
- 优化资源配置
- 监控应用指标

## 最佳实践

### 1. 代码提交
- 小批量提交
- 清晰的提交信息
- 代码审查

### 2. 测试策略
- 单元测试覆盖
- 集成测试验证
- 性能测试

### 3. 部署策略
- 蓝绿部署
- 滚动更新
- 回滚计划

## 联系信息

如有问题，请联系：
- 邮箱：xichen@example.com
- GitHub：@xichen
