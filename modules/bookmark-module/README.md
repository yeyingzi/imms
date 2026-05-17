# 网址收藏合集模块 (bookmark-module)

> 版本：1.0.0 | 状态：✅ 已完成基础实现

## 📌 模块简介

**网址收藏合集**是一个共享的网址收藏管理模块，专为多人协作场景设计（如程序员合租、团队共享资源）。

### 核心特性

- ✅ **用户绑定**：每个网址记录创建者，只有创建者或管理员可以编辑/删除
- ✅ **隐私保护**：支持私密模式，私密网址只有创建者和管理员可见
- ✅ **智能搜索**：支持关键词模糊搜索（标题/描述/URL）
- ✅ **卡片展示**：美观的卡片式布局，支持响应式设计
- ✅ **一键操作**：复制URL、新窗口打开、点击计数统计
- ✅ **权限控制**：基于角色的细粒度权限管理

---

## 🎯 适用场景

| 场景 | 说明 |
|------|------|
| 👨‍💻 程序员合租 | 共享技术文档、工具链接（3人合租场景） |
| 👥 团队协作 | 共享项目相关资料、API文档 |
| 👨‍👩‍👧 家庭使用 | 共享生活服务网站、常用工具 |

---

## 📁 模块结构

```
bookmark-module/
├── module.json              # 模块元信息配置
├── README.md                # 本文档 - 模块说明
│
├── frontend/               # 前端代码（Vite 自动扫描）
│   └── src/
│       ├── index.ts        # ⭐ 模块入口（路由/菜单/权限配置）
│       ├── views/
│       │   └── Index.vue   # 主页面组件（完整功能实现）
│       └── api/
│           └── index.ts    # API 接口封装
│
├── backend/                # 后端代码（需复制到主项目）
│   └── src/main/java/com/platform/module/bookmark/
│       ├── controller/
│       │   └── BookmarkController.java      # 控制层（6个接口）
│       ├── service/
│       │   ├── BookmarkService.java         # 服务接口
│       │   └── impl/
│       │       └── BookmarkServiceImpl.java # 服务实现（核心业务逻辑）
│       ├── mapper/
│       │   └── BookmarkMapper.java          # 数据访问层
│       └── entity/
│           └── Bookmark.java                # 实体类
│
└── sql/
    └── init.sql            # 数据库建表脚本（单表+示例数据）
```

---

## 💾 数据库设计

### 表结构概览

| 表名 | 说明 | 记录数 |
|------|------|--------|
| `bm_bookmark` | 网址收藏表 | 6条示例数据 |

#### 网址收藏表 (bm_bookmark)

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:----:|:------:|------|
| id | BIGINT | PK | AUTO | 主键，自增 |
| title | VARCHAR(200) | ✅ | - | 网页标题 |
| url | VARCHAR(500) | ✅ | - | URL地址（唯一约束） |
| description | TEXT | ❌ | NULL | 简短描述（为什么收藏） |
| icon | VARCHAR(500) | ❌ | NULL | 网站图标URL(favicon) |
| created_by | VARCHAR(50) | ✅ | - | 创建者用户名 |
| is_private | TINYINT | ❌ | 0 | 是否私密(0公开,1私密) |
| click_count | INT | ❌ | 0 | 点击次数统计 |
| created_at | DATETIME | ❌ | NOW() | 创建时间 |
| updated_at | DATETIME | ❌ | NOW() | 更新时间 |

### 设计亮点

- **单表结构**：无需分类和标签，简化到极致
- **隐私字段**：`is_private` 实现灵活的可见性控制
- **完整索引**：针对常用查询字段建立索引，保证性能
- **软删除移除**：采用硬删除，简化逻辑

---

## 🔧 功能清单

### ✅ 已实现功能

#### 1️⃣ 网址管理 (CRUD)

- [x] 新增网址（弹窗表单，支持URL验证）
- [x] 编辑网址信息（创建者或管理员）
- [x] 删除网址（创建者或管理员，硬删除）
- [x] 表单验证（URL格式必填，标题必填）

#### 2️⃣ 搜索与筛选

- [x] 关键词模糊搜索（同时搜索标题、描述、URL）
- [x] "只看我的"筛选（快速查看自己收藏的网址）
- [x] 排序功能（最新添加 / 最热门）
- [x] 排序方向（升序 / 降序）
- [x] 回车键触发搜索

#### 3️⃣ 隐私保护系统

- [x] 隐私开关（滑块按钮，实时切换）
- [x] 私密网址标识（左侧橙色边框 + 降低透明度）
- [x] 权限控制：
  - 公开网址：所有人可见
  - 私密网址：仅创建者和管理员可见
  - 切换权限：仅创建者和管理员可操作

