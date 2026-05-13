-- ========== C4: 草稿箱 & 版本管理 ==========

CREATE TABLE IF NOT EXISTS `t_article_draft` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `article_id` bigint(20) DEFAULT NULL COMMENT '关联文章ID，新文章草稿为null',
  `title` varchar(256) DEFAULT NULL,
  `content` longtext,
  `title_image` varchar(512) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `tags` varchar(1024) DEFAULT NULL COMMENT '标签ID逗号分隔',
  `visibility` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_article` (`user_id`, `article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章草稿表';

CREATE TABLE IF NOT EXISTS `t_article_version` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章版本快照表';

-- ========== D1: 点赞/收藏/分享 ==========

ALTER TABLE `t_article`
  ADD COLUMN IF NOT EXISTS `like_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '点赞数' AFTER `read_num`,
  ADD COLUMN IF NOT EXISTS `favorite_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '收藏数' AFTER `like_num`;

CREATE TABLE IF NOT EXISTS `t_article_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL COMMENT '登录用户ID',
  `visitor_id` varchar(64) DEFAULT NULL COMMENT '匿名访客标识',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_article_visitor` (`article_id`, `visitor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞记录表';

CREATE TABLE IF NOT EXISTS `t_article_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章收藏记录表';