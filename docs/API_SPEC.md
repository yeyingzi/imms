# 内网万用平台 - API接口规范

## 1. 文档概述

### 1.1 文档目的
本文档定义了内网万用平台前后端的API接口规范，包括接口定义、请求格式、响应格式、错误处理等内容，为前端和后端开发提供统一的接口标准。

### 1.2 适用范围
- 前端开发团队：按照本文档规范调用后端接口
- 后端开发团队：按照本文档规范实现接口功能
- 测试团队：按照本文档进行接口测试

---

## 2. 接口基础规范

### 2.1 接口地址

| 环境 | Base URL | 说明 |
| :--- | :--- | :--- |
| 后端服务 | `http://localhost:8888` | 后端直接访问 |
| 前端代理 | `http://localhost:5173` | 前端开发服务器（通过代理访问后端） |
| 生产环境 | `https://api.platform.com` | 完整API地址 |

### 2.2 接口路径规范

| 层级 | 路径 | 说明 |
| :--- | :--- | :--- |
| 后端Context Path | `/` | 无 |
| Controller路径 | `/api/v1/xxx` | 后端接口完整路径 |
| 前端请求路径 | `/v1/xxx` | 前端请求路径（不含baseURL） |
| Vite代理转发 | `/api` → `http://localhost:8888` | 前端代理到后端 |

**前端API调用示例：**
```typescript
// 前端请求路径（不含baseURL）
request.get('/v1/auth/login', data)

// 实际请求路径
// 前端: /v1/auth/login
// 代理后: /api/v1/auth/login
// 后端处理: /api/v1/auth/login
```

### 2.3 认证方式
- 采用JWT Token认证
- Token通过 `Authorization` Header传递
- 格式：`Authorization: Bearer <token>`

### 2.4 请求格式

| 项目 | 规范 |
| :--- | :--- |
| 内容类型 | `application/json` |
| 字符编码 | `UTF-8` |
| 请求方法 | GET / POST / PUT / DELETE |

### 2.5 通用请求头

```http
Content-Type: application/json
Authorization: Bearer <token>
```

---

## 3. 响应格式规范

### 3.1 通用响应格式

#### 3.1.1 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1715971200000
}
```

#### 3.1.2 错误响应

```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "timestamp": 1715971200000
}
```

### 3.2 响应字段说明

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `code` | Integer | 是 | 状态码 |
| `message` | String | 是 | 提示信息 |
| `data` | Object/Array | 否 | 响应数据 |
| `timestamp` | Long | 是 | 时间戳 |

### 3.3 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1715971200000
}
```

---

## 4. HTTP状态码

### 4.1 业务状态码

| 状态码 | 含义 | 说明 |
| :--- | :--- | :--- |
| 200 | 成功 | 请求成功处理 |
| 400 | 请求错误 | 参数错误或请求格式错误 |
| 401 | 未认证 | 需要登录或Token无效 |
| 403 | 未授权 | 无权限访问该资源 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器错误 | 服务器内部错误 |

---

## 5. 认证接口

### 5.1 用户登录

**接口地址：** `POST /api/v1/auth/login`

**请求参数：**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `username` | String | 是 | 用户名 |
| `password` | String | 是 | 密码 |

**成功响应：**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "avatar": null,
      "roles": ["SUPER_ADMIN"],
      "permissions": ["user-menu", "role-menu", "module-menu", "user:view", "user:create", ...]
    }
  },
  "timestamp": 1715971200000
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `token` | String | JWT访问令牌 |
| `refreshToken` | String | JWT刷新令牌 |
| `user.roles` | String[] | 用户角色编码列表，如 `["SUPER_ADMIN"]` |
| `user.permissions` | String[] | 用户权限编码列表，如 `["user-menu", "user:view"]` |

**错误响应：**

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null,
  "timestamp": 1715971200000
}
```

### 5.2 用户登出

**接口地址：** `POST /api/v1/auth/logout`

**请求头：**

```
Authorization: Bearer <token>
```

**成功响应：**

```json
{
  "code": 200,
  "message": "退出成功",
  "data": null,
  "timestamp": 1715971200000
}
```

### 5.3 获取用户信息

**接口地址：** `GET /api/v1/auth/userinfo`

**请求头：**

```
Authorization: Bearer <token>
```

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "avatar": null,
    "roles": ["SUPER_ADMIN"],
    "permissions": ["user-menu", "role-menu", "module-menu", "user:view", "user:create", ...]
  },
  "timestamp": 1715971200000
}
```

