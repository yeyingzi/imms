
# 内网万用平台 - 需求规格说明书

## 1. 文档概述

### 1.1 文档目的
本文档旨在明确内网万用平台的需求规格，为开发团队提供完整的开发依据，包括功能需求、非功能需求、技术架构、数据库设计、API接口规范等内容。

### 1.2 项目背景
基于模块化架构理念，构建一个可扩展的内网万用平台。平台采用"模块加载器"模式，支持按需添加功能模块，满足家庭成员日常使用及平台维护管理需求。

### 1.3 文档范围
- 功能需求：用户管理、权限管理、模块管理、系统配置
- 非功能需求：性能、安全、可用性
- 技术架构：前后端技术选型、系统架构设计
- 数据库设计：数据表结构、关系设计
- API接口设计：接口规范、参数定义

### 1.4 版本历史

| 版本 | 日期 | 作者 | 修改说明 |
| :--- | :--- | :--- | :--- |
| V1.14.0 | 2026-05-17 | System Architect | 标准三层架构重构 + 文件损坏修复 |
| V1.13.0 | 2026-05-17 | System Architect | 双界面架构完善 + 路由动画 |
| V1.12.0 | 2026-05-17 | System Architect | 界面全面升级 |
| V1.10.0 | 2026-05-17 | System Architect | 界面优化（用户界面美化+后台管理精简） |
| V1.0 | 2026-05-17 | System Architect | 初始版本 |

---

## 2. 需求分析

### 2.1 业务背景

#### 2.1.1 目标用户
- **普通用户（家庭成员）**：使用平台提供的各类功能模块
- **管理员（平台维护者）**：负责平台管理、模块配置、用户管理

#### 2.1.2 核心价值
- 模块化架构：按需加载，灵活扩展
- 内网部署：安全可控，性能稳定
- 统一平台：整合各类家庭/办公应用

### 2.2 功能需求

#### 2.2.1 用户管理模块

| 需求编号 | 功能描述 | 需求来源 | 优先级 |
| :--- | :--- | :--- | :--- |
| REQ-USR-001 | 用户注册：支持手机号/邮箱注册 | 家庭成员注册 | 高 |
| REQ-USR-002 | 用户登录：支持账号密码登录 | 系统访问 | 高 |
| REQ-USR-003 | 用户角色分配：管理员/普通用户 | 权限控制 | 高 |
| REQ-USR-004 | 用户信息管理：查看/编辑用户资料 | 用户维护 | 中 |
| REQ-USR-005 | 用户状态管理：启用/禁用用户账号 | 安全管理 | 高 |

#### 2.2.2 权限管理模块

| 需求编号 | 功能描述 | 需求来源 | 优先级 |
| :--- | :--- | :--- | :--- |
| REQ-PERM-001 | 角色定义：创建/编辑/删除角色 | 权限配置 | 高 |
| REQ-PERM-002 | 权限分配：为角色分配模块访问权限 | 细粒度控制 | 高 |
| REQ-PERM-003 | 菜单权限：控制用户可见菜单 | 界面控制 | 高 |
| REQ-PERM-004 | 操作权限：控制用户操作权限（增删改查） | 功能控制 | 高 |

#### 2.2.3 模块管理模块

| 需求编号 | 功能描述 | 需求来源 | 优先级 |
| :--- | :--- | :--- | :--- |
| REQ-MOD-001 | 模块注册：将新模块接入平台 | 扩展能力 | 高 |
| REQ-MOD-002 | 模块安装：部署模块到运行环境 | 生命周期 | 高 |
| REQ-MOD-003 | 模块启用/停用：控制模块可用性 | 生命周期 | 高 |
| REQ-MOD-004 | 模块卸载：从平台移除模块 | 生命周期 | 中 |
| REQ-MOD-005 | 模块版本管理：记录模块版本信息 | 版本控制 | 中 |

#### 2.2.4 系统配置模块