#### 4️⃣ 卡片展示系统

- [x] 响应式网格布局（桌面3列 / 平板2列 / 手机1列）
- [x] Favicon图标自动获取（Google Favicon API）
- [x] 卡片悬停效果（上浮 + 阴影加深）
- [x] 信息展示层次清晰：
  - 第一行：Favicon + 标题 + 隐私开关
  - 第二行：URL地址（蓝色可识别）
  - 第三行：描述文字（最多2行，超出省略）
  - 第四行：元信息（创建者 · 点击次数 · 时间）
  - 第五行：操作按钮组

#### 5️⃣ 交互操作

- [x] 一键复制URL（Clipboard API，失败提示手动复制）
- [x] 新窗口打开网址（target="_blank"）
- [x] 点击计数统计（打开时自动+1）
- [x] 分页功能（支持12/24/36/48每页）
- [x] 加载状态指示（v-loading指令）
- [x] 空状态提示（el-empty组件）

#### 6️⃣ 权限管理系统

- [x] 基于角色的访问控制：
  - **普通用户**：只能编辑/删除自己的网址
  - **管理员（SUPER_ADMIN）**：可以操作所有网址
- [x] 按钮级权限显示：
  - "添加网址"按钮：需要 `bookmark-module:create` 权限
  - "编辑"按钮：需要 `bookmark-module:edit` 权限 + 是创建者或管理员
  - "删除"按钮：需要 `bookmark-module:delete` 权限 + 是创建者或管理员
- [x] 删除确认（二次确认弹窗，防止误删）

---

## 🔗 API接口列表

基础路径：`/api/v1/bookmarks`

### 核心接口（6个）

#### 1. 获取网址列表

**GET** `/api/v1/bookmarks`

**请求参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:----:|:------:|------|
| page | Integer | ❌ | 1 | 页码 |
| pageSize | Integer | ❌ | 12 | 每页数量 |
| keyword | String | ❌ | - | 搜索关键词（模糊匹配） |
| currentUser | String | ❌ | - | 当前登录用户名 |
| mineOnly | Boolean | ❌ | false | 是否只看我的 |
| isPrivate | Integer | ❌ | - | 隐私状态筛选（0/1） |
| sortBy | String | ❌ | createdAt | 排序字段（createdAt/clickCount） |
| sortOrder | String | ❌ | desc | 排序方向（asc/desc） |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "size": 12,
    "current": 1,
    "pages": 9
  }
}
```

---

#### 2. 新增网址

**POST** `/api/v1/bookmarks`

**请求体：**
```json
{
  "title": "Vue.js 官方文档",
  "url": "https://vuejs.org/",
  "description": "Vue3官方文档",
  "icon": "",
  "createdBy": "admin",
  "isPrivate": 0,
  "clickCount": 0
}
```

---

#### 3. 编辑网址

**PUT** `/api/v1/bookmarks/{id}`

**权限要求**：创建者或管理员

**请求体：**
```json
{
  "title": "更新后的标题",
  "url": "https://updated-url.com/",
  "description": "更新描述"
}
```

---

#### 4. 删除网址

**DELETE** `/api/v1/bookmarks/{id}`

**权限要求**：创建者或管理员

**注意**：硬删除，不可恢复

---

#### 5. 切换隐私状态

**PUT** `/api/v1/bookmarks/{id}/privacy`

**权限要求**：创建者或管理员

**功能**：在公开(0)和私密(1)之间切换

**响应示例：**
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 6. 记录点击次数

**PUT** `/api/v1/bookmarks/{id}/click`

**功能**：点击次数+1（前端打开网址时调用）

---

## 🎨 页面布局预览

### 整体页面结构

```
┌─────────────────────────────────────────────────────┐
│                                                     │
│  网址收藏合集                    [+ 添加网址]         │
│                                                     │
├─────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────┐   │
│  │ 🔍 搜索网址标题、描述或URL...          [搜索] │   │
│  ├─────────────────────────────────────────────┤   │
│  │ ☑ 只看我的  [最新添加 ▼]  [降序 ▼]          │   │
│  └─────────────────────────────────────────────┘   │
│                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
│  │ 🌐 Vue.js    │  │ 🌐 Spring   │  │ 🌐 MySQL    │ │
│  │ 官方文档     │  │ Boot指南    │  │ 官方文档    │ │
│  │             │  │             │  │             │ │
│  │ https://..  │  │ https://..  │  │ https://..  │ │
│  │             │  │             │  │             │ │
│  │ Vue3官方... │  │ SpringBoot  │  │ MySQL完整   │ │
│  │             │  │ 最佳实践... │  │ 文档和SQL  │ │
│  │             │  │             │  │ 语法参考... │ │
│  │ ───────────│  │ ───────────│  │ ───────────│ │
│  │ admin·56次  │  │ admin·34次  │  │ admin·28次  │ │
│  │ 2天前       │  │ 5天前       │  │ 1周前       │ │
│  │             │  │             │  │             │ │
│  │[复制][打开] │  │[复制][打开] │  │[复制][打开] │ │
│  │      [编辑] │  │      [编辑] │  │      [编辑] │ │
│  │      [删除] │  │      [删除] │  │      [删除] │ │
│  └─────────────┘  └─────────────┘  └─────────────┘ │
│                                                     │
│  ... 更多卡片 ...                                    │
│                                                     │
│              总共 100 条  [< 1 2 3 4 5 >]          │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### 单个卡片详情

