# 内网万用平台

基于 Vue 3 + Spring Boot 的模块化内网应用平台。

## 项目简介

内网万用平台是一个模块化的内网应用系统，支持用户角色权限管理（RBAC）、运行时模块启用/停用、双界面架构（暗色用户界面 + 亮色管理后台）、路由过渡动画。

### 界面预览

| 界面类型 | 路径 | 风格 | 说明 |
|:---|:---|:---|:---|
| 普通用户界面 | `/home` | 暗色星空主题 | Canvas 流动星空背景、玻璃态卡片、流光特效、模块分页 |
| 管理后台 | `/admin` | 亮色专业面板 | 暗色侧边栏 + 白色内容区、无内部页面动画 |

## 项目结构

```
project_V/
├── frontend/                  # Vue3前端项目
│   ├── src/
│   │   ├── layouts/        # 布局组件（UserLayout / MainLayout）
│   │   │   ├── UserLayout.vue    # 用户界面布局（暗色主题）
│   │   │   └── MainLayout.vue    # 管理后台布局（亮色内容+暗色侧边栏）
│   │   ├── views/          # 页面组件
│   │   │   ├── home/       # 用户首页（模块卡片网格 + 分页）
│   │   │   ├── dashboard/  # 管理后台首页（真实数据统计）
│   │   │   ├── module/     # 模块管理页
│   │   │   ├── config/     # 系统配置页
│   │   │   ├── user/       # 用户管理页
│   │   │   ├── role/       # 角色管理页
│   │   │   ├── log/        # 日志管理页
│   │   │   └── ...         # 其他页面
│   │   ├── api/            # API接口定义
│   │   ├── stores/         # Pinia状态管理（含菜单动态加载）
│   │   ├── router/         # 路由配置（含模块自动扫描 + 过渡动画控制）
│   │   └── components/     # 公共组件
│   ├── App.vue             # 全局路由过渡动画入口
│   └── vite.config.ts      # Vite配置（别名、代理）
│
├── backend/                 # Spring Boot后端项目
│   └── src/main/
│       ├── java/com/platform/
│       │   ├── controller/  # 控制层（请求/响应处理）
│       │   ├── service/     # 服务接口层
│       │   │   └── impl/    # 服务实现层
│       │   ├── dto/         # 数据传输对象层
│       │   ├── mapper/      # 数据访问层
│       │   ├── entity/      # 实体类
│       │   ├── config/      # 配置类
│       │   ├── common/      # 通用组件
│       │   ├── util/        # 工具类
│       │   └── module/       # 模块后端代码（编译时合并到此目录）
│       └── resources/
│           └── application.yml
│
├── modules/                 # 业务模块存放目录（v2.0 模块体系）
│   ├── example-module/     # ⭐ 示例模块 v2.0（生产级模板）
│   │   ├── module.json     # 模块元信息
│   │   ├── README.md       # 模块文档
│   │   ├── frontend/src/   # 模块前端代码（Vite 自动扫描）
│   │   ├── backend/src/    # 模块后端代码（需复制到主项目）
│   │   └── sql/            # ⭐ 数据库脚本体系
│   │       ├── install.sql   # 安装脚本（建表+注册权限，一键执行）
│   │       └── uninstall.sql # 卸载脚本（清除所有数据）
│   │
│   └── bookmark-module/    # 🆕 网址收藏模块（v1.0.0）
│       ├── module.json     # 模块元信息
│       ├── README.md       # 模块文档
│       ├── frontend/src/   # 模块前端代码
│       ├── backend/src/    # 模块后端代码
│       └── sql/            # 数据库脚本体系
│           ├── install.sql   # 安装脚本
│           └── uninstall.sql # 卸载脚本
│
├── sql/
│   └── init.sql            # 数据库初始化脚本（系统表）
│
└── docs/                   # 项目文档
```

## 技术栈

### 前端
- Vue 3.4+ - 渐进式 JavaScript 框架
- Vite 6.x - 下一代前端构建工具
- Vue Router 4.x - Vue 官方路由管理器（含动态路由注册）
- Pinia 2.x - Vue 状态管理库
- Element Plus 2.x - Vue3 UI 组件库

### 后端
- Spring Boot 3.2+ - Spring 企业级开发框架
- MyBatis Plus 3.5+ - MyBatis 增强框架
- MySQL 8.0+ - 关系型数据库
- JWT - JSON Web Token 认证
- Spring Security - 安全框架

## 快速部署

### 环境要求

- Node.js 18+
- JDK 17+
- MySQL 8.0+
- Maven 3.8+

### 部署步骤

#### 1. 配置数据库连接

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/platform
    username: 你的数据库用户名
    password: 你的数据库密码
```

#### 2. 初始化数据库

```bash
mysql -u 你的数据库用户名 -p < backend/sql/init.sql
```

#### 3. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务：`http://localhost:8888`

