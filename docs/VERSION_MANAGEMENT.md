# 🏷️ 版本管理指南

## 📋 概述

本项目支持两种版本管理策略：
- **develop分支**：基于最新标签自动递增PATCH版本
- **大版本分支**：基于大版本自动递增小版本

## 🌿 分支策略

### 📊 分支类型对比
| 分支类型 | 命名规则 | 版本格式 | 用途 | 示例 |
|----------|----------|----------|------|------|
| **develop** | `develop` | v1.0.0, v1.0.1, v1.0.2 | 日常开发 | v1.0.0 → v1.0.1 |
| **大版本分支** | `v*/develop` | v1.0.0, v1.0.1, v1.0.2 | 大版本开发 | v1.0.0 → v1.0.1 |
| **main** | `main` | 无版本发布 | 代码管理 | 仅验证 |

## 🚀 版本发布流程

### 1. develop分支发布

#### 触发条件
- 推送到 `develop` 分支

#### 版本生成逻辑
```bash
# 获取最新标签
LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "v0.0.0")

# 递增PATCH版本
VERSION_NUM=$(echo $LATEST_TAG | sed 's/v//' | awk -F. '{print $1"."$2"."$3+1}')
VERSION="v$VERSION_NUM"
```

#### 示例流程
```bash
# 1. 开发功能
git checkout develop
git add .
git commit -m "feat: add new feature"
git push origin develop

# 2. 自动生成版本
# 最新标签: v1.0.0
# 新版本: v1.0.1

# 3. 自动发布
# ✅ 创建Git标签: v1.0.1
# ✅ 创建GitHub Release
# ✅ 构建Docker镜像
# ✅ 部署到生产环境
```

### 2. 大版本分支发布

#### 触发条件
- 推送到 `v*/develop` 分支（如：`v1.0.0/develop`）

#### 版本生成逻辑
```bash
# 提取大版本
MAJOR_VERSION="v1.0.0"

# 获取该大版本下的最新标签
LATEST_TAG=$(git tag --list "${MAJOR_VERSION}.*" --sort=-version:refname | head -n 1)

if [ -z "$LATEST_TAG" ]; then
    # 首次发布，从 v1.0.0 开始
    VERSION="${MAJOR_VERSION}"
else
    # 递增PATCH版本
    VERSION_NUM=$(echo $LATEST_TAG | sed 's/v//' | awk -F. '{print $1"."$2"."$3+1}')
    VERSION="v$VERSION_NUM"
fi
```

#### 示例流程
```bash
# 1. 创建大版本分支
git checkout -b v1.0.0/develop
git push origin v1.0.0/develop

# 2. 首次发布
git add .
git commit -m "feat: initial v1.0.0 release"
git push origin v1.0.0/develop

# 3. 自动生成版本
# 大版本: v1.0.0
# 首次发布: v1.0.0

# 4. 后续发布
git add .
git commit -m "feat: add v1.0.0 feature"
git push origin v1.0.0/develop

# 5. 自动生成版本
# 最新标签: v1.0.0
# 新版本: v1.0.1
```

## 📊 版本号示例

### develop分支版本
| 最新标签 | 新版本 | 说明 |
|----------|--------|------|
| v0.0.0 | v0.0.1 | 首次发布 |
| v1.0.0 | v1.0.1 | 递增PATCH |
| v1.0.5 | v1.0.6 | 继续递增 |

### 大版本分支版本
| 分支 | 最新标签 | 新版本 | 说明 |
|------|----------|--------|------|
| v1.0.0/develop | 无 | v1.0.0 | 首次发布 |
| v1.0.0/develop | v1.0.0 | v1.0.1 | 递增PATCH版本 |
| v1.0.0/develop | v1.0.5 | v1.0.6 | 继续递增 |
| v2.0.0/develop | 无 | v2.0.0 | 新大版本首次发布 |

## 🎯 使用场景

### 场景1：日常开发
```bash
# 适用于：功能开发、bug修复、小版本更新
git checkout develop
# 开发功能...
git push origin develop
# 自动发布：v1.0.0 → v1.0.1
```

### 场景2：大版本开发
```bash
# 适用于：大版本功能开发、长期开发
git checkout -b v1.0.0/develop
# 开发大版本功能...
git push origin v1.0.0/develop
# 自动发布：v1.0.0 → v1.0.1
```

### 场景3：多版本并行开发
```bash
# 同时维护多个大版本
git checkout -b v1.0.0/develop  # v1.0.0系列
git checkout -b v2.0.0/develop  # v2.0.0系列
git checkout -b v3.0.0/develop  # v3.0.0系列
```

## 🔧 手动发布

### 手动触发发布
```bash
# 在GitHub Actions页面手动触发release.yml
# 输入参数：
# - version: v2.0.0-beta
# - release_notes: Beta release for testing
# - source_branch: develop
```

### 手动创建标签
```bash
# 手动创建标签
git tag v1.0.0
git push origin v1.0.0

# 手动创建Release
# 在GitHub页面创建Release
```

## 📈 版本管理最佳实践

### ✅ 推荐做法
- **小功能开发**：使用develop分支
- **大版本开发**：使用v*/develop分支
- **版本命名**：遵循语义化版本规范
- **发布说明**：提供清晰的发布说明

### ❌ 避免做法
- **直接修改标签**：不要手动修改已发布的标签
- **跳过版本号**：不要跳过版本号
- **混乱分支**：不要在错误的分支上开发

## 🔍 故障排除

### 问题1：版本号重复
```bash
# 原因：标签已存在
# 解决：检查现有标签，使用下一个版本号
git tag --list | grep v1.0.0
```

### 问题2：分支名称错误
```bash
# 原因：分支名称不符合规范
# 解决：使用正确的分支命名
# 正确：v1.0.0/develop
# 错误：v1.0.0-develop, v1.0.0_develop
```

### 问题3：权限不足
```bash
# 原因：GitHub Actions权限不足
# 解决：检查工作流权限设置
permissions:
  contents: write
  packages: write
```

## 📞 联系信息

如有问题，请联系：
- 📧 **邮箱**：xichen@example.com
- 🐙 **GitHub**：@xichen
- 📱 **Slack**：#wiki-dev