```
┌──────────────────────────────────────────────┐
│                                              │
│  [🌐]  Vue.js 官方文档              [🔓/🔒]  │
│        https://vuejs.org                    │
│                                              │
│  Vue3官方文档，必看！渐进式JavaScript框架...  │
│  （最多显示2行，超出部分省略号显示）            │
│                                              │
│  ─────────────────────────────────────────── │
│  👤 admin  ·  👁️ 56次  ·  🕐 2天前          │
│                                              │
│  [📋 复制]  [🔗 打开新窗口]  [✏️ 编辑]  [🗑️]  │
│                                              │
└──────────────────────────────────────────────┘
     ↑            ↑              ↑        ↑
  一键复制     新窗口打开      仅所有者   二次确认
                          或管理员可见   防误删
```

### 添加/编辑网址弹窗

```
┌─────────────────────────────────────────────┐
│  添加网址                              [×]   │
├─────────────────────────────────────────────┤
│                                             │
│  URL地址 *                                   │
│  ┌─────────────────────────────────────┐   │
│  │ https://vuejs.org                   │   │
│  └─────────────────────────────────────┘   │
│                                             │
│  网页标题 *                                 │
│  ┌─────────────────────────────────────┐   │
│  │ Vue.js 官方文档                     │   │
│  └─────────────────────────────────────┘   │
│                                             │
│  描述                                       │
│  ┌─────────────────────────────────────┐   │
│  │ Vue3官方文档，必看！渐进式JavaScript │   │
│  │ 框架...                             │   │
│  │                                     │   │
│  └─────────────────────────────────────┘   │
│                                             │
│  网站图标                                   │
│  ┌─────────────────────────────────────┐   │
│  │ （可选，留空则自动获取favicon）       │   │
│  └─────────────────────────────────────┘   │
│                                             │
│                    [取消]  [保存]            │
└─────────────────────────────────────────────┘
```

---

## 🔐 权限矩阵

### 操作权限对照表

| 操作 | 创建者本人 | 管理员(SUPER_ADMIN) | 其他普通用户 |
|------|:----------:|:-------------------:|:------------:|
| **查看公开网址** | ✅ | ✅ | ✅ |
| **查看私密网址** | ✅ | ✅ | ❌ |
| **添加网址** | ✅ (有create权限) | ✅ (有create权限) | ✅ (有create权限) |
| **编辑自己的网址** | ✅ (有edit权限) | ✅ (有edit权限) | ❌ |
| **编辑别人的网址** | ❌ | ✅ (有edit权限) | ❌ |
| **删除自己的网址** | ✅ (有delete权限) | ✅ (有delete权限) | ❌ |
| **删除别人的网址** | ❌ | ✅ (有delete权限) | ❌ |
| **切换自己网址的隐私** | ✅ | ✅ | ❌ |
| **切换别人网址的隐私** | ❌ | ✅ | ❌ |

### 权限码定义

| 权限码 | 名称 | 类型 | 说明 |
|--------|------|:----:|------|
| `bookmark-module:view` | 查看网址收藏 | menu | 菜单访问权限 |
| `bookmark-module:create` | 添加网址 | button | 创建新网址 |
| `bookmark-module:edit` | 编辑网址 | button | 编辑已有网址 |
| `bookmark-module:delete` | 删除网址 | button | 删除网址 |

---

## 🚀 快速开始

### Step 1: 初始化数据库

```bash
# 在 MySQL 中执行建表脚本
mysql -u root -p platform < modules/bookmark-module/sql/init.sql
```

