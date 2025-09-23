# Wiki知识库后端服务

## 项目概述

这是一个基于Spring Boot 3.x开发的Wiki知识库后端服务，为移动端Wiki知识库APP提供API接口支持。项目采用MyBatis Plus、Redis、MySQL等技术栈，实现了用户管理、文档管理、电子书管理等核心功能。

## 技术栈

- **开发框架**: Spring Boot 3.5.6
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.0
- **ORM框架**: MyBatis Plus 3.5.4
- **安全框架**: Spring Security + JWT
- **API文档**: SpringDoc OpenAPI 2.1.0
- **构建工具**: Maven
- **Java版本**: 17

## 项目结构

```
src/main/java/com/xichen/wiki/
├── common/                 # 通用组件
│   └── Result.java        # 统一响应格式
├── config/                # 配置类
│   ├── MyBatisPlusConfig.java
│   └── SecurityConfig.java
├── controller/            # 控制器层
│   ├── AuthController.java
│   ├── UserController.java
│   ├── DocumentController.java
│   └── TestController.java
├── dto/                   # 数据传输对象
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── UserUpdateRequest.java
├── entity/                # 实体类
│   ├── User.java
│   ├── Document.java
│   ├── Ebook.java
│   ├── Category.java
│   ├── Bookmark.java
│   └── Tag.java
├── exception/             # 异常处理
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── mapper/                # MyBatis Mapper接口
│   ├── UserMapper.java
│   ├── DocumentMapper.java
│   ├── EbookMapper.java
│   ├── CategoryMapper.java
│   ├── BookmarkMapper.java
│   └── TagMapper.java
├── security/              # 安全相关
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── service/               # 服务层
│   ├── UserService.java
│   ├── DocumentService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       └── DocumentServiceImpl.java
├── util/                  # 工具类
│   └── JwtUtil.java
└── WikiApplication.java   # 启动类
```

## 核心功能

### 1. 用户管理
- 用户注册/登录
- JWT认证
- 用户信息管理
- 密码加密存储

### 2. 文档管理
- 文档CRUD操作
- 文档分类管理
- 文档搜索
- 文档收藏
- 文档版本控制

### 3. 电子书管理
- 电子书上传/下载
- 电子书分类
- 书签管理
- 阅读进度记录

### 4. 系统功能
- 统一异常处理
- 参数校验
- API文档生成
- 健康检查

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+

### 1. 数据库准备

```sql
-- 创建数据库
CREATE DATABASE wiki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行初始化脚本
-- 运行 src/main/resources/sql/schema.sql
-- 运行 src/main/resources/sql/data.sql
```

### 2. 配置修改

修改 `src/main/resources/application.properties` 中的数据库和Redis连接信息：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/wiki?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password

# Redis配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=your_redis_password
```

### 3. 启动应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/wiki-0.0.1-SNAPSHOT.jar
```

### 4. 访问应用

- 应用地址: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html
- 健康检查: http://localhost:8080/api/v1/test/health

## API接口

### 认证接口

#### 用户注册
```http
POST /api/v1/auth/register
Content-Type: application/json

{
    "username": "testuser",
    "email": "test@example.com",
    "password": "123456"
}
```

#### 用户登录
```http
POST /api/v1/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "123456"
}
```

### 用户接口

#### 获取当前用户信息
```http
GET /api/v1/users/me
Authorization: Bearer {token}
```

#### 更新用户信息
```http
PUT /api/v1/users/me
Authorization: Bearer {token}
Content-Type: application/json

{
    "username": "newusername",
    "email": "newemail@example.com",
    "avatarUrl": "http://example.com/avatar.jpg"
}
```

### 文档接口

#### 创建文档
```http
POST /api/v1/documents
Authorization: Bearer {token}
Content-Type: application/json

{
    "title": "文档标题",
    "content": "文档内容",
    "categoryId": 1
}
```

#### 获取文档列表
```http
GET /api/v1/documents?page=1&size=10&categoryId=1&keyword=搜索关键词
Authorization: Bearer {token}
```

#### 获取文档详情
```http
GET /api/v1/documents/{id}
Authorization: Bearer {token}
```

#### 更新文档
```http
PUT /api/v1/documents/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
    "title": "更新后的标题",
    "content": "更新后的内容",
    "categoryId": 2
}
```

#### 删除文档
```http
DELETE /api/v1/documents/{id}
Authorization: Bearer {token}
```

#### 切换收藏状态
```http
POST /api/v1/documents/{id}/favorite
Authorization: Bearer {token}
```

## 测试数据

系统已预置以下测试数据：

### 测试用户
- 用户名: `admin`, 密码: `123456`
- 用户名: `testuser`, 密码: `123456`

### 默认分类
- 技术文档
- 学习笔记
- 工作文档
- 技术书籍
- 文学书籍
- 教育书籍

### 默认标签
- Java, Spring, 数据库, 前端, 后端
- 学习, 工作, 技术, 编程, 开发

## 开发说明

### 代码规范
- 使用Lombok减少样板代码
- 统一使用Result类作为API响应格式
- 使用@Valid注解进行参数校验
- 使用@Operation注解生成API文档

### 安全配置
- 使用JWT进行身份认证
- 密码使用BCrypt加密
- 支持CORS跨域请求
- 统一的异常处理

### 数据库设计
- 支持逻辑删除
- 自动填充创建时间和更新时间
- 建立合适的索引提高查询性能
- 支持全文搜索

## 部署说明

### Docker部署（推荐）

1. 创建Dockerfile
2. 构建镜像
3. 使用docker-compose启动服务

### 传统部署

1. 打包应用: `mvn clean package`
2. 上传jar包到服务器
3. 配置数据库和Redis连接
4. 启动应用: `java -jar wiki-0.0.1-SNAPSHOT.jar`

## 监控和日志

- 应用健康检查: `/api/v1/test/health`
- 系统信息: `/api/v1/test/info`
- 日志级别可在application.properties中配置
- 支持Swagger UI进行API测试

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认数据库用户名密码正确
- 检查数据库是否存在

### 2. Redis连接失败
- 检查Redis服务是否启动
- 确认Redis密码配置正确
- 检查网络连接

### 3. JWT认证失败
- 检查token是否过期
- 确认token格式正确（Bearer + 空格 + token）
- 检查JWT密钥配置

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

本项目采用MIT许可证，详情请查看LICENSE文件。

## 联系方式

如有问题或建议，请联系项目维护者。