| 需求编号 | 功能描述 | 需求来源 | 优先级 |
| :--- | :--- | :--- | :--- |
| REQ-SYS-001 | 平台基础配置：站点名称、Logo、主题色 | 个性化 | 中 |
| REQ-SYS-002 | 安全配置：登录超时时间、密码规则 | 安全管理 | 高 |
| REQ-SYS-003 | 日志配置：日志级别、保留期限 | 运维支持 | 中 |

#### 2.2.5 日志审计模块

| 需求编号 | 功能描述 | 需求来源 | 优先级 |
| :--- | :--- | :--- | :--- |
| REQ-AUD-001 | 操作日志记录：记录用户操作行为 | 安全审计 | 中 |
| REQ-AUD-002 | 登录日志记录：记录登录成功/失败信息 | 安全审计 | 高 |
| REQ-AUD-003 | 日志查询：支持按时间、用户、操作类型查询 | 运维排查 | 中 |

### 2.3 非功能需求

#### 2.3.1 性能需求
| 需求编号 | 描述 | 指标 |
| :--- | :--- | :--- |
| NFR-PERF-001 | 登录响应时间 | ≤ 200ms |
| NFR-PERF-002 | 页面加载时间 | ≤ 1s |
| NFR-PERF-003 | 接口响应时间 | ≤ 500ms |
| NFR-PERF-004 | 支持并发用户数 | ≥ 100 |

#### 2.3.2 安全需求
| 需求编号 | 描述 |
| :--- | :--- |
| NFR-SEC-001 | 密码采用BCrypt加密存储 |
| NFR-SEC-002 | 登录失败5次后账号锁定15分钟 |
| NFR-SEC-003 | 接口请求需携带JWT Token |
| NFR-SEC-004 | 敏感操作需二次验证 |

#### 2.3.3 可用性需求
| 需求编号 | 描述 |
| :--- | :--- |
| NFR-AVAIL-001 | 平台可用性 | ≥ 99.9% |
| NFR-AVAIL-002 | 数据备份策略 | 每日自动备份 |

#### 2.3.4 兼容性需求
| 需求编号 | 描述 |
| :--- | :--- |
| NFR-COMP-001 | 支持浏览器 | Chrome ≥ 90, Firefox ≥ 88, Edge ≥ 90 |
| NFR-COMP-002 | 部署环境 | Linux (CentOS 7+), Windows Server 2019+ |

---

## 3. 技术架构

### 3.1 技术选型

| 分类 | 技术 | 版本 | 选型理由 |
| :--- | :--- | :--- | :--- |
| 前端框架 | Vue.js | 3.x | 响应式组件化开发，性能优异，生态成熟 |
| UI组件库 | Element Plus | 2.x | Vue3官方推荐，组件丰富，文档完善 |
| 前端构建 | Vite | 6.x | 快速构建，热更新，现代化工具链 |
| 状态管理 | Pinia | 2.x | Vue3官方状态管理，简洁高效 |
| 路由管理 | Vue Router | 4.x | Vue官方路由，支持动态路由 |
| 后端框架 | Spring Boot | 3.x | 社区成熟，生态完善，便于快速开发 |
| 数据库 | MySQL | 8.x | 稳定可靠，性能优秀，社区活跃 |
| 缓存 | Redis | 7.x | 提升性能，支持Session共享 |
| 构建工具 | Maven | 3.9.x | Java项目标准构建工具 |
| Node.js | Node.js | 20.x | 前端构建及工具链支持 |

### 3.2 系统架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        前端层 (Vue3)                                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │
│  │  用户模块   │ │  权限模块   │ │  模块管理   │ │  业务模块   │   │
│  │  (User)    │ │  (Perm)     │ │  (Module)   │ │  (Custom)   │   │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │
│         │               │               │               │           │
│         └───────────────┴───────┬───────┴───────────────┘           │
│                                 ▼                                   │
│  ┌─────────────────────────────────────────────────────┐           │
│  │              路由层 (Vue Router)                     │           │
│  │    动态路由注册 / 权限拦截 / 导航守卫                 │           │
│  └─────────────────────────────────────────────────────┘           │
│                                 │                                   │
└───────────────────────────────────┼─────────────────────────────────┘
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        网关层 (API Gateway)                          │
│              统一认证 / 请求路由 / 限流熔断 / 日志记录                 │
└───────────────────────────────────┬─────────────────────────────────┘
                                    │
