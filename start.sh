#!/bin/bash

echo "========================================"
echo "   Wiki知识库后端服务启动脚本"
echo "========================================"
echo

echo "检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请安装JDK 17+"
    exit 1
fi
java -version

echo
echo "检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请安装Maven 3.6+"
    exit 1
fi
mvn -version

echo
echo "开始编译项目..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "错误: 项目编译失败"
    exit 1
fi

echo
echo "启动应用..."
echo "应用将在 http://localhost:8080 启动"
echo "API文档地址: http://localhost:8080/swagger-ui.html"
echo "健康检查: http://localhost:8080/api/v1/test/health"
echo
echo "按 Ctrl+C 停止应用"
echo

mvn spring-boot:run
