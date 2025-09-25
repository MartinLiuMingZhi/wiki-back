# Wiki 知识管理系统架构文档

## 🏗️ 系统架构

### 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                       前端层 (Frontend)                        │
├─────────────────────────────────────────────────────────────┤
│  Web界面 / 移动端 / API客户端                                    │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                      API网关层 (Gateway)                      │
├─────────────────────────────────────────────────────────────┤
│  Spring Security + JWT认证 / 请求路由 / 负载均衡                │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                     业务服务层 (Service)                      │
├─────────────────────────────────────────────────────────────┤
│  Controller → Service → Mapper → Entity                     │
│  • 用户服务    • 文档服务    • 文件服务                        │
│  • 认证服务    • 搜索服务    • 统计服务                        │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                     数据存储层 (Storage)                      │
├─────────────────────────────────────────────────────────────┤
│  MySQL数据库  │  Redis缓存  │  七牛云存储  │  邮件服务        │
└─────────────────────────────────────────────────────────────┘
```

## 📁 项目结构

```
wiki/
├── src/main/java/com/xichen/wiki/
│   ├── common/              # 通用组件
│   │   └── Result.java      # 统一响应格式
│   ├── config/              # 配置类
│   │   ├── SecurityConfig.java
│   │   ├── RedisConfig.java
│   │   ├── MailConfig.java
│   │   └── ...
│   ├── constant/            # 常量定义
│   │   └── VerificationCodeConstants.java
│   ├── controller/          # REST控制器
│   │   ├── AuthController.java
│   │   ├── DocumentController.java
│   │   ├── UserController.java
│   │   └── ...
│   ├── dto/                 # 数据传输对象
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── ...
│   ├── entity/              # 实体类
│   │   ├── User.java
│   │   ├── Document.java
│   │   ├── Ebook.java
│   │   └── ...
│   ├── exception/           # 异常处理
│   │   ├── BusinessException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── VerificationCodeException.java
│   ├── mapper/              # 数据访问层
│   │   ├── UserMapper.java
│   │   ├── DocumentMapper.java
│   │   └── ...
│   ├── security/            # 安全配置
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   └── CustomUserDetailsService.java
│   ├── service/             # 服务接口层
│   │   ├── UserService.java
│   │   ├── DocumentService.java
│   │   ├── impl/            # 服务实现层
│   │   │   ├── UserServiceImpl.java
│   │   │   ├── DocumentServiceImpl.java
│   │   │   └── ...
│   │   └── ...
│   ├── util/                # 工具类
│   │   ├── JwtUtil.java
│   │   ├── UserContext.java
│   │   └── RedisKeyUtil.java
│   └── WikiApplication.java # 启动类
├── src/main/resources/
│   ├── application.properties # 应用配置
│   ├── templates/             # 邮件模板
│   │   └── verification-code-email.html
│   └── sql/                   # 数据库脚本
│       ├── schema.sql         # 表结构
│       ├── data.sql           # 初始数据
│       └── init.sql           # 完整初始化
└── src/test/                  # 测试代码
```

## 🔧 技术架构

### 分层架构

```
┌─────────────────────────────────────┐
│            Controller层              │  # REST API控制器
│  • 接收HTTP请求                      │
│  • 参数验证和转换                     │
│  • 调用Service层                     │
│  • 返回统一响应格式                   │
├─────────────────────────────────────┤
│            Service层                │  # 业务逻辑接口
│  • 业务逻辑处理                      │
│  • 事务管理                         │
│  • 缓存管理                         │
│  • 调用Mapper层                     │
├─────────────────────────────────────┤
│            Mapper层                 │  # 数据访问层
│  • 数据库操作                       │
│  • SQL映射                          │
│  • 结果映射                         │
├─────────────────────────────────────┤
│            Entity层                 │  # 实体类
│  • 数据模型定义                      │
│  • 字段映射                         │
│  • 关系映射                         │
└─────────────────────────────────────┘
```

### 核心组件

#### 1. 认证授权模块
- **JWT Token**: 无状态认证
- **Spring Security**: 安全框架
- **角色权限**: 基于角色的访问控制

#### 2. 数据存储模块
- **MySQL**: 主数据库，存储业务数据
- **Redis**: 缓存数据库，存储会话和临时数据
- **七牛云**: 文件存储，存储上传的文件

#### 3. 业务服务模块
- **用户服务**: 用户管理、认证、权限
- **文档服务**: 文档CRUD、版本控制
- **文件服务**: 文件上传、下载、管理
- **搜索服务**: 全文搜索、高级搜索
- **统计服务**: 数据统计、分析

## 🔄 数据流

### 用户请求处理流程

```
1. 用户请求 → 2. 安全验证 → 3. 参数验证 → 4. 业务处理 → 5. 数据访问 → 6. 响应返回
     │              │              │              │              │              │
     ▼              ▼              ▼              ▼              ▼              ▼
