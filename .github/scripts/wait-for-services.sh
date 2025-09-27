#!/bin/bash

# 等待服务就绪脚本
# 用于GitHub Actions中等待MySQL和Redis服务启动

set -e

echo "🔍 开始检查服务状态..."

# 等待MySQL服务
echo "⏳ 等待MySQL服务启动..."
timeout 60 bash -c '
until mysqladmin ping -h"127.0.0.1" -P"3306" -u"root" -p"root123456" --silent; do 
    echo "MySQL未就绪，继续等待..."
    sleep 2
done
'
echo "✅ MySQL服务已就绪"

# 等待Redis服务 - 使用多种方法
echo "⏳ 等待Redis服务启动..."

# 方法1: 使用netcat检查端口
if command -v nc >/dev/null 2>&1; then
    echo "使用netcat检查Redis端口..."
    timeout 60 bash -c '
    until nc -z 127.0.0.1 6379; do 
        echo "Redis端口未开放，继续等待..."
        sleep 2
    done
    '
elif command -v telnet >/dev/null 2>&1; then
    echo "使用telnet检查Redis端口..."
    timeout 60 bash -c '
    until echo "quit" | telnet 127.0.0.1 6379 >/dev/null 2>&1; do 
        echo "Redis端口未开放，继续等待..."
        sleep 2
    done
    '
else
    echo "使用Docker检查Redis服务..."
    timeout 60 bash -c '
    until docker exec $(docker ps -q --filter "ancestor=redis:7-alpine") redis-cli ping >/dev/null 2>&1; do 
        echo "Redis服务未就绪，继续等待..."
        sleep 2
    done
    '
fi

echo "✅ Redis服务已就绪"
echo "🎉 所有服务检查完成！"