┌───────────────────────────────────┼─────────────────────────────────┐
│                        后端服务层 (Spring Boot)                      │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │
│  │  用户服务   │ │  权限服务   │ │  模块服务   │ │  配置服务   │   │
│  │  (Auth)    │ │  (Perm)     │ │  (Module)   │ │  (Config)   │   │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │
│         │               │               │               │           │
│         └───────────────┴───────┬───────┴───────────────┘           │
│                                 ▼                                   │
│  ┌─────────────────────────────────────────────────────┐           │
│  │              数据访问层 (MyBatis Plus)                │           │
│  │    ORM映射 / 通用CRUD / 分页查询 / 条件构造           │           │
│  └─────────────────────────────────────────────────────┘           │
│                                 │                                   │
└───────────────────────────────────┼─────────────────────────────────┘
          │                         │                                 │
          ▼                         ▼                                 ▼
┌───────────────┐         ┌───────────────┐         ┌───────────────┐
│   MySQL       │         │    Redis      │         │   模块存储    │
│   主数据      │         │   缓存/会话    │         │   (文件系统)  │
└───────────────┘         └───────────────┘         └───────────────┘
```

### 3.3 模块架构设计

#### 3.3.1 模块接口规范

所有业务模块需实现以下接口：

| 接口方法 | 功能描述 | 参数 | 返回值 |
| :--- | :--- | :--- | :--- |
| `init()` | 模块初始化 | 无 | `boolean` |
| `start()` | 启动模块 | 无 | `boolean` |
| `stop()` | 停止模块 | 无 | `boolean` |
| `destroy()` | 销毁模块 | 无 | `boolean` |
| `getMetadata()` | 获取模块元信息 | 无 | `ModuleMeta` |
| `getRoutes()` | 获取模块路由配置 | 无 | `RouteConfig[]` |

#### 3.3.2 模块元数据结构

```typescript
interface ModuleMeta {
  id: string;           // 模块唯一标识
  name: string;         // 模块名称
  version: string;      // 模块版本
  description: string;  // 模块描述
  author: string;       // 作者
  icon: string;         // 图标名称
  status: 'installed' | 'enabled' | 'disabled';  // 状态
  dependencies: string[];  // 依赖模块ID列表
}
```

---

## 4. 数据库设计

### 4.1 数据库表概览

| 表名 | 功能描述 | 关联表 |
| :--- | :--- | :--- |
| `sys_user` | 用户信息表 | `sys_role` |
| `sys_role` | 角色信息表 | `sys_user`, `sys_permission` |
| `sys_permission` | 权限信息表 | `sys_role` |
| `sys_module` | 模块信息表 | 无 |
| `sys_user_role` | 用户角色关联表 | `sys_user`, `sys_role` |
| `sys_role_permission` | 角色权限关联表 | `sys_role`, `sys_permission` |
| `sys_operation_log` | 操作日志表 | `sys_user` |
| `sys_login_log` | 登录日志表 | `sys_user` |
| `sys_config` | 系统配置表 | 无 |

### 4.2 数据表详细设计

#### 4.2.1 用户信息表 (`sys_user`)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 用户ID |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| `password` | VARCHAR(255) | NOT NULL | 密码(BCrypt加密) |
| `real_name` | VARCHAR(50) | | 真实姓名 |
| `phone` | VARCHAR(20) | UNIQUE | 手机号 |
| `email` | VARCHAR(100) | UNIQUE | 邮箱 |
| `avatar` | VARCHAR(255) | | 头像URL |
| `status` | TINYINT | DEFAULT 1 | 状态(0禁用,1启用) |
| `created_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| `updated_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

#### 4.2.2 角色信息表 (`sys_role`)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 角色ID |
| `name` | VARCHAR(50) | NOT NULL, UNIQUE | 角色名称 |
| `code` | VARCHAR(50) | NOT NULL, UNIQUE | 角色编码 |
| `description` | VARCHAR(255) | | 角色描述 |
| `created_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| `updated_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

#### 4.2.3 权限信息表 (`sys_permission`)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 权限ID |
| `name` | VARCHAR(50) | NOT NULL | 权限名称 |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE | 权限编码（程序判断依据） |
| `type` | TINYINT | NOT NULL | 类型(1菜单,2按钮) |
| `path` | VARCHAR(255) | | 菜单路径（预留字段） |
| `parent_id` | BIGINT | DEFAULT 0 | 父级权限ID（预留字段） |
| `sort_order` | INT | DEFAULT 0 | 排序号 |
| `created_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