这将创建：
- 1张数据表：`bm_bookmark`
- 6条示例网址数据（包含不同用户和隐私状态的测试数据）

**示例数据预览：**

| ID | 标题 | 创建者 | 隐私 | 点击数 |
|:--:|------|:------:|:----:|:-----:|
| 1 | Vue.js 官方文档 | admin | 公开 | 56 |
| 2 | Spring Boot 官方指南 | admin | 公开 | 34 |
| 3 | MySQL 官方文档 | admin | 公开 | 28 |
| 4 | MDN Web 文档 | zhangsan | 公开 | 45 |
| 5 | GitHub | zhangsan | **私密** | 89 |
| 6 | Stack Overflow | lisi | 公开 | 67 |

### Step 2: 复制后端代码（必须！）

将后端代码从模块目录复制到主项目：

**Windows PowerShell:**
```powershell
xcopy "modules\bookmark-module\backend\src\main\java\com\platform\module\bookmark" "backend\src\main\java\com\platform\module\bookmark" /E /I /Y
```

**Linux/Mac:**
```bash
cp -r modules/bookmark-module/backend/src/main/java/com/platform/module/bookmark backend/src/main/java/com/platform/module/bookmark
```

**复制的文件清单：**
```
✅ entity/Bookmark.java              # 实体类
✅ mapper/BookmarkMapper.java         # 数据访问层
✅ service/BookmarkService.java       # 服务接口
✅ service/impl/BookmarkServiceImpl.java  # 服务实现
✅ controller/BookmarkController.java # 控制器（6个API接口）
```

### Step 3: 重启服务

```bash
# 重启后端（必须！因为后端代码需要重新编译）
cd backend
mvn spring-boot:run

# 重启前端（前端会自动扫描模块目录）
cd frontend
npm run dev
```

### Step 4: 访问模块

打开浏览器访问：

- **用户界面**：`http://localhost:5173/bookmark-module`
- **管理后台**：`http://localhost:5173/admin/bookmark-module`

**默认测试账号：**
- 用户名：`admin`
- 密码：`Admin@123456`

---

## 💡 使用指南

### 如何添加网址？

1. 点击右上角 **"+ 添加网址"** 按钮
2. 在弹窗中填写：
   - **URL地址**（必填）：输入完整的网址，如 `https://vuejs.org/`
   - **网页标题**（必填）：如 "Vue.js 官方文档"
   - **描述**（可选）：简短说明为什么收藏这个网址
   - **网站图标**（可选）：留空会自动使用 Google Favicon API 获取
3. 点击 **"保存"** 按钮

### 如何搜索网址？

- 在顶部搜索框输入关键词（支持模糊匹配标题、描述、URL）
- 按 **Enter 键** 或点击 **"搜索"** 按钮
- 支持的搜索示例：
  - 输入 `vue` → 找到所有包含 "vue" 的网址
  - 输入 `github.com` → 找到 URL 中包含 github 的网址

### 如何使用隐私功能？

**设置私密：**
1. 找到自己创建的网址卡片
2. 点击右侧的 **开关滑块** 切换到关闭状态（🔒）
3. 该网址对其他用户变为不可见（半透明 + 左侧橙色边框）

**取消私密：**
1. 再次点击开关滑块切换到开启状态（🔓）
2. 该网址恢复为所有人可见

**注意：**
- 只能修改**自己创建的**网址的隐私状态
- 管理员可以修改任何网址的隐私状态
- 私密网址对自己和管理员始终可见

### 如何编辑或删除网址？

**编辑：**
1. 找到目标网址卡片
2. 点击 **"✏️ 编辑"** 按钮（只有创建者或管理员能看到此按钮）
3. 在弹窗中修改信息
4. 点击保存

**删除：**
1. 找到目标网址卡片
2. 点击 **"🗑️ 删除"** 按钮
3. 在确认弹窗中点击 **"确定"**

⚠️ **注意**：删除是**永久性**的，无法恢复！

### 如何查看自己的网址？

勾选搜索栏下方的 **"☑ 只看我的"** 复选框，即可只显示当前登录用户创建的所有网址。

### 如何排序？

- **按时间排序**：选择 "最新添加" 或 "最旧添加"
- **按热度排序**：选择 "最热门"（按点击次数降序）
- **排序方向**：选择 "降序"（从高到低）或 "升序"（从低到高）

---

## 🎯 技术特性

### 前端技术栈

- **框架**：Vue 3 + Composition API (`<script setup>`)
- **UI组件库**：Element Plus 2.x
- **状态管理**：Pinia（集成 userStore 获取当前用户信息）
- **路由**：Vue Router 4（模块自动注册）
- **构建工具**：Vite 6.x（支持热更新）
- **语言**：TypeScript

