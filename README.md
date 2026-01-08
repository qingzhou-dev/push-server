# push-server

基于 Spring Boot 的推送服务，提供统一的 HTTP 接口，将消息推送到企业微信等渠道。

## 运行环境

- JDK 25
- Maven（或使用 `./mvnw`）
- 本地构建 native image 需要 GraalVM JDK 25 + `native-image`

## 配置

应用会额外加载 `./config/` 目录下的配置（见 `src/main/resources/application.yml`）。
建议将生产配置放到 `config/application-prod.yml`，示例字段如下：

```yaml
push:
  auth:
    key: 替换为自己的key
  wecom:
    app-key:
    app-secret:
    agent-id:
    webhook-url:
server:
  port: 8000
```

`X-API-Key` 需要与 `push.auth.key` 保持一致。

## 本地运行

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

构建并运行 JAR：

```bash
./mvnw -DskipTests package
SPRING_PROFILES_ACTIVE=prod java -jar target/push-server-0.0.1-SNAPSHOT.jar
```

## 接口示例

```bash
curl -X POST "http://localhost:8000/api/push" \
  -H "Content-Type: application/json" \
  -H "X-API-Key: <your-key>" \
  -d '{
    "target": "@all",
    "type": "text",
    "title": "Hello",
    "content": "hello world"
  }'
```

## 本地构建 Native Image

```bash
# 需要 GraalVM JDK 25，并安装 native-image 组件
# gu install native-image
./mvnw -DskipTests native:compile
```

产物位于 `target/push-server`（Windows 为 `target/push-server.exe`）。

## GitHub Actions

仓库包含 `native-image` 工作流，会在 Linux / macOS / Windows 上构建 native image，
并将可执行文件作为构建产物上传。