> **说明**：模块与权限通过权限编码前缀隐式关联（如 `example-module:view`），无需 `module_id` 外键。

#### 4.2.4 模块信息表 (`sys_module`)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 模块ID |
| `module_key` | VARCHAR(100) | NOT NULL, UNIQUE | 模块唯一标识 |
| `name` | VARCHAR(100) | NOT NULL | 模块名称 |
| `version` | VARCHAR(20) | NOT NULL | 模块版本 |
| `description` | VARCHAR(500) | | 模块描述 |
| `author` | VARCHAR(100) | | 作者（预留字段） |
| `icon` | VARCHAR(100) | | 图标名称 |
| `status` | TINYINT | DEFAULT 1 | 状态(0停用,1启用) |
| `created_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| `updated_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

> **说明**：采用编译时合并（冷插拔）机制，模块代码在构建阶段合并到主项目，无需 `install_path` 运行时路径。

#### 4.2.5 用户角色关联表 (`sys_user_role`)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| `user_id` | BIGINT | PRIMARY KEY, FOREIGN KEY | 用户ID |
| `role_id` | BIGINT | PRIMARY KEY, FOREIGN KEY | 角色ID |

#### 4.2.6 角色权限关联表 (`sys_role_permission`)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| `role_id` | BIGINT | PRIMARY KEY, FOREIGN KEY | 角色ID |
| `permission_id` | BIGINT | PRIMARY KEY, FOREIGN KEY | 权限ID |

#### 4.2.7 操作日志表 (`sys_operation_log`)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 日志ID |
| `user_id` | BIGINT | FOREIGN KEY | 用户ID |
| `username` | VARCHAR(50) | | 用户名 |
| `module` | VARCHAR(100) | | 操作模块 |
| `action` | VARCHAR(100) | | 操作动作 |
| `description` | VARCHAR(500) | | 操作描述 |
| `ip_address` | VARCHAR(50) | | 客户端IP |
| `created_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP | 操作时间 |

#### 4.2.8 登录日志表 (`sys_login_log`)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 日志ID |
| `user_id` | BIGINT | FOREIGN KEY | 用户ID |
| `username` | VARCHAR(50) | | 用户名 |
| `login_type` | TINYINT | | 登录类型(1登录,2登出) |
| `status` | TINYINT | | 状态(0失败,1成功) |
| `error_msg` | VARCHAR(255) | | 错误信息 |
| `ip_address` | VARCHAR(50) | | 客户端IP |
| `user_agent` | VARCHAR(500) | | 浏览器信息 |
| `created_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP | 登录时间 |

#### 4.2.9 系统配置表 (`sys_config`)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 配置ID |
| `config_key` | VARCHAR(100) | NOT NULL, UNIQUE | 配置键 |
| `config_value` | TEXT | | 配置值 |
| `description` | VARCHAR(255) | | 配置说明 |
| `created_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| `updated_at` | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

### 4.3 实体关系图 (ERD)

```
sys_user 1 ─── * sys_user_role * ─── 1 sys_role
                                     │
                                     * ─── sys_role_permission * ─── 1 sys_permission

sys_module (独立模块注册表，无外键关联)

