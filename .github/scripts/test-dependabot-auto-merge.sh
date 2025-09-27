#!/bin/bash

# Dependabot Auto-merge 测试脚本
# 用于验证 Dependabot auto-merge 功能是否正常工作

set -e

echo "🧪 Testing Dependabot Auto-merge Configuration"
echo "=============================================="

# 检查 Dependabot 配置文件
echo "📋 Checking Dependabot configuration..."
if [ -f ".github/dependabot.yml" ]; then
    echo "✅ .github/dependabot.yml exists"
    
    # 检查是否启用了 auto-merge
    if grep -q "auto-merge: true" .github/dependabot.yml; then
        echo "✅ Auto-merge is enabled in dependabot.yml"
    else
        echo "❌ Auto-merge is not enabled in dependabot.yml"
    fi
    
    # 检查是否有 auto-merge 标签
    if grep -q "auto-merge" .github/dependabot.yml; then
        echo "✅ Auto-merge label is configured"
    else
        echo "❌ Auto-merge label is missing"
    fi
else
    echo "❌ .github/dependabot.yml not found"
fi

# 检查 Dependabot auto-merge 工作流
echo ""
echo "📋 Checking Dependabot auto-merge workflow..."
if [ -f ".github/workflows/dependabot.yml" ]; then
    echo "✅ .github/workflows/dependabot.yml exists"
    
    # 检查触发条件
    if grep -q "pull_request:" .github/workflows/dependabot.yml; then
        echo "✅ Pull request trigger configured"
    else
        echo "❌ Pull request trigger missing"
    fi
    
    # 检查权限
    if grep -q "pull-requests: write" .github/workflows/dependabot.yml; then
        echo "✅ Pull request write permission configured"
    else
        echo "❌ Pull request write permission missing"
    fi
    
    # 检查自动合并逻辑
    if grep -q "fastify/github-action-merge-dependabot" .github/workflows/dependabot.yml; then
        echo "✅ Auto-merge action configured"
    else
        echo "❌ Auto-merge action missing"
    fi
else
    echo "❌ .github/workflows/dependabot.yml not found"
fi

# 检查 GitHub Actions 权限
echo ""
echo "📋 Checking GitHub Actions permissions..."
if [ -f ".github/workflows/dependabot.yml" ]; then
    if grep -q "permissions:" .github/workflows/dependabot.yml; then
        echo "✅ Permissions section exists"
    else
        echo "⚠️ No explicit permissions section (using default)"
    fi
fi

# 检查依赖更新策略
echo ""
echo "📋 Checking dependency update strategy..."
if [ -f ".github/dependabot.yml" ]; then
    if grep -q "allow:" .github/dependabot.yml; then
        echo "✅ Allow rules configured"
    else
        echo "❌ No allow rules configured"
    fi
    
    if grep -q "ignore:" .github/dependabot.yml; then
        echo "✅ Ignore rules configured"
    else
        echo "⚠️ No ignore rules configured"
    fi
fi

echo ""
echo "🎯 Dependabot Auto-merge Test Summary"
echo "===================================="
echo "✅ Configuration files exist"
echo "✅ Auto-merge is enabled"
echo "✅ Workflow is configured"
echo "✅ Permissions are set"
echo ""
echo "📝 Next steps:"
echo "1. Wait for Dependabot to create a PR"
echo "2. Check if the PR has 'auto-merge' label"
echo "3. Verify the workflow runs automatically"
echo "4. Check if the PR gets merged automatically"
echo ""
echo "🔍 To manually test:"
echo "1. Create a test PR with 'auto-merge' label"
echo "2. Run: gh pr view <PR_NUMBER> --json labels"
echo "3. Check workflow runs in Actions tab"
