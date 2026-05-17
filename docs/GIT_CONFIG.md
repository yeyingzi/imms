# Git 配置文档

> 本文档记录项目 Git 仓库的基本配置和常用命令，便于后续快速恢复和协作开发。

---

## 📋 基本信息

| 项目 | 信息 |
|:---|:---|
| 仓库名称 | imms |
| 仓库地址 | https://github.com/yeyingzi/imms.git |
| 所有者 | yeyingzi |
| 默认分支 | main |
| 当前版本 | v1.14.0 |

---

## 🔗 远程仓库配置

### 查看当前远程仓库

```bash
git remote -v
```

预期输出：
```
origin  https://github.com/yeyingzi/imms.git (fetch)
origin  https://github.com/yeyingzi/imms.git (push)
```

### 添加远程仓库（如果未配置）

```bash
git remote add origin https://github.com/yeyingzi/imms.git
```

### 重命名分支为 main

```bash
git branch -M main
```

---

## 📥 首次克隆仓库

```bash
# 克隆仓库
git clone https://github.com/yeyingzi/imms.git

# 进入项目目录
cd imms

# 查看状态
git status
```

---

## 🔄 日常工作流

### 1. 获取最新代码

```bash
git pull origin main
```

### 2. 创建功能分支

```bash
git checkout -b feature/your-feature-name
# 或
git checkout -b fix/bug-description
```

### 3. 提交代码

```bash
# 添加文件
git add .

# 或者添加指定文件
git add backend/src/main/java/.../xxx.java
git add docs/CHANGELOG.md

# 提交（使用简洁的提交信息）
git commit -m "feat: 添加新功能"

# 提交信息规范：
# feat: 新功能
# fix: 修复bug
# docs: 文档更新
# refactor: 重构
# style: 代码格式
# test: 测试相关
# chore: 构建/工具相关
```

### 4. 推送到远程

```bash
# 首次推送分支
git push -u origin feature/your-feature-name

# 后续推送
git push
```

### 5. 合并到主分支

```bash
# 切换到主分支
git checkout main

# 拉取最新代码
git pull origin main

# 合并功能分支
git merge feature/your-feature-name

# 推送到远程
git push origin main
```

---

## 🔙 版本回退

### 查看提交历史

```bash
git log --oneline -10
```

### 回退到上一个版本

```bash
git reset --hard HEAD^
```

### 回退到指定版本

```bash
git reset --hard [commit-hash]
# 例如：git reset --hard 74f14fa
```

### 强制推送到远程

```bash
git push --force origin main
```

---

## 🛡️ .gitignore 规则

以下文件不会被 Git 跟踪：

| 类型 | 文件/目录 |
|:---|:---|
| **敏感配置** | `backend/src/main/resources/application.yml` |
| **后端构建** | `backend/target/` |
| **前端依赖** | `frontend/node_modules/` |
| **前端构建** | `frontend/dist/` |
| **日志** | `*.log`, `logs/` |
| **临时文件** | `*.tmp`, `*.bak` |

### 敏感信息配置

项目使用配置文件分离敏感信息：

```bash
# 配置模板
backend/src/main/resources/application.yml.example
```

首次克隆后需要：
```bash
cp backend/src/main/resources/application.yml.example backend/src/main/resources/application.yml
# 然后修改 database password 等敏感信息
```

---

## 🏷️ 常用标签管理

### 创建版本标签

```bash
# 创建标签
git tag -a v1.14.0 -m "v1.14.0 发布"

# 推送标签
git push origin v1.14.0

# 或推送所有标签
git push origin --tags
```

### 查看标签

```bash
git tag -l
```

---

## 🔧 Git 配置

### 用户信息

```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 简化提交信息（避免中文乱码）

```bash
git config --global i18n.commit.encoding utf-8
git config --global i18n.logOutputEncoding utf-8
```

### 设置默认分支

```bash
git config --global init.defaultBranch main
```

### 查看所有配置

```bash
git config --list
```

---

## 📊 分支管理

### 查看所有分支

```bash
# 本地分支
git branch

# 所有分支（包括远程）
git branch -a
```

### 删除分支

```bash
# 删除本地分支
git branch -d feature/your-feature-name

# 删除远程分支
git push origin --delete feature/your-feature-name
```

---

## 🆘 故障排除

### 问题：合并冲突

```bash
# 查看冲突文件
git status

# 手动解决冲突后
git add .
git commit -m "fix: 解决合并冲突"
```

### 问题：忘记添加文件到上次提交

```bash
# 添加遗漏的文件
git add forgotten-file.java

# 修改提交信息（不创建新提交）
git commit --amend --no-edit
```

### 问题：需要暂存当前修改

```bash
# 暂存当前修改
git stash

# 暂存并添加备注
git stash save "暂存修改描述"

# 查看暂存列表
git stash list

# 恢复暂存内容
git stash pop

# 丢弃暂存
git stash drop
```

---

## 📝 提交信息规范

推荐格式：

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

| Type | 说明 |
|:---|:---|
| feat | 新功能 |
| fix | 修复 bug |
| docs | 文档更新 |
| style | 代码格式（不影响功能） |
| refactor | 重构 |
| perf | 性能优化 |
| test | 测试相关 |
| chore | 构建/工具相关 |

### 示例

```
feat(user): 添加用户手机号验证功能

- 添加手机号格式校验
- 修改 UserServiceImpl.createUser() 方法
- 更新数据库字段约束

Closes #123
```

---

## 🔗 相关文档

| 文档 | 说明 |
|:---|:---|
| [CHANGELOG.md](./CHANGELOG.md) | 版本更新记录 |
| [README.md](./README.md) | 项目说明文档 |
| [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) | 部署指南 |

---

## 📞 快速参考

```bash
# 完整推送流程
git add .
git commit -m "your message"
git push origin main

# 首次设置后克隆
git clone https://github.com/yeyingzi/imms.git
cd imms
npm install
mvn clean install
```