sys_user 1 ─── * sys_operation_log
sys_user 1 ─── * sys_login_log
```

> **说明**：模块与权限通过权限编码前缀隐式关联（如 `example-module:view`），无需外键约束。

---

## 5. API接口设计

### 5.1 接口规范

#### 5.1.1 通用约定

| 项目 | 规范 |
| :--- | :--- |
| 协议 | HTTPS |
| 编码 | UTF-8 |
| 接口前缀 | `/api/v1` |
| 认证方式 | JWT Token (Header: `Authorization: Bearer {token}`) |
| 响应格式 | JSON |

#### 5.1.2 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1715971200000
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `code` | INT | 状态码(200成功, 400请求错误, 401未认证, 403未授权, 500服务器错误) |
| `message` | STRING | 提示信息 |
| `data` | OBJECT/ARRAY | 响应数据 |
| `timestamp` | LONG | 时间戳 |

#### 5.1.3 分页响应格式

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

### 5.2 用户管理接口

| API路径 | HTTP方法 | 功能描述 | 所属文件 |
| :--- | :--- | :--- | :--- |
| `/api/v1/auth/login` | POST | 用户登录 | `AuthController.java` |
| `/api/v1/auth/logout` | POST | 用户登出 | `AuthController.java` |
| `/api/v1/users` | GET | 查询用户列表 | `UserController.java` |
| `/api/v1/users/{id}` | GET | 查询用户详情 | `UserController.java` |
| `/api/v1/users` | POST | 创建用户 | `UserController.java` |
| `/api/v1/users/{id}` | PUT | 更新用户信息 | `UserController.java` |
| `/api/v1/users/{id}` | DELETE | 删除用户 | `UserController.java` |
| `/api/v1/users/{id}/status` | PUT | 更新用户状态 | `UserController.java` |

#### 5.2.1 登录接口

**请求体:**
```json
{
  "username": "string (必填, 用户名)",
  "password": "string (必填, 密码)"
}
```

**成功响应:**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "roles": ["admin"],
      "permissions": ["user:view", "user:edit", "module:manage"]
    }
  },
  "timestamp": 1715971200000
}
```

### 5.3 角色管理接口

| API路径 | HTTP方法 | 功能描述 | 所属文件 |
| :--- | :--- | :--- | :--- |
| `/api/v1/roles` | GET | 查询角色列表 | `RoleController.java` |
| `/api/v1/roles/{id}` | GET | 查询角色详情 | `RoleController.java` |
| `/api/v1/roles` | POST | 创建角色 | `RoleController.java` |
| `/api/v1/roles/{id}` | PUT | 更新角色 | `RoleController.java` |
| `/api/v1/roles/{id}` | DELETE | 删除角色 | `RoleController.java` |
| `/api/v1/roles/{id}/permissions` | GET | 获取角色权限 | `RoleController.java` |
| `/api/v1/roles/{id}/permissions` | PUT | 分配角色权限 | `RoleController.java` |

### 5.4 权限管理接口

| API路径 | HTTP方法 | 功能描述 | 所属文件 |
| :--- | :--- | :--- | :--- |
| `/api/v1/permissions` | GET | 查询权限列表 | `PermissionController.java` |
| `/api/v1/permissions/{id}` | GET | 查询权限详情 | `PermissionController.java` |
| `/api/v1/permissions` | POST | 创建权限 | `PermissionController.java` |
| `/api/v1/permissions/{id}` | PUT | 更新权限 | `PermissionController.java` |
| `/api/v1/permissions/{id}` | DELETE | 删除权限 | `PermissionController.java` |

### 5.5 模块管理接口

| API路径 | HTTP方法 | 功能描述 | 所属文件 |
| :--- | :--- | :--- | :--- |
| `/api/v1/modules` | GET | 查询模块列表 | `ModuleController.java` |
| `/api/v1/modules/{id}` | GET | 查询模块详情 | `ModuleController.java` |
| `/api/v1/modules` | POST | 注册模块 | `ModuleController.java` |
| `/api/v1/modules/{id}/install` | POST | 安装模块 | `ModuleController.java` |
| `/api/v1/modules/{id}/enable` | POST | 启用模块 | `ModuleController.java` |
| `/api/v1/modules/{id}/disable` | POST | 停用模块 | `ModuleController.java` |
| `/api/v1/modules/{id}/uninstall` | POST | 卸载模块 | `ModuleController.java` |

### 5.6 系统配置接口

