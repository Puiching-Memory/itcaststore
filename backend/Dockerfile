FROM maven:3.9-eclipse-temurin-25
WORKDIR /app

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    procps \
    wget \
    && rm -rf /var/lib/apt/lists/*

# 复制 Maven 配置和依赖文件（利用 Docker 缓存层）
COPY pom.xml .

# 预下载依赖（利用 Docker 缓存，加速后续构建）
RUN mvn dependency:go-offline -B || true

# 暴露端口
EXPOSE 8080

# 启动命令：使用 spring-boot:run 支持热重载
# Spring Boot DevTools 会自动检测文件变化并重启
CMD ["mvn", "spring-boot:run"]
