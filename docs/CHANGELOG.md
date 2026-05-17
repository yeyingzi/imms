# 更新日志

## v1.14.0 (2026-05-17)

> **重要更新**：本次更新包含文件损坏事件的完整修复记录，以及标准三层架构重构。

---

## 🔴 文件损坏事件记录

### 事件概述

在 v1.13.x 版本期间，项目遭遇了**文件损坏和删除事件**，导致部分功能不可用。以下是事件详情和恢复过程。

### 损坏/删除的文件清单

#### 前端文件（已恢复）
| 文件路径 | 状态 | 说明 |
|:---|:---:|:---|
| `src/api/user.ts` | ✅ 已恢复 | 用户管理 API |
| `src/api/role.ts` | ✅ 已恢复 | 角色管理 API |
| `src/api/log.ts` | ✅ 已恢复 | 日志管理 API |
| `src/api/permission.ts` | ✅ 已恢复 | 权限管理 API |
| `src/api/module.ts` | ✅ 已恢复 | 模块管理 API |
| `src/api/config.ts` | ✅ 已恢复 | 系统配置 API |
| `src/api/dashboard.ts` | ✅ 已恢复 | 仪表盘统计 API |
| `src/stores/loading.ts` | ✅ 已恢复 | Loading 状态管理 |
| `src/components/GlobalLoading.vue` | ✅ 已恢复 | 全局加载动画组件 |

#### 后端文件（Service/DTO 层重构）
| 文件路径 | 状态 | 说明 |
|:---|:---:|:---|
| `backend/src/main/java/com/platform/service/` | 🆕 新增 | 服务接口层（新建） |
| `backend/src/main/java/com/platform/service/impl/` | 🆕 新增 | 服务实现层（新建） |
| `backend/src/main/java/com/platform/dto/` | 🆕 新增 | 数据传输对象层（新建） |

### 损坏原因分析

1. **文件误删除**：部分前端 API 文件和组件被意外删除
2. **架构不完整**：原架构缺少 Service 和 DTO 层，业务逻辑全部堆积在 Controller
3. **代码不规范**：Controller 中缺少空值保护、事务管理等企业级特性

### 恢复措施

1. **文件恢复**：重新创建所有被删除的前端文件
2. **架构重构**：重构为标准三层架构（Controller → Service → Mapper）
3. **代码修复**：修复所有已发现的代码问题

---

## 🏗️ 架构重构：标准三层架构

### 背景
为了提升代码质量和可维护性，将原有的扁平架构重构为标准三层架构。

### 新增 Service 服务层
| Service 接口 | Service 实现 | 职责 |
|:---|:---|:---|
| `UserService` | `UserServiceImpl` | 用户管理业务逻辑 |
| `RoleService` | `RoleServiceImpl` | 角色管理业务逻辑 |
| `PermissionService` | `PermissionServiceImpl` | 权限管理业务逻辑 |
| `ModuleService` | `ModuleServiceImpl` | 模块管理业务逻辑 |
| `AuthService` | `AuthServiceImpl` | 认证授权业务逻辑 |

### 新增 DTO 数据传输对象
| DTO 类 | 说明 |
|:---|:---|
| `LoginVO` | 登录响应数据 |
| `UserVO` | 用户信息数据传输对象 |
| `LoginRequest` | 登录请求参数 |
| `PasswordChangeRequest` | 修改密码请求参数 |

### Controller 重构
所有 Controller 重构为调用 Service 层：

| 原 Controller | 现调用 Service | 重构内容 |
|:---|:---|:---|
| `UserController` | `UserService` | 业务逻辑迁移 |
| `RoleController` | `RoleService` | 业务逻辑迁移 |
| `PermissionController` | `PermissionService` | 业务逻辑迁移 |
| `ModuleController` | `ModuleService` | 业务逻辑迁移 |
| `AuthController` | `AuthService` | 业务逻辑迁移 |

### 目录结构（更新后）
```
backend/src/main/java/com/platform/
├── controller/     # 控制层（请求/响应处理）
├── service/       # 服务接口层（业务逻辑定义）
│   └── impl/     # 服务实现层（业务逻辑实现）
├── dto/          # 数据传输对象层（数据传输封装）
├── entity/       # 实体类层（数据库表映射）
├── mapper/       # 数据访问层（数据库操作）
├── config/       # 配置类
├── common/      # 通用组件
├── util/         # 工具类
└── aspect/      # 切面（AOP 日志等）
```

---

## 🐛 Bug 修复

### 1. 前端请求拦截器修复
- **问题**：登录成功后无法进入主页面，`request.ts` 响应拦截器返回了原始 axios 响应对象，而不是 API 响应的 `data` 部分
- **影响**：所有 API 调用无法获取实际数据
- **修复**：调整响应拦截器，正确返回 `response.data`
- **文件**：`frontend/src/utils/request.ts`

