# Open API 参考文档 (v2)

本文档详细介绍了通过 Push Server 发送消息的 V2 Open API。

## 接口地址

**POST** `/api/v2/openapi/messages/send`

### 鉴权认证

该接口受 **API Key** 保护。您必须在请求头（Header）中包含 `X-API-Key`。
API Key 可以在 **Portal 管理后台** 的“应用管理 (Apps)”页面中生成和管理。

### 请求参数 (Request Body)

接口接收 JSON 格式的 Payload，支持以下参数。

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `toUser` | string | 选填 | 目标用户 ID，多个用户用 `\|` 分隔。使用 `@all` 发送给所有人。<br> (兼容参数: `target`) |
| `toParty` | string | 选填 | 目标部门 ID，多个部门用 `\|` 分隔。 |
| `toAll` | boolean | 选填 | 如果为 `true`，则发送给该应用下的所有用户。 |
| `msgType` | string | **是** | 消息类型。支持：`text`, `markdown`, `textcard`, `news`。<br> (兼容参数: `type`) |
| `content` | string | **是** | 消息的主要内容。 |
| `title` | string | 选填 | 消息标题（适用于 `textcard` 或 `news` 类型）。 |
| `description`| string | 选填 | 消息描述（适用于 `textcard` 类型）。 |
| `url` | string | 选填 | 点击跳转链接（适用于 `textcard` 类型）。 |
| `btnText` | string | 选填 | 按钮文字（适用于 `textcard` 类型）。 |
| `articles` | array | 选填 | 文章列表（适用于 `news` 类型）。 |

#### 关于 `@all` (全员推送) 的特殊说明
您可以通过以下任意一种方式向所有用户发送消息：
- 设置 `"toAll": true`
- 设置 `"toUser": "@all"`
- 设置 `"target": "@all"` (兼容写法)

### 请求示例

#### 1. 发送文本消息 (指定用户)
```json
{
  "toUser": "zhangsan|lisi",
  "msgType": "text",
  "content": "系统通知：服务器负载过高！"
}
```

#### 2. 发送全员消息 (使用兼容格式)
```json
{
  "target": "@all",
  "type": "text",
  "content": "注意：系统将在10分钟后进行维护。"
}
```

#### 3. 发送文本卡片消息 (TextCard)
```json
{
  "toUser": "@all",
  "msgType": "textcard",
  "title": "告警通知",
  "description": "请立即检查仪表盘状态。",
  "url": "https://dashboard.example.com",
  "btnText": "查看详情"
}
```

### 响应 (Response)

API 返回标准化的 JSON 响应。

```json
{
  "success": true,
  "message": "ok",
  "data": {
    "id": 1024,
    "createdAt": 1705651200000,
    "success": 1,
    "requestJson": "...",
    "responseJson": "..."
  }
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `success` | boolean | 请求处理状态（成功为 true）。 |
| `message` | string | 状态消息或错误描述。 |
| `data` | object | 消息日志详情（成功时返回）。 |