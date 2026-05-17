# 内网万用平台 - 项目部署指南

> 面向运维人员和使用者：从零部署到正常运行

---

## 目录

1. [项目概述](#1-项目概述)
2. [环境准备](#2-环境准备)
3. [部署流程](#3-部署流程)
4. [架构详解](#4-架构详解)
5. [部署验证](#5-部署验证)
6. [常见部署问题](#6-常见部署问题)

---

## 1. 项目概述

### 技术栈

| 层 | 技术 | 版本 |
|---|------|------|
| 前端框架 | Vue 3 + TypeScript | ^3.x |
| 构建工具 | Vite | ^5.x |
| UI 组件库 | Element Plus | ^2.x |
| 状态管理 | Pinia | ^2.x |
| 路由 | Vue Router | ^4.x |
| 后端框架 | Spring Boot | 3.x |
| ORM | MyBatis Plus | ^3.5 |
| 数据库 | MySQL | 8.0+ |

### 项目目录结构

```
project_V/
├── backend/                  # 后端主项目（Spring Boot）
│   ├── src/main/java/com/platform/
│   │   ├── common/           # 公共工具类（Result、PageResult 等）
│   │   ├── controller/       # 系统核心控制器
│   │   ├── service/          # 系统核心服务层
│   │   └── module/           # 模块后端代码（编译时合并到此目录）
│   ├── sql/init.sql          # 系统数据库初始化脚本
│   └── pom.xml               # Maven 配置
│
├── frontend/                 # 前端主项目（Vue 3 + Vite）
│   ├── src/
│   │   ├── api/              # API 请求封装
│   │   ├── layouts/          # 布局组件（UserLayout / MainLayout）
│   │   ├── router/           # 路由配置（含模块自动扫描）
│   │   ├── stores/           # Pinia 状态管理
│   │   └── views/            # 页面视图
│   ├── vite.config.ts        # Vite 配置（别名、代理）
│   └── package.json          # 依赖配置
│
├── modules/                  # 业务模块存放目录
│   └── example-module/       # 内置示例模块
│       ├── frontend/src/     # 模块前端代码
│       ├── backend/src/      # 模块后端代码
│       └── sql/init.sql      # 模块数据库脚本
│
└── docs/                     # 项目文档
    ├── DEPLOYMENT_GUIDE.md   # 本文档 — 部署指南
    └── MODULE_DEVELOPMENT_GUIDE.md  # 模块开发指南
```

### 双界面设计

本系统采用**双界面**架构，面向不同角色，风格截然不同：

| 界面 | 路由前缀 | 布局组件 | 面向用户 | 功能范围 | 风格 |
|------|---------|---------|---------|---------|------|
| **用户界面** | `/` | UserLayout.vue | 普通用户 | 个人中心 + 业务模块（卡片+分页） | 暗色星空主题 |
| **管理后台** | `/admin` | MainLayout.vue | 管理员 | 用户/角色/模块/日志/配置（6 项） | 亮色专业面板 |

两个界面共享同一套业务模块代码，但展示方式不同：
- **用户界面**：首页以精美卡片网格展示所有已启用模块，支持分页（每页 8 个），点击进入功能页面
- **管理后台**：通过「模块管理」页面控制模块的启用/停用，侧边栏不再重复显示业务模块入口

---

## 2. 环境准备

### 必需软件

| 软件 | 最低版本 | 推荐版本 | 用途 |
|------|---------|---------|------|
| **Node.js** | 18.x | 20 LTS | 前端运行环境 |
| **JDK** | 17 | 17 LTS | 后端 Java 运行环境 |
| **Maven** | 3.8+ | 3.9+ | 后端构建工具 |
| **MySQL** | 8.0+ | 8.0+ | 关系型数据库 |
| **IDE**（可选） | - | VS Code / IntelliJ IDEA | 开发调试 |

### 端口规划

| 服务 | 默认端口 | 说明 | 可修改位置 |
|------|---------|------|-----------|
| 前端 Dev Server | **5173** | Vite 开发服务器 | `frontend/vite.config.ts` → `server.port` |
| 后端 API 服务 | **8888** | Spring Boot 应用 | `backend/src/main/resources/application.yml` |
| MySQL 数据库 | **3306** | 数据存储 | MySQL 配置文件 |

### 网络要求

- 前端通过 Vite Proxy 将 API 请求代理到后端，无需跨域配置
- 生产环境建议使用 Nginx 反向代理前后端

---

## 3. 部署流程

### Step 1：初始化数据库

#### 1.1 安装并启动 MySQL

确保 MySQL 服务正在运行：

```bash
# Windows（以管理员身份）
net start mysql

# 或在 MySQL 安装目录下手动启动
mysqld --console
```

#### 1.2 创建数据库并执行初始化脚本

```bash
# 方式一：命令行连接后执行
mysql -u root -p < F:/OutPut/Trea/project_V/backend/sql/init.sql

# 方式二：进入 MySQL 客户端后执行
mysql -u root -p
mysql> source F:/OutPut/Trea/project_V/backend/sql/init.sql
```

#### 1.3 初始化脚本创建的内容

**系统核心表（9 张）：**

| 表名 | 用途 | 说明 |
|------|------|------|
| `sys_user` | 用户表 | 存储所有用户账号信息 |
| `sys_role` | 角色表 | SUPER_ADMIN / NORMAL_USER |
| `sys_permission` | 权限表 | 菜单权限 + 按钮权限 |
| `sys_module` | 模块注册表 | 记录已安装的模块信息 |
| `sys_user_role` | 用户-角色关联 | 多对多关系 |
| `sys_role_permission` | 角色-权限关联 | 多对多关系 |
| `sys_operation_log` | 操作日志 | 记录用户操作行为 |
| `sys_login_log` | 登录日志 | 记录登录/登出事件 |
| `sys_config` | 系统配置 | 键值对形式的系统参数 |

**初始数据：**

| 角色 | 账号 | 密码 | 权限范围 |
|------|------|------|---------|
| 超级管理员 | `admin` | `Admin@123456` | 所有权限 |
| 普通用户 | （无预设账号） | - | 基础权限 |

> **安全提示**：生产环境部署后请立即修改默认密码！

#### 1.4 执行示例模块的数据库脚本

示例模块有独立的数据表，需要在 MySQL 中单独执行：

```sql
-- 在 platform 数据库中执行以下 SQL（或直接执行 modules/example-module/sql/init.sql）：

CREATE TABLE IF NOT EXISTS exm_example (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用,1启用)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例表';

INSERT INTO exm_example (name, description, status) VALUES
('示例1', '这是一个示例记录', 1),
('示例2', '这是第二个示例记录', 1),
('示例3', '这是第三个示例记录', 0);
```

> **注意**：每个业务模块都需要单独执行其 `sql/init.sql` 来创建数据表。详见 [模块开发指南](./MODULE_DEVELOPMENT_GUIDE.md)。

---

### Step 2：启动后端服务

```bash
cd F:/OutPut/Trea/project_V/backend

# 方式一：Maven 直接运行（推荐开发时使用）
mvn spring-boot:run

# 方式二：打包后运行（推荐生产环境）
mvn clean package -DskipTests
java -jar target/platform.jar

# 方式三：在 IDE 中运行 Application.java 主类
```

**验证后端启动成功：**

- 控制台应显示类似 `Started PlatformApplication in x.xxx seconds`
- 浏览器访问 `http://localhost:8888` 应返回响应（或显示 Whitelabel Error Page 表示服务已运行）

**如果端口冲突，修改方式：**

编辑 `backend/src/main/resources/application.yml`：
```yaml
server:
  port: 8889  # 改为其他端口
```
同时需要同步修改 `frontend/vite.config.ts` 的 proxy target。

---

### Step 3：安装前端依赖并启动

```bash
cd F:/OutPut/Trea/project_V/frontend

# 首次部署需要安装依赖（约 2-5 分钟）
npm install

# 启动 Vite 开发服务器
npm run dev
```

**验证前端启动成功：**

- 控制台应显示：
  ```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: http://192.168.x.x:5173/
  ```
- 浏览器自动打开 `http://localhost:5173`

**常用前端命令：**

| 命令 | 作用 |
|------|------|
| `npm run dev` | 启动开发服务器（热更新） |
| `npm run build` | 构建生产版本到 `dist/` |
| `npm run preview` | 预览生产构建结果 |
| `npm run lint` | 代码检查 |

---

### Step 4：登录验证

1. 打开浏览器访问 `http://localhost:5173`
2. 进入登录页面
3. 使用默认管理员账号登录：
   - 用户名：`admin`
   - 密码：`Admin@123456`
4. 登录成功后跳转到首页

---

## 4. 架构详解

### 4.1 整体架构图

```
┌─────────────────────────────────────────────────────┐
│                    浏览器                            │
│  ┌─────────────────┐  ┌───────────────────────────┐ │
│  │  用户界面 (/)    │  │  管理后台 (/admin)         │ │
│  │  UserLayout     │  │  MainLayout               │ │
│  │  (暗色星空主题)   │  │  (亮色专业面板)            │ │
│  │  ├─ 首页        │  │  ├─ 仪表盘（真实数据）      │ │
│  │  │  ├─ 模块卡片* │  │  ├─ 用户管理               │ │
│  │  │  └─ 分页(8/页)│  │  ├─ 角色管理               │ │
│  │  ├─ 个人中心    │  │  ├─ 模块管理               │ │
│  │  └─ 业务模块页面 │  │  ├─ 系统配置               │ │
│  └────────┬────────┘  │  ├─ 日志管理               │ │
│           │            │  └─ (无业务模块子菜单)      │ │
│           │            └─────────────┬─────────────┘ │
│           │                          │               │
│  ┌────────▼──────────────────────────▼─────────────┐ │
│  │              Vue 3 + Vue Router                  │
│  │         import.meta.glob 自动扫描模块             │
│  │     路由过渡动画: clip-path(布局切换)/即时(后台内) │ │
│  └──────────────────────┬──────────────────────────┘ │
└─────────────────────────┼───────────────────────────┘
                          │ HTTP (Vite Proxy)
                          ▼
┌─────────────────────────────────────────────────────┐
│              Spring Boot (Port 8888)                 │
│  ┌──────────────┐  ┌──────────────────────────────┐  │
│  │  系统控制器    │  │  模块控制器（编译时集成）        │  │
│  │  AuthController│  │  ExampleController          │  │
│  │  UserController│  │  YourModuleController       │  │
│  │  RoleController│  │  ...                        │  │
│  └──────┬───────┘  └──────────────┬───────────────┘  │
│         │                      │                    │
│  ┌──────▼──────────────────────▼─────────────────┐  │
│  │              MyBatis Plus + MySQL              │  │
│  └────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

### 4.2 前端模块自动加载机制

平台的核心特性是**模块自动发现和加载**，由以下三个环节协作完成：

#### （1）Vite 别名配置

[vite.config.ts](../frontend/vite.config.ts) 中定义了关键别名：

```typescript
resolve: {
  alias: {
    '@': resolve(__dirname, 'src'),           // @ → frontend/src/
    '@modules': resolve(__dirname, '../modules')  // @modules → modules/
  }
}
```

`@modules` 别名使得 Vite 能扫描到项目根目录下的 `modules/` 文件夹。

#### （2）路由自动注册

[router/index.ts](../frontend/src/router/index.ts) 在应用启动时扫描所有模块入口文件：

```typescript
const modules = import.meta.glob('@modules/*/frontend/src/index.ts', {
  eager: true,
  import: 'default'
})
// 提取路由 → 注入 UserLayout 和 MainLayout 的 children
// 提取菜单 → 存入 menuStore
```

**路由注入规则：**
- 模块路由不是顶级路由，而是嵌套在布局组件的 children 中
- 用户界面路径：`/example-module`（去掉开头的 `/`）
- 管理后台路径：`/admin/example-module`（加 `/admin` 前缀）
- Admin 路由名自动加 `Admin` 前缀避免名称冲突

#### （3）菜单统一管理

[stores/menu.ts](../frontend/src/stores/menu.ts) 统一管理动态菜单数据：

- 不在 store 定义时自动调用（防止循环依赖）
- 由布局组件在 `onMounted` 时手动触发 `loadModuleMenus()`
- `loaded` 标志确保只加载一次
- 模块菜单**不做权限过滤**（可见性由数据库模块状态控制）

#### （4）API 请求代理

前端调用 `/v1/example-module/list` 时，经 Vite Proxy 处理：

```
前端请求:  GET /v1/example-module/list?pageNum=1&pageSize=10
    ↓ Vite Proxy (vite.config.ts)
代理目标:  http://localhost:8888/api/v1/example-module/list?pageNum=1&pageSize=10
    ↓ Spring Boot
Controller: ExampleController.list()
    ↓ MyBatis Plus
MySQL: SELECT * FROM exm_example LIMIT 10 OFFSET 0
```

### 4.3 后端模块集成方式（编译时合并）

本平台后端采用**编译时合并**机制：

```
模块代码位置（源码）          运行时位置（编译后）
modules/{name}/backend/  →  backend/src/main/java/com/platform/module/{name}/
```

**工作原理：**
1. 模块的 Java 代码存放在 `modules/{name}/backend/src/...`（作为参考模板）
2. 部署时将需要的模块代码**复制**到主项目的 `backend/src/main/java/com/platform/module/.../`
3. 重启后端服务，Spring Boot 自动扫描并加载 Controller
4. 模块的前端代码则由 Vite 在启动时自动扫描 `modules/*/frontend/src/`，无需复制

**为什么不用运行时动态加载？**
- Spring Boot 的类加载机制不支持运行时动态添加 Controller
- 编译时合并更简单可靠，适合内网部署场景
- 模块更新只需重启服务即可（通常几秒内完成）

### 4.4 运行时模块启用/停用

虽然模块代码在编译时就已打包进系统，但平台支持**运行时控制模块的可见性**：

#### 工作原理

```
管理员点击「停用」开关
    ↓
PUT /api/v1/modules/{id}/toggle  →  DB: sys_module.status = 0
    ↓
前端 menuStore.refreshMenus()
    ↓
重新 GET /v1/modules 获取模块状态列表
    ↓
过滤掉 status=0 的模块 → 不加载其菜单
    ↓
侧边栏：该模块消失 ✅
直接访问该模块 URL → 路由守卫拦截并重定向 ✅
```

#### 技术本质

| 维度 | 说明 |
|------|------|
| **实现方式** | 基于数据库 `sys_module.status` 字段的软禁用（Soft Disable） |
| **前端行为** | 停用后隐藏菜单入口 + 路由守卫拦截直接访问 URL |
| **内存占用** | 模块代码仍在浏览器 bundle 中（未释放），但用户无法感知 |
| **是否需要重启** | ❌ 不需要，切换即时生效 |
| **与"热插拔"的区别** | 真正的热插拔会动态加载/卸载代码和释放内存；本方案是运行时可见性控制 |

#### 使用方式

1. 进入管理后台 `/admin`
2. 左侧菜单选择「模块管理」
3. 找到目标模块，切换「启用/停用」开关
4. 侧边栏立即反映变化（无需刷新页面）

> **注意**：新增或删除模块仍需重启前端服务（因为依赖 `import.meta.glob` 编译时扫描）。启用/停用仅对**已有模块**的可见性进行控制。

---

## 5. 部署验证清单

部署完成后，逐项确认：

### 基础功能验证

- [ ] 数据库 `platform` 已创建，包含 9 张系统表
- [ ] 示例模块表 `exm_example` 已创建，包含 3 条测试数据
- [ ] 后端服务正常启动，控制台无报错
- [ ] 前端服务正常启动，能访问 `http://localhost:5173`
- [ ] 使用 `admin / Admin@123456` 可以成功登录

### 页面功能验证

- [ ] **首页**：显示星空背景、用户信息卡片、「可用模块」数量 > 0、「我的应用」区域有模块卡片（每页 8 个）
- [ ] **个人中心**：可以查看和编辑个人信息
- [ ] **管理后台**：点击「管理后台」能进入 `/admin` 界面，切换动画流畅
- [ ] **侧边栏菜单**：包含 6 项 — 首页 / 用户管理 / 角色管理 / 模块管理 / 系统配置 / 日志管理（无业务模块子菜单）
- [ ] **模块卡片**：用户界面首页展示已启用模块卡片，支持分页切换

### 示例模块功能验证

- [ ] 点击「示例模块」（用户界面或管理后台均可），页面正常加载
- [ ] 显示表格数据（示例1、示例2、示例3）
- [ ] 分页功能正常
- [ ] 新增/编辑/删除操作正常（如有实现）

### 控制台日志验证

打开浏览器开发者工具（F12），切换到 Console 标签：

```
[Router] 扫描到模块: 1 个          ✅ glob 扫描成功
[Router] 加载模块路由: 1 个        ✅ 路由注册成功
[Router] 加载模块菜单: 1 个        ✅ 菜单提取成功
[Menu] 已加载 1 个菜单             ✅ menuStore 加载成功
Platform Application Started      ✅ 应用启动完成
[App] Token exists, fetching user info...  ✅ 登录状态正常
[App] User info loaded: [...]     ✅ 用户信息获取成功
```

---

## 6. 常见部署问题

### Q1：数据库连接失败

**症状：** 后端启动报错 `Communications link failure` 或 `Access denied`

| 可能原因 | 解决方案 |
|---------|---------|
| MySQL 未启动 | 启动 MySQL 服务：`net start mysql`（Windows） |
| 用户名/密码错误 | 检查 `application.yml` 中的数据库配置 |
| 数据库名不存在 | 先执行 `init.sql` 创建数据库 |
| 端口不对 | 确认 MySQL 监听在 3306 端口 |

**检查数据库配置文件：**
```yaml
# backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/platform?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password    # ← 改为你的 MySQL 密码
```

### Q2：前端依赖安装失败

**症状：** `npm install` 报错 `ERR!` 或 `ENOTFOUND`

| 可能原因 | 解决方案 |
|---------|---------|
| Node.js 版本过低 | 升级到 18.x 以上：`node --version` |
| 网络问题（国内） | 设置淘宝镜像：`npm config set registry https://registry.npmmirror.com` |
| 缓存损坏 | 清除缓存：`npm cache clean --force`，然后重新 install |

### Q3：端口被占用

**症状：** `Error: listen EADDRINUSE: address already in use :::5173`

```bash
# 查看 5173 端口占用情况（Windows）
netstat -ano | findstr :5173

# 结束占用进程（将 PID 替换为实际进程 ID）
taskkill /PID <PID> /F

# 或者修改 Vite 端口（frontend/vite.config.ts）
server: { port: 5174 }  // 改为其他端口
```

### Q4：模块不显示（可用模块: 0）

**症状：** 首页显示「可用模块: 0」，控制台可能无 `[Router] 扫描到模块` 日志

| 可能原因 | 排查方法 | 解决方案 |
|---------|---------|---------|
| `import.meta.glob` 路径错误 | 检查 router/index.ts 中的 glob 路径 | 必须使用 `@modules/*/frontend/src/index.ts` |
| 模块缺少入口文件 | 确认 `modules/example-module/frontend/src/index.ts` 存在 | 检查文件是否完整 |
| Vite 未重启 | 新增模块后未重启 dev server | 重启 `npm run dev` |
| `@modules` 别名未生效 | 检查 vite.config.ts 中 alias 配置 | 确认 `@modules` 指向 `../modules` |

### Q5：模块显示了但点击报 404

**症状：** 菜单可见，但页面加载时报「请求功能不存在」

| 可能原因 | 解决方案 |
|---------|---------|
| 后端 Controller 未加载 | 将模块 Java 文件从 `modules/.../backend/` 复制到 `backend/src/main/java/...` |
| 后端未重启 | 重启后端服务（`mvn spring-boot:run`） |
| 数据表不存在 | 在 MySQL 中执行模块的 `sql/init.sql` |
| API 路径不匹配 | 确认前端 `/v1/xxx` 与后端 `/api/v1/xxx` 一致 |

### Q6：跨域问题（CORS）

**症状：** 控制台报错 `Access-Control-Allow-Origin`

| 场景 | 解决方案 |
|------|---------|
| 开发环境 | Vite Proxy 已处理，不应出现此问题；检查 proxy 配置 |
| 生产环境 | 在 Nginx 或后端添加 CORS 配置 |

### Q7：后端 StackOverflowError

**症状：** 调用模块接口时报 `java.lang.StackOverflowError`

**原因：** ServiceImpl 中重复包装了父类方法导致无限递归。

```java
// ❌ 错误：递归调用自己
public Example getById(Long id) { return this.getById(id); }

// ✅ 正确：只写自定义方法，父类方法直接可用
public class MyServiceImpl extends ServiceImpl<..., Entity> implements MyService {
    @Override
    public Page<Entity> selectPage(Page<Entity> page) { return this.page(page); }
    // getById/save/updateById/removeById 无需重写！
}
```

---

## 附录：生产环境部署建议

### 前端构建与部署

```bash
cd frontend
npm run build
# 产物在 dist/ 目录
```

使用 Nginx 托管静态文件：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /path/to/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /v1/ {
        proxy_pass http://127.0.0.1:8888/api/v1/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 后端打包与部署

```bash
cd backend
mvn clean package -DskipTests
# 产物在 target/platform.jar
```

后台运行：

```bash
nohup java -jar platform.jar --spring.profiles.active=prod > app.log 2>&1 &
```

### 安全加固项

- [ ] 修改默认管理员密码
- [ ] 更换 JWT 密钥
- [ ] 配置 HTTPS（Let's Encrypt）
- [ ] 限制数据库只允许本地访问
- [ ] 配置防火墙规则