| API路径 | HTTP方法 | 功能描述 | 所属文件 |
| :--- | :--- | :--- | :--- |
| `/api/v1/configs` | GET | 查询配置列表 | `ConfigController.java` |
| `/api/v1/configs/{key}` | GET | 查询配置值 | `ConfigController.java` |
| `/api/v1/configs` | POST | 创建配置 | `ConfigController.java` |
| `/api/v1/configs/{key}` | PUT | 更新配置 | `ConfigController.java` |
| `/api/v1/configs/{key}` | DELETE | 删除配置 | `ConfigController.java` |

### 5.7 日志查询接口

| API路径 | HTTP方法 | 功能描述 | 所属文件 |
| :--- | :--- | :--- | :--- |
| `/api/v1/logs/operation` | GET | 查询操作日志 | `LogController.java` |
| `/api/v1/logs/login` | GET | 查询登录日志 | `LogController.java` |

---

## 6. 部署方案

### 6.1 环境要求

| 环境 | 要求 |
| :--- | :--- |
| 操作系统 | Linux (CentOS 7+/Ubuntu 20.04+) / Windows Server 2019+ |
| JDK | 21+ |
| Node.js | 20+ |
| MySQL | 8.0+ |
| Redis | 7.0+ |
| Maven | 3.9+ |

### 6.2 端口配置

| 服务 | 端口 | 说明 |
| :--- | :--- | :--- |
| 前端 | 5173 | Vite开发服务器端口（http://localhost:5173） |
| 后端 | 8888 | Spring Boot服务端口（http://localhost:8888） |

> ⚠️ **跨域配置说明**：跨域问题由前端通过Vite开发服务器的代理功能解决，后端无需配置跨域支持。前端请求 `/api` 开头的接口会自动代理到后端服务。

### 6.3 目录结构

```
project_V/
├── backend/                    # 后端代码
│   ├── src/
│   │   └── main/
│   │       ├── java/          # Java源码
│   │       └── resources/     # 配置文件
│   └── pom.xml                # Maven配置
├── frontend/                  # 前端代码
│   ├── src/                   # Vue源码
│   ├── public/                # 静态资源
│   ├── package.json           # 依赖配置
│   └── vite.config.ts         # Vite配置
├── modules/                   # 业务模块目录
│   └── [module_name]/         # 模块文件夹
├── docker/                    # Docker配置
│   ├── docker-compose.yml     # 容器编排
│   └── .env                   # 环境变量
└── docs/                      # 文档
    └── SPEC.md                # 本文件
```

### 6.4 启动方式

#### 6.4.1 开发环境

**后端启动:**
```bash
cd backend
mvn spring-boot:run
```

**前端启动:**
```bash
cd frontend
npm install
npm run dev
```

#### 6.4.2 生产环境

**后端构建:**
```bash
cd backend
mvn clean package
java -jar target/platform-backend-1.0.0.jar
```

**前端构建:**
```bash
cd frontend
npm install
npm run build
```

#### 6.4.3 Docker部署

```bash
cd project_V
docker-compose up -d
```

---

## 7. 模块化架构设计

### 7.1 模块化设计理念

本平台采用"模块加载器"设计理念，支持按需添加功能模块。核心平台提供基础管理功能，业务模块可独立开发、测试和部署。

#### 7.1.1 核心平台 vs 业务模块

| 组件 | 说明 | 特点 |
| :--- | :--- | :--- |
| **核心平台** | 系统基础功能 | 用户管理、角色权限、系统配置等 |
| **业务模块** | 可扩展功能 | 独立开发，按需加载 |
| **模块加载器** | 基础设施 | 动态路由、菜单注册、权限管理 |

#### 7.1.2 模块目录结构

```
project_V/
├── frontend/                  # 前端核心代码
│   ├── src/
│   │   ├── views/          # 核心页面
│   │   ├── router/         # 路由配置
│   │   └── stores/         # 状态管理
│   └── vite.config.ts      # Vite配置
│
├── backend/                   # 后端核心代码
│   └── src/main/java/com/platform/
│       ├── controller/      # 核心控制器
│       ├── service/        # 核心服务
│       └── mapper/         # 核心Mapper
│
├── modules/                   # ⭐ 业务模块目录
│   ├── example-module/     # 示例模块
│   │   ├── frontend/       # 前端代码
│   │   ├── backend/       # 后端代码
│   │   └── sql/          # 数据库脚本
│   └── your-module/       # 自定义模块
│
└── docs/                     # 项目文档
```