---

## 6. 用户管理接口

### 6.1 查询用户列表

**接口地址：** `GET /api/v1/users`

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `username` | String | 否 | 用户名（模糊查询） |
| `status` | Integer | 否 | 状态（0禁用，1启用） |
| `pageNum` | Integer | 否 | 页码（默认1） |
| `pageSize` | Integer | 否 | 每页条数（默认10） |

**请求示例：**

```
GET /api/v1/users?username=admin&status=1&pageNum=1&pageSize=10
```

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "username": "admin",
        "realName": "管理员",
        "phone": "13800138000",
        "email": "admin@example.com",
        "status": 1,
        "createdAt": "2026-05-17 10:00:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1715971200000
}
```

### 6.2 查询用户详情

**接口地址：** `GET /api/v1/users/{id}`

**路径参数：**

| 参数 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | Long | 用户ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "phone": "13800138000",
    "email": "admin@example.com",
    "status": 1,
    "createdAt": "2026-05-17 10:00:00"
  },
  "timestamp": 1715971200000
}
```

### 6.3 创建用户

**接口地址：** `POST /api/v1/users`

**请求参数：**

```json
{
  "username": "newuser",
  "password": "password123",
  "realName": "新用户",
  "phone": "13900139000",
  "email": "newuser@example.com",
  "status": 1
}
```

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `username` | String | 是 | 用户名 |
| `password` | String | 是 | 密码 |
| `realName` | String | 是 | 真实姓名 |
| `phone` | String | 否 | 手机号 |
| `email` | String | 否 | 邮箱 |
| `status` | Integer | 否 | 状态（默认1） |

**成功响应：**

```json
{
  "code": 200,
  "message": "创建成功",
  "data": null,
  "timestamp": 1715971200000
}
```

### 6.4 更新用户信息

**接口地址：** `PUT /api/v1/users/{id}`

**路径参数：**

| 参数 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | Long | 用户ID |

**请求参数：**

```json
{
  "realName": "管理员",
  "phone": "13800138000",
  "email": "admin@example.com",
  "status": 1,
  "password": "newpassword"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1715971200000
}
```

### 6.5 删除用户

**接口地址：** `DELETE /api/v1/users/{id}`

**路径参数：**

| 参数 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | Long | 用户ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1715971200000
}
```

### 6.6 更新用户状态

**接口地址：** `PUT /api/v1/users/{id}/status`

**路径参数：**

| 参数 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | Long | 用户ID |

**请求参数：**

```json
{
  "status": 0
}
```

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `status` | Integer | 是 | 状态（0禁用，1启用） |

**成功响应：**

```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null,
  "timestamp": 1715971200000
}
```

### 6.7 获取用户角色

**接口地址：** `GET /api/v1/users/{id}/roles`

**路径参数：**

| 参数 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | Long | 用户ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [1, 2],
  "timestamp": 1715971200000
}
```

**响应说明：**
- 返回用户关联的角色ID列表

### 6.8 分配用户角色

**接口地址：** `PUT /api/v1/users/{id}/roles`

**路径参数：**

| 参数 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | Long | 用户ID |

**请求参数：**

```json
{
  "roles": [1, 2]
}
```

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `roles` | Long[] | 是 | 角色ID列表 |

**成功响应：**

```json
{
  "code": 200,
  "message": "角色分配成功",
  "data": null,
  "timestamp": 1715971200000
}
```

---

## 7. 角色管理接口

### 7.1 查询角色列表

**接口地址：** `GET /api/v1/roles`

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `pageNum` | Integer | 否 | 页码（默认1） |
| `pageSize` | Integer | 否 | 每页条数（默认10） |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "超级管理员",
        "code": "admin",
        "description": "拥有所有权限",
        "createdAt": "2026-05-17 10:00:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1715971200000
}
```

### 7.2 查询角色详情

**接口地址：** `GET /api/v1/roles/{id}`

**路径参数：**

| 参数 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | Long | 角色ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "超级管理员",
    "code": "admin",
    "description": "拥有所有权限",
    "createdAt": "2026-05-17 10:00:00"
  },
  "timestamp": 1715971200000
}
```

### 7.3 创建角色

**接口地址：** `POST /api/v1/roles`

**请求参数：**

```json
{
  "name": "普通用户",
  "code": "user",
  "description": "普通用户权限"
}
```

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `name` | String | 是 | 角色名称 |
| `code` | String | 是 | 角色编码 |
| `description` | String | 否 | 角色描述 |

