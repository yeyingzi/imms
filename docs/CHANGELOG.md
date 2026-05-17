# 更新日志

## v2.0.0 (2026-05-18) 🎉

> **里程碑版本**：模块体系全面升级至生产级标准，新增网址收藏模块，建立完整的 SQL 脚本体系和文档体系。

---

## ✨ 核心功能

### 🆕 新增：网址收藏模块 (bookmark-module)

**模块定位**：公共数据 + 公共权限的网址收藏合集，适合团队共享场景。

#### 功能特性
- ✅ **CRUD 完整功能**：创建、查看、编辑、删除网址
- ✅ **隐私保护机制**：
  - 网址与创建者绑定（`created_by` 字段）
  - 支持私密标记（`is_private` 字段）
  - 私密网址仅创建者和管理员可见
  - 个人用户可通过滑块按钮隐藏/展示私密网址
- ✅ **搜索功能**：
  - 精确搜索：按标题/URL/描述精确匹配
  - 模糊搜索：关键词全局模糊匹配
- ✅ **卡片式 UI**：
  - 响应式网格布局（桌面/平板/移动端适配）
  - 卡片展示：图标、标题、URL、描述
  - 操作按钮：复制链接、跳转、编辑、删除
- ✅ **权限控制**：
  - 创建者和管理员可编辑/删除自己的网址
  - 普通用户只能查看和收藏公开网址

#### 技术实现
- **前端**：Vue 3 + Element Plus + TypeScript
- **后端**：Spring Boot + MyBatis Plus
- **数据库表**：`bm_bookmark`（8个字段）
- **API 接口**：5 个 RESTful API（分页查询/详情/创建/更新/删除）

---

## 🔧 示例模块重构至 v2.0 (example-module)

### 升级内容

#### 1. 后端代码完善（从无到有）

| 文件 | 职责 | 关键特性 |
|:---|:---|:---|
| `entity/Example.java` | 实体类 | Lombok 注解 + MyBatis Plus |
| `mapper/ExampleMapper.java` | Mapper 接口 | 继承 BaseMapper |
| `service/IExampleService.java` | 服务接口 | 继承 IService |
| `service/impl/ExampleServiceImpl.java` | 服务实现 | **不重复包装父类方法** ⭐ |
| `controller/ExampleController.java` | 控制器 | 支持 keyword 搜索 |

#### 2. 前端代码增强

| 改进点 | 说明 |
|:---|:---|
| **搜索功能** | 新增 keyword 搜索框，支持名称/描述模糊查询 |
| **表单验证** | 添加必填项校验、长度限制、去首尾空格 |
| **空状态处理** | 无数据时显示友好提示 |
| **加载状态** | 按钮禁用 + loading 提示 |
| **TypeScript 类型** | 完整的类型定义和导出 |

#### 3. SQL 脚本体系重构 ⭐⭐⭐

**旧版（v1.x）**：
```sql
-- sql/init.sql：只建表 + 测试数据
CREATE TABLE IF NOT EXISTS exm_example (...);
INSERT INTO exm_example (...) VALUES ('测试数据1', ...);  -- ❌ 包含测试数据
```

**新版（v2.0）**：

**install.sql（一键安装）**：
```sql
-- 5 步自动化流程
SET NAMES utf8mb4;
-- Step 1: 清理旧数据（支持重复执行）
-- Step 2: 创建数据表（遵循最简原则）✨
-- Step 3: 注册模块到系统表
-- Step 4: 注册菜单权限 + 按钮权限
-- Step 5: 分配权限给超级管理员
```

**uninstall.sql（完整卸载）**：
```sql
-- 4 步纯粹卸载
-- Step 1: 删除角色权限关联
-- Step 2: 删除权限记录
-- Step 3: 删除模块注册信息
-- Step 4: 删除数据表
```

**核心改进**：
- ✅ **最简原则**：不包含测试数据，只做必要操作
- ✅ **支持重复执行**：先清理再创建，安全可靠
- ✅ **UNHEX() 中文支持**：确保中文正确存储到数据库
- ✅ **完整卸载**：清除所有相关数据，无残留

#### 4. 文档完善

**example-module/README.md**（300+ 行）：
- 快速安装指南（30秒上手）
- 功能特性说明
- 数据库设计详解
- API 接口文档
- 开发指南（如何基于此模板开发新模块）
- 常见问题 FAQ
- 更新日志

