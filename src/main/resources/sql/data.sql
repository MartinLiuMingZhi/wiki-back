-- ============================================
-- Wiki 数据库初始数据
-- 包含：默认角色、分类、标签、测试用户数据
-- ============================================

USE wiki;

-- ============================================
-- 1. 插入默认角色
-- ============================================
INSERT INTO roles (name, description) VALUES 
('USER', '普通用户'),
('ADMIN', '管理员');

-- ============================================
-- 2. 插入默认分类
-- ============================================
INSERT INTO categories (name, type, user_id) VALUES 
('技术文档', 'document', NULL),
('学习笔记', 'document', NULL),
('工作文档', 'document', NULL),
('技术书籍', 'ebook', NULL),
('文学书籍', 'ebook', NULL),
('教育书籍', 'ebook', NULL);

-- ============================================
-- 3. 插入默认标签
-- ============================================
INSERT INTO tags (name, user_id) VALUES 
('Java', NULL),
('Spring', NULL),
('数据库', NULL),
('前端', NULL),
('后端', NULL),
('学习', NULL),
('工作', NULL),
('技术', NULL),
('编程', NULL),
('开发', NULL);

-- ============================================
-- 4. 插入测试用户
-- ============================================
-- 密码为123456，已使用BCrypt加密
INSERT INTO users (username, email, password_hash, status) VALUES 
('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 1),
('testuser', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 1);

-- ============================================
-- 5. 为用户分配角色
-- ============================================
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 2), -- admin用户分配管理员角色
(2, 1); -- testuser用户分配普通用户角色

-- ============================================
-- 初始数据插入完成
-- ============================================