### 后端技术栈

- **框架**：Spring Boot 3.x
- **ORM框架**：MyBatis Plus 3.5+
- **数据库**：MySQL 8.0+
- **安全框架**：Spring Security + JWT
- **构建工具**：Maven 3.9+

### 代码规范

- ✅ 遵循 RESTful API 设计规范
- ✅ 统一响应格式：`{code, message, data}`
- ✅ 使用 LambdaQueryWrapper 进行类型安全的条件查询
- ✅ 分页使用 MyBatis Plus 的 Page 对象
- ✅ 前端使用 TypeScript 定义接口类型
- ✅ 表单验证使用 Element Plus 的 Form Rules
- ✅ 错误处理使用 ElMessage 统一提示

---

## 📊 性能优化

### 数据库层面

- **索引优化**：为 `created_by`, `is_private`, `click_count`, `created_at` 字段建立索引
- **分页查询**：避免一次性加载大量数据
- **模糊搜索限制**：只在标题、描述、URL三个字段搜索，避免全表扫描

### 前端层面

- **虚拟滚动**：（可选扩展）当数据量超过1000条时可考虑
- **懒加载图片**：Favicon 图标使用懒加载
- **防抖搜索**：（可选优化）搜索输入框可添加防抖，避免频繁请求
- **缓存策略**：（可选扩展）可考虑对搜索结果进行短期缓存

---

## 🔮 未来规划

### v1.1.0（计划中）

- [ ] **批量操作**：批量删除、批量设为私密
- [ ] **高级筛选面板**：时间范围筛选、精确URL匹配
- [ ] **快捷键支持**：Ctrl+F 聚焦搜索框、Ctrl+N 新增网址
- [ ] **导出功能**：导出为 JSON/HTML/Markdown 格式

### v1.2.0（远期）

- [ ] **网址有效性检测**：定期检查链接是否失效
- [ ] **导入浏览器书签**：支持 Chrome/Firefox 书签导入
- [ ] **网站截图预览**：生成网页缩略图
- [ ] **协作评论系统**：对网址进行讨论和评价

### v2.0.0（愿景）

- [ ] **分类标签系统回归**：如果用户反馈强烈需求
- [ ] **智能推荐**：基于浏览历史推荐相关网址
- [ ] **多租户支持**：支持多个独立空间
- [ ] **PWA离线访问**：支持离线浏览已缓存的网址

---

## ❓ 常见问题

**Q: 为什么看不到别人设置为私密的网址？**
A: 这是正常行为。私密网址的设计初衷是保护个人隐私，只有创建者和管理员可以看到。如果你是管理员，应该能看到所有网址；如果是普通用户，只能看到公开网址和自己创建的私密网址。

**Q: 我能编辑或删除别人创建的网址吗？**
A: 不能。除非你是管理员（SUPER_ADMIN角色），否则只能操作自己创建的网址。这是为了保护数据安全和归属权。

**Q: 删除的网址能恢复吗？**
A: 不能。本模块采用硬删除机制，删除后数据将永久丢失。请谨慎操作，删除前会有二次确认提示。

**Q: 搜索支持哪些内容？**
A: 支持同时搜索以下三个字段：
- 网页标题（如 "Vue.js"）
- 描述文字（如 "官方文档"）
- URL地址（如 "vuejs.org"）

**Q: Favicon 图标是从哪里获取的？**
A: 使用 Google 的 Favicon API（`https://www.google.com/s2/favicons?domain=xxx&sz=32`）。如果获取失败，会显示一个默认的灰色占位图标。你也可以在添加/编辑时手动指定图标URL。

**Q: 如何让新模块生效？**
A:
1. 确保 `modules/bookmark-module/` 目录存在且文件完整
2. 执行数据库初始化脚本 `sql/init.sql`
3. **复制后端代码到主项目的对应目录**（这是必须的步骤！）
4. 重启后端服务和前端服务
5. 访问 `/bookmark-module` 路径

**Q: 可以修改示例数据吗？**
A: 可以！示例数据只是用于演示和测试。你可以通过页面上的编辑/删除功能进行修改，或者直接在数据库中操作。

---

## 🤝 参与贡献

欢迎提交 Issue 和 Pull Request！

### 开发规范

1. 遵循项目的 [模块开发指南](../../docs/MODULE_DEVELOPMENT_GUIDE.md)
2. 保持代码风格与现有实现一致
3. 提交前确保所有功能正常工作
4. 更新本文档的相关章节

### Git 提交规范

