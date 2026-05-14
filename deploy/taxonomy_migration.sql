-- ========== Taxonomy 系统：数据库迁移 ==========

-- 扩展 t_category 表
ALTER TABLE `t_category`
  ADD COLUMN IF NOT EXISTS `parent_id` bigint(20) DEFAULT NULL COMMENT '父分类ID' AFTER `user_id`,
  ADD COLUMN IF NOT EXISTS `code` varchar(64) DEFAULT NULL COMMENT '分类编码' AFTER `name`,
  ADD COLUMN IF NOT EXISTS `level` int(11) NOT NULL DEFAULT '1' COMMENT '层级' AFTER `code`,
  ADD COLUMN IF NOT EXISTS `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序' AFTER `level`,
  ADD COLUMN IF NOT EXISTS `is_system` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否系统预定义' AFTER `sort`,
  ADD COLUMN IF NOT EXISTS `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 1启用 0停用' AFTER `is_system`;

-- 扩展 t_tag 表
ALTER TABLE `t_tag`
  ADD COLUMN IF NOT EXISTS `category_id` bigint(20) DEFAULT NULL COMMENT '所属分类ID' AFTER `user_id`,
  ADD COLUMN IF NOT EXISTS `code` varchar(64) DEFAULT NULL COMMENT '标签编码' AFTER `name`,
  ADD COLUMN IF NOT EXISTS `alias_json` varchar(1024) DEFAULT NULL COMMENT '标签别名JSON' AFTER `code`,
  ADD COLUMN IF NOT EXISTS `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序' AFTER `alias_json`,
  ADD COLUMN IF NOT EXISTS `is_system` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否系统预定义' AFTER `sort`,
  ADD COLUMN IF NOT EXISTS `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 1启用 0停用' AFTER `is_system`;

-- 新增索引：t_category
ALTER TABLE `t_category` ADD INDEX IF NOT EXISTS `idx_parent_id` (`parent_id`);
ALTER TABLE `t_category` ADD INDEX IF NOT EXISTS `idx_code` (`code`);
ALTER TABLE `t_category` ADD INDEX IF NOT EXISTS `idx_status` (`status`);

-- 新增索引：t_tag
ALTER TABLE `t_tag` ADD INDEX IF NOT EXISTS `idx_category_id` (`category_id`);
ALTER TABLE `t_tag` ADD INDEX IF NOT EXISTS `idx_code` (`code`);
ALTER TABLE `t_tag` ADD INDEX IF NOT EXISTS `idx_status` (`status`);
ALTER TABLE `t_tag` ADD INDEX IF NOT EXISTS `idx_name` (`name`);

-- 旧数据兼容：给旧分类补默认层级和状态
UPDATE `t_category` SET `level` = 1, `status` = 1 WHERE `level` IS NULL OR `level` = 0;
UPDATE `t_category` SET `status` = 1 WHERE `status` IS NULL;

-- 旧数据兼容：给旧标签补默认状态
UPDATE `t_tag` SET `status` = 1 WHERE `status` IS NULL;

-- ========== 初始化 taxonomy 数据 ==========

-- 一级分类：技术
INSERT INTO `t_category` (`id`, `user_id`, `parent_id`, `name`, `code`, `level`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (100, 1, NULL, '技术', 'tech', 1, 0, 1, 1, NOW(), NOW());

-- 二级分类：技术/前端
INSERT INTO `t_category` (`id`, `user_id`, `parent_id`, `name`, `code`, `level`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (101, 1, 100, '前端', 'tech-frontend', 2, 1, 1, 1, NOW(), NOW());

-- 二级分类：技术/后端
INSERT INTO `t_category` (`id`, `user_id`, `parent_id`, `name`, `code`, `level`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (102, 1, 100, '后端', 'tech-backend', 2, 2, 1, 1, NOW(), NOW());

-- 二级分类：技术/DevOps
INSERT INTO `t_category` (`id`, `user_id`, `parent_id`, `name`, `code`, `level`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (103, 1, 100, 'DevOps', 'tech-devops', 2, 3, 1, 1, NOW(), NOW());

-- 一级分类：生活
INSERT INTO `t_category` (`id`, `user_id`, `parent_id`, `name`, `code`, `level`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (200, 1, NULL, '生活', 'life', 1, 1, 1, 1, NOW(), NOW());

-- 二级分类：生活/阅读
INSERT INTO `t_category` (`id`, `user_id`, `parent_id`, `name`, `code`, `level`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (201, 1, 200, '阅读', 'life-reading', 2, 1, 1, 1, NOW(), NOW());

-- 二级分类：生活/旅行
INSERT INTO `t_category` (`id`, `user_id`, `parent_id`, `name`, `code`, `level`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (202, 1, 200, '旅行', 'life-travel', 2, 2, 1, 1, NOW(), NOW());

-- ========== 预定义标签池 ==========

-- 前端标签
INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 101, 'Vue3', 'vue3', '["Vue.js 3","Vue 3"]', 1, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 101, 'React', 'react', '["React.js","ReactJS"]', 2, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 101, 'TypeScript', 'typescript', '["TS"]', 3, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 101, 'CSS', 'css', '["CSS3","Stylesheet"]', 4, 1, 1, NOW(), NOW());

-- 后端标签
INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 102, 'Spring Boot', 'spring-boot', '["SpringBoot"]', 1, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 102, 'Java', 'java', '["JDK"]', 2, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 102, 'MyBatis', 'mybatis', '["MyBatis-Plus"]', 3, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 102, 'MySQL', 'mysql', '["MariaDB"]', 4, 1, 1, NOW(), NOW());

-- DevOps标签
INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 103, 'Docker', 'docker', '["Container"]', 1, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 103, 'Kubernetes', 'kubernetes', '["K8s"]', 2, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 103, 'CI/CD', 'cicd', '["Jenkins","GitHub Actions"]', 3, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 103, 'Linux', 'linux', '["Ubuntu","CentOS"]', 4, 1, 1, NOW(), NOW());

-- 阅读标签
INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 201, '读书笔记', 'reading-notes', '["书评","读后感"]', 1, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 201, '技术书籍', 'tech-books', '["编程书籍"]', 2, 1, 1, NOW(), NOW());

-- 旅行标签
INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 202, '国内旅行', 'domestic-travel', '["国内游"]', 1, 1, 1, NOW(), NOW());

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES (1, 202, '海外旅行', 'overseas-travel', '["出国游"]', 2, 1, 1, NOW(), NOW());
