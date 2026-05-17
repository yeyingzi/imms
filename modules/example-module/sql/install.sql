-- ===========================================
-- 示例模块 (example-module) - 安装脚本
-- 版本：2.0.0 | 日期：2026-05-18
--
-- 使用：mysql -u root -p{password} {database} < install.sql
-- ===========================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 清理旧数据（支持重复执行）
DELETE FROM sys_role_permission WHERE permission_id IN (SELECT id FROM sys_permission WHERE code LIKE 'example-module:%');
DELETE FROM sys_permission WHERE code LIKE 'example-module:%';
DELETE FROM sys_module WHERE module_key = 'example-module';
DROP TABLE IF EXISTS exm_example;

-- 1. 创建数据表
CREATE TABLE exm_example (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '名称',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用,1启用)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例表';

-- 2. 注册模块到系统
INSERT INTO sys_module (module_key, name, version, author, description, icon, status) VALUES
('example-module', UNHEX('E7A4BAE4BE8B6A1E58F97'), '2.0.0', 'Platform Team', UNHEX('E6A8A5E4B88BE68AA8AE588B0E5ADA6EFBC8CE794A8E4BA8EE5B08FE5BC80E58F91E8A784E8BDA3E69CAFE8A784'), 'Box', 1);

-- 3. 注册菜单权限
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
(UNHEX('E7A4BAE4BE8B6A1E58F97'), 'example-module:view', 1, '/example-module', 0, 99);

SET @menu_id = LAST_INSERT_ID();

-- 4. 注册按钮权限
INSERT INTO sys_permission (name, code, type, path, parent_id, sort_order) VALUES
(UNHEX('E69FA5E5BBBAE7A4BAE4BE8B6'), 'example-module:create', 2, NULL, @menu_id, 1),
(UNHEX('E7BC96E8BE91E7A4BAE4BE8B6'), 'example-module:edit', 2, NULL, @menu_id, 2),
(UNHEX('E588A0E999A4E7A4BAE4BE8B6'), 'example-module:delete', 2, NULL, @menu_id, 3);

-- 5. 分配权限给超级管理员
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE code LIKE 'example-module:%';

SELECT '✅ example-module 安装完成' AS '';