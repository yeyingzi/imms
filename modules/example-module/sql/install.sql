-- ===========================================
-- 示例模块 (example-module) - 安装脚本
-- 版本：2.0.1
--
-- 功能：
-- 1. 创建数据表
-- 2. 注册模块信息到 sys_module
--
-- 注意：模块内部权限完全自治，无需在主平台注册
-- 使用：mysql -u root -p{password} {database} < install.sql
-- ===========================================

SET NAMES utf8mb4;

-- 清理旧数据
DELETE FROM sys_module WHERE module_key = 'example-module';
DROP TABLE IF EXISTS exm_example;

-- 1. 创建数据表
CREATE TABLE exm_example (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '名称',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用,1启用)',
    created_by VARCHAR(50) COMMENT '创建者',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例表';

-- 2. 注册模块信息
INSERT INTO sys_module (module_key, name, version, author, description, icon, status) VALUES
('example-module', '示例模块', '2.0.1', 'Platform Team', '示例模块，用于展示模块开发规范', 'Box', 1);

SELECT '✅ example-module 安装完成' AS '';
