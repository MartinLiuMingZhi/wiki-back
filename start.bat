@echo off
echo ========================================
echo    Wiki知识库后端服务启动脚本
echo ========================================
echo.

echo 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请安装JDK 17+
    pause
    exit /b 1
)

echo.
echo 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请安装Maven 3.6+
    pause
    exit /b 1
)

echo.
echo 开始编译项目...
mvn clean compile
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

echo.
echo 启动应用...
echo 应用将在 http://localhost:8080 启动
echo API文档地址: http://localhost:8080/swagger-ui.html
echo 健康检查: http://localhost:8080/api/v1/test/health
echo.
echo 按 Ctrl+C 停止应用
echo.

mvn spring-boot:run

pause
