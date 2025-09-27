#!/bin/bash

# 代码质量检查脚本
# 允许某些检查失败但不中断构建

set -e

echo "🔍 开始运行代码质量检查..."

# 编译项目
echo "📦 编译项目..."
./mvnw clean compile -T 1C

# SpotBugs检查
echo "🔍 运行SpotBugs检查..."
if ./mvnw spotbugs:check -T 1C; then
    echo "✅ SpotBugs检查通过"
else
    echo "⚠️ SpotBugs检查失败，但继续构建"
fi

# Checkstyle检查
echo "📋 运行Checkstyle检查..."
if ./mvnw checkstyle:check -T 1C; then
    echo "✅ Checkstyle检查通过"
else
    echo "⚠️ Checkstyle检查失败，但继续构建"
fi

# PMD检查
echo "🔧 运行PMD检查..."
if ./mvnw pmd:check -T 1C; then
    echo "✅ PMD检查通过"
else
    echo "⚠️ PMD检查失败，但继续构建"
fi

echo "✅ 代码质量检查完成"
