# 示例模块 (example-module)

> **模块开发模板** - 展示最佳实践和规范
>
> 版本：2.0.0 | 更新时间：2026-05-18

---

## 📖 模块简介

这是一个标准的模块开发示例，展示了如何从零开始创建一个完整的、可复用的功能模块。

**核心特性**：
- ✅ 完整的 CRUD 功能（增删改查）
- ✅ 分页和搜索功能
- ✅ 响应式 UI 设计
- ✅ 标准的权限控制
- ✅ 一键安装/卸载脚本

---

## ⚡ 快速安装（30秒）

### 1️⃣ 初始化数据库

```bash
mysql -u root -p{password} {database} < modules/example-module/sql/install.sql
```

### 2️⃣ 复制后端代码（一次性操作）

```bash
# Windows PowerShell
xcopy "modules\example-module\backend\src\main\java\com\platform\module\example" ^
      "backend\src\main\java\com\platform\module\example" /E /I /Y

# Linux/Mac
cp -r modules/example-module/backend/src/main/java/com/platform/module/example \
      backend/src/main/java/com/platform/module/example/
```

### 3️⃣ 重启后端服务

```bash
cd backend && mvn spring-boot:run
```

### 4️⃣ 刷新浏览器

访问 `http://localhost:5173/admin/example-module` 即可看到新模块。

---

## 🎯 模块结构

```
modules/example-module/
├── module.json                          # 模块元信息
│
├── sql/                                 # 数据库脚本
│   ├── install.sql                      # ⭐ 安装脚本（建表+注册）
│   ├── uninstall.sql                    # ⭐ 卸载脚本
│   └── init.sql                         # 旧版建表脚本（仅供参考）
│
├── frontend/                            # 前端代码（Vite自动扫描）
│   └── src/
│       ├── index.ts                     # ⭐ 模块入口（路由+菜单+权限）
│       ├── api/index.ts                 # API 接口封装
│       └── views/Index.vue              # 主页面组件
│
├── backend/                             # 后端代码（需复制到主项目）
│   └── src/main/java/com/platform/module/example/
│       ├── entity/Example.java          # 实体类
│       ├── controller/ExampleController.java  # 控制器
│       ├── service/IExampleService.java # 服务接口
│       ├── service/impl/ExampleServiceImpl.java  # 服务实现
│       └── mapper/ExampleMapper.java    # 数据访问层
│
└── README.md                            # 本文档
```

---

## 📦 安装与卸载

### 安装模块

```bash
mysql -u root -p{password} {database} < modules/example-module/sql/install.sql
```

**脚本会自动完成**：
1. 清理旧数据（支持重复执行）
2. 创建 `exm_example` 数据表
3. 注册模块到系统表
4. 注册菜单和按钮权限
5. 为超级管理员分配权限

### 卸载模块

```bash
mysql -u root -p{password} {database} < modules/example-module/sql/uninstall.sql
```

**脚本会清除**：
- 角色权限关联
- 权限记录
- 模块注册信息
- 数据表

---

## 🛠️ 开发指南

### 从此模板开发新模块

#### Step 1: 复制模板

```bash
cp -r modules/example-module modules/my-new-module
```

#### Step 2: 修改关键文件

| 文件 | 需要修改的内容 |
|------|---------------|
| `module.json` | moduleKey, name, description |
| `sql/install.sql` | 表名、字段、模块key |
| `frontend/src/index.ts` | 路由path、name、权限码 |
| `frontend/src/api/index.ts` | API路径、接口定义 |
| `frontend/src/views/Index.vue` | 页面UI和业务逻辑 |
| `backend/.../*.java` | 实体类、控制器等 |

#### Step 3: 执行安装

```bash
# 1. 初始化数据库
mysql ... < my-new-module/sql/install.sql

# 2. 复制后端代码
xcopy ... /E /I /Y

# 3. 重启服务
cd backend && mvn spring-boot:run
```

---

## 🔑 关键注意事项

### 前端开发要点

