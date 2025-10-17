#!/bin/bash

echo "🚗 坦克大战游戏服务器启动脚本"
echo "================================"

echo "检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境，请先安装Java 11或更高版本"
    exit 1
fi

echo "✅ Java环境检查通过"

echo "检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven环境，请先安装Maven 3.6或更高版本"
    exit 1
fi

echo "✅ Maven环境检查通过"

echo "进入后端目录..."
cd backend

echo "清理并编译项目..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ 编译失败，请检查代码"
    exit 1
fi

echo "✅ 编译成功"

echo "启动服务器..."
echo "🌐 服务器地址: http://localhost:8080"
echo "🎮 WebSocket地址: ws://localhost:8080/tank-war"
echo "📊 H2控制台: http://localhost:8080/h2-console"
echo ""
echo "按 Ctrl+C 停止服务器"
echo "================================"

mvn spring-boot:run
