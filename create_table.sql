-- WeBlog 表结构（与 MyBatis-Plus 实体、create_data.sql 一致）
-- 使用顺序：先本文件建表，再导入 create_data.sql

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `t_ai_usage_record`;
DROP TABLE IF EXISTS `t_article_version`;
DROP TABLE IF EXISTS `t_article_draft`;
DROP TABLE IF EXISTS `t_article_tag_rel`;
DROP TABLE IF EXISTS `t_article_category_rel`;
DROP TABLE IF EXISTS `t_article_content`;
DROP TABLE IF EXISTS `t_article`;
DROP TABLE IF EXISTS `t_tag`;
DROP TABLE IF EXISTS `t_category`;
DROP TABLE IF EXISTS `t_user_role`;
DROP TABLE IF EXISTS `t_user`;
DROP TABLE IF EXISTS `t_blog_setting`;
DROP TABLE IF EXISTS `t_visitor_record`;
DROP TABLE IF EXISTS `t_statistics_user_pv`;
DROP TABLE IF EXISTS `t_statistics_article_pv`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `t_blog_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `blog_name` varchar(128) NOT NULL,
  `author` varchar(64) DEFAULT NULL,
  `avatar` varchar(512) DEFAULT NULL,
  `introduction` varchar(1024) DEFAULT NULL,
  `github_home` varchar(255) DEFAULT NULL,
  `csdn_home` varchar(255) DEFAULT NULL,
  `gitee_home` varchar(255) DEFAULT NULL,
  `zhihu_home` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(256) NOT NULL,
  `nickname` varchar(64) DEFAULT NULL,
  `avatar` varchar(512) DEFAULT NULL,
  `bio` varchar(512) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  `github_home` varchar(255) DEFAULT NULL,
  `gitee_home` varchar(255) DEFAULT NULL,
  `csdn_home` varchar(255) DEFAULT NULL,
  `zhihu_home` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `role` varchar(32) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `title` varchar(256) NOT NULL,
  `title_image` varchar(512) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `visibility` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `read_num` bigint(20) NOT NULL DEFAULT '0',
  `like_num` bigint(20) NOT NULL DEFAULT '0',
  `favorite_num` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_article_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `content` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_article_category_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `category_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_article_tag_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `tag_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_visitor_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `visitor` varchar(64) DEFAULT NULL,
  `ip_address` varchar(64) DEFAULT NULL,
  `ip_region` varchar(128) DEFAULT NULL,
  `visit_time` datetime DEFAULT NULL,
  `is_notify` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_statistics_user_pv` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `pv_date` date NOT NULL,
  `pv_count` bigint(20) NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_statistics_article_pv` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pv_date` date NOT NULL,
  `pv_count` bigint(20) NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_ai_usage_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `usage_date` date NOT NULL,
  `article_gen_count` int(11) NOT NULL DEFAULT '0',
  `token_used` bigint(20) NOT NULL DEFAULT '0',
  `last_interaction_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `usage_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_article_draft` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `article_id` bigint(20) DEFAULT NULL,
  `title` varchar(256) DEFAULT NULL,
  `content` longtext,
  `title_image` varchar(512) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `tags` varchar(1024) DEFAULT NULL,
  `visibility` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_article` (`user_id`, `article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_article_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `title` varchar(256) NOT NULL,
  `content` longtext NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `change_summary` varchar(256) DEFAULT NULL,
  `version_num` int(11) NOT NULL DEFAULT '1',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_article_ver` (`article_id`, `version_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
