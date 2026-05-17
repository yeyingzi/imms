-- ===========================================
-- 模块：example-module
-- 版本：1.0.0
-- 作者：Platform Team
-- 创建时间：2026-05-17
-- 描述：示例模块数据库脚本
-- ===========================================

-- 创建示例表
CREATE TABLE IF NOT EXISTS exm_example (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用,1启用)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='示例表';

-- 插入测试数据
INSERT INTO exm_example (name, description, status) VALUES
('示例1', '这是一个示例记录', 1),
('示例2', '这是第二个示例记录', 1),
('示例3', '这是第三个示例记录', 0);
