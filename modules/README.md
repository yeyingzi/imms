# 模块目录

本目录用于存放平台的所有扩展模块。

## 📁 目录结构

```
modules/
├── example-module/          # 示例模块（参考模板）
└── your-module/            # 你的自定义模块
```

## 🚀 快速开始

### 创建新模块

1. 复制示例模块作为模板：
```bash
cp -r modules/example-module modules/your-module-name
```

2. 修改模块配置：
   - 编辑 `module.json`
   - 修改前端代码
   - 修改后端代码
   - 修改数据库脚本

3. 重启服务，新模块将自动加载

## 📚 开发文档

详细开发指南请参考：[模块开发指南](../docs/MODULE_DEVELOPMENT_GUIDE.md)

## 📦 已安装模块

| 模块名称 | 版本 | 状态 | 说明 |
| :--- | :--- | :--- | :--- |
| example-module | 1.0.0 | enabled | 示例模块 |

## 🔧 模块命名规范

- 使用中划线分隔：`your-module-name`
- 避免使用特殊字符
- 保持简短有意义

## ❓ 常见问题

**Q: 如何让新模块生效？**
A: 将模块放入 `modules/` 目录，重启前端服务即可自动加载。如需后端接口，还需将后端代码复制到 `backend/src/main/java/com/platform/module/` 并重启后端。

**Q: 如何停用某个模块？**
A: 进入管理后台 `/admin` → 模块管理 → 切换对应模块的启用/停用开关。停用后模块菜单立即消失，路由不可访问。无需重启服务。

**Q: 如何调试模块？**
A: 查看控制台日志 `[Router]` 和 `[Menu]` 的输出。

**Q: 如何删除模块？**
A: 停止前端服务，删除模块文件夹，重启前端服务。同时从数据库 `sys_module` 表删除对应记录（可选）。

## 📞 获取帮助

- 模块开发指南：[docs/MODULE_DEVELOPMENT_GUIDE.md](../docs/MODULE_DEVELOPMENT_GUIDE.md)
- 示例模块代码：[example-module/](./example-module/)
