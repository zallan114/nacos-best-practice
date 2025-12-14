#!/bin/bash

# Nacos Best Practice 停止脚本

# 颜色定义
GREEN="\033[0;32m"
YELLOW="\033[1;33m"
RED="\033[0;31m"
NC="\033[0m" # No Color

echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}   Nacos Best Practice 停止脚本   ${NC}"
echo -e "${GREEN}=====================================${NC}"

# 停止 nacos-gateway
echo -e "${YELLOW}[1/4] 正在停止 nacos-gateway...${NC}"
PID=$(ps aux | grep 'nacos-gateway-1.0.0.jar' | grep -v grep | awk '{print $2}')
if [ ! -z "$PID" ]; then
    kill -9 $PID
    echo -e "${GREEN}nacos-gateway 已停止${NC}"
else
    echo -e "${YELLOW}nacos-gateway 未运行${NC}"
fi

# 停止 nacos-config-demo
echo -e "${YELLOW}[2/4] 正在停止 nacos-config-demo...${NC}"
PID=$(ps aux | grep 'nacos-config-demo-1.0.0.jar' | grep -v grep | awk '{print $2}')
if [ ! -z "$PID" ]; then
    kill -9 $PID
    echo -e "${GREEN}nacos-config-demo 已停止${NC}"
else
    echo -e "${YELLOW}nacos-config-demo 未运行${NC}"
fi

# 停止 nacos-consumer
echo -e "${YELLOW}[3/4] 正在停止 nacos-consumer...${NC}"
PID=$(ps aux | grep 'nacos-consumer-1.0.0.jar' | grep -v grep | awk '{print $2}')
if [ ! -z "$PID" ]; then
    kill -9 $PID
    echo -e "${GREEN}nacos-consumer 已停止${NC}"
else
    echo -e "${YELLOW}nacos-consumer 未运行${NC}"
fi

# 停止 nacos-provider
echo -e "${YELLOW}[4/4] 正在停止 nacos-provider...${NC}"
PID=$(ps aux | grep 'nacos-provider-1.0.0.jar' | grep -v grep | awk '{print $2}')
if [ ! -z "$PID" ]; then
    kill -9 $PID
    echo -e "${GREEN}nacos-provider 已停止${NC}"
else
    echo -e "${YELLOW}nacos-provider 未运行${NC}"
fi

echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}所有应用停止完成！${NC}"
echo -e "${GREEN}=====================================${NC}"