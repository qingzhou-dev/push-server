# push-server

基于 Spring Boot 的推送服务，提供统一的 HTTP 接口，将消息推送到企业微信等渠道。

## 运行环境

- JDK 25
- Maven（或使用 `./mvnw`）
- 本地构建 native image 需要 GraalVM JDK 25（包含 `native-image`）

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

## 服务使用

- 默认端口来自 `server.port`（示例为 8000）
- 默认 profile 由构建时的 Maven profile 决定（默认 prod），运行时可用 `SPRING_PROFILES_ACTIVE` 覆盖
- 请求地址：`POST /api/push`
- 鉴权：请求头 `X-API-Key` 必须等于 `push.auth.key`
- 支持类型（`type`）：`text`（默认）、`markdown`、`text-card`、`image`、`news`
- 通用字段：`target` 必填
- 字段要求：
  - `text`：`content`
  - `markdown`：`title`、`content`
  - `text-card`：`title`、`content`、`url`
  - `image`：`mediaId`
  - `news`：`articles`（每项必填 `title`、`url`，可选 `description`、`picUrl`）

使用 Release 里的 native image 时，将 `config/application-prod.yml` 放在可执行文件同级的 `config/` 目录，
然后直接运行 `./push-server`（Windows 为 `push-server.exe`）。

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
# 需要 GraalVM JDK 25
./mvnw -DskipTests native:compile
```

产物位于 `target/push-server`（Windows 为 `target/push-server.exe`）。

## GitHub Actions

仓库包含 `native-image` 工作流，在发布 Release 时构建 Linux / macOS / Windows 的 native image，
并将可执行文件上传到 GitHub Releases 的 assets。
