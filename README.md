# Wiki 知识管理系统

一个基于Spring Boot 3.5.6的现代化知识管理系统，采用微服务架构设计，支持文档管理、用户认证、邮箱验证、文件存储等功能。

> 📖 **English Documentation**: [README_EN.md](./README_EN.md) - Complete English documentation for international users

## ✨ 核心功能

- 📚 **文档管理**: 创建、编辑、分类、搜索文档，支持版本控制
- 👤 **用户系统**: 注册、登录、权限管理，支持邮箱验证
- 📧 **邮箱验证**: QQ邮箱验证码登录注册，安全可靠
- 🔐 **安全认证**: JWT + Spring Security，多层安全防护
- 📁 **文件存储**: 七牛云文件上传，支持多种文件格式
- 🔍 **全文搜索**: 智能文档搜索，支持高级搜索
- 📊 **数据统计**: 用户行为分析，系统监控
- 🏷️ **标签系统**: 灵活的标签管理，便于分类
- 📖 **电子书管理**: 电子书上传、阅读、书签功能

## 🚀 技术栈

- **后端框架**: Spring Boot 3.5.6
- **数据库**: MySQL 8.0 + Redis 6.0
- **ORM框架**: MyBatis Plus
- **安全框架**: Spring Security + JWT
- **邮件服务**: Spring Mail + QQ邮箱
- **文件存储**: 七牛云对象存储
- **API文档**: Swagger/OpenAPI 3.0
- **配置加密**: Jasypt
- **构建工具**: Maven 3.6+

## 📁 项目结构

```
wiki/
├── src/main/java/com/xichen/wiki/
│   ├── common/          # 通用组件
│   ├── config/          # 配置类
│   ├── constant/        # 常量定义
│   ├── controller/      # REST控制器
│   ├── dto/            # 数据传输对象
│   ├── entity/         # 实体类
│   ├── exception/      # 异常处理
│   ├── mapper/         # 数据访问层
│   ├── security/      # 安全配置
│   ├── service/       # 服务接口层
│   │   └── impl/      # 服务实现层
│   └── util/          # 工具类
├── src/main/resources/
│   ├── application.properties  # 应用配置
│   ├── templates/              # 邮件模板
│   └── sql/                   # 数据库脚本
└── src/test/                  # 测试代码
```

## 🏗️ 架构设计

### 分层架构

```
┌─────────────────────────────────────┐
│            Controller层              │  # REST API控制器
├─────────────────────────────────────┤
│            Service层                │  # 业务逻辑接口
│            └── impl/               │  # 业务逻辑实现
├─────────────────────────────────────┤
│            Mapper层                 │  # 数据访问层
├─────────────────────────────────────┤
│            Entity层                 │  # 实体类
└─────────────────────────────────────┘
```

### 核心特性

- **接口分离**：所有服务都有对应的接口，便于测试和扩展
- **配置分离**：Redis、Mail等配置独立管理，降低耦合度
- **HTML邮件**：美观的邮件模板，提升用户体验
- **安全加密**：敏感信息使用Jasypt加密存储
- **缓存优化**：Redis + 数据库混合存储验证码
- **统一架构**：所有服务层接口和实现分离，架构一致
- **代码优化**：修复了所有编译错误，统一了方法签名

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.6+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd wiki
   ```

2. **配置数据库**
   ```sql
   CREATE DATABASE wiki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **配置Redis**
   ```bash
   # 启动Redis服务
   redis-server
   ```

4. **配置应用**
   ```bash
   # 编辑配置文件
   vim src/main/resources/application.properties
   
   # 更新数据库连接
   spring.datasource.url=jdbc:mysql://localhost:3306/wiki
   spring.datasource.username=root
   spring.datasource.password=your_password
   
   # 配置QQ邮箱（可选）
   spring.mail.username=your_email@qq.com
   spring.mail.password=your_authorization_code
   ```

5. **运行项目**
   ```bash
   # 方式1: Maven运行
   mvn spring-boot:run
   
   # 方式2: 打包运行
   mvn clean package
   java -jar target/wiki.jar
   
   # 方式3: Docker运行
   docker-compose up -d
   ```

6. **访问应用**
   - 🌐 **应用地址**: http://localhost:8080
   - 📚 **API文档**: http://localhost:8080/swagger-ui.html
   - 🔧 **健康检查**: http://localhost:8080/actuator/health

## 🔐 安全配置

### 敏感信息加密

项目使用Jasypt对敏感信息进行加密保护：

```properties
# 数据库密码加密
spring.datasource.password=ENC(加密后的密码)

# JWT密钥加密
jwt.secret=ENC(加密后的JWT密钥)

# 七牛云配置加密
qiniu.access-key=ENC(加密后的访问密钥)
qiniu.secret-key=ENC(加密后的秘密密钥)
```

### 生成加密值

