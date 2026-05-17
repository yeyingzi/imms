# 内网万用平台 - 模块开发指南

> 面向开发者：自定义模块开发和冷加载机制详解

> **前置阅读**：[项目部署指南](./DEPLOYMENT_GUIDE.md) — 了解环境搭建和部署流程

---

## 目录

1. [模块系统原理](#1-模块系统原理)
2. [模块目录结构规范](#2-模块目录结构规范)
3. [开发步骤（7 步）](#3-开发步骤7-步)
4. [各层代码模板](#4-各层代码模板)
5. [让模块生效（部署操作）](#5-让模块生效部署操作)
6. [关键文件参考](#6-关键文件参考)
7. [常见开发问题](#7-常见开发问题)

---

## 1. 模块系统原理

### 1.1 模块生命周期

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│  1. 放置代码  │ →  │  2. 执行 SQL  │ →  │  3. 复制后端  │
│ modules/ 目录 │    │  建表+初始化  │    │  到主项目    │
└─────────────┘    └──────────────┘    └─────────────┘
                                               ↓
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│  6. 模块可用  │ ←  │  5. 刷新页面  │ ←  │  4. 重启服务  │
│  双界面显示   │    │  glob 扫描   │    │  前端+后端   │
└─────────────┘    └──────────────┘    └─────────────┘
```

### 1.2 前端自动发现机制

核心是 Vite 的 `import.meta.glob` 静态文件扫描：

```typescript
// router/index.ts 和 stores/menu.ts 中都使用此方式
const modules = import.meta.glob('@modules/*/frontend/src/index.ts', {
  eager: true,        // 同步导入（启动时立即执行）
  import: 'default'   // 只取 default 导出
})
```

**扫描过程：**

```
@modules/*/frontend/src/index.ts
        ↓ Vite 在启动时解析别名并扫描文件系统
modules/example-module/frontend/src/index.ts  ← 找到！
modules/your-module/frontend/src/index.ts     ← 找到！（如果有）
        ↓ eager:true 立即执行每个 index.ts
获取导出的 ModuleConfig 对象 { routes, menus, permissions }
        ↓ 提取数据
routes[] → 注入 Vue Router 的布局 children
menus[]   → 存入 menuStore.dynamicMenus[]
permissions[] → 声明式记录（供未来权限系统使用）
```

**关键约束：**

| 约束 | 说明 |
|------|------|
| 路径必须用 `@modules` 别名 | 不能用超出项目 root 的相对路径如 `../../../modules/...`，会被静默忽略 |
| 必须有 `index.ts` 入口文件 | 位于 `{module}/frontend/src/index.ts` |
| 必须 `export default` | 导出包含 `routes`、`menus`、`permissions` 的对象 |
| 新增模块后需重启前端 | Vite 的 glob 在 dev server 启动时扫描 |

### 1.3 后端集成方式（编译时合并）

**前端 vs 后端的集成方式不同：**

| 层 | 集成方式 | 是否需要手动操作 |
|---|---------|----------------|
| 前端代码 | Vite 启动时自动扫描 `modules/*/frontend/src/` | ❌ 不需要，放进去就生效 |
| 后端代码 | Spring Boot 编译时扫描 `backend/src/main/java/...` | ✅ 需要从 modules 复制到 backend |

**为什么后端不能像前端一样自动扫描？**
- Spring Boot 的组件扫描基于 classpath，编译后的类必须在指定包路径下
- `modules/` 目录不在 Spring Boot 的 classpath 扫描范围内
- 运行时动态添加 Controller 需要 JVM Agent 或自定义 ClassLoader，复杂度高

**实际操作流程：**

```
开发阶段：
modules/my-module/backend/src/main/java/com/platform/module/my-module/
  （编写和调试代码）

部署阶段：
复制 → backend/src/main/java/com/platform/module/my-module/
  （进入主项目的编译路径）

重启后端 → Spring Boot 自动发现并注册 Controller ✅
```

### 1.4 运行时模块启用/停用机制

模块部署后，管理员可通过管理后台控制每个模块的**运行时可见性**：

#### 工作流程

```
┌──────────────────────────────────────────────────────┐
│                  管理后台 /admin/module                │
│                                                      │
│   模块列表 → [示例模块] ──●── [启用/停用开关]         │
│                      │                               │
│                      ▼                               │
│   PUT /api/v1/modules/{id}/toggle                    │
│                      │                               │
│                      ▼                               │
│   DB: sys_module.status = 0 (停用) 或 1 (启用)        │
│                      │                               │
│                      ▼                               │
│   前端 menuStore.refreshMenus()                       │
│      → GET /v1/modules 获取最新状态                   │
│      → 过滤 status=0 的模块                           │
│      → 停用模块的菜单不加载                            │
│      → 路由守卫拦截该模块 URL 访问                     │
└──────────────────────────────────────────────────────┘
```

#### 关键文件

| 文件 | 职责 |
|------|------|
| [stores/menu.ts](../frontend/src/stores/menu.ts) | 加载菜单前查询模块 API，过滤停用模块 |
| [router/index.ts](../frontend/src/router/index.ts) | 导航守卫拦截已停用模块的直接 URL 访问 |
| [views/module/index.vue](../frontend/src/views/module/index.vue) | 管理界面开关，切换后调用 refreshMenus |
| [ModuleController.java](../backend/src/main/java/com/platform/controller/ModuleController.java) | 后端 toggle 接口，更新数据库状态 |

#### 技术说明

> **这不是真正的热插拔。** 模块代码在 Vite 编译时就已全部打包进前端 bundle，启用/停用只是基于数据库状态的**软禁用（Soft Disable）**——隐藏菜单入口并拦截路由访问。模块的 JS/CSS/组件仍驻留在浏览器内存中，但用户无法感知到它们的存在。
>
> 对于内网平台而言，这种方案足够实用：无需重启、即时生效、实现简单。

### 1.5 数据流全景

```
                    ┌─────────────────────────────────────┐
                    │         模块 index.ts 入口文件         │
                    │  导出: routes[], menus[], permissions[]│
                    └──────────┬──────────┬────────────────┘
                               │          │
                    ┌──────────▼──┐  ┌────▼────────┐
                    │ router/index │  │ stores/menu  │
                    │ 自动注册路由  │  │ 加载菜单数据  │
                    └──────┬───────┘  └──────┬───────┘
                           │                 │
              ┌────────────▼──────┐  ┌───────▼────────┐
              │ UserLayout children│  │ home/index.vue│
              │ (/my-module)      │  │ 模块卡片+分页  │
              │ 用户界面入口       │  │ 用户界面展示   │
              └───────────────────┘  └────────────────┘

注意：模块功能页面在管理后台也可通过 /admin/my-module 访问（路由已注册），
但管理后台侧边栏不再显示「业务模块」子菜单组，避免与模块管理和用户界面重复。
模块的功能入口统一在用户界面首页以卡片形式展示。
```

---

## 2. 模块目录结构规范

### 完整结构

```
modules/
└── your-module-name/           # 模块名（小写中划线分隔）
    ├── module.json             # 模块元信息配置
    │
    ├── frontend/               # ⭐ 前端代码（Vite 自动扫描）
    │   └── src/
    │       ├── views/Index.vue # 主页面组件
    │       ├── api/index.ts    # API 接口封装
    │       └── index.ts        # ⭐ 模块入口（核心文件）
    │
    ├── backend/                # 后端代码（需复制到主项目）
    │   └── src/main/java/com/platform/module/your-module/
    │       ├── controller/YourModuleController.java
    │       ├── service/YourModuleService.java
    │       ├── service/impl/YourModuleServiceImpl.java
    │       ├── mapper/YourModuleMapper.java
    │       └── entity/YourModule.java
    │
    └── sql/init.sql            # 数据库建表脚本
```

### 文件职责说明

| 文件 | 必填 | 职责 | 被谁使用 |
|------|------|------|---------|
| `frontend/src/index.ts` | ✅ 核心 | 定义路由、菜单、权限，导出 ModuleConfig | router + menuStore 自动导入 |
| `frontend/src/views/Index.vue` | ✅ | 模块主页面 UI | 被 index.ts 的路由引用 |
| `frontend/src/api/index.ts` | ✅ | API 请求封装 | 被 Index.vue 调用 |
| `module.json` | 可选 | 模块元信息（名称、版本、依赖等） | 未来扩展用 |
| `backend/.../*.java` | 可选 | 后端 CRUD 接口 | 需复制到主项目 |
| `sql/init.sql` | 可选 | 数据表定义 | 手动在 MySQL 执行 |

---

## 3. 开发步骤（7 步）

### 快速开始：复制示例模块

最快捷的方式是从示例模块复制一份作为起点：

```bash
# 复制示例模块
cp -r modules/example-module modules/my-module

# 然后按以下步骤修改各个文件...
```

---

### Step 1：确定模块基本信息

在开始编码前，先确定：

| 信息 | 示例值 | 说明 |
|------|--------|------|
| 模块标识（key） | `my-module` | 小写中划线，全局唯一 |
| 显示名称 | `我的模块` | 在菜单和页面标题中使用 |
| 版本号 | `1.0.0` | 语义化版本 |
| 表名前缀 | `mm_` | 建议取模块名缩写，避免冲突 |
| API 路径前缀 | `/v1/my-module` | 与模块 key 对应 |

---

### Step 2：编写模块入口 — `frontend/src/index.ts`

这是**整个模块最重要的文件**，所有其他文件的存在意义都是为了支撑它导出的内容。

```typescript
import type { RouteRecordRaw } from 'vue-router'

export interface ModuleConfig {
  key: string
  name: string
  version: string
  routes: RouteRecordRaw[]
  menus: any[]
  permissions: string[]
}

// ══════════════════════════════════════
// 1️⃣ 路由定义（会被注入 UserLayout 和 MainLayout 的 children，但仅在用户界面首页展示模块入口）
// ══════════════════════════════════════
const routes: RouteRecordRaw[] = [
  {
    path: '/my-module',                           // ⚠️ 以 / 开头
    name: 'MyModule',                             // PascalCase（Admin界面会加Admin前缀）
    component: () => import('./views/Index.vue'),  // ⚠️ 必须用相对路径！
    meta: {
      title: '我的模块',                           // 浏览器标签页标题
      icon: 'Box',                                // Element Plus 图标名
      permission: 'my-module:view'               // 权限码（保留用于按钮级控制）
    }
  }
]

// ══════════════════════════════════════
// 2️⃣ 菜单定义（会在两个界面的侧边栏显示）
// ══════════════════════════════════════
const menus = [
  {
    name: '我的模块',
    icon: 'Box',
    path: '/my-module',          // 与路由 path 对应
    permission: 'my-module:view' // 目前不过滤，保留用于未来扩展
  }
]

// ══════════════════════════════════════
// 3️⃣ 权限声明（声明此模块需要的所有权限码）
// ══════════════════════════════════════
const permissions = [
  'my-module:view',
  'my-module:list',
  'my-module:create',
  'my-module:edit',
  'my-module:delete'
]

// ══════════════════════════════════════
// 4️⃣ 导出配置对象（必须 default 导出）
// ══════════════════════════════════════
const moduleConfig: ModuleConfig = {
  key: 'my-module',
  name: '我的模块',
  version: '1.0.0',
  routes,
  menus,
  permissions
}

export default moduleConfig
```

**⚠️ 关键注意事项速查表：**

| 要点 | 规则 | 错误示例 |
|------|------|---------|
| 组件导入 | **必须用相对路径** `./views/xxx.vue` | `import('@modules/...')` ❌ |
| 路由 path | 以 `/` 开头，如 `/my-module` | `'my-module'`（无斜杠）❌ |
| 路由 name | PascalCase，唯一 | `'my-module'`（小写）可以但不推荐 |
| export 方式 | `export default` 一个对象 | `export const config = ...` ❌ |
| 导出对象字段 | 必须包含 `key`, `name`, `version`, `routes`, `menus`, `permissions` | 缺少任一字段会导致运行时报错 |

---

### Step 3：编写页面组件 — `views/Index.vue`

标准 CRUD 页面模板：

<details>
<summary>📄 点击展开完整模板</summary>

```vue
<template>
  <div class="my-module">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>我的模块</span>
          <el-button type="primary" @click="handleCreate">新增</el-button>
        </div>
      </template>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        @current-change="loadData"
        @size-change="loadData"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="formData.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { myModuleApi } from '../api'

interface Item {
  id?: number
  name: string
  description?: string
  status?: number
}

const loading = ref(false)
const tableData = ref<Item[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('新增')
const formData = reactive<Item>({ name: '', description: '', status: 1 })
const editingId = ref<number | null>(null)

const loadData = async () => {
  loading.value = true
  try {
    const res = await myModuleApi.getList({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.name = ''
  formData.description = ''
  formData.status = 1
  editingId.value = null
}

const handleCreate = () => {
  resetForm()
  dialogTitle.value = '新增'
  dialogVisible.value = true
}

const handleEdit = (row: Item) => {
  editingId.value = row.id!
  formData.name = row.name
  formData.description = row.description || ''
  formData.status = row.status ?? 1
  dialogTitle.value = '编辑'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formData.name.trim()) {
    ElMessage.warning('请输入名称')
    return
  }
  try {
    if (editingId.value) {
      await myModuleApi.update(editingId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await myModuleApi.create({ ...formData })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row: Item) => {
  try {
    await ElMessageBox.confirm(`确定删除「${row.name}」？`, '提示', { type: 'warning' })
    await myModuleApi.delete(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* 用户取消 */ }
}

onMounted(() => {
  loadData()
})
</script>
```

</details>

---

### Step 4：编写 API 封装 — `api/index.ts`

```typescript
import request from '@/utils/request'

export interface MyItem {
  id?: number
  name: string
  description?: string
  status?: number
}

export const myModuleApi = {
  getList: (params?: { pageNum?: number; pageSize?: number }) => {
    return request.get<any, { data: any }>('/v1/my-module/list', { params })
  },

  getById: (id: number) => {
    return request.get<any, { data: MyItem }>(`/v1/my-module/${id}`)
  },

  create: (data: Omit<MyItem, 'id'>) => {
    return request.post<any, { data: any }>('/v1/my-module', data)
  },

  update: (id: number, data: Partial<MyItem>) => {
    return request.put<any, { data: any }>(`/v1/my-module/${id}`, data)
  },

  delete: (id: number) => {
    return request.delete<any, { data: any }>(`/v1/my-module/${id}`)
  }
}
```

**API 路径规则：** 前端请求 `/v1/{module-key}/...`，经 Vite Proxy 转发为后端 `/api/v1/{module-key}/...`。

---

### Step 5：编写后端代码（可选，纯前端模块可跳过）

如果模块只需要前端展示（不需要后端接口），可跳过本步骤。

#### 5.1 Entity 实体类

```java
package com.platform.module.my.module.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("mm_my_entity")  // 表名：建议用模块缩写前缀
public class MyEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

#### 5.2 Mapper 接口

```java
package com.platform.module.my.module.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.module.my.module.entity.MyEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyModuleMapper extends BaseMapper<MyEntity> {
}
```

#### 5.3 Service 接口 + 实现

**接口：**
```java
package com.platform.module.my.module.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.module.my.module.entity.MyEntity;

public interface MyModuleService extends IService<MyEntity> {
    Page<MyEntity> selectPage(Page<MyEntity> page);
}
```

**实现：**
```java
package com.platform.module.my.module.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.module.my.module.entity.MyEntity;
import com.platform.module.my.module.mapper.MyModuleMapper;
import com.platform.module.my.module.service.MyModuleService;
import org.springframework.stereotype.Service;

@Service
public class MyModuleServiceImpl
    extends ServiceImpl<MyModuleMapper, MyEntity>
    implements MyModuleService {

    @Override
    public Page<MyEntity> selectPage(Page<MyEntity> page) {
        return this.page(page);  // 使用父类的分页方法
    }

    // ⚠️ 不要重写 getById/save/updateById/removeById！
    // 这些方法已由 ServiceImpl 父类提供，重写会导致无限递归 StackOverflow
}
```

#### 5.4 Controller 控制器

```java
package com.platform.module.my.module.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.result.PageResult;
import com.platform.common.result.Result;
import com.platform.module.my.module.entity.MyEntity;
import com.platform.module.my.module.service.MyModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/my-module")   // ⚠️ 与前端 api/index.ts 的路径对应
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MyModuleController {

    @Autowired
    private MyModuleService myModuleService;

    @GetMapping("/list")
    public Result<PageResult<MyEntity>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<MyEntity> page = new Page<>(pageNum, pageSize);
        Page<MyEntity> result = myModuleService.selectPage(page);

        return Result.success(new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            (int) result.getCurrent(),
            (int) result.getSize()
        ));
    }

    @GetMapping("/{id}")
    public Result<MyEntity> getById(@PathVariable Long id) {
        MyEntity entity = myModuleService.getById(id);
        if (entity == null) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(entity);
    }

    @PostMapping
    public Result<?> create(@RequestBody MyEntity entity) {
        myModuleService.save(entity);
        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody MyEntity entity) {
        entity.setId(id);
        myModuleService.updateById(entity);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        myModuleService.removeById(id);
        return Result.success("删除成功", null);
    }
}
```

---

### Step 6：编写数据库脚本 — `sql/init.sql`

```sql
-- ===========================================
-- 模块：my-module
-- ===========================================

CREATE TABLE IF NOT EXISTS mm_my_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用,1启用)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='我的模块表';

-- 初始化测试数据
INSERT INTO mm_my_entity (name, description, status) VALUES
('测试数据1', '这是第一条测试数据', 1),
('测试数据2', '这是第二条测试数据', 1);
```

**表命名规范：** `{模块前缀}_{实体名}`，例如模块 `my-module` → 前缀 `mm_`。

---

### Step 7：让模块生效（部署操作）

完成编码后，按顺序执行以下操作：

```
① 【仅后端模块需要】复制 Java 文件
   源: modules/my-module/backend/src/main/java/com/platform/module/my-module/*
   目标: backend/src/main/java/com/platform/module/my-module/*

② 在 MySQL 中执行 sql/init.sql（建表+初始化数据）

③ 重启后端服务（新 Controller 生效）

④ 重启前端 dev server（glob 重新扫描模块）

⑤ 刷新浏览器验证
   - 首页「可用模块」数量增加
   - 侧边栏出现「业务模块」→「我的模块」
   - 点击进入，数据和功能正常 ✅
```

> **纯前端模块**（无后端代码）：只需执行 ②④⑤ 步骤。

---

## 4. 各层代码模板汇总

### 前端三层

| 层 | 文件 | 核心要点 |
|---|------|---------|
| **入口** | `src/index.ts` | 导出 routes/menus/permissions，组件用相对路径导入 |
| **页面** | `views/Index.vue` | Vue SFC + Element Plus + 调用 api |
| **接口** | `api/index.ts` | 封装 request，路径 `/v1/{module-key}/...` |

### 后端四层

| 层 | 文件 | 核心要点 |
|---|------|---------|
| **实体** | `entity/Xxx.java` | `@TableName` + `@TableId(AUTO)` + Lombok `@Data` |
| **Mapper** | `mapper/XxxMapper.java` | 继承 `BaseMapper<Xxx>` + `@Mapper` |
| **Service** | `service/XxxService.java` | 继承 `IService<Xxx>`，只声明自定义方法 |
| **实现** | `service/impl/XxxServiceImpl.java` | 继承 `ServiceImpl`，**不要**重写父类方法 |
| **控制器** | `controller/XxxController.java` | `@RequestMapping("/api/v1/{key}")` + 5 个标准接口 |

### 数据库

| 文件 | 核心要点 |
|------|---------|
| `sql/init.sql` | `CREATE TABLE IF NOT EXISTS` + `INSERT` 测试数据 |

---

## 5. 关键文件参考

### 示例模块（可直接复制修改）

| 文件 | 内容概要 |
|------|---------|
| [example-module/frontend/src/index.ts](../modules/example-module/frontend/src/index.ts) | 模块入口：1 个路由 + 1 个菜单 + 5 个权限 |
| [example-module/frontend/src/views/Index.vue](../modules/example-module/frontend/src/views/Index.vue) | 模块主页：表格 + 分页 + CRUD |
| [example-module/frontend/src/api/index.ts](../modules/example-module/frontend/src/api/index.ts) | API 封装：5 个标准方法 |
| [example-module/backend/.../ExampleController.java](../modules/example-module/backend/src/main/java/com/platform/module/example/controller/ExampleController.java) | REST 控制器完整示例 |
| [example-module/sql/init.sql](../modules/example-module/sql/init.sql) | 建表脚本 + 3 条测试数据 |

### 平台核心文件（理解机制必读）

| 文件 | 作用 |
|------|------|
| [frontend/vite.config.ts](../frontend/vite.config.ts) | `@modules` 别名定义 + API proxy 配置 |
| [frontend/src/router/index.ts](../frontend/src/router/index.ts) | 模块路由扫描 + 注入布局 children |
| [frontend/src/stores/menu.ts](../frontend/src/stores/menu.ts) | 动态菜单管理 + 去重逻辑 |
| [frontend/src/layouts/UserLayout.vue](../frontend/src/layouts/UserLayout.vue) | 用户界面布局 + 模块菜单渲染 |
| [frontend/src/layouts/MainLayout.vue](../frontend/src/layouts/MainLayout.vue) | 管理后台布局（6 项系统管理菜单，不含业务模块子菜单） |
| [frontend/src/views/home/index.vue](../frontend/src/views/home/index.vue) | 首页：展示可用模块卡片 |

### 已集成的后端模块代码位置

```
backend/src/main/java/com/platform/module/example/   ← 示例模块（已集成）
├── controller/ExampleController.java
├── service/ExampleService.java
├── service/impl/ExampleServiceImpl.java
├── mapper/ExampleMapper.java
└── entity/Example.java
```

---

## 6. 常见开发问题

### Q1：模块不显示（可用模块: 0）

**排查清单：**

1. `router/index.ts` 中 `import.meta.glob` 使用的是 `@modules` 别名吗？
2. `modules/{name}/frontend/src/index.ts` 文件存在吗？
3. 该文件的最后一行是否为 `export default moduleConfig`？
4. 新增模块后是否重启了 `npm run dev`？
5. 控制台是否有 `[Router] 扫描到模块: X 个` 日志？

### Q2：点击模块报 404「请求功能不存在」

**排查方向：**

| 检查项 | 操作 |
|--------|------|
| 后端代码是否在编译路径内？ | 确认 Java 文件在 `backend/src/main/java/.../` 下 |
| 后端是否已重启？ | 重启 `mvn spring-boot:run` |
| 数据表是否存在？ | MySQL 中检查表是否存在 |
| API 路径是否匹配？ | 前端 `/v1/xxx` ↔ 后端 `/api/v1/xxx` |

### Q3：ServiceImpl 无限递归 StackOverflow

**根因：** 重复包装了父类方法。

```java
// ❌ 致命错误
public Example getById(Long id) { return this.getById(id); }     // 调自己→死循环
public void save(Example e) { this.save(e); }                     // 同上
public void update(Example e) { this.updateById(e); }             // 同上
public void delete(Long id) { this.removeById(id); }              // 同上

// ✅ 正确做法
public class XxxServiceImpl extends ServiceImpl<..., Xxx> implements XxxService {
    @Override
    public Page<Xxx> selectPage(Page<Xxx> page) { return this.page(page); }
    // getById / save / updateById / removeById 由父类提供，无需重写！
}
```

### Q4：多个模块路由冲突

**症状：** Vue Router 警告 `Duplicate route name`

**处理方式：** 系统已自动处理——Admin 界面的路由名会加 `Admin` 前缀。开发者只需确保不同模块之间的路由名不重复即可。

### Q5：想给模块添加更多页面

在 `index.ts` 的 `routes` 数组中添加更多路由即可：

```typescript
const routes: RouteRecordRaw[] = [
  {
    path: '/my-module',
    name: 'MyModule',
    component: () => import('./views/Index.vue'),
    meta: { title: '我的模块', icon: 'Box' }
  },
  {
    path: '/my-module/detail/:id',
    name: 'MyModuleDetail',
    component: () => import('./views/Detail.vue'),
    meta: { title: '详情', icon: 'Document' }
  }
]
```

### Q6：纯前端模块（无需后端）

如果模块只做前端展示（如图表、静态内容），只需创建：
- `frontend/src/index.ts`（路由 + 菜单）
- `frontend/src/views/Index.vue`（页面组件）

跳过后端代码和 SQL 脚本即可。

---

## 附录：快速检查清单

开发新模块时，逐项核对：

**前端部分：**
- [ ] 模块目录已创建：`modules/{module-name}/`
- [ ] `frontend/src/index.ts` 存在且 `export default` 正确的配置对象
- [ ] `index.ts` 中组件导入使用**相对路径** `./views/xxx.vue`
- [ ] `index.ts` 中路由 `path` 以 `/` 开头
- [ ] `views/Index.vue` 页面组件已完成
- [ ] `api/index.ts` API 路径使用 `/v1/{module-key}/...`

**后端部分（如需要）：**
- [ ] Java 代码已**复制**到 `backend/src/main/java/com/platform/module/.../`
- [ ] Controller 的 `@RequestMapping` 为 `/api/v1/{module-key}`
- [ ] ServiceImpl **没有**重复包装父类方法（getById/save 等）
- [ ] Entity 的 `@TableName` 与 SQL 建表的表名一致

**数据库部分：**
- [ ] 已在 MySQL 中执行 `sql/init.sql` 建表

**部署操作：**
- [ ] **重启了后端服务**（如有后端代码）
- [ ] **重启了前端 dev server**
- [ ] **刷新浏览器验证**