```
feat: 新增xxx功能
fix: 修复xxx bug
docs: 更新文档
style: 代码格式调整
refactor: 重构xxx
test: 添加测试用例
chore: 构建/工具链相关
```

---

## 📝 版本历史

| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| **v1.0.0** | 2026-05-18 | Platform Team | 初始版本，完成全部基础功能 |

### v1.0.0 功能清单

✅ **已完成的核心功能：**
- 用户绑定的网址CRUD管理
- 隐私保护系统（私密/公开切换）
- 智能搜索（模糊匹配+筛选+排序）
- 精美的卡片式UI（响应式设计）
- 完整的权限控制系统
- 一键复制、跳转、点击统计
- 分页加载、空状态提示
- 表单验证、二次确认删除

**代码统计：**
- 前端文件：3个（index.ts, Index.vue, api/index.ts）
- 后端文件：5个（Entity, Mapper, Service接口+实现, Controller）
- 配置文件：2个（module.json, init.sql）
- 文档文件：1个（README.md）
- 总计：**11个文件**，约 **800+ 行代码**

---

## 📞 相关链接

- **项目主页**：[README.md](../../README.md)
- **模块开发指南**：[MODULE_DEVELOPMENT_GUIDE.md](../../docs/MODULE_DEVELOPMENT_GUIDE.md)
- **API接口规范**：[API_SPEC.md](../../docs/API_SPEC.md)
- **数据库设计**：[DATABASE_SCHEMA.md](../../docs/DATABASE_SCHEMA.md)
- **部署指南**：[DEPLOYMENT_GUIDE.md](../../docs/DEPLOYMENT_GUIDE.md)
- **示例模块参考**：[example-module](../example-module/)

---

## 📄 许可证

## 🚨 常见问题排查（FAQ）

### ❓ 问题1：后端编译报错 - 找不到 Result 类

**错误信息**：
```
找不到符号: 类 Result
位置: 程序包 com.platform.common
```

**原因**：导入路径错误

**解决方案**：
```java
// 错误写法
import com.platform.common.Result;

// 正确写法
import com.platform.common.result.Result;
```

修改文件：`backend/src/main/java/com/platform/module/bookmark/controller/BookmarkController.java`

---

### ❓ 问题2：前端编译报错 - Failed to resolve import

**错误信息**：
```
Failed to resolve import "./api/index" from "Index.vue"
```

**原因**：API 导入路径不符合项目规范

**解决方案**：
```typescript
// 错误写法
import { bookmarkApi, type Bookmark } from './api/index';

// 正确写法（与 example-module 保持一致）
import { bookmarkApi, type Bookmark } from '../api';
```

修改文件：`frontend/src/views/Index.vue`

---

### ❓ 问题3：模块管理页面看不到新模块

**现象**：后台管理 → 模块管理 页面只显示"示例模块"，没有"网址收藏合集"

**原因**：模块未在数据库中注册！

**重要说明**：
- ✅ 仅有代码文件是不够的
- ✅ 必须在 `sys_module` 表中注册模块信息
- ✅ 必须在 `sys_permission` 表中注册菜单和按钮权限
- ✅ 必须为角色分配权限

**解决方案**：执行注册脚本

#### 方法A：使用 UTF-8 安全版本（推荐）

```bash
mysql -u root -pyeyingzi platform < modules/bookmark-module/sql/register_module_utf8.sql
```

此脚本使用 `UNHEX()` 函数，完全避免终端编码问题。

#### 方法B：手动执行 SQL

```sql
-- 1. 注册模块
INSERT INTO sys_module (module_key, name, version, author, description, icon, status)
VALUES ('bookmark-module', '网址收藏合集', '1.0.0', 'Platform Team',
        '共享网址收藏管理模块', 'Collection', 1);

-- 2. 注册菜单权限
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order)
VALUES ('网址收藏', 'bookmark-module:view', 1, '/bookmark-module', 0, 70);

-- 3. 注册按钮权限
SET @menu_id = LAST_INSERT_ID();
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
('添加网址', 'bookmark-module:create', 2, NULL, @menu_id, 1),
('编辑网址', 'bookmark-module:edit', 2, NULL, @menu_id, 2),
('删除网址', 'bookmark-module:delete', 2, NULL, @menu_id, 3);

-- 4. 为超级管理员分配权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE code LIKE 'bookmark-module:%';
```

**验证是否成功**：
```sql
SELECT * FROM sys_module WHERE module_key = 'bookmark-module';
-- 应该能看到 status=1 的记录
```

---

### ❓ 问题4：数据库显示中文乱码

