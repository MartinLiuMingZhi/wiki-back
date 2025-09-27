# 📚 Wiki 知识管理系统 API 文档

## 📋 接口概览

### 基础信息

- **Base URL**: `http://localhost:8080`
- **API版本**: v1
- **认证方式**: JWT Bearer Token
- **响应格式**: JSON
- **字符编码**: UTF-8
- **文档版本**: v1.0.0
- **最后更新**: 2024年12月

### 🎯 API 特性

- ✅ **RESTful设计** - 遵循REST API设计原则
- ✅ **统一响应格式** - 标准化的响应结构
- ✅ **JWT认证** - 安全的Token认证机制
- ✅ **参数验证** - 完整的请求参数验证
- ✅ **错误处理** - 统一的错误响应格式
- ✅ **API文档** - 自动生成的Swagger文档

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-01T00:00:00"
}
```

## 🔐 认证接口

### 用户注册

**POST** `/api/v1/auth/register`

**请求体**:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "123456"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

### 用户登录

**POST** `/api/v1/auth/login`

**请求体**:
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com"
    }
  }
}
```

### 发送验证码

**POST** `/api/v1/auth/send-verification-code`

**请求体**:
```json
{
  "email": "test@example.com",
  "type": "register"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "验证码发送成功"
}
```

### 邮箱验证码注册

**POST** `/api/v1/auth/register-with-email`

**请求体**:
```json
{
  "email": "test@example.com",
  "verificationCode": "123456"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "test@example.com"
    }
  }
}
```

## 👤 用户管理

### 获取用户信息

**GET** `/api/v1/users/profile`

**请求头**:
```
Authorization: Bearer <token>
```

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "avatarUrl": "https://example.com/avatar.jpg",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 更新用户信息

**PUT** `/api/v1/users/profile`

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "username": "newusername",
  "email": "newemail@example.com"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "username": "newusername",
    "email": "newemail@example.com"
  }
}
```

### 修改密码

**POST** `/api/v1/users/change-password`

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "oldPassword": "123456",
  "newPassword": "newpassword"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "密码修改成功"
}
```

### 更新头像

**POST** `/api/v1/users/avatar`

**请求头**:
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**请求体**:
```
avatar: <file>
```

**响应**:
```json
{
  "code": 200,
  "message": "头像更新成功",
  "data": {
    "avatarUrl": "https://example.com/avatar.jpg"
  }
}
```

## 📄 文档管理

### 获取文档列表

**GET** `/api/v1/documents`

**请求头**:
```
Authorization: Bearer <token>
```

**查询参数**:
- `page`: 页码 (默认: 1)
- `size`: 每页数量 (默认: 10)
- `categoryId`: 分类ID (可选)
- `keyword`: 搜索关键词 (可选)

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "文档标题",
        "content": "文档内容",
        "categoryId": 1,
        "isFavorite": false,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

### 创建文档

**POST** `/api/v1/documents`

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "title": "文档标题",
  "content": "文档内容",
  "categoryId": 1,
  "tagIds": [1, 2, 3]
}
```

**响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "title": "文档标题",
    "content": "文档内容",
    "categoryId": 1,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 获取文档详情

**GET** `/api/v1/documents/{id}`

**请求头**:
```
Authorization: Bearer <token>
```

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "title": "文档标题",
    "content": "文档内容",
    "categoryId": 1,
    "tags": [
      {"id": 1, "name": "标签1"},
      {"id": 2, "name": "标签2"}
    ],
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

### 更新文档

**PUT** `/api/v1/documents/{id}`

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "title": "更新后的标题",
  "content": "更新后的内容",
  "categoryId": 2,
  "tagIds": [1, 3, 4]
}
```

**响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "title": "更新后的标题",
    "content": "更新后的内容",
    "categoryId": 2,
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

### 删除文档

**DELETE** `/api/v1/documents/{id}`

**请求头**:
```
Authorization: Bearer <token>
```

**响应**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

## 📁 分类管理

### 获取分类列表

**GET** `/api/v1/categories`

**请求头**:
```
Authorization: Bearer <token>
```

**查询参数**:
- `type`: 分类类型 (document/ebook)

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "技术文档",
      "type": "document",
      "parentId": null,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 创建分类

**POST** `/api/v1/categories`

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "name": "新分类",
  "parentId": 1
}
```

**响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 2,
    "name": "新分类",
    "parentId": 1,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

## 🏷️ 标签管理

### 获取标签列表

**GET** `/api/v1/tags`

**请求头**:
```
Authorization: Bearer <token>
```

**查询参数**:
- `page`: 页码 (默认: 1)
- `size`: 每页数量 (默认: 10)
- `keyword`: 搜索关键词 (可选)

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "Java",
        "usageCount": 10,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 50,
    "current": 1,
    "size": 10
  }
}
```