**成功响应：**

```json
{
  "code": 200,
  "message": "创建成功",
  "data": null,
  "timestamp": 1715971200000
}
```

### 7.4 更新角色

**接口地址：** `PUT /api/v1/roles/{id}`

**请求参数：**

```json
{
  "name": "普通用户",
  "description": "普通用户权限更新"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1715971200000
}
```

### 7.5 删除角色

**接口地址：** `DELETE /api/v1/roles/{id}`

**成功响应：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1715971200000
}
```

### 7.6 获取权限列表

**接口地址：** `GET /api/v1/roles/permissions`

**说明：** 获取所有权限，用于角色权限分配

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "用户管理",
      "code": "user-menu",
      "type": 1,
      "path": "/user",
      "parentId": 0,
      "sortOrder": 10
    }
  ],
  "timestamp": 1715971200000
}
```

### 7.7 获取角色权限

**接口地址：** `GET /api/v1/roles/{id}/permissions`

**说明：** 获取指定角色已分配的权限ID列表

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [1, 2, 3, 4, 5],
  "timestamp": 1715971200000
}
```

### 7.8 分配角色权限

**接口地址：** `PUT /api/v1/roles/{id}/permissions`

**说明：** 为指定角色分配权限

**请求参数：**

```json
{
  "permissions": [1, 2, 3, 4, 5, 6, 7, 8]
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "权限分配成功",
  "data": null,
  "timestamp": 1715971200000
}
```

---

## 8. 模块管理接口

### 8.1 查询模块列表

**接口地址：** `GET /api/v1/modules`

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `pageNum` | Integer | 否 | 页码（默认1） |
| `pageSize` | Integer | 否 | 每页条数（默认10） |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "moduleKey": "user-management",
        "name": "用户管理模块",
        "version": "1.0.0",
        "author": "System",
        "status": 1,
        "description": "用户管理功能模块",
        "createdAt": "2026-05-17 10:00:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1715971200000
}
```

### 8.2 查询模块详情

**接口地址：** `GET /api/v1/modules/{id}`

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "moduleKey": "user-management",
    "name": "用户管理模块",
    "version": "1.0.0",
    "author": "System",
    "status": 2,
    "description": "用户管理功能模块",
    "createdAt": "2026-05-17 10:00:00"
  },
  "timestamp": 1715971200000
}
```

### 8.3 注册模块

**接口地址：** `POST /api/v1/modules`

**请求参数：**

```json
{
  "moduleKey": "custom-module",
  "name": "自定义模块",
  "version": "1.0.0",
  "author": "Developer",
  "installPath": "/modules/custom-module",
  "description": "自定义功能模块"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "模块注册成功",
  "data": null,
  "timestamp": 1715971200000
}
```

### 8.3 切换模块启用/停用状态

**接口地址：** `PUT /api/v1/modules/{id}/toggle`

**功能说明：** 切换模块的启用/停用状态。若当前为停用（status=0）则切换为启用（status=1），反之亦然。切换后前端会自动刷新菜单，停用的模块将从侧边栏消失且路由不可访问。

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": 1,
    "message": "模块已启用"
  },
  "timestamp": 1715971200000
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `status` | Integer | 切换后的状态值（0=停用, 1=启用） |
| `message` | String | 状态描述 |

> **注意：** 本平台未实现独立的 install/uninstall/enable/disable 接口。模块的安装通过将代码放入 `modules/` 目录并重启前端完成；模块的启用/停用统一使用本 toggle 接口。

---

## 9. 系统配置接口

### 9.1 查询配置列表

**接口地址：** `GET /api/v1/configs`

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "platformName": "内网万用平台",
    "logo": "",
    "themeColor": "#409eff",
    "loginTimeout": 120,
    "passwordMinLength": 6,
    "maxLoginFailures": 5,
    "lockoutDuration": 15,
    "logRetentionDays": 90
  },
  "timestamp": 1715971200000
}
```

### 10.2 获取单个配置

