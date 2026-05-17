-- ===========================================
-- 网址收藏合集模块 (bookmark-module) 安装脚本
-- 版本：1.0.0 | 日期：2026-05-18
-- ===========================================

-- 使用：mysql -u root -pyeyingzi platform < install.sql

SET NAMES utf8mb4;

-- 清理旧数据
DELETE FROM sys_permission WHERE code LIKE 'bookmark-module:%';
DELETE FROM sys_role_permission WHERE role_id IN (SELECT id FROM sys_permission WHERE code LIKE 'bookmark-module:%');
DELETE FROM sys_module WHERE module_key = 'bookmark-module';
DROP TABLE IF EXISTS bm_bookmark;

-- 1. 创建表
CREATE TABLE bm_bookmark (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '网页标题',
    url VARCHAR(500) NOT NULL UNIQUE COMMENT 'URL地址',
    description TEXT COMMENT '描述',
    icon VARCHAR(500) COMMENT '图标URL',
    created_by VARCHAR(50) NOT NULL COMMENT '创建者',
    is_private TINYINT DEFAULT 0 COMMENT '是否私密(0公开,1私密)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_created_by (created_by),
    INDEX idx_is_private (is_private)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网址收藏表';

-- 2. 注册模块
INSERT INTO sys_module (module_key, name, version, author, description, icon, status) VALUES
('bookmark-module', '网址收藏合集', '1.0.0', 'Platform Team', '共享网址收藏管理模块，支持搜索、隐私保护等功能', 'Collection', 1);

-- 3. 注册权限
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
('网址收藏', 'bookmark-module:view', 1, '/bookmark-module', 0, 70);

SET @menu_id = LAST_INSERT_ID();

INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
('添加网址', 'bookmark-module:create', 2, NULL, @menu_id, 1),
('编辑网址', 'bookmark-module:edit', 2, NULL, @menu_id, 2),
('删除网址', 'bookmark-module:delete', 2, NULL, @menu_id, 3);

-- 4. 分配权限给超级管理员
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE code LIKE 'bookmark-module:%';

SELECT 'bookmark-module 安装完成' AS '结果';