---

## 📚 文档体系升级

### modules/README.md 重构为导航入口页

**改进策略**：从"重复的开发教程"转型为"快速索引 + 导航中心"

| 维度 | 旧版 | 新版 |
|:---|:---|:---|
| 定位 | 详细开发教程 | **导航入口页** |
| 长度 | 64行 | 114行 |
| 核心内容 | 7步开发流程 | **快速开始 + 目录结构 + 已安装模块列表 + 文档索引** |

**新增亮点**：
- 🚀 **30秒快速开始**：精确命令，复制即用
- 📁 **标准模块结构**：完整目录树 + 每个文件注释
- 📦 **已安装模块表格**：模块名称 + 版本 + 文档链接
- 📚 **三级文档导航**：快速开始 → 详细指南 → 模块文档
- 🎯 **开发流程可视化**：ASCII 流程图
- ❓ **FAQ 速查**：4个常见问题快速解答

**保留价值**：
- ✅ 全局视角：已安装模块一览表（唯一性）
- ✅ 快速入口：30秒上手，无需阅读长文档
- ✅ 规范说明：集中强调命名规范

### MODULE_DEVELOPMENT_GUIDE.md 升级至 v2.0

**规模**：905行 → ~1000行（新增95+行核心内容）

**主要更新点**：

1. **文档头部**
   - 添加版本号 v2.0 和更新日期
   - 新增「v2.0 主要更新」章节
   - 添加迁移指南（v1.x → v2.0）

2. **第2章：模块目录结构规范**
   ```
   旧版：
   └── sql/init.sql            # 数据库建表脚本
   
   新版：
   └── sql/                    # ⭐ 数据库脚本体系
       ├── install.sql         # 安装脚本（建表+注册权限）
       └── uninstall.sql       # 卸载脚本（清除所有数据）
   ```

3. **Step 6：数据库脚本**（完全重写）
   - 旧版：30行（建表 + 测试数据）
   - 新版：95行（install.sql 完整流程 + uninstall.sql 卸载方案）
   - 新增核心要点说明（最简原则、UNHEX()、重复执行）

4. **Step 7：部署操作**
   ```bash
   # 旧版
   ② 在 MySQL 中执行 sql/init.sql
   
   # 新版
   ② mysql ... < install.sql  # 一键完成建表+注册
   > 卸载：mysql ... < uninstall.sql
   ```

5. **第4章：代码模板汇总**
   - 数据库部分：从1行扩展为2行（安装+卸载）
   - 关键文件参考：5个文件 → 10个文件（标注所有 v2.0 改进）

6. **附录：快速检查清单**
   - 数据库部分：1项 → 4项（install/uninstall/UNHEX/重复执行）
   - 部署操作：新增"已执行 install.sql"

7. **FAQ 新增 Q7**
   - SQL 脚本体系对比（init.sql vs install.sql）
   - 最佳实践说明（测试数据、UNHEX、重复执行）

---

## 🐛 问题修复记录

### 1. 数据库中文乱码问题

**问题描述**：MySQL 终端查询结果显示乱码（如"缃戝潃鏀惰棌鍚堥泦"）

**根因分析**：终端编码与 MySQL 字符集不匹配

**解决方案**：使用 UNHEX() 函数直接插入 UTF-8 字节
```sql
-- ❌ 旧方式（可能乱码）
INSERT INTO sys_module (name) VALUES ('示例模块');

-- ✅ 新方式（确保正确）
INSERT INTO sys_module (name) VALUES (UNHEX('E7A4BAE4BE8B6A1E58F97'));
```

**影响文件**：
- `bookmark-module/sql/install.sql`
- `example-module/sql/install.sql`

---

### 2. 前端编译错误

**错误信息**：
```
[plugin:vite:import-analysis] Failed to resolve import "./api/index"
from "Index.vue"
```

**根因分析**：API 导入路径错误（相对路径层级不对）

**解决方案**：
```typescript
// ❌ 错误路径
import { bookmarkApi } from './api/index'

// ✅ 正确路径
import { bookmarkApi } from '../api'
```

**影响文件**：`bookmark-module/frontend/src/views/Index.vue`

---

### 3. 后端编译错误

**错误信息**：
```
找不到符号: 类 Result
位置: 程序包 com.platform.common
```

