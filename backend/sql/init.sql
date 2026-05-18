-- ============================================
-- 内网万用平台 - 数据库初始化脚本
-- 版本: 2.0
-- 说明: 精简版，只包含核心表结构
-- 用户名: admin
-- 密码: Admin@123456 (BCrypt加密)
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE platform;

-- ============================================
-- 1. 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用,1启用)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ============================================
-- 2. 角色表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ============================================
-- 3. 权限表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    type TINYINT NOT NULL COMMENT '类型(1菜单,2按钮)',
    path VARCHAR(255) COMMENT '菜单路径',
    parent_id BIGINT DEFAULT 0 COMMENT '父级权限ID',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限信息表';

-- ============================================
-- 4. 模块表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_module (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模块ID',
    module_key VARCHAR(100) NOT NULL UNIQUE COMMENT '模块标识',
    name VARCHAR(100) NOT NULL COMMENT '模块名称',
    version VARCHAR(20) NOT NULL COMMENT '模块版本',
    description VARCHAR(500) COMMENT '模块描述',
    author VARCHAR(100) COMMENT '作者',
    icon VARCHAR(100) COMMENT '图标名称',
    status TINYINT DEFAULT 1 COMMENT '状态(0停用,1启用)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块信息表';

-- ============================================
-- 5. 用户角色关联表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================================
-- 6. 角色权限关联表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================
-- 7. 操作日志表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    module VARCHAR(100) COMMENT '操作模块',
    action VARCHAR(100) COMMENT '操作动作',
    description VARCHAR(500) COMMENT '操作描述',
    ip_address VARCHAR(50) COMMENT '客户端IP',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================
-- 8. 登录日志表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    login_type TINYINT COMMENT '登录类型(1登录,2登出)',
    status TINYINT COMMENT '状态(0失败,1成功)',
    error_msg VARCHAR(255) COMMENT '错误信息',
    ip_address VARCHAR(50) COMMENT '客户端IP',
    user_agent VARCHAR(500) COMMENT '浏览器信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================================
-- 9. 系统配置表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(255) COMMENT '配置说明',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入角色
INSERT INTO sys_role (name, code, description) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有系统所有权限'),
('普通用户', 'NORMAL_USER', '普通用户角色');

-- 插入菜单权限
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
('用户管理', 'user-menu', 1, '/user', 0, 10),
('角色管理', 'role-menu', 1, '/role', 0, 20),
('模块管理', 'module-menu', 1, '/module', 0, 30),
('系统配置', 'config-menu', 1, '/config', 0, 40),
('日志管理', 'log-menu', 1, '/log', 0, 50);

-- 插入按钮权限 - 用户管理
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
('查看用户', 'user:view', 2, NULL, 1, 1),
('创建用户', 'user:create', 2, NULL, 1, 2),
('编辑用户', 'user:edit', 2, NULL, 1, 3),
('删除用户', 'user:delete', 2, NULL, 1, 4),
('分配角色', 'user:assign-roles', 2, NULL, 1, 5);

-- 插入按钮权限 - 角色管理
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
('查看角色', 'role:view', 2, NULL, 2, 1),
('创建角色', 'role:create', 2, NULL, 2, 2),
('编辑角色', 'role:edit', 2, NULL, 2, 3),
('删除角色', 'role:delete', 2, NULL, 2, 4),
('分配权限', 'role:assign-permissions', 2, NULL, 2, 5);

-- 插入按钮权限 - 模块管理
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
('查看模块', 'module:view', 2, NULL, 3, 1),
('安装模块', 'module:install', 2, NULL, 3, 2),
('卸载模块', 'module:uninstall', 2, NULL, 3, 3),
('启用模块', 'module:enable', 2, NULL, 3, 4),
('停用模块', 'module:disable', 2, NULL, 3, 5);

-- 插入按钮权限 - 系统配置
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
('查看配置', 'config:view', 2, NULL, 4, 1),
('编辑配置', 'config:edit', 2, NULL, 4, 2);

-- 插入按钮权限 - 日志管理
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
('查看日志', 'log:view', 2, NULL, 5, 1),
('导出日志', 'log:export', 2, NULL, 5, 2),
('删除日志', 'log:delete', 2, NULL, 5, 3);

-- 为超级管理员分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 插入管理员用户 (密码: Admin@123456)
INSERT INTO sys_user (username, password, real_name, status) VALUES
('admin', '$2a$10$oaJUvFJBBrNZBlto68huRupWDIh4cNWNRR5ieuRc5bhruERFee.qq', '管理员', 1);

-- 为管理员分配超级管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- ============================================
-- 完成
-- ============================================
SELECT '数据库初始化完成！' AS message;
SELECT '默认管理员账号：admin / Admin@123456' AS account;