### 2. 登录页面数据访问修复
- **问题**：前端代码使用 `res.token` 但实际响应结构是 `res.data.token`
- **影响**：登录功能失效
- **修复**：修改 `login/index.vue` 中的数据访问路径为 `res.data.token`
- **文件**：`frontend/src/views/login/index.vue`

### 3. 前端数据访问路径修复
- **问题**：用户管理、角色管理、日志管理页面使用 `res.list` 访问数据，但实际结构是 `res.data.list`
- **影响**：列表数据无法显示
- **修复**：更新以下页面使用正确的数据路径 `res.data.list`

| 页面 | 文件 |
|:---|:---|
| 用户管理列表 | `frontend/src/views/user/index.vue` |
| 角色管理列表和权限树 | `frontend/src/views/role/index.vue` |
| 日志管理列表 | `frontend/src/views/log/index.vue` |
| 登录页面平台名称加载 | `frontend/src/views/login/index.vue` |
| 用户布局平台名称加载 | `frontend/src/layouts/UserLayout.vue` |

### 4. 系统配置加载修复
- **问题**：平台名称、Logo 等配置从 API 加载时路径错误
- **影响**：首页和底部显示硬编码的"家庭助手"而非数据库配置
- **修复**：修正配置加载路径为 `res.data.xxx`
- **文件**：
  - `frontend/src/views/login/index.vue`
  - `frontend/src/layouts/UserLayout.vue`

### 5. 后端代码规范修复

#### UserController 空值保护
- **问题**：更新用户信息时无条件覆盖 `phone`、`email` 等字段，导致 null 值覆盖已有数据
- **修复**：对所有字段添加 null 检查，只在值不为 null 时才更新
- **文件**：`backend/src/main/java/com/platform/controller/UserController.java`

#### RoleController 空值保护
- **问题**：更新角色信息时无条件覆盖 `name`、`description` 字段
- **修复**：对 `name` 和 `description` 字段添加 null 检查
- **文件**：`backend/src/main/java/com/platform/controller/RoleController.java`

### 6. 登录日志记录修复
- **问题**：重构 Service 层时丢失了登录日志记录功能
- **修复**：
  - `AuthService.login()` 方法添加 IP 地址和 UserAgent 参数
  - 添加 `saveLoginLog()` 方法记录登录日志
  - `AuthController.login()` 传递客户端信息到 Service 层
- **文件**：
  - `backend/src/main/java/com/platform/service/AuthService.java`
  - `backend/src/main/java/com/platform/service/impl/AuthServiceImpl.java`
  - `backend/src/main/java/com/platform/controller/AuthController.java`

### 7. DTO 类结构修复
- **问题**：`LoginVO` 继承 `UserVO` 导致 `setUser()` 方法冲突
- **修复**：`LoginVO` 改为独立类，包含 `token`, `refreshToken`, `user` 三个字段
- **文件**：`backend/src/main/java/com/platform/dto/LoginVO.java`

### 8. Java 版本兼容性修复
- **问题**：Maven 使用的 JDK 17 与项目配置的 Java 21 不匹配
- **修复**：`pom.xml` 中的 `java.version` 从 21 调整为 17
- **文件**：`backend/pom.xml`

---

## 📦 修改文件清单

### 前端文件
| 文件 | 操作 | 说明 |
|:---|:---:|:---|
| `frontend/src/utils/request.ts` | 修改 | 修复响应拦截器 |
| `frontend/src/views/login/index.vue` | 修改 | 修复数据访问路径和配置加载 |
| `frontend/src/views/user/index.vue` | 修改 | 修复数据访问路径 |
| `frontend/src/views/role/index.vue` | 修改 | 修复数据访问路径 |
| `frontend/src/views/log/index.vue` | 修改 | 修复数据访问路径 |
| `frontend/src/layouts/UserLayout.vue` | 修改 | 修复配置加载路径 |
| `frontend/src/App.vue` | 修改 | 集成 GlobalLoading 组件 |
| `frontend/src/api/module.ts` | 新增 | 模块管理 API |
| `frontend/src/api/config.ts` | 新增 | 系统配置 API |
| `frontend/src/api/dashboard.ts` | 新增 | 仪表盘统计 API |
| `frontend/src/components/GlobalLoading.vue` | 新增 | 全局加载动画组件 |
| `frontend/src/stores/loading.ts` | 恢复 | Loading 状态管理 |