**接口地址：** `GET /api/v1/configs/{key}`

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": "内网万用平台",
  "timestamp": 1715971200000
}
```

### 10.3 更新配置

**接口地址：** `PUT /api/v1/configs`

**请求参数：**

```json
{
  "platformName": "新平台名称",
  "themeColor": "#f56c6c"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "配置更新成功",
  "data": null,
  "timestamp": 1715971200000
}
```

---

## 11. 日志查询接口

### 11.1 查询操作日志

**接口地址：** `GET /api/v1/logs/operation`

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `username` | String | 否 | 用户名（模糊查询） |
| `module` | String | 否 | 操作模块 |
| `pageNum` | Integer | 否 | 页码（默认1） |
| `pageSize` | Integer | 否 | 每页条数（默认10） |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 1,
        "username": "admin",
        "module": "用户管理",
        "action": "创建用户",
        "description": "创建新用户newuser",
        "ipAddress": "192.168.1.100",
        "createdAt": "2026-05-17 10:00:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1715971200000
}
```

### 11.2 查询登录日志

**接口地址：** `GET /api/v1/logs/login`

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `username` | String | 否 | 用户名（模糊查询） |
| `status` | Integer | 否 | 状态（0失败，1成功） |
| `pageNum` | Integer | 否 | 页码（默认1） |
| `pageSize` | Integer | 否 | 每页条数（默认10） |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 1,
        "username": "admin",
        "loginType": 1,
        "status": 1,
        "errorMsg": null,
        "ipAddress": "192.168.1.100",
        "userAgent": "Mozilla/5.0...",
        "createdAt": "2026-05-17 10:00:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1715971200000
}
```

---

## 12. 仪表盘统计接口

> 管理后台首页仪表盘使用此接口获取所有统计数据，替代原有的硬编码数据。

### 12.1 获取仪表盘统计数据

**接口地址：** `GET /api/v1/dashboard/stats`

**请求头：**

```
Authorization: Bearer <token>
```

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userCount": 5,
    "moduleCount": 1,
    "operationLogCount": 128,
    "loginLogCount": 256,
    "onlineUserCount": 3
  },
  "timestamp": 1715971200000
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `userCount` | Integer | 用户总数（从 sys_user 表统计） |
| `moduleCount` | Integer | 已启用模块数（从 sys_module 表统计 status=1） |
| `operationLogCount` | Integer | 操作日志总数（从 sys_operation_log 表统计） |
| `loginLogCount` | Integer | 登录日志总数（从 sys_login_log 表统计） |
| `onlineUserCount` | Integer | 在线用户数（最近 30 分钟内有登录记录的去重用户数） |

### 12.2 获取系统信息

**接口地址：** `GET /api/v1/dashboard/system-info`

**请求头：**

```
Authorization: Bearer <token>
```

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "platformName": "内网万用平台",
    "version": "v1.12.0",
    "javaVersion": "17.0.10",
    "osName": "Windows 11",
    "uptime": "2小时35分钟"
  },
  "timestamp": 1715971200000
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `platformName` | String | 平台名称（来自系统配置） |
| `version` | String | 平台版本号 |
| `javaVersion` | String | JVM 版本 |
| `osName` | String | 操作系统名称 |
| `uptime` | String | 系统运行时长 |

---

## 13. 错误处理规范

### 13.1 错误响应格式

```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "timestamp": 1715971200000
}
```

### 13.2 常见错误场景

| 错误场景 | 状态码 | 错误信息 |
| :--- | :--- | :--- |
| 参数缺失 | 400 | "xxx不能为空" |
| 参数格式错误 | 400 | "xxx格式错误" |
| 资源不存在 | 404 | "xxx不存在" |
| 重复资源 | 400 | "xxx已存在" |
| 无权限 | 403 | "无权限访问" |
| 服务器错误 | 500 | "服务器内部错误" |

---

## 14. 接口版本管理

### 14.1 版本号规范
- 主版本号：Breaking Changes时递增
- 当前版本：v1

### 13.2 版本控制
- 通过URL路径控制：`/api/v1/xxx`
- 向下兼容的变更可以不发新版本
- Breaking Changes必须发布新版本

---

## 15. 附录

### 15.1 数据类型说明

| 类型 | 说明 |
| :--- | :--- |
| String | 字符串 |
| Integer | 整数 |
| Long | 长整数 |
| Boolean | 布尔值 |
| Object | 对象 |
| Array | 数组 |
| DateTime | 日期时间（格式：yyyy-MM-dd HH:mm:ss） |

### 15.2 状态值说明

**用户状态：**
- `0`：禁用
- `1`：启用

**模块状态：**
- `0`：停用（模块菜单隐藏，路由不可访问）
- `1`：启用（模块正常显示和可用）

**登录日志状态：**
- `0`：失败
- `1`：成功

**权限类型：**
- `1`：菜单
- `2`：按钮

**登录类型：**
- `1`：登录
- `2`：登出
