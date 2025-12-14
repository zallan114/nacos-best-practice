#!/bin/bash

# Nacos Best Practice 启动脚本

# 颜色定义
GREEN="\033[0;32m"
YELLOW="\033[1;33m"
RED="\033[0;31m"
NC="\033[0m" # No Color

echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}   Nacos Best Practice 启动脚本   ${NC}"
echo -e "${GREEN}=====================================${NC}"

# 编译项目
echo -e "${YELLOW}[1/5] 正在编译整个项目...${NC}"
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}编译失败！${NC}"
    exit 1
fi

# 创建日志目录
mkdir -p logs

# 启动 nacos-provider
echo -e "${YELLOW}[2/5] 正在启动 nacos-provider...${NC}"
cd nacos-provider
nohup java -jar target/nacos-provider-1.0.0.jar > ../logs/provider.log 2>&1 &
echo $! > ../logs/provider.pid
cd ..
echo -e "${GREEN}nacos-provider 已启动，PID: $(cat logs/provider.pid)${NC}"

# 等待1秒
sleep 1

# 启动 nacos-consumer
echo -e "${YELLOW}[3/5] 正在启动 nacos-consumer...${NC}"
cd nacos-consumer
nohup java -jar target/nacos-consumer-1.0.0.jar > ../logs/consumer.log 2>&1 &
echo $! > ../logs/consumer.pid
cd ..
echo -e "${GREEN}nacos-consumer 已启动，PID: $(cat logs/consumer.pid)${NC}"

# 等待1秒
sleep 1

# 启动 nacos-config-demo
echo -e "${YELLOW}[4/5] 正在启动 nacos-config-demo...${NC}"
cd nacos-config-demo
nohup java -jar target/nacos-config-demo-1.0.0.jar > ../logs/config-demo.log 2>&1 &
echo $! > ../logs/config-demo.pid
cd ..
echo -e "${GREEN}nacos-config-demo 已启动，PID: $(cat logs/config-demo.pid)${NC}"

# 等待1秒
sleep 1

# 启动 nacos-gateway
echo -e "${YELLOW}[5/5] 正在启动 nacos-gateway...${NC}"
cd nacos-gateway
nohup java -jar target/nacos-gateway-1.0.0.jar > ../logs/gateway.log 2>&1 &
echo $! > ../logs/gateway.pid
cd ..
echo -e "${GREEN}nacos-gateway 已启动，PID: $(cat logs/gateway.pid)${NC}"

echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}所有应用启动完成！${NC}"
echo -e "${GREEN}日志文件路径：$(pwd)/logs/${NC}"
echo -e "${GREEN}=====================================${NC}"