#### 4. 安装前端依赖并启动

```bash
cd frontend
npm install
npm run dev
```

前端服务：`http://localhost:5173`

#### 5. 安装模块（可选）

**方式一：示例模块（推荐用于学习）**
```bash
# 一键安装（建表 + 注册权限）
mysql -u root -p platform < modules/example-module/sql/install.sql

# 如需后端接口，复制后端代码
xcopy "modules\example-module\backend\src\main\java\com\platform\module\example" ^
      "backend\src\main\java\com\platform\module\example" /E /I /Y
```

**方式二：网址收藏模块（推荐用于使用）**
```bash
# 一键安装
mysql -u root -p platform < modules/bookmark-module/sql/install.sql

# 复制后端代码
xcopy "modules\bookmark-module\backend\src\main\java\com\platform\module\bookmark" ^
      "backend\src\main\java\com\platform\module\bookmark" /E /I /Y
```

> **卸载模块**：执行 `mysql ... < modules/{module-name}/sql/uninstall.sql` 即可完全清除

#### 7. 访问平台

打开浏览器访问 `http://localhost:5173`

## 默认账号

- **用户名**：admin
- **密码**：Admin@123456

> ⚠️ **安全提示**：首次登录后请立即修改默认密码！

## 界面说明

### 普通用户界面 (`/home`) — 暗色主题

- **Canvas 星空流动背景**：深色底色上绘制缓慢移动的星星粒子
- **玻璃态 UI 组件**：半透明卡片、模糊效果、渐变边框
- **流光特效**：Logo 区域扫光动画、图标发光效果
- **模块卡片分页**：每页显示 8 个模块，支持页码切换和平滑滚动
- **无导航栏**：移除了无实际作用的导航栏，仅保留 Logo 和用户信息区域
- **路由过渡动画**：与管理后台之间切换时使用 clip-path 滑动展开效果

### 管理后台 (`/admin`) — 专业面板风格

- **暗色侧边栏**（`#1d1d2e`）：高对比度文字，子菜单同步暗色背景
- **白色内容区**（`#f5f6fa`）：保持后台管理的专业可读性
- **无内部页面动画**：管理后台内部切换即时响应，不添加任何过渡效果
- **真实数据统计**：首页仪表盘从数据库动态获取用户数、模块数、日志数等
- **侧边栏菜单**（6 项）：首页 / 用户管理 / 角色管理 / 模块管理 / 系统配置 / 日志管理
  - 注意：业务模块功能入口仅在用户界面展示，管理后台不再重复显示

### 路由过渡动画策略

| 切换场景 | 动画效果 |
|:---|:---|
| 用户界面 ↔ 管理后台 | clip-path 滑动展开（0.5s cubic-bezier） |
| 登录页 ↔ 任意界面 | 缩放淡入 |
| 管理后台内部页面切换 | ❌ 即时切换，无动画 |
| 用户界面内部切换 | 轻柔交叉淡化 |

## 已实现模块

> **版本**：v2.0.0 | 更新日期：2026-05-18

| 模块 | 版本 | 类型 | 说明 | 文档 |
|:---|:---:|:---|:---|:---|
| **example-module** | v2.0.0 | 示例/模板 | ⭐ 生产级模块开发模板，可直接复制使用 | [README](modules/example-module/README.md) |
| **bookmark-module** | v1.0.0 | 业务模块 | 🆕 网址收藏合集，支持隐私保护和搜索功能 | [README](modules/bookmark-module/README.md) |

### 快速开发新模块

```bash
# 1. 复制示例模块
cp -r modules/example-module modules/your-module

# 2. 一键安装到数据库
mysql -u root -p platform < modules/your-module/sql/install.sql

# 3. 复制后端代码（如需后端接口）
xcopy "modules\your-module\backend\src\..." "backend\src\..." /E /I /Y

# 4. 重启服务 → 刷新浏览器 → 完成！
```

> 📖 **完整开发教程**：[模块开发指南](docs/MODULE_DEVELOPMENT_GUIDE.md)（v2.0，7步完整教程）

## 项目文档

| 文档 | 说明 |
|:---|:---|
| [需求规格说明书](docs/SPEC.md) | 系统需求和架构设计 |
| [项目部署指南](docs/DEPLOYMENT_GUIDE.md) | 从零部署到正常运行（运维/使用者） |
| [API 接口规范](docs/API_SPEC.md) | 后端 API 接口定义 |
| [模块开发指南](docs/MODULE_DEVELOPMENT_GUIDE.md) | 如何开发自定义模块 |
| [数据库表结构文档](docs/DATABASE_SCHEMA.md) | 数据库表结构详解 |
| [Git 配置指南](docs/GIT_CONFIG.md) | Git 仓库配置和常用命令（新） |
| [更新日志](docs/CHANGELOG.md) | 版本更新记录 |

> 当前版本: **v1.14.0**