**根因分析**：Result 类导入路径不正确

**解决方案**：
```java
// ❌ 错误导入
import com.platform.common.Result;

// ✅ 正确导入
import com.platform.common.result.Result;
```

**影响文件**：`bookmark-module/backend/.../BookmarkController.java`

---

### 4. 模块管理页面看不到新模块

**问题描述**：执行完所有步骤后，后台管理的模块列表中看不到新模块

**根因分析**：模块未在数据库 `sys_module` 表中注册

**解决方案**：通过 install.sql 自动注册模块信息、菜单权限、按钮权限，并分配给超级管理员角色

**验证方法**：
```sql
SELECT * FROM sys_module WHERE module_key = 'bookmark-module';
SELECT * FROM sys_permission WHERE code LIKE 'bookmark-module:%';
```

---

## 📊 开发统计数据

### 本次开发的代码量

| 类别 | 文件数 | 代码行数（估算） | 说明 |
|:---|:---:|:---:|:---|
| **bookmark-module** | | | |
| 前端代码 | 3 | ~400 | Index.vue + api/index.ts + index.ts |
| 后端代码 | 5 | ~300 | Entity + Controller + Service + Mapper |
| SQL 脚本 | 2 | ~150 | install.sql + uninstall.sql |
| 文档 | 1 | ~200 | README.md |
| **小计** | **11** | **~1050** | |
| **example-module v2.0** | | | |
| 前端代码 | 3 | ~350 | 重构增强 |
| 后端代码 | 5 | ~350 | 从无到有 |
| SQL 脚本 | 2 | ~120 | 重构 |
| 文档 | 1 | ~300 | README.md |
| **小计** | **11** | **~1120** | |
| **文档更新** | | | |
| MODULE_DEVELOPMENT_GUIDE.md | 1 | +95 | v2.0 升级 |
| modules/README.md | 1 | +50 | 重构为导航页 |
| CHANGELOG.md | 1 | +250 | 本文档 |
| **小计** | **3** | **~395** | |
| **总计** | **25** | **~2565** | |

### 解决的技术难题

- ✅ 数据库中文乱码（UNHEX 方案）
- ✅ 前端路径解析错误
- ✅ 后端类导入路径错误
- ✅ 模块注册机制理解
- ✅ 权限分配流程
- ✅ SQL 脚本最佳实践（最简原则、重复执行、完整卸载）

---

## 🎯 版本总结

### v2.0 的三大里程碑

1. **🏗️ 生产级模块模板**
   - example-module 从"简单示例"升级为"可直接复用的生产级模板"
   - 完整的前后端代码 + SQL 脚本 + 文档
   - 遵循最佳实践（最简原则、类型安全、错误处理）

2. **🔧 SQL 脚本体系标准化**
   - 建立 install.sql + uninstall.sql 双脚本标准
   - 解决中文存储、重复执行、完整卸载等问题
   - 为后续所有模块提供统一的脚本模板

3. **📚 三级文档体系成型**
   ```
   Level 1: modules/README.md           # 导航入口（快速索引）
   Level 2: docs/MODULE_DEVELOPMENT_GUIDE.md  # 系统教程（完整指南）
   Level 3: example-module/README.md    # 模块文档（使用手册）
   Level 4: example-module/*            # 代码实现（生产级模板）
   ```

### 向后兼容性

- ✅ **完全兼容**：v1.x 模块无需修改即可继续运行
- ✅ **渐进式迁移**：提供清晰的迁移指南（v1.x → v2.0）
- ✅ **双轨并存**：旧的 init.sql 方式仍然支持，但推荐使用新的 SQL 体系

### 下一步计划

- [ ] 开发更多业务模块（如：笔记模块、任务管理模块等）
- [ ] 优化模块热加载机制（无需重启服务）
- [ ] 建立模块市场/仓库概念
- [ ] 编写自动化测试用例
- [ ] 性能优化和监控

---

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

### 9. 用户创建时 phone/email 空字符串处理
- **问题**：`UserServiceImpl.createUser()` 直接使用前端传入的 phone/email，当为空字符串时触发数据库唯一约束冲突
- **修复**：在插入前检查 phone/email 是否为空字符串，如果为空则设置为 null
- **文件**：`backend/src/main/java/com/platform/service/impl/UserServiceImpl.java`

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
