# 📚 Wiki 知识管理系统

[![CI/CD](https://github.com/MartinLiuMingZhi/wiki-back/workflows/unified-ci-cd.yml/badge.svg)](https://github.com/MartinLiuMingZhi/wiki-back/actions)
[![Dependabot](https://img.shields.io/badge/dependabot-enabled-blue.svg)](https://github.com/xichen/wiki/security/dependabot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 基于Spring Boot构建的现代化、全功能知识管理系统，具备文档管理、电子书库和协作功能。

[English Documentation](README.md) | [中文文档](README_CN.md)

## ✨ 功能特性

### 📖 文档管理
- **富文本编辑器**: 创建和编辑具有高级格式的文档
- **版本控制**: 跟踪文档变更并维护历史记录
- **协作编辑**: 多用户实时协作
- **文档分类**: 使用层次化分类组织文档
- **搜索过滤**: 强大的搜索功能，支持全文搜索

### 📚 电子书库
- **电子书上传**: 支持PDF、EPUB等多种格式
- **阅读进度**: 跨设备跟踪阅读进度
- **书签系统**: 保存和组织书签
- **个人图书馆**: 私人电子书收藏
- **公共分享**: 与社区分享电子书

### 👥 用户管理
- **身份认证**: 基于JWT的安全认证
- **用户档案**: 可自定义的用户档案和偏好设置
- **基于角色的访问**: 细粒度权限系统
- **邮箱验证**: 安全的账户验证流程

### 🔍 高级功能
- **全文搜索**: 基于Elasticsearch的搜索引擎
- **标签系统**: 灵活的内容组织标签
- **统计面板**: 全面的分析和洞察
- **文件管理**: 安全的文件上传和存储
- **API文档**: 完整的REST API和Swagger文档

## 🚀 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker (可选)

### 安装步骤

1. **克隆仓库**
   ```bash
   git clone https://github.com/xichen/wiki.git
   cd wiki
   ```

2. **配置数据库**
   ```bash
   # 创建MySQL数据库
   mysql -u root -p
   CREATE DATABASE wiki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **配置应用属性**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   # 编辑配置文件，设置数据库和Redis连接信息
   ```

4. **运行应用**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **访问应用**
   - API文档: http://localhost:8080/swagger-ui.html
   - 应用首页: http://localhost:8080

### Docker部署

```bash
# 使用Docker Compose构建和运行
docker-compose up -d

# 或构建Docker镜像
docker build -t wiki-app .
docker run -p 8080:8080 wiki-app
```

## 🏗️ 系统架构

### 技术栈
- **后端**: Spring Boot 3.5.6, Spring Security, MyBatis Plus
- **数据库**: MySQL 8.0, Redis 6.0
- **认证**: JWT (JSON Web Tokens)
- **文件存储**: 本地文件系统 / 云存储
- **搜索**: Elasticsearch (可选)
- **构建工具**: Maven 3.9+
- **Java版本**: OpenJDK 17

### 项目结构
```
src/
├── main/
│   ├── java/com/xichen/wiki/
│   │   ├── controller/     # REST API控制器
│   │   ├── service/        # 业务逻辑服务
│   │   ├── entity/         # 数据库实体
│   │   ├── mapper/         # MyBatis映射器
│   │   ├── dto/            # 数据传输对象
│   │   ├── config/         # 配置类
│   │   ├── security/       # 安全配置
│   │   └── util/           # 工具类
│   └── resources/
│       ├── application.properties
│       ├── sql/            # 数据库脚本
│       └── templates/      # 邮件模板
└── test/                   # 测试文件
```

## 📖 API文档

### 核心接口

#### 身份认证
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出
- `POST /api/auth/verify-email` - 邮箱验证

#### 文档管理
- `GET /api/documents` - 获取文档列表
- `POST /api/documents` - 创建文档
- `PUT /api/documents/{id}` - 更新文档
- `DELETE /api/documents/{id}` - 删除文档
- `GET /api/documents/{id}` - 获取文档详情

#### 电子书管理
- `GET /api/ebooks` - 获取电子书列表
- `POST /api/ebooks/upload` - 上传电子书
- `GET /api/ebooks/{id}` - 获取电子书详情
- `POST /api/ebooks/{id}/bookmark` - 添加书签
- `GET /api/ebooks/{id}/progress` - 获取阅读进度

#### 分类管理
- `GET /api/categories` - 获取分类列表
- `POST /api/categories` - 创建分类
- `PUT /api/categories/{id}` - 更新分类
- `DELETE /api/categories/{id}` - 删除分类

#### 搜索功能
- `GET /api/search` - 全局搜索
- `GET /api/search/documents` - 搜索文档
- `GET /api/search/ebooks` - 搜索电子书

### 交互式API文档
访问 http://localhost:8080/swagger-ui.html 获取完整的API文档和交互式测试功能。

## 🔧 配置说明

### 环境变量
```bash
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/wiki
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# Redis配置
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=your_redis_password

# JWT配置
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# 邮件配置
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
```

### 应用属性配置
```properties
# 服务器配置
server.port=8080
server.servlet.context-path=/

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/wiki?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Redis配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.database=0

# JWT配置
jwt.secret=your-secret-key
jwt.expiration=86400000
```

## 🧪 测试

### 运行测试
```bash
# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=UserServiceTest

# 运行测试并生成覆盖率报告
./mvnw test jacoco:report
```

### 测试覆盖
- **单元测试**: 服务层和工具类
- **集成测试**: API端点和数据库操作
- **安全测试**: 身份认证和授权
- **性能测试**: 负载测试和优化

## 🚀 部署

### 生产环境部署

1. **构建应用**
   ```bash
   ./mvnw clean package -Pproduction
   ```

2. **Docker部署**
   ```bash
   docker-compose -f docker-compose.production.yml up -d
   ```

3. **Kubernetes部署**
   ```bash
   kubectl apply -f k8s/production/
   ```

### CI/CD流水线
项目包含完整的CI/CD流水线，包括：
- 自动化测试
- 代码质量检查
- 安全扫描
- Docker镜像构建
- 自动化部署

## 🤝 贡献指南

1. Fork 仓库
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

### 开发规范
- 遵循Java编码标准
- 编写全面的测试
- 更新文档
- 确保所有测试通过
- 遵循现有代码风格

## 📊 性能指标

### 基准测试
- **响应时间**: 大多数API调用 < 100ms
- **吞吐量**: 1000+ 请求/秒
- **数据库**: 优化查询和适当索引
- **缓存**: 基于Redis的缓存提升性能

### 监控
- 使用Micrometer的应用指标
- 数据库性能监控
- Redis缓存统计
- JVM内存和GC监控

## 🔒 安全特性

### 安全功能
- 基于JWT的身份认证
- 基于角色的访问控制 (RBAC)
- 输入验证和清理
- SQL注入防护
- XSS防护
- CSRF防护
- 安全文件上传验证

### 安全最佳实践
- 定期依赖更新
- OWASP安全扫描
- 安全配置管理
- 审计日志
- 密码加密

## 📈 路线图

### 即将推出的功能
- [ ] 实时协作
- [ ] 移动应用
- [ ] 高级分析
- [ ] 插件系统
- [ ] 多语言支持
- [ ] 高级搜索过滤器
- [ ] 文档模板
- [ ] 工作流自动化

## 🐛 故障排除

### 常见问题

1. **数据库连接问题**
   - 检查MySQL服务状态
   - 验证连接凭据
   - 确保数据库存在

2. **Redis连接问题**
   - 检查Redis服务状态
   - 验证Redis配置
   - 检查网络连接

3. **文件上传问题**
   - 检查文件权限
   - 验证存储配置
   - 检查文件大小限制

### 获取帮助
- 查看 [Issues](https://github.com/xichen/wiki/issues) 页面
- 阅读 [文档](https://github.com/xichen/wiki/wiki)
- 加入我们的 [讨论](https://github.com/xichen/wiki/discussions)

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- Spring Boot团队提供的优秀框架
- MyBatis Plus提供的强大ORM
- 所有贡献者和项目用户

## 📞 支持

- **文档**: [Wiki](https://github.com/xichen/wiki/wiki)
- **问题**: [GitHub Issues](https://github.com/xichen/wiki/issues)
- **讨论**: [GitHub Discussions](https://github.com/xichen/wiki/discussions)
- **邮箱**: support@example.com

---

⭐ **如果这个项目对你有帮助，请给它一个星标！**