**现象**：MySQL 终端查询结果显示乱码（如"缃戝潃鏀惰棌鍚堥泦"）

**原因**：PowerShell 终端编码不支持 UTF-8 显示

**说明**：
- ✅ **数据存储是正确的**（UTF-8 编码）
- ✅ **前端/浏览器会正确显示中文**
- ✅ **GUI 工具（Navicat/DBeaver）会正确显示**
- ⚠️ **仅 PowerShell 命令行显示异常**

**验证数据是否正确**：
```sql
-- 查看十六进制编码确认存储正确
SELECT HEX(name) FROM sys_module WHERE module_key = 'bookmark-module';
-- 应输出：E7BD91E59D80E694B6E8978FE59088E99B86 (这是"网址收藏合集"的UTF-8编码)
```

**解决方案**：

如果需要修复已存在的乱码数据，使用 [register_module_utf8.sql](./sql/register_module_utf8.sql) 脚本重新注册。

---

### ❓ 问题5：左侧菜单不显示新模块

**排查步骤**：

1. **检查数据库注册**
   ```sql
   SELECT * FROM sys_module WHERE module_key = 'bookmark-module' AND status = 1;
   ```
   如果没有记录或 `status=0`，请先执行问题3的解决方案。

2. **检查权限分配**
   ```sql
   SELECT rp.role_id, p.code 
   FROM sys_role_permission rp 
   JOIN sys_permission p ON rp.permission_id = p.id 
   WHERE p.code LIKE 'bookmark-module:%';
   ```
   当前登录用户必须拥有 `bookmark-module:view` 权限。

3. **强制刷新浏览器**
   - 按 `Ctrl + Shift + R`（Windows）或 `Cmd + Shift + R`（Mac）
   - 或退出登录后重新登录

4. **重启开发服务器**
   ```bash
   # 后端
   cd backend && mvn spring-boot:run
   
   # 前端
   cd frontend && npm run dev
   ```

5. **直接访问路由测试**
   
   在浏览器地址栏输入完整URL测试：
   - 用户界面：`http://localhost:5173/bookmark-module`
   - 管理后台：`http://localhost:5173/admin/bookmark-module`
   
   如果能打开页面但菜单不显示，说明是菜单加载逻辑的问题。

---

## ⚡ 快速安装指南（推荐）

### 🎯 一键安装

```bash
mysql -u root -pyeyingzi platform < modules/bookmark-module/sql/install.sql
```

**脚本会自动完成**：
1. 清理旧数据（支持重复执行）
2. 创建 `bm_bookmark` 数据表
3. 注册模块和权限到系统表
4. 为超级管理员分配权限

**安装后只需**：
```bash
# 复制后端代码到主项目（一次性操作）
xcopy "modules\bookmark-module\backend\src\main\java\com\platform\module\bookmark" ^
      "backend\src\main\java\com\platform\module\bookmark" /E /I /Y

# 重启后端服务
cd backend && mvn spring-boot:run

# 刷新浏览器访问
# http://localhost:5173/admin/bookmark-module
```

---

### 📦 完整部署清单（首次安装 - 详细版）

#### Step 1: 初始化数据库（一键命令）

```bash
# 推荐方式：使用整合后的 install.sql
mysql -u root -pyeyingzi platform < modules/bookmark-module/sql/install.sql

# 或者分步执行（旧方法，不推荐）：
# mysql -u root -pyeyingzi platform < modules/bookmark-module/sql/init.sql
# mysql -u root -pyeyingzi platform < modules/bookmark-module/sql/register_module_utf8.sql
```

### Step 2: 复制后端代码到主项目

**Windows PowerShell**:
```powershell
xcopy "modules\bookmark-module\backend\src\main\java\com\platform\module\bookmark" ^
      "backend\src\main\java\com\platform\module\bookmark" /E /I /Y
```

**Linux/Mac**:
```bash
cp -r modules/bookmark-module/backend/src/main/java/com/platform/module/bookmark \
      backend/src/main/java/com/platform/module/bookmark
```

**复制的文件列表**：
```
✅ entity/Bookmark.java              # 实体类
✅ mapper/BookmarkMapper.java         # 数据访问层
✅ service/BookmarkService.java       # 服务接口
✅ service/impl/BookmarkServiceImpl.java  # 服务实现
✅ controller/BookmarkController.java # 控制器
```

### Step 3: 重启服务

```bash
# 重启后端（必须！代码变更后需要重新编译）
cd backend
mvn spring-boot:run

# 重启前端（Vite 会自动扫描模块目录，通常不需要重启）
cd frontend
npm run dev
```

