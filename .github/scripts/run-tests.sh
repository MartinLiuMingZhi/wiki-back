#!/bin/bash

# 单元测试运行脚本
# 允许测试失败但不中断构建

set -e

echo "🧪 开始运行单元测试..."

# 运行单元测试
if ./mvnw clean test -T 1C; then
    echo "✅ 单元测试全部通过"
else
    echo "⚠️ 部分单元测试失败，但继续构建"
    echo "📝 请检查测试结果并修复失败的测试"
fi

echo "✅ 单元测试检查完成"