### 创建标签

**POST** `/api/v1/tags`

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "name": "新标签",
  "description": "标签描述"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "name": "新标签",
    "description": "标签描述",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

## 📖 电子书管理

### 获取电子书列表

**GET** `/api/v1/ebooks`

**请求头**:
```
Authorization: Bearer <token>
```

**查询参数**:
- `page`: 页码 (默认: 1)
- `size`: 每页数量 (默认: 10)
- `categoryId`: 分类ID (可选)
- `keyword`: 搜索关键词 (可选)

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "电子书标题",
        "author": "作者",
        "fileSize": 1024000,
        "pageCount": 100,
        "downloadCount": 10,
        "viewCount": 50,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

### 上传电子书

**POST** `/api/v1/ebooks/upload`

**请求头**:
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**请求体**:
```
file: <file>
title: 电子书标题
author: 作者
description: 描述
category: 分类
```

**响应**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "id": 1,
    "title": "电子书标题",
    "fileKey": "ebooks/2024/01/01/ebook.pdf",
    "fileSize": 1024000,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

## 🔍 搜索功能

### 全文搜索

**GET** `/api/v1/search`

**请求头**:
```
Authorization: Bearer <token>
```

**查询参数**:
- `keyword`: 搜索关键词
- `type`: 搜索类型 (document/ebook/all)
- `page`: 页码 (默认: 1)
- `size`: 每页数量 (默认: 10)

**响应**:
```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "documents": {
      "records": [...],
      "total": 10
    },
    "ebooks": {
      "records": [...],
      "total": 5
    }
  }
}
```

### 高级搜索

**POST** `/api/v1/search/advanced`

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "keyword": "搜索关键词",
  "type": "document",
  "categoryId": 1,
  "sortBy": "created_at",
  "sortOrder": "desc"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "records": [...],
    "total": 10
  }
}
```

### 搜索建议

**GET** `/api/v1/search/suggestions`

**请求头**:
```
Authorization: Bearer <token>
```

**查询参数**:
- `keyword`: 搜索关键词
- `limit`: 建议数量 (默认: 10)

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": ["Java", "Spring", "数据库", "前端"]
}
```

## 📁 文件管理

### 获取上传URL

**POST** `/api/v1/files/upload-url`

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "fileName": "example.pdf",
  "fileSize": 1024000,
  "fileType": "application/pdf"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "uploadUrl": "https://upload.qiniu.com/...",
    "fileKey": "files/2024/01/01/example.pdf",
    "expires": 3600
  }
}
```

### 确认上传

**POST** `/api/v1/files/confirm`

**请求头**:
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "fileKey": "files/2024/01/01/example.pdf",
  "fileName": "example.pdf",
  "fileSize": 1024000
}
```

**响应**:
```json
{
  "code": 200,
  "message": "确认成功",
  "data": {
    "id": 1,
    "fileName": "example.pdf",
    "fileUrl": "https://example.com/files/example.pdf",
    "fileSize": 1024000
  }
}
```

## 📊 统计功能

### 获取用户统计

**GET** `/api/v1/statistics/user`

**请求头**:
```
Authorization: Bearer <token>
```

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "documentCount": 10,
    "ebookCount": 5,
    "bookmarkCount": 20,
    "totalViews": 100
  }
}
```

### 获取系统统计

**GET** `/api/v1/statistics/system`

**请求头**:
```
Authorization: Bearer <token>
```

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalUsers": 100,
    "totalDocuments": 500,
    "totalEbooks": 200,
    "totalViews": 10000
  }
}
```

## 🔧 系统监控

### 健康检查

**GET** `/api/v1/system/health`

**响应**:
```json
{
  "code": 200,
  "message": "系统运行正常",
  "data": {
    "status": "UP",
    "timestamp": "2024-01-01T00:00:00",
    "message": "Wiki知识管理系统运行正常"
  }
}
```

### 系统信息

**GET** `/api/v1/system/info`

**响应**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "application": "Wiki知识管理系统",
    "version": "1.0.0",
    "javaVersion": "17.0.1",
    "springBootVersion": "3.5.6",
    "timestamp": "2024-01-01T00:00:00"
  }
}
```

## ❌ 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token无效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 📝 注意事项

1. **认证**: 除登录、注册接口外，所有接口都需要在请求头中携带JWT Token
2. **分页**: 支持分页的接口都使用`page`和`size`参数
3. **文件上传**: 文件上传接口使用`multipart/form-data`格式
4. **时间格式**: 所有时间字段都使用ISO 8601格式
5. **字符编码**: 所有请求和响应都使用UTF-8编码

---

**注意**: 本API文档会随着系统更新持续维护，请关注最新版本。