### 7.2 模块配置规范

#### 7.2.1 module.json 配置文件

每个模块必须包含 `module.json` 配置文件：

```json
{
  "moduleKey": "module-name",
  "name": "模块名称",
  "version": "1.0.0",
  "author": "作者",
  "description": "模块描述",
  "icon": "ElementPlus图标名",
  "status": "enabled",
  "permissions": [
    {
      "code": "module-name:view",
      "name": "查看模块",
      "type": "menu"
    },
    {
      "code": "module-name:edit",
      "name": "编辑",
      "type": "button"
    }
  ],
  "menus": [
    {
      "name": "菜单名称",
      "icon": "Grid",
      "path": "/module-name",
      "permission": "module-name:view"
    }
  ]
}
```

#### 7.2.2 配置字段说明

| 字段 | 必填 | 说明 |
| :--- | :--- | :--- |
| `moduleKey` | 是 | 模块唯一标识，使用中划线分隔 |
| `name` | 是 | 模块显示名称 |
| `version` | 是 | 模块版本号，遵循语义化版本 |
| `permissions` | 是 | 模块包含的权限列表 |
| `menus` | 是 | 模块的菜单配置 |

### 7.3 前端模块开发规范

#### 7.3.1 模块入口文件

```typescript
// modules/{module-name}/frontend/src/index.ts
import type { RouteRecordRaw } from 'vue-router'

export interface ModuleConfig {
  key: string
  name: string
  version: string
  routes: RouteRecordRaw[]
  menus: MenuItem[]
  permissions: string[]
}

const routes: RouteRecordRaw[] = [
  {
    path: '/module-name',
    name: 'ModuleName',
    component: () => import('./views/Index.vue'),
    meta: {
      title: '模块名称',
      icon: 'Grid',
      permission: 'module-name:view'
    }
  }
]

const menus = [
  {
    name: '模块名称',
    icon: 'Grid',
    path: '/module-name',
    permission: 'module-name:view'
  }
]

const permissions = [
  'module-name:view',
  'module-name:edit'
]

export default {
  key: 'module-name',
  name: '模块名称',
  version: '1.0.0',
  routes,
  menus,
  permissions
} as ModuleConfig
```

#### 7.3.2 动态路由加载机制

```typescript
// frontend/src/router/index.ts
const modules = import.meta.glob('@/../../../modules/*/frontend/src/index.ts', {
  eager: true,
  import: 'default'
})

const moduleRoutes: RouteRecordRaw[] = []
Object.values(modules).forEach((moduleConfig: any) => {
  if (moduleConfig && moduleConfig.routes) {
    moduleRoutes.push(...moduleConfig.routes)
  }
})
```

#### 7.3.3 动态菜单注册

```typescript
// frontend/src/stores/menu.ts
const menuStore = useMenuStore()

const modules = import.meta.glob('@/../../../modules/*/frontend/src/index.ts', {
  eager: true,
  import: 'default'
})

Object.values(modules).forEach((moduleConfig: any) => {
  if (moduleConfig && moduleConfig.menus) {
    moduleConfig.menus.forEach((menu: MenuItem) => {
      menuStore.registerMenus([menu])
    })
  }
})
```

### 7.4 后端模块开发规范

#### 7.4.1 包结构规范

```
backend/src/main/java/com/platform/module/{module-name}/
├── controller/
│   └── ModuleController.java
├── service/
│   ├── ModuleService.java
│   └── impl/
│       └── ModuleServiceImpl.java
├── mapper/
│   └── ModuleMapper.java
└── entity/
    └── Module.java
```

#### 7.4.2 包扫描配置

```java
// PlatformApplication.java
@SpringBootApplication
@MapperScan({"com.platform.mapper", "com.platform.module.*.mapper"})
public class PlatformApplication {
    // ...
}
```

#### 7.4.3 API路径规范

