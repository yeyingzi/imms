-- ===========================================
-- 网址收藏模块 (bookmark-module) - 卸载脚本
-- ===========================================
-- 功能：完全卸载模块，清除所有相关数据
-- 使用：mysql -u root -pyeyingzi platform < uninstall.sql
-- ===========================================

SELECT '================================' AS '';
SELECT '  开始卸载：网址收藏模块' AS '';
SELECT '================================' AS '';

-- Step 1: 删除角色权限关联
DELETE FROM sys_role_permission 
WHERE permission_id IN (
    SELECT id FROM sys_permission WHERE code LIKE 'bookmark-module:%'
);

-- Step 2: 删除权限记录
DELETE FROM sys_permission WHERE code LIKE 'bookmark-module:%';

-- Step 3: 删除模块注册信息
DELETE FROM sys_module WHERE module_key = 'bookmark-module';

-- Step 4: 删除数据表
DROP TABLE IF EXISTS bm_bookmark;
SELECT '✓ 数据表 bm_bookmark 已删除' AS '';

SELECT '================================' AS '';
SELECT '  ✓ 卸载完成' AS '';
SELECT '================================' AS '';
