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

# 拉取最新代码
# echo -e "${YELLOW}正在拉取最新代码...${NC}"
# git pull

# 编译项目
# echo -e "${YELLOW}[1/6] 正在编译整个项目...${NC}"
# mvn clean package -DskipTests

# if [ $? -ne 0 ]; then
#     echo -e "${RED}编译失败！${NC}"
#     exit 1
# fi

# 启动 nacos-provider 实例 1
echo -e "${YELLOW}[1/6] 正在启动 nacos-provider 实例 1 (端口 8081)...${NC}"
cd nacos-provider
java -jar target/nacos-provider-1.0.0.jar > /dev/null 2>&1 &
cd ..
echo -e "${GREEN}nacos-provider 实例 1 已启动${NC}"

# 等待1秒
 sleep 1

# 启动 nacos-provider 实例 2
echo -e "${YELLOW}[2/6] 正在启动 nacos-provider 实例 2 (端口 8082)...${NC}"
cd nacos-provider
java -jar -Dserver.port=8082 target/nacos-provider-1.0.0.jar > /dev/null 2>&1 &
cd ..
echo -e "${GREEN}nacos-provider 实例 2 已启动${NC}"

# 等待1秒
sleep 1

# 启动 nacos-consumer
echo -e "${YELLOW}[4/6] 正在启动 nacos-consumer...${NC}"
cd nacos-consumer
java -jar target/nacos-consumer-1.0.0.jar > /dev/null 2>&1 &
cd ..
echo -e "${GREEN}nacos-consumer 已启动${NC}"

# 等待1秒
sleep 1

# 启动 nacos-config-demo
echo -e "${YELLOW}[5/6] 正在启动 nacos-config-demo...${NC}"
cd nacos-config-demo
java -jar target/nacos-config-demo-1.0.0.jar > /dev/null 2>&1 &
cd ..
echo -e "${GREEN}nacos-config-demo 已启动${NC}"

# 等待1秒
sleep 1

# 启动 nacos-gateway
echo -e "${YELLOW}[6/6] 正在启动 nacos-gateway...${NC}"
cd nacos-gateway
java -jar target/nacos-gateway-1.0.0.jar > /dev/null 2>&1 &
cd ..
echo -e "${GREEN}nacos-gateway 已启动${NC}"

echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}所有应用启动完成！${NC}"
echo -e "${GREEN}=====================================${NC}"