1. **路由配置**
   - path 必须以 `/` 开头
   - 组件导入必须使用相对路径 `./views/Index.vue`
   - 必须使用 `export default` 导出配置对象

2. **API 封装**
   - 路径格式：`/v1/{module-key}/...`
   - 使用 TypeScript 类型定义
   - 返回值类型要明确

3. **页面组件**
   - 使用 Composition API (`<script setup>`)
   - 包含搜索、分页、空状态处理
   - 表单验证必不可少

### 后端开发要点

1. **实体类**
   - `@TableName` 与 SQL 表名一致
   - 使用 Lombok 简化代码
   - 字段名使用驼峰命名

2. **控制器**
   - `@RequestMapping` 路径为 `/api/v1/{module-key}`
   - 返回统一格式 `Result<T>`
   - ServiceImpl 不要重复包装父类方法

3. **部署要求**
   - Java 文件必须复制到 `backend/src/main/java/...`
   - 重启后端服务才能生效

---

## 📚 相关文件索引

| 文件路径 | 说明 | 重要程度 |
|----------|------|:--------:|
| [module.json](./module.json) | 模块元信息配置 | ⭐⭐⭐ |
| **SQL 脚本** |||
| **[sql/install.sql](./sql/install.sql)** | **⭐ 安装脚本（建表+注册）** | ⭐⭐⭐ |
| **[sql/uninstall.sql](./sql/uninstall.sql)** | **⭐ 卸载脚本** | ⭐⭐⭐ |
| **前端代码** |||
| [frontend/src/index.ts](./frontend/src/index.ts) | 前端入口（路由/菜单/权限） | ⭐⭐⭐ |
| [frontend/src/views/Index.vue](./frontend/src/views/Index.vue) | 主页面组件 | ⭐⭐⭐ |
| [frontend/src/api/index.ts](./frontend/src/api/index.ts) | API 接口封装 | ⭐⭐ |
| **后端代码** |||
| [backend/.../entity/Example.java](./backend/src/main/java/com/platform/module/example/entity/Example.java) | 实体类 | ⭐⭐ |
| [backend/.../controller/ExampleController.java](./backend/src/main/java/com/platform/module/example/controller/ExampleController.java) | 控制器 | ⭐⭐ |
| [backend/.../service/impl/ExampleServiceImpl.java](./backend/src/main/java/com/platform/module/example/service/impl/ExampleServiceImpl.java) | 服务实现 | ⭐⭐ |

### 🎯 快速上手文件

**首次开发只需关注这 3 个文件**：

1. **[sql/install.sql](./sql/install.sql)** - 执行此脚本完成数据库初始化
2. 复制 `backend/` 目录下的 Java 文件到主项目
3. 重启后端服务 → 刷新浏览器 → 完成！

---

## ❓ 常见问题

### Q1: 模块不显示？

检查清单：
- [ ] SQL 脚本是否执行成功？
- [ ] 后端代码是否已复制并重启？
- [ ] 浏览器是否刷新？
- [ ] 控制台是否有错误？

### Q2: 如何添加更多页面？

在 `index.ts` 的 routes 数组中添加：

```typescript
const routes = [
  {
    path: '/my-module',
    name: 'MyModule',
    component: () => import('./views/Index.vue'),
    meta: { title: '我的模块' }
  },
  {
    path: '/my-module/detail/:id',
    name: 'MyModuleDetail',
    component: () => import('./views/Detail.vue'),
    meta: { title: '详情页' }
  }
]
```

### Q3: 纯前端模块如何开发？

只需创建以下文件即可：
- `frontend/src/index.ts`（路由 + 菜单）
- `frontend/src/views/Index.vue`（页面组件）

跳过后端代码和 SQL 脚本。

---

## 📊 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|---------|
| v2.0.0 | 2026-05-18 | 重构为生产级模板，添加完整SQL脚本体系 |
| v1.0.0 | 2026-05-17 | 初始版本 |

---

**最后更新时间**：2026-05-18
**文档版本**：v2.0.0
**维护者**：Platform Team