```
/api/v1/{module-key}/{resource}
/api/v1/{module-key}/{resource}/{id}
```

### 7.5 数据库模块规范

#### 7.5.1 表命名规范

```
{模块前缀}_{实体名}
```

示例：
- 模块标识：`your-module`
- 表前缀：`ym_`
- 完整表名：`ym_your_entity`

#### 7.5.2 数据库脚本模板

```sql
-- ===========================================
-- 模块：your-module
-- 版本：1.0.0
-- ===========================================

CREATE TABLE IF NOT EXISTS ym_your_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 7.6 模块生命周期管理

#### 7.6.1 状态流转

```
未注册 → 已注册 → 已安装 → 已启用 → 运行中
                ↓
              已停用 ← 运行中
                ↓
             已卸载
```

#### 7.6.2 状态说明

| 状态 | 说明 | 操作 |
| :--- | :--- | :--- |
| `unregistered` | 未注册 | 上传模块包 |
| `registered` | 已注册 | 安装模块 |
| `installed` | 已安装 | 启用模块 |
| `enabled` | 已启用 | 停用模块 |
| `disabled` | 已停用 | 卸载/启用模块 |

#### 7.6.3 管理操作

| 操作 | 说明 | 数据影响 |
| :--- | :--- | :--- |
| **注册** | 上传模块包 | 识别模块 |
| **安装** | 执行SQL脚本 | 创建数据表 |
| **启用** | 加载路由菜单 | 模块可见可用 |
| **停用** | 卸载路由菜单 | 模块不可见 |
| **卸载** | 删除数据表 | 完全移除 |

### 7.7 权限编码规范

#### 7.7.1 编码格式

```
{module-key}:{resource}:{action}
```

#### 7.7.2 权限类型

| 类型 | 说明 |
| :--- | :--- |
| `menu` | 菜单权限 |
| `button` | 按钮权限 |

#### 7.7.3 权限示例

| 权限编码 | 说明 | 类型 |
| :--- | :--- | :--- |
| `example-module:view` | 查看模块 | menu |
| `example-module:list` | 列表查看 | button |
| `example-module:create` | 创建 | button |
| `example-module:edit` | 编辑 | button |
| `example-module:delete` | 删除 | button |

---

## 8. 安全方案

### 8.1 认证机制

- **JWT Token**: 无状态认证，Token有效期默认2小时
- **Refresh Token**: 用于刷新Access Token，有效期7天
- **Token存储**: 前端存储在localStorage，后端不保存Session

### 8.2 权限控制

- **RBAC模型**: 基于角色的访问控制
- **动态路由**: 根据用户权限动态生成路由
- **按钮级权限**: 细粒度控制操作权限

### 8.3 安全防护

| 措施 | 描述 |
| :--- | :--- |
| 密码加密 | BCrypt算法，强度10+ |
| 暴力破解防护 | 登录失败5次锁定15分钟 |
| XSS防护 | 前端输入过滤，后端参数校验 |
| CSRF防护 | Token验证 |
| SQL注入防护 | MyBatis参数化查询 |

---

## 9. 附录

### 9.1 状态码说明

| 状态码 | 含义 |
| :--- | :--- |
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未认证，需要登录 |
| 403 | 未授权，无访问权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 9.2 角色权限矩阵

| 权限编码 | 权限名称 | 管理员 | 普通用户 |
| :--- | :--- | :---: | :---: |
| `user:view` | 查看用户 | ✓ | ✗ |
| `user:edit` | 编辑用户 | ✓ | ✗ |
| `user:delete` | 删除用户 | ✓ | ✗ |
| `role:manage` | 角色管理 | ✓ | ✗ |
| `module:manage` | 模块管理 | ✓ | ✗ |
| `module:use` | 使用模块 | ✓ | ✓ |
| `config:manage` | 配置管理 | ✓ | ✗ |
| `log:view` | 查看日志 | ✓ | ✗ |

### 9.3 模块生命周期

```
注册 → 安装 → 启用 → 运行 → 停用 → 卸载
  ↓      ↓      ↓
  └──────┴──────┴── 可更新
```
