-- ===========================================
-- 网址收藏合集模块 (bookmark-module) - 卸载脚本
-- 版本：1.0.0 | 日期：2026-05-18
--
-- 功能：完全卸载模块，清除所有相关数据
-- 使用：mysql -u root -pyeyingzi platform < uninstall.sql
-- ===========================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

SELECT '============================================' AS '';
SELECT '  开始卸载：网址收藏合集模块 (bookmark-module)' AS '';
SELECT '============================================' AS '';

-- Step 1: 删除角色权限关联
DELETE FROM sys_role_permission 
WHERE permission_id IN (
    SELECT id FROM sys_permission WHERE code LIKE 'bookmark-module:%'
);
SELECT CONCAT('✓ 角色权限已清理 (', ROW_COUNT(), ' 条)') AS '';

-- Step 2: 删除权限记录
DELETE FROM sys_permission WHERE code LIKE 'bookmark-module:%';
SELECT CONCAT('✓ 权限记录已删除 (', ROW_COUNT(), ' 条)') AS '';

-- Step 3: 删除模块注册信息
DELETE FROM sys_module WHERE module_key = 'bookmark-module';
SELECT CONCAT('✓ 模块记录已删除 (', ROW_COUNT(), ' 条)') AS '';

-- Step 4: 删除数据表
DROP TABLE IF EXISTS bm_bookmark;
SELECT '✓ 数据表 bm_bookmark 已删除' AS '';

-- 验证
SELECT '' AS '';
SELECT '============================================' AS '';
SELECT '  ✓ 卸载完成' AS '';
SELECT '============================================' AS '';