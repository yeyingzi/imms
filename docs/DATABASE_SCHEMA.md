# 数据库表结构文档

> 版本: v1.13.0 | 更新日期: 2026-05-17

---

## 目录

1. [数据库概览](#数据库概览)
2. [表结构详解](#表结构详解)
   - [sys_user 用户表](#1-sys_user-用户表)
   - [sys_role 角色表](#2-sys_role-角色表)
   - [sys_permission 权限表](#3-sys_permission-权限表)
   - [sys_module 模块表](#4-sys_module-模块表)
   - [sys_user_role 用户角色关联表](#5-sys_user_role-用户角色关联表)
   - [sys_role_permission 角色权限关联表](#6-sys_role_permission-角色权限关联表)
   - [sys_operation_log 操作日志表](#7-sys_operation_log-操作日志表)
   - [sys_login_log 登录日志表](#8-sys_login_log-登录日志表)
   - [sys_config 系统配置表](#9-sys_config-系统配置表)
3. [ER 关系图](#er-关系图)
4. [字段使用状态总览](#字段使用状态总览)
5. [设计决策说明](#设计决策说明)

---

## 数据库概览

| 属性 | 值 |
|:---|:---|
| **数据库名称** | `platform` |
| **字符集** | `utf8mb4` |
| **排序规则** | `utf8mb4_unicode_ci` |
| **存储引擎** | `InnoDB` |
| **表总数** | 9 张 |

### 表分类

| 分类 | 表名 | 说明 |
|:---|:---|:---|
| **核心业务** | sys_user, sys_role, sys_permission, sys_module | RBAC 权限 + 模块化架构 |
| **关联关系** | sys_user_role, sys_role_permission | 多对多关系映射 |
| **审计日志** | sys_operation_log, sys_login_log | 操作追踪与安全审计 |
| **系统配置** | sys_config | KV 形式配置存储 |

---

## 表结构详解

### 1. sys_user 用户表

存储系统所有用户的基本信息。

```sql
CREATE TABLE sys_user (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username        VARCHAR(50) NOT NULL UNIQUE      COMMENT '用户名(登录凭证)',
    password        VARCHAR(255) NOT NULL            COMMENT '密码(BCrypt加密)',
    real_name       VARCHAR(50)                      COMMENT '真实姓名',
    phone           VARCHAR(20) UNIQUE               COMMENT '手机号',
    email           VARCHAR(100) UNIQUE              COMMENT '邮箱地址',
    avatar          VARCHAR(255)                     COMMENT '头像URL',
    status          TINYINT DEFAULT 1                COMMENT '状态(0禁用,1启用)',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

#### 字段说明

| 字段 | 类型 | 必填 | 默认值 | 使用状态 | 说明 |
|:---|:---|:---:|:---|:---:|:---|
| `id` | BIGINT | PK | AUTO | ✅ 活跃 | 主键，自增 |
| `username` | VARCHAR(50) | UNIQUE | — | ✅ 核心 | 登录凭证，全局唯一 |
| `password` | VARCHAR(255) | YES | — | ✅ 核心 | BCrypt 加密存储 |
| `real_name` | VARCHAR(50) | NO | NULL | ✅ 活跃 | 显示名称，profile 页可编辑 |
| `phone` | VARCHAR(20) | UNIQUE | NULL | ✅ 活跃 | 联系方式，profile 页可编辑 |
| `email` | VARCHAR(100) | UNIQUE | NULL | ✅ 活跃 | 联系方式，profile 页可编辑 |
| `avatar` | VARCHAR(255) | NO | NULL | 🔵 预留 | 头像 URL，待实现上传功能 |
| `status` | TINYINT | NO | 1 | ✅ 核心 | 账号启用/禁用状态 |
| `created_at` | DATETIME | NO | NOW() | 🟢 审计 | 记录创建时间，供未来统计使用 |
| `updated_at` | DATETIME | NO | NOW() | 🟢 审计 | 自动更新，记录最后修改时间 |

#### 索引

| 索引名 | 字段 | 类型 | 用途 |
|:---|:---|:---|:---|
| idx_username | username | UNIQUE | 登录查询、唯一性约束 |
| idx_status | status | NORMAL | 按状态筛选用户列表 |

#### 使用场景
- **认证**: `AuthController.login()` 按 username 查询 + 密码验证
- **用户管理**: `UserController` CRUD + 状态切换
- **个人信息**: `Profile` 页面读取/编辑 real_name/phone/email
- **JWT**: token 中携带 userId + username

#### 未来扩展
- `avatar`: 实现头像上传功能（OSS/本地存储）
- `created_at`: 支持「按注册时间排序」的用户列表
- `updated_at`: 支持数据变更追踪

---

### 2. sys_role 角色表

定义系统中所有角色。

```sql
CREATE TABLE sys_role (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    name          VARCHAR(50) NOT NULL             COMMENT '角色名称',
    code          VARCHAR(50) NOT NULL UNIQUE      COMMENT '角色编码(如 SUPER_ADMIN)',
    description   VARCHAR(255)                     COMMENT '角色描述',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

#### 字段说明

| 字段 | 类型 | 必填 | 默认值 | 使用状态 | 说明 |
|:---|:---|:---:|:---|:---:|:---|
| `id` | BIGINT | PK | AUTO | ✅ 活跃 | 主键 |
| `name` | VARCHAR(50) | YES | — | ✅ 活跃 | 显示名称（如「超级管理员」） |
| `code` | VARCHAR(50) | UNIQUE | — | ✅ 核心 | 编码标识，用于程序判断（SUPER_ADMIN / NORMAL_USER） |
| `description` | VARCHAR(255) | NO | NULL | ✅ 活跃 | 角色说明文字 |
| `created_at` | DATETIME | NO | NOW() | 🟢 审计 | 创建时间 |
| `updated_at` | DATETIME | NO | NOW() | 🟢 审计 | 更新时间 |

#### 初始数据

| id | name | code | description |
|:---:|:---|:---|:---|
| 1 | 超级管理员 | SUPER_ADMIN | 拥有系统所有权限 |
| 2 | 普通用户 | NORMAL_USER | 普通用户角色 |

#### 使用场景
- **角色管理**: `RoleController` CRUD
- **权限分配**: 通过 `sys_role_permission` 关联权限
- **前端判断**: `userStore.userInfo.roles.includes('SUPER_ADMIN')`

---

### 3. sys_permission 权限表

定义系统中所有细粒度权限项。

> **v1.13.0 变更**: 移除了 `module_id` 字段。模块与权限的关系通过**权限编码前缀**隐式建立（如 `example-module:view` 属于 example-module）。

```sql
CREATE TABLE sys_permission (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    name        VARCHAR(50) NOT NULL             COMMENT '权限名称(中文显示)',
    code        VARCHAR(100) NOT NULL UNIQUE     COMMENT '权限编码(程序使用)',
    type        TINYINT NOT NULL                 COMMENT '类型(1菜单,2按钮)',
    path        VARCHAR(255)                     COMMENT '菜单路由路径',
    parent_id   BIGINT DEFAULT 0                 COMMENT '父级权限ID(树形结构)',
    sort_order  INT DEFAULT 0                    COMMENT '排序号',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);
```

#### 字段说明

| 字段 | 类型 | 必填 | 默认值 | 使用状态 | 说明 |
|:---|:---|:---:|:---|:---:|:---|
| `id` | BIGINT | PK | AUTO | ✅ 活跃 | 主键 |
| `name` | VARCHAR(50) | YES | — | ✅ 核心 | 权限中文名称（如「查看用户」），用于前端展示 |
| `code` | VARCHAR(100) | UNIQUE | — | ✅ 核心 | 权限编码（如 `user:view`），程序判断依据 |
| `type` | TINYINT | YES | — | 🔵 预留 | 1=菜单权限, 2=按钮权限；当前硬编码菜单时未使用 |
| `path` | VARCHAR(255) | NO | NULL | 🔵 预留 | 菜单对应的路由路径；动态菜单功能待实现 |
| `parent_id` | BIGINT | NO | 0 | 🔵 预留 | 父节点 ID；用于构建树形菜单结构 |
| `sort_order` | INT | NO | 0 | ✅ 活跃 | 同级排序权重 |
| `created_at` | DATETIME | NO | NOW() | 🟢 审计 | 创建时间 |

#### 权限编码规范

```
{模块前缀}:{操作}

系统内置权限:
├── user:view / create / edit / delete / assign-roles
├── role:view / create / edit / delete / assign-permissions
├── module:view / install / uninstall / enable / disable
├── config:view / edit
├── log:view / export / delete
└── admin-access (后台访问权限)

模块自定义权限 (以示例模块为例):
└── example-module:view / list / create / edit / delete
```

#### 设计决策：为什么移除 module_id？

| 方案 | 说明 | 选择原因 |
|:---|:---|:---|
| ~~module_id 外键~~ | 通过外键显式关联模块 | ❌ 当前系统采用冷插拔，模块权限在初始化时一次性写入，module_id 全部为 NULL |
| **编码前缀隐式关联** | 通过 `example-module:*` 前缀识别所属模块 | ✅ 更灵活，无需维护外键关系，且支持模块卸载后权限保留 |

#### 未来扩展
- 当实现**动态菜单渲染**功能时，`type`、`path`、`parent_id` 三个字段将被启用：
  - `type=1` 的权限作为菜单节点渲染到侧边栏
  - `path` 定义菜单点击后的路由跳转路径
  - `parent_id` 构建多级菜单树

---

### 4. sys_module 模块表

注册所有已安装的业务模块。

> **v1.13.0 变更**: 移除了 `install_path` 字段。当前采用**编译时合并（冷插拔）**机制，模块代码在构建阶段合并到主项目，无需记录运行时路径。

```sql
CREATE TABLE sys_module (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模块ID',
    module_key    VARCHAR(100) NOT NULL UNIQUE      COMMENT '模块唯一标识(如 example-module)',
    name          VARCHAR(100) NOT NULL             COMMENT '模块显示名称',
    version       VARCHAR(20) NOT NULL              COMMENT '模块版本号(语义化版本)',
    description   VARCHAR(500)                     COMMENT '模块功能描述',
    author        VARCHAR(100)                     COMMENT '模块作者',
    icon          VARCHAR(100)                     COMMENT 'Element Plus 图标组件名',
    status        TINYINT DEFAULT 1                COMMENT '状态(0停用,1启用)',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

#### 字段说明

| 字段 | 类型 | 必填 | 默认值 | 使用状态 | 说明 |
|:---|:---|:---:|:---|:---:|:---|
| `id` | BIGINT | PK | AUTO | ✅ 活跃 | 主键 |
| `module_key` | VARCHAR(100) | UNIQUE | — | ✅ 核心 | 模块唯一标识，对应目录名 |
| `name` | VARCHAR(100) | YES | — | ✅ 核心 | 显示名称（首页卡片标题） |
| `version` | VARCHAR(20) | YES | — | ✅ 活跃 | 版本号，首页卡片展示 |
| `description` | VARCHAR(500) | NO | NULL | ✅ 活跃 | 功能描述，首页卡片副标题 |
| `author` | VARCHAR(100) | NO | NULL | 🔵 预留 | 作者信息，可用于模块详情页 |
| `icon` | VARCHAR(100) | NO | NULL | ✅ 活跃 | Element Plus 图标名（如 `Box`、`Document`） |
| `status` | TINYINT | NO | 1 | ✅ 核心 | 运行时启停控制（软禁用） |
| `created_at` | DATETIME | NO | NOW() | 🟢 审计 | 创建时间 |
| `updated_at` | DATETIME | NO | NOW() | 🟢 审计 | 更新时间 |

#### 使用场景
- **首页展示**: `menuStore.loadModuleMenus()` 读取启用状态的模块，渲染为卡片
- **模块管理后台**: `ModuleController.list()` + toggle 启停切换
- **路由守卫**: 停用模块的 URL 访问会被拦截重定向

#### 初始数据

| module_key | name | version | author | description | icon | status |
|:---|:---|:---|:---|:---|:---|:---:|
| example-module | 示例模块 | 1.0.0 | Platform Team | 示例模块，用于展示模块开发规范 | Box | 1 |

---

### 5. sys_user_role 用户角色关联表

用户与角色的多对多关系映射。

```sql
CREATE TABLE sys_user_role (
    user_id   BIGINT NOT NULL COMMENT '用户ID',
    role_id   BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
);
```

#### 字段说明

| 字段 | 类型 | 必填 | 使用状态 | 说明 |
|:---|:---|:---:|:---:|:---|
| `user_id` | BIGINT | PK+FK | ✅ 核心 | 关联 sys_user.id |
| `role_id` | BIGINT | PK+FK | ✅ 核心 | 关联 sys_role.id |

#### 使用场景
- **登录时**: `AuthController.login()` 查询用户拥有的所有角色编码
- **角色分配**: `UserController.assignRoles()` 分配/修改用户角色
- **权限计算**: 通过 role → permission 链路获取用户最终权限列表

---

### 6. sys_role_permission 角色权限关联表

角色与权限的多对多关系映射。

```sql
CREATE TABLE sys_role_permission (
    role_id       BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
);
```

#### 字段说明

| 字段 | 类型 | 必填 | 使用状态 | 说明 |
|:---|:---|:---:|:---:|:---|
| `role_id` | BIGINT | PK+FK | ✅ 核心 | 关联 sys_role.id |
| `permission_id` | BIGINT | PK+FK | ✅ 核心 | 关联 sys_permission.id |

#### 使用场景
- **登录时**: 查询角色关联的所有权限编码，返回给前端
- **权限分配**: `RoleController.assignPermissions()` 修改角色权限
- **超级管理员**: 初始化时自动分配全部权限

---

### 7. sys_operation_log 操作日志表

记录用户在系统中的所有写操作（增删改）。

> **v1.13.0 变更**: 新增 AOP 自动记录机制。通过 `OperationLogAspect` 切面自动拦截 Controller 层的 POST/PUT/DELETE 请求并写入此表。

```sql
CREATE TABLE sys_operation_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id     BIGINT                           COMMENT '操作用户ID(未登录为NULL)',
    username    VARCHAR(50)                      COMMENT '操作用户名',
    module      VARCHAR(100)                     COMMENT '操作所属模块',
    action      VARCHAR(100)                     COMMENT '操作类型(新增/修改/删除)',
    description VARCHAR(500)                     COMMENT '操作详情(请求参数摘要)',
    ip_address  VARCHAR(50)                      COMMENT '客户端IP地址',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
);
```

#### 字段说明

| 字段 | 类型 | 必填 | 默认值 | 使用状态 | 说明 |
|:---|:---|:---:|:---|:---:|:---|
| `id` | BIGINT | PK | AUTO | ✅ 活跃 | 主键 |
| `user_id` | BIGINT | NO | NULL | ✅ 活跃 | 操作者用户 ID，未登录时为 NULL |
| `username` | VARCHAR(50) | NO | NULL | ✅ 活跃 | 操作者用户名，支持模糊搜索 |
| `module` | VARCHAR(100) | NO | NULL | ✅ 活跃 | 自动提取（用户管理/角色管理/...） |
| `action` | VARCHAR(100) | NO | NULL | ✅ 活跃 | 自动生成（POST→新增, PUT→修改, DELETE→删除） |
| `description` | VARCHAR(500) | NO | NULL | ✅ 活跃 | 请求参数 JSON 摘要（截断 200 字符）+ 执行耗时 |
| `ip_address` | VARCHAR(50) | NO | NULL | ✅ 活跃 | 客户端真实 IP（支持代理转发） |
| `created_at` | DATETIME | NO | NOW() | ✅ 核心 | 操作时间，用于排序 |

#### 自动记录机制

通过 Spring AOP 实现，切面配置：

```java
@Pointcut("execution(* com.platform.controller.*.*(..)) && " +
          "!execution(* com.platform.controller.AuthController.*(..)) && " +
          "!execution(* com.platform.controller.LogController.*(..)) && " +
          "!execution(* com.platform.controller.DashboardController.*(..))")
```

**排除的 Controller**:
- `AuthController`: 登录/登出由 `sys_login_log` 记录，避免重复
- `LogController`: 日志查询本身不需要记录
- `DashboardController`: 统计查询接口

#### 日志示例

| username | module | action | description |
|:---|:---|:---|:---|
| admin | 用户管理 | 新增 | {"username":"test","realName":"测试用户"...} [耗时: 45ms] |
| admin | 角色管理 | 修改 | {"name":"新角色","code":"NEW_ROLE"...} [耗时: 23ms] |
| admin | 模块管理 | 修改 | {} [耗时: 12ms] |

---

### 8. sys_login_log 登录日志表

记录用户的登录/登出行为，用于安全审计。

```sql
CREATE TABLE sys_login_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id     BIGINT                           COMMENT '用户ID',
    username    VARCHAR(50)                      COMMENT '用户名',
    login_type  TINYINT                          COMMENT '登录类型(1登录,2登出)',
    status      TINYINT                          COMMENT '状态(0失败,1成功)',
    error_msg   VARCHAR(255)                     COMMENT '失败原因(密码错误/账号禁用等)',
    ip_address  VARCHAR(50)                      COMMENT '客户端IP',
    user_agent  VARCHAR(500)                     COMMENT '浏览器User-Agent信息',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间'
);
```

#### 字段说明

| 字段 | 类型 | 必填 | 默认值 | 使用状态 | 说明 |
|:---|:---|:---:|:---|:---:|:---|
| `id` | BIGINT | PK | AUTO | ✅ 活跃 | 主键 |
| `user_id` | BIGINT | NO | NULL | ✅ 活跃 | 用户 ID，Dashboard 统计在线用户时使用 |
| `username` | VARCHAR(50) | NO | NULL | ✅ 核心 | 用户名，搜索和展示 |
| `login_type` | TINYINT | NO | NULL | ✅ 活跃 | 1=登录, 2=登出 |
| `status` | TINYINT | NO | NULL | ✅ 核心 | 0=失败, 1=成功 |
| `error_msg` | VARCHAR(255) | NO | NULL | ✅ 活跃 | 失败时的具体原因（密码错误/账号禁用/用户不存在） |
| `ip_address` | VARCHAR(50) | NO | NULL | ✅ 活跃 | 客户端 IP，安全审计 |
| `user_agent` | VARCHAR(500) | NO | NULL | ✅ 活跃 | 浏览器信息，识别异常登录 |
| `created_at` | DATETIME | NO | NOW() | ✅ 核心 | 时间戳，排序依据 |

#### 写入时机
- **AuthController.login()**: 无论成功失败都会写入
- **AuthController.logout()**: 登出时写入（当前仅返回成功，未实际记录）

#### Dashboard 统计使用
- **在线用户数**: 查询最近 N 分钟内登录成功的不同用户数（去重）

---

### 9. sys_config 系统配置表

KV 形式的系统配置存储，支持运行时修改。

```sql
CREATE TABLE sys_config (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key   VARCHAR(100) NOT NULL UNIQUE      COMMENT '配置键(英文标识)',
    config_value TEXT                              COMMENT '配置值',
    description  VARCHAR(255)                      COMMENT '配置说明(帮助文字)',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

#### 字段说明

| 字段 | 类型 | 必填 | 默认值 | 使用状态 | 说明 |
|:---|:---|:---:|:---|:---:|:---|
| `id` | BIGINT | PK | AUTO | ✅ 活跃 | 主键 |
| `config_key` | VARCHAR(100) | UNIQUE | — | ✅ 核心 | 配置键名（如 platformName） |
| `config_value` | TEXT | NO | NULL | ✅ 核心 | 配置值（任意类型，接口层转换） |
| `description` | VARCHAR(255) | NO | NULL | ✅ 活跃 | 配置项说明，已通过 `_descriptions` 字段返回给前端 |
| `created_at` | DATETIME | NO | NOW() | 🟢 审计 | 创建时间 |
| `updated_at` | DATETIME | NO | NOW() | 🟢 审计 | 更新时间 |

#### 内置配置项

| config_key | 类型 | 默认值 | 说明 | 前端使用位置 |
|:---|:---|:---|:---|:---|
| `platformName` | String | 内网万用平台 | 平台显示名称 | 导航栏 Logo、登录页标题、底部版权栏 |
| `logo` | String | (空) | 平台 Logo URL | 为空时使用默认 SVG |
| `themeColor` | String | #409eff | 主题颜色 | 已预留，待实现主题切换 |
| `loginTimeout` | Integer | 120 | 登录超时(分钟) | 会话过期检查 |
| `passwordMinLength` | Integer | 6 | 密码最小长度 | 修改密码校验 |
| `maxLoginFailures` | Integer | 5 | 登录失败锁定次数 | 待实现账户锁定 |
| `lockoutDuration` | Integer | 15 | 锁定时长(分钟) | 同上 |
| `logRetentionDays` | Integer | 90 | 日志保留天数 | 待实现日志清理任务 |

#### API 响应格式

```json
{
  "code": 200,
  "data": {
    "platformName": "内网万用平台",
    "logo": "",
    "themeColor": "#409eff",
    "loginTimeout": 120,
    "_descriptions": {
      "platformName": "平台显示名称",
      "logo": "平台Logo URL",
      "themeColor": "主题颜色"
    }
  }
}
```

> `_descriptions` 子对象包含每个配置项的帮助说明文字。

---

## ER 关系图

```
┌─────────────┐       ┌─────────────────┐       ┌──────────────────┐
│  sys_user   │◄──────│  sys_user_role  │──────►│    sys_role      │
│─────────────│       │─────────────────│       │──────────────────│
│ id (PK)     │       │ user_id (PK/FK) │       │ id (PK)          │
│ username    │       │ role_id (PK/FK) │       │ name             │
│ password    │       └─────────────────┘       │ code (UNIQUE)    │
│ real_name   │                                   │ description      │
│ phone       │       ┌─────────────────────┐   └──────────────────┘
│ email       │       │ sys_role_permission │
│ avatar      │◄──────│─────────────────────│────►┌────────────────────┐
│ status      │       │ role_id (PK/FK)     │     │  sys_permission    │
│ created_at  │       │ permission_id(PK/FK)│     │────────────────────│
│ updated_at  │       └─────────────────────┘     │ id (PK)            │
└─────────────┘                                   │ name               │
                                                   │ code (UNIQUE)      │
                                                   │ type (预留)         │
                                                   │ path (预留)         │
                                                   │ parent_id (预留)    │
                                                   │ sort_order          │
                                                   └────────────────────┘

┌─────────────┐
│  sys_module │  (独立实体，通过权限编码前缀关联)
│─────────────│
│ id (PK)     │
│ module_key  │
│ name        │
│ version     │
│ description │
│ author      │
│ icon        │
│ status      │
│ created_at  │
│ updated_at  │
└─────────────┘

┌────────────────────┐   ┌─────────────────────┐
│  sys_operation_log │   │    sys_login_log    │
│────────────────────│   │─────────────────────│
│ id (PK)            │   │ id (PK)             │
│ user_id            │   │ user_id             │
│ username           │   │ username            │
│ module             │   │ login_type          │
│ action             │   │ status              │
│ description        │   │ error_msg           │
│ ip_address         │   │ ip_address          │
│ created_at         │   │ user_agent          │
└────────────────────┘   │ created_at          │
                          └─────────────────────┘

┌─────────────┐
│  sys_config │  (独立 KV 存储)
│─────────────│
│ id (PK)     │
│ config_key  │
│ config_value│
│ description │
│ created_at  │
│ updated_at  │
└─────────────┘
```

---

## 字段使用状态总览

### 图例说明

| 状态 | 含义 | 处理建议 |
|:---:|:---|:---|
| ✅ **活跃** | 当前正在使用 | 保持现状 |
| 🔵 **预留** | 有明确未来扩展计划 | 保留，等待实现 |
| 🟢 **审计** | 通用审计字段 | 保留，不主动移除 |
| ❌ **已移除** | v1.13.0 清理掉的冗余字段 | 不再存在于数据库 |

### 各表字段矩阵

#### sys_user (11 字段)

| 字段 | 状态 | 备注 |
|:---|:---:|:---|
| id | ✅ | |
| username | ✅ | |
| password | ✅ | |
| real_name | ✅ | |
| phone | ✅ | |
| email | ✅ | |
| avatar | 🔵 | 待实现头像上传 |
| status | ✅ | |
| created_at | 🟢 | 审计 |
| updated_at | 🟢 | 审计 |

#### sys_role (6 字段)

| 字段 | 状态 | 备注 |
|:---|:---:|:---|
| id | ✅ | |
| name | ✅ | |
| code | ✅ | |
| description | ✅ | |
| created_at | 🟢 | 审计 |
| updated_at | 🟢 | 审计 |

#### sys_permission (8 字段)

| 字段 | 状态 | 备注 |
|:---|:---:|:---|
| id | ✅ | |
| name | ✅ | |
| code | ✅ | |
| type | 🔵 | 动态菜单功能待实现 |
| path | 🔵 | 动态菜单功能待实现 |
| parent_id | 🔵 | 动态菜单功能待实现 |
| sort_order | ✅ | |
| created_at | 🟢 | 审计 |

> **v1.13.0 移除**: `module_id` (❌) — 设计意图与实际架构不符

#### sys_module (10 字段)

| 字段 | 状态 | 备注 |
|:---|:---:|:---|
| id | ✅ | |
| module_key | ✅ | |
| name | ✅ | |
| version | ✅ | |
| description | ✅ | |
| author | 🔵 | 可在模块详情展示 |
| icon | ✅ | |
| status | ✅ | |
| created_at | 🟢 | 审计 |
| updated_at | 🟢 | 审计 |

> **v1.13.0 移除**: `install_path` (❌) — 冷插拔机制下无意义

#### sys_operation_log (8 字段)

| 字段 | 状态 | 备注 |
|:---|:---:|:---|
| id | ✅ | |
| user_id | ✅ | |
| username | ✅ | |
| module | ✅ | AOP 自动提取 |
| action | ✅ | AOP 自动生成 |
| description | ✅ | 参数摘要 + 耗时 |
| ip_address | ✅ | |
| created_at | ✅ | |

#### sys_login_log (9 字段)

| 字段 | 状态 | 备注 |
|:---|:---:|:---|
| id | ✅ | |
| user_id | ✅ | |
| username | ✅ | |
| login_type | ✅ | |
| status | ✅ | |
| error_msg | ✅ | 前端已展示列 |
| ip_address | ✅ | 前端已展示列 |
| userAgent | ✅ | 前端已展示列 |
| created_at | ✅ | |

#### sys_config (6 字段)

| 字段 | 状态 | 备注 |
|:---|:---:|:---|
| id | ✅ | |
| config_key | ✅ | |
| config_value | ✅ | |
| description | ✅ | v1.13.0 开始返回 |
| created_at | 🟢 | 审计 |
| updated_at | 🟢 | 审计 |

---

## 设计决策说明

### 1. 为什么保留时间戳字段？

所有表的 `created_at` 和 `updated_at` 字段虽然当前未被前端直接展示，但属于**通用审计基础设施**：
- 支持未来「数据变更时间线」功能
- 支持数据迁移时的增量同步
- 支持问题排查时的时序分析
- 存储成本极低（每条记录 8 字节），移除收益不大

### 2. 为什么保留 type/path/parent_id？

这三个字段是**标准 RBAC 菜单树模型**的核心组成部分：
- 当前系统使用**硬编码菜单**（MainLayout.vue 中静态定义）
- 未来若要实现**动态菜单渲染**（根据角色自动生成侧边栏），这三个字段将立即被激活
- 移除后再重建的成本高于保留成本

### 3. 为什么 operation_log 采用 AOP 而非手动埋点？

| 方案 | 优点 | 缺点 |
|:---|:---|:---|
| 手动埋点 | 精确控制记录内容 | 易遗漏、代码侵入性强 |
| **AOP 切面** ✅ | 自动化、无侵入、统一格式 | 无法记录业务上下文细节 |

选择 AOP 是因为：
- 本系统的日志需求是「谁在什么时间做了什么写操作」
- AOP 可以自动捕获方法参数、执行耗时、调用者 IP
- 特殊场景（如登录日志）仍使用手动埋点

### 4. 为什么 config 返回 _descriptions 子对象？

而非直接在每个配置项上增加 description 字段：
- **向后兼容**: 原有前端代码无需修改即可正常运行
- **按需使用**: 只有配置页面需要显示帮助文字
- **扩展性**: 未来可在 `_descriptions` 中添加更多元数据（如 validation rules）

---

## 附录：SQL 快速参考

### 完整建表语句

详见 [`backend/sql/init.sql`](../backend/sql/init.sql)

### 清理历史冗余字段（升级用）

如果从旧版本升级，需执行以下 SQL：

```sql
-- v1.13.0: 移除 sys_permission.module_id
ALTER TABLE sys_permission DROP COLUMN IF EXISTS module_id;
ALTER TABLE sys_permission DROP INDEX IF EXISTS idx_module_id;

-- v1.13.0: 移除 sys_module.install_path
ALTER TABLE sys_module DROP COLUMN IF EXISTS install_path;
```

---

*文档最后更新: 2026-05-17*
