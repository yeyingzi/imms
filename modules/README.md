# 模块目录

本目录用于存放平台的所有扩展模块。

---

## 🚀 快速开始（30秒）

### 创建新模块

```bash
# 1. 复制示例模块模板
cp -r modules/example-module modules/your-module-name

# 2. 执行安装脚本初始化数据库
mysql -u root -p{password} {database} < modules/your-module-name/sql/install.sql

# 3. 复制后端代码到主项目（如需后端接口）
xcopy "modules\your-module-name\backend\src\..." "backend\src\..." /E /I /Y

# 4. 重启服务，刷新浏览器 → 完成！
```

> � **详细开发指南**：[MODULE_DEVELOPMENT_GUIDE.md](../docs/MODULE_DEVELOPMENT_GUIDE.md)（7步完整教程）

---

## � 标准模块结构

```
modules/{module-name}/
├── module.json                    # 模块元信息
├── README.md                      # 模块文档（必写）
│
├── sql/                           # 数据库脚本 ⭐
│   ├── install.sql                # 安装脚本（建表+注册）
│   └── uninstall.sql              # 卸载脚本
│
├── frontend/                      # 前端代码（Vite自动扫描）
│   └── src/
│       ├── index.ts               # 模块入口（路由+菜单+权限）⭐核心
│       ├── api/index.ts           # API封装
│       └── views/Index.vue        # 主页面组件
│
└── backend/                       # 后端代码（需复制到主项目）
    └── src/main/java/com/platform/module/{module}/
        ├── entity/                # 实体类
        ├── controller/            # 控制器
        ├── service/               # 服务层
        └── mapper/                # 数据访问层
```

---

## �📦 已安装模块

| 模块名称 | 版本 | 说明 | 文档 |
| :--- | :--- | :--- | :--- |
| **example-module** | v2.0.0 | 示例模块（开发模板）⭐ | [README](./example-module/README.md) |
| bookmark-module | v1.1.0 | 网址收藏合集 | [README](./bookmark-module/README.md) |

---

## 🔧 命名规范

- ✅ 使用小写中划线：`your-module-name`、`bookmark-module`
- ✅ 表名前缀用缩写：`bm_bookmark`、`exm_example`
- ❌ 避免特殊字符和中文
- ✅ 保持简短有意义（2-4个单词）

---

## 📚 文档索引

| 文档 | 路径 | 说明 |
|------|------|------|
| **模块开发指南** | [docs/MODULE_DEVELOPMENT_GUIDE.md](../docs/MODULE_DEVELOPMENT_GUIDE.md) | 完整开发教程（原理+步骤+模板+FAQ） |
| **示例模块** | [example-module/README.md](./example-module/README.md) | 生产级模板，可直接复制使用 |
| **网址收藏模块** | [bookmark-module/README.md](./bookmark-module/README.md) | 实战案例参考 |

---

## ❓ 常见问题速查

**Q: 如何让新模块生效？**
A: 放入 `modules/` 目录 → 执行 `install.sql` → 复制后端代码（如需要）→ 重启服务

**Q: 如何停用/卸载模块？**
A: 停用：管理后台切换开关；卸载：执行 `uninstall.sql`

**Q: 纯前端模块需要什么？**
A: 只需 `frontend/` 目录（index.ts + views + api），无需后端和SQL

**Q: 开发时遇到问题？**
A: 查看 [开发指南FAQ](../docs/MODULE_DEVELOPMENT_GUIDE.md#7-常见开发问题) 或对应模块的 README

---

## 🎯 开发流程总结

```
1️⃣ 复制 example-module
      ↓
2️⃣ 修改配置和代码
      ↓
3️⃣ 执行 install.sql
      ↓
4️⃣ 部署后端（如需要）
      ↓
5️⃣ 重启服务 → 完成！
```

**详细步骤请阅读**：👉 [模块开发指南](../docs/MODULE_DEVELOPMENT_GUIDE.md)
