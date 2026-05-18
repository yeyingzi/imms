-- ===========================================
-- 网址收藏模块 (bookmark-module) - 安装脚本
-- 版本：1.1.0
--
-- 功能：
-- 1. 创建数据表
-- 2. 注册模块信息到 sys_module
--
-- 权限控制说明（公私混合模式）：
-- ├─ 可见性：
-- │  ├─ is_private = 0（公开）：所有人可见
-- │  └─ is_private = 1（私密）：仅创建者可见
-- └─ 可操作性：
--    └─ 所有人可操作自己创建的书签（无管理员越权）
--
-- 使用：mysql -u root -p {database} < install.sql
-- ===========================================

SET NAMES utf8mb4;

-- 清理旧数据
DELETE FROM sys_module WHERE module_key = 'bookmark-module';
DROP TABLE IF EXISTS bm_bookmark;

-- 1. 创建表
CREATE TABLE bm_bookmark (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '网页标题',
    url VARCHAR(500) NOT NULL UNIQUE COMMENT 'URL地址',
    description TEXT COMMENT '描述',
    icon VARCHAR(500) COMMENT '网站图标URL',
    created_by VARCHAR(50) NOT NULL COMMENT '创建者用户名',
    is_private TINYINT DEFAULT 0 COMMENT '是否私密(0公开,1私密)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_created_by (created_by),
    INDEX idx_is_private (is_private),
    INDEX idx_title (title),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网址收藏表（公私混合模式：公开书签所有人可见，私密书签仅创建者可见）';

-- 2. 注册模块信息
INSERT INTO sys_module (module_key, name, version, author, description, icon, status) VALUES
('bookmark-module', '网址收藏合集', '1.1.0', 'Platform Team', '共享网址收藏管理模块，支持搜索、隐私保护等功能', 'Collection', 1);

SELECT '✅ bookmark-module 安装完成' AS '';
