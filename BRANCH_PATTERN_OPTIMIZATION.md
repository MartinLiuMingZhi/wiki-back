# 🌟 分支模式优化：使用通配符 `v*/develop`

## 🎯 优化原因

### 为什么使用 `v*/develop` 而不是 `v1.0.0/develop`？

#### ✅ 优势
1. **版本无关性**：支持所有版本分支（v1.0.0, v1.1.0, v2.0.0 等）
2. **未来兼容**：新版本分支自动支持
3. **维护简单**：无需每次更新工作流配置
4. **灵活性**：支持不同的版本命名策略

#### ❌ 固定分支名的限制
- 只支持特定版本（如 v1.0.0）
- 新版本需要手动更新工作流
- 维护成本高

## 📋 当前配置

### 更新后的工作流配置

#### 1. Unified CI/CD Pipeline
```yaml
on:
  push:
    branches: [ main, v*/develop ]  # ✅ 支持所有版本分支
  pull_request:
    branches: [ main, v*/develop ]
```

#### 2. Release Workflow
```yaml
on:
  push:
    branches: [ v*/develop ]  # ✅ 支持所有版本分支
```

#### 3. Dependabot (无需更改)
```yaml
on:
  pull_request:
    types: [opened, synchronize, labeled, ready_for_review]
  # 不依赖特定分支，保持原样
```

## 🔍 支持的分支模式

### 当前支持的分支
```
✅ main                    # 主分支
✅ v1.0.0/develop         # 当前版本开发分支
✅ v1.1.0/develop         # 未来版本分支
✅ v2.0.0/develop         # 未来版本分支
✅ v*/develop             # 所有版本分支（通配符）
```

### 分支命名策略
```
v1.0.0/develop    # 版本 1.0.0 开发分支
v1.1.0/develop    # 版本 1.1.0 开发分支
v2.0.0/develop    # 版本 2.0.0 开发分支
```

## 🚀 实际效果

### 触发条件
- **推送到 `main`** → 触发 CI/CD
- **推送到 `v*/develop`** → 触发 CI/CD + Release
- **创建 PR 到 `main` 或 `v*/develop`** → 触发 CI/CD

### 支持的版本分支示例
```bash
# 这些分支都会触发工作流
v1.0.0/develop
v1.1.0/develop
v1.2.0/develop
v2.0.0/develop
v2.1.0/develop
```

## 🔧 其他通配符选项

### 更灵活的配置选项

#### 选项1：支持所有develop分支
```yaml
branches: [ main, '**/develop' ]
# 支持：develop, v1.0.0/develop, feature/develop 等
```

#### 选项2：支持版本和标准分支
```yaml
branches: [ main, develop, v*/develop ]
# 支持：main, develop, v1.0.0/develop 等
```

#### 选项3：更严格的版本模式
```yaml
branches: [ main, 'v[0-9]*/develop' ]
# 只支持数字版本：v1.0.0/develop, v2.1.0/develop
```

## 📊 配置对比

| 配置方式 | 灵活性 | 维护性 | 推荐度 |
|----------|--------|--------|--------|
| `v1.0.0/develop` | ❌ 低 | ❌ 需要手动更新 | ⭐⭐ |
| `v*/develop` | ✅ 高 | ✅ 自动支持 | ⭐⭐⭐⭐⭐ |
| `**/develop` | ✅ 很高 | ✅ 完全自动 | ⭐⭐⭐⭐ |

## 🎯 最佳实践建议

### 推荐配置
```yaml
# 最灵活的配置
on:
  push:
    branches: [ main, develop, v*/develop ]
  pull_request:
    branches: [ main, develop, v*/develop ]
```

### 分支策略建议
```
main:           # 生产分支
develop:        # 标准开发分支
v*/develop:     # 版本开发分支
feature/*:      # 功能分支（不触发主要工作流）
hotfix/*:       # 热修复分支
```

## 🔄 更新步骤

### 已完成的更新
1. ✅ 更新 `unified-ci-cd.yml` 使用 `v*/develop`
2. ✅ 更新 `release.yml` 使用 `v*/develop`
3. ✅ 保持 `dependabot.yml` 不变

### 验证方法
```bash
# 测试当前分支
git push origin v1.0.0/develop

# 测试其他版本分支（如果存在）
git push origin v1.1.0/develop
```

## 🎉 优化效果

### 现在的优势
- ✅ 支持所有版本分支
- ✅ 无需手动更新工作流
- ✅ 未来版本自动兼容
- ✅ 维护成本低

### 触发场景
```
推送到 v1.0.0/develop  → ✅ 触发 CI/CD + Release
推送到 v1.1.0/develop  → ✅ 触发 CI/CD + Release  
推送到 v2.0.0/develop  → ✅ 触发 CI/CD + Release
推送到 main           → ✅ 触发 CI/CD
```

---
*优化完成时间：2025年9月27日*  
*配置方式：使用通配符 `v*/develop`*  
*效果：支持所有版本分支，无需手动维护*