### 后端文件
| 文件 | 操作 | 说明 |
|:---|:---:|:---|
| `backend/src/main/java/com/platform/service/UserService.java` | 新增 | 用户服务接口 |
| `backend/src/main/java/com/platform/service/impl/UserServiceImpl.java` | 新增 | 用户服务实现 |
| `backend/src/main/java/com/platform/service/RoleService.java` | 新增 | 角色服务接口 |
| `backend/src/main/java/com/platform/service/impl/RoleServiceImpl.java` | 新增 | 角色服务实现 |
| `backend/src/main/java/com/platform/service/PermissionService.java` | 新增 | 权限服务接口 |
| `backend/src/main/java/com/platform/service/impl/PermissionServiceImpl.java` | 新增 | 权限服务实现 |
| `backend/src/main/java/com/platform/service/ModuleService.java` | 新增 | 模块服务接口 |
| `backend/src/main/java/com/platform/service/impl/ModuleServiceImpl.java` | 新增 | 模块服务实现 |
| `backend/src/main/java/com/platform/service/AuthService.java` | 新增 | 认证服务接口 |
| `backend/src/main/java/com/platform/service/impl/AuthServiceImpl.java` | 新增 | 认证服务实现 |
| `backend/src/main/java/com/platform/dto/LoginVO.java` | 新增 | 登录响应 DTO |
| `backend/src/main/java/com/platform/dto/UserVO.java` | 新增 | 用户信息 DTO |
| `backend/src/main/java/com/platform/dto/LoginRequest.java` | 新增 | 登录请求 DTO |
| `backend/src/main/java/com/platform/dto/PasswordChangeRequest.java` | 新增 | 修改密码请求 DTO |
| `backend/src/main/java/com/platform/controller/UserController.java` | 重构 | 调用 Service 层 + 空值保护 |
| `backend/src/main/java/com/platform/controller/RoleController.java` | 重构 | 调用 Service 层 + 空值保护 |
| `backend/src/main/java/com/platform/controller/PermissionController.java` | 重构 | 调用 Service 层 |
| `backend/src/main/java/com/platform/controller/ModuleController.java` | 重构 | 调用 Service 层 |
| `backend/src/main/java/com/platform/controller/AuthController.java` | 重构 | 调用 Service 层 + 登录日志 |
| `backend/src/main/resources/application.yml` | 修改 | 版本号更新 |
| `backend/pom.xml` | 修改 | Java 版本调整 |

---

## v1.13.2 (2026-05-17)

### 🐛 Bug 修复

#### 后端编译错误修复
- **ConfigController 类型转换错误**：
  - 问题：`DEFAULTS.get(key)` 返回 `Object` 类型，无法直接赋值给 `String`
  - 修复：将变量类型改为 `Object`，使用 `.toString()` 安全转换
  - 文件：`backend/src/main/java/com/platform/controller/ConfigController.java`

- **OperationLogAspect AOP 表达式语法错误**：
  - 问题：`@annotation(org.springframework.web.bind.annotation.*)` 不支持通配符语法
  - 修复：移除 `@annotation()` 部分，利用 `around()` 方法中的 HTTP 方法过滤逻辑
  - 文件：`backend/src/main/java/com/platform/aspect/OperationLogAspect.java`

---

## v1.13.1 (2026-05-17)

### 📦 数据库结构优化

#### 表结构清理
- **移除 `sys_permission.module_id` 字段**：
  - 原因：模块与权限通过权限编码前缀隐式关联（如 `example-module:*`）
  - 影响：减少冗余外键约束，简化模块权限管理
  - 文件：`backend/sql/init.sql`

- **移除 `sys_module.install_path` 字段**：
  - 原因：采用编译时合并（冷插拔）机制，无需运行时路径
  - 影响：简化模块注册流程
  - 文件：`backend/sql/init.sql`

---

## v1.13.0 (2026-05-17)

### ✨ 新功能

#### 双界面架构完善
- **用户界面** (`/home`)：
  - 暗色星空主题
  - Canvas 流动星空背景
  - 玻璃态 UI 组件
  - 流光特效

- **管理后台** (`/admin`)：
  - 亮色专业面板
  - 暗色侧边栏
  - 白色内容区

#### 路由过渡动画
- 用户界面 ↔ 管理后台：clip-path 滑动展开
- 登录页 ↔ 任意界面：缩放淡入
- 管理后台内部切换：无动画（即时响应）

---

## 版本历史

| 版本 | 日期 | 主要变更 |
|:---|:---|:---|
| v1.14.0 | 2026-05-17 | 三层架构重构 + 文件损坏修复 |
| v1.13.2 | 2026-05-17 | 编译错误修复 |
| v1.13.1 | 2026-05-17 | 数据库结构优化 |
| v1.13.0 | 2026-05-17 | 双界面架构 + 路由动画 |
| v1.12.0 | 2026-05-17 | 界面全面升级 |
| v1.11.0 | 2026-05-17 | 模块启用/停用功能 |
| v1.10.0 | 2026-05-17 | 界面优化 |
| v1.9.0 | 2026-05-17 | 配置简化 |
| v1.8.0 | 2026-05-17 | 部署简化 |
| v1.7.0 | 2026-05-17 | 界面分离 |
| v1.6.0 | 2026-05-17 | 安全性改进 |
| v1.5.0 | 2026-05-17 | 界面分离 |
| v1.4.0 | 2026-05-17 | 权限系统 |
| v1.0.0 | 2026-05-17 | 初始版本 |