```bash
# 使用Maven插件生成加密值
mvn jasypt:encrypt -Djasypt.encryptor.password=WikiSecretKey2024!@#

# 或使用命令行工具
java -cp "target/classes;target/dependency/*" org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI \
  input="your_plain_text" password="WikiSecretKey2024!@#" algorithm="PBEWithMD5AndDES"
```

## 📚 API文档

### 🔐 认证接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/auth/login` | POST | 用户名密码登录 |
| `/api/v1/auth/register` | POST | 用户注册 |
| `/api/v1/auth/send-verification-code` | POST | 发送邮箱验证码 |
| `/api/v1/auth/register-with-email` | POST | 邮箱验证码注册 |
| `/api/v1/auth/login-with-email` | POST | 邮箱验证码登录 |

### 📄 文档管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/documents` | GET | 获取文档列表 |
| `/api/v1/documents` | POST | 创建文档 |
| `/api/v1/documents/{id}` | GET | 获取文档详情 |
| `/api/v1/documents/{id}` | PUT | 更新文档 |
| `/api/v1/documents/{id}` | DELETE | 删除文档 |

### 👤 用户管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/users/profile` | GET | 获取用户信息 |
| `/api/v1/users/profile` | PUT | 更新用户信息 |
| `/api/v1/users/change-password` | POST | 修改密码 |
| `/api/v1/users/avatar` | POST | 更新头像 |

### 📁 文件管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/files/upload-url` | POST | 获取上传URL |
| `/api/v1/files/confirm` | POST | 确认上传 |
| `/api/v1/files/{id}` | GET | 文件下载 |

### 🔍 搜索功能

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/search` | GET | 全文搜索 |
| `/api/v1/search/advanced` | POST | 高级搜索 |

## 🗄️ 数据库设计

### 核心表结构

| 表名 | 描述 | 主要字段 |
|------|------|----------|
| `users` | 用户表 | id, username, email, password, avatar_url |
| `documents` | 文档表 | id, title, content, author_id, category_id |
| `categories` | 分类表 | id, name, description, parent_id |
| `tags` | 标签表 | id, name, color, description |
| `bookmarks` | 书签表 | id, user_id, document_id, created_time |
| `user_activities` | 用户活动表 | id, user_id, activity_type, description |
| `verification_codes` | 验证码表 | id, email, code, type, used, expire_time |

### 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE wiki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行初始化脚本
source src/main/resources/sql/schema.sql;
source src/main/resources/sql/data.sql;
source src/main/resources/sql/verification_codes.sql;
```

## 🚀 部署

### Docker部署（推荐）

```bash
# 使用Docker Compose一键部署
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f wiki
```

### 传统部署

```bash
# 1. 打包应用
mvn clean package -DskipTests

# 2. 运行应用
java -jar target/wiki.jar

# 3. 后台运行
nohup java -jar target/wiki.jar > wiki.log 2>&1 &
```

### 生产环境配置

```bash
# 设置环境变量
export JASYPT_ENCRYPTOR_PASSWORD=YourProductionSecretKey2024!@#
export SPRING_PROFILES_ACTIVE=prod

# 启动应用
java -jar target/wiki.jar
```

## 🔧 配置说明

### 核心配置

| 配置项 | 描述 | 示例值 |
|--------|------|--------|
| `spring.datasource.url` | 数据库连接 | `jdbc:mysql://localhost:3306/wiki` |
| `spring.data.redis.host` | Redis地址 | `localhost` |
| `spring.mail.username` | 邮箱用户名 | `your_email@qq.com` |
| `jwt.secret` | JWT密钥 | `ENC(加密后的密钥)` |
| `qiniu.access-key` | 七牛云访问密钥 | `ENC(加密后的密钥)` |

### 环境变量配置

```bash
# 开发环境
export SPRING_PROFILES_ACTIVE=dev
export JASYPT_ENCRYPTOR_PASSWORD=WikiSecretKey2024!@#

# 生产环境
export SPRING_PROFILES_ACTIVE=prod
export JASYPT_ENCRYPTOR_PASSWORD=YourProductionSecretKey2024!@#
```

## 🐛 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 确认数据库连接信息正确
   - 检查网络连接

2. **Redis连接失败**
   - 检查Redis服务是否启动
   - 确认Redis配置正确

3. **文件上传失败**
   - 检查七牛云配置
   - 确认网络连接正常

### 日志配置

```properties
logging.level.com.xichen.wiki=DEBUG
```

## 📝 开发指南

### 代码规范

- 使用Lombok减少样板代码
- 遵循RESTful API设计
- 统一异常处理
- 使用Swagger文档注解

### 测试

```bash
# 运行测试
mvn test

# 运行特定测试
mvn test -Dtest=UserServiceTest
```

## 🤝 贡献指南

1. Fork项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 📄 许可证

本项目采用MIT许可证。

## 📞 支持

如有问题，请提交Issue或联系开发团队。

---

**注意**: 请确保在生产环境中使用强密钥，并定期更新加密配置。