HTTP请求    JWT验证/权限检查    @Valid验证     Service业务逻辑    Mapper数据操作    JSON响应
```

### 数据存储流程

```
业务数据 → MySQL数据库
临时数据 → Redis缓存
文件数据 → 七牛云存储
邮件数据 → SMTP服务
```

## 🛡️ 安全架构

### 认证流程

```
1. 用户登录 → 2. 验证凭据 → 3. 生成JWT → 4. 返回Token → 5. 后续请求携带Token
```

### 授权流程

```
1. 请求Token → 2. 验证Token → 3. 检查权限 → 4. 允许/拒绝访问
```

### 数据安全

- **密码加密**: BCrypt哈希加密
- **敏感配置**: Jasypt加密存储
- **SQL注入防护**: MyBatis-Plus参数化查询
- **XSS防护**: 输入验证和输出转义

## 📊 性能优化

### 缓存策略

- **Redis缓存**: 用户会话、验证码、热门数据
- **数据库缓存**: MyBatis-Plus二级缓存
- **应用缓存**: Spring Cache注解

### 数据库优化

- **索引优化**: 关键字段建立索引
- **分页查询**: 避免大数据量查询
- **连接池**: HikariCP高性能连接池

### 文件存储优化

- **CDN加速**: 七牛云CDN分发
- **压缩存储**: 文件压缩存储
- **异步上传**: 大文件异步处理

## 🔧 配置管理

### 环境配置

- **开发环境**: 本地开发配置
- **测试环境**: 测试环境配置
- **生产环境**: 生产环境配置

### 配置加密

- **数据库密码**: Jasypt加密
- **JWT密钥**: Jasypt加密
- **第三方密钥**: Jasypt加密

## 📈 监控和日志

### 系统监控

- **健康检查**: `/api/v1/system/health`
- **系统信息**: `/api/v1/system/info`
- **性能指标**: JVM监控

### 日志管理

- **应用日志**: 业务操作日志
- **错误日志**: 异常和错误日志
- **访问日志**: HTTP请求日志

## 🚀 部署架构

### 容器化部署

```yaml
# docker-compose.yml
services:
  wiki-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - mysql
      - redis
  
  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=wiki
  
  redis:
    image: redis:6.0
```

### 传统部署

```bash
# 1. 编译打包
mvn clean package -DskipTests

# 2. 运行应用
java -jar target/wiki.jar

# 3. 后台运行
nohup java -jar target/wiki.jar > wiki.log 2>&1 &
```

## 🔄 扩展性设计

### 水平扩展

- **无状态设计**: 应用无状态，支持水平扩展
- **负载均衡**: 支持多实例负载均衡
- **数据库分离**: 读写分离，主从复制

### 功能扩展

- **插件化**: 支持功能插件扩展
- **API版本**: 支持API版本管理
- **微服务**: 支持拆分为微服务架构

## 📝 开发规范

### 代码规范

- **命名规范**: 驼峰命名法
- **注释规范**: JavaDoc注释
- **异常处理**: 统一异常处理
- **日志规范**: 统一日志格式

### 接口规范

- **RESTful**: 遵循REST设计原则
- **统一响应**: 统一响应格式
- **参数验证**: 统一参数验证
- **错误处理**: 统一错误处理

---

**注意**: 本架构文档会随着系统演进持续更新，请关注最新版本。