### Step 4: 验证部署成功

| 验证项 | 方法 | 预期结果 |
|--------|------|----------|
| **数据库表** | `SHOW TABLES LIKE 'bm_%';` | 能看到 `bm_bookmark` 表 |
| **模块注册** | 查看"模块管理"页面 | 能看到"网址收藏合集"，状态为启用 |
| **API接口** | 访问 `GET /api/v1/bookmarks` | 返回6条示例数据 |
| **前端页面** | 访问 `/admin/bookmark-module` | 显示卡片式界面 |
| **左侧菜单** | 刷新管理后台 | 出现"网址收藏"菜单项 |

### Step 5: 开始使用

使用默认管理员账号登录：
- **用户名**：`admin`
- **密码**：`Admin@123456`
- **访问地址**：`http://localhost:5173/admin/bookmark-module`

---

## 🔧 维护操作指南

### 更新模块代码

1. 修改 `modules/bookmark-module/` 下的源码
2. **后端代码**：复制到主项目后重启后端
3. **前端代码**：Vite 自动热更新（无需重启）

### 卸载模块

```bash
mysql -u root -pyeyingzi platform < modules/bookmark-module/sql/uninstall.sql
```

脚本会清除：
- 角色权限关联
- 权限记录
- 模块注册信息
- 数据表

### 备份模块数据

```bash
# 备份模块数据表
mysqldump -u root -pyeyingzi platform bm_bookmark > bookmark_backup_$(date +%Y%m%d).sql

# 备份模块配置
mysqldump -u root -pyeyingzi platform sys_module sys_permission sys_role_permission \
  --where="module_key='bookmark-module' OR code LIKE 'bookmark-module:%'" > bookmark_config_backup.sql
```

---

## 📚 相关文件索引

| 文件路径 | 说明 | 重要程度 |
|----------|------|:--------:|
| [module.json](./module.json) | 模块元信息配置 | ⭐⭐⭐ |
| [README.md](./README.md) | 本文档（完整使用手册） | ⭐⭐⭐ |
| **SQL 脚本** |||
| **[sql/install.sql](./sql/install.sql)** | **⭐ 安装脚本（建表+注册）** | ⭐⭐⭐ |
| **[sql/uninstall.sql](./sql/uninstall.sql)** | **⭐ 卸载脚本** | ⭐⭐⭐ |
| [sql/init.sql](./sql/init.sql) | 建表脚本 + 示例数据（已被 install.sql 整合） | ⭐⭐ |
| [sql/register_module_utf8.sql](./sql/register_module_utf8.sql) | 模块注册脚本（已被 install.sql 整合） | ⭐⭐ |
| **前端代码** |||
| [frontend/src/index.ts](./frontend/src/index.ts) | 前端入口（路由/菜单/权限配置） | ⭐⭐⭐ |
| [frontend/src/views/Index.vue](./frontend/src/views/Index.vue) | 主页面组件（卡片式UI） | ⭐⭐⭐ |
| [frontend/src/api/index.ts](./frontend/src/api/index.ts) | API 接口封装（6个请求函数） | ⭐⭐ |
| **后端代码** |||
| [backend/.../entity/Bookmark.java](./backend/src/main/java/com/platform/module/bookmark/entity/Bookmark.java) | 实体类 | ⭐⭐ |
| [backend/.../controller/BookmarkController.java](./backend/src/main/java/com/platform/module/bookmark/controller/BookmarkController.java) | 控制器（6个API接口） | ⭐⭐ |
| [backend/.../service/impl/BookmarkServiceImpl.java](./backend/src/main/java/com/platform/module/bookmark/service/impl/BookmarkServiceImpl.java) | 服务实现（核心业务逻辑） | ⭐⭐ |

### 🎯 快速上手文件

**首次安装只需关注这 3 个文件**：

1. **[sql/install.sql](./sql/install.sql)** - 执行此脚本完成数据库初始化
2. 复制 `backend/` 目录下的 Java 文件到主项目
3. 重启后端服务 → 刷新浏览器 → 完成！

---

## 🙏 致谢

感谢 Element Plus、Vue 3、Spring Boot、MyBatis Plus 等优秀开源项目！

---

**最后更新时间**：2026-05-18
**文档版本**：v1.2.0
**维护者**：Platform Team
**更新内容**：
- ✨ 新增 `install.sql` 安装脚本（建表+注册）
- ✨ 新增 `uninstall.sql` 卸载脚本
- 📝 优化快速安装指南，简化部署流程
- 🎯 添加"快速上手文件"指引，降低使用门槛
