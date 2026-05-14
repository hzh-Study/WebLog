-- ========== 推荐系统 ==========

CREATE TABLE IF NOT EXISTS `t_user_behavior_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `event_type` varchar(32) NOT NULL,
  `target_id` bigint(20) NOT NULL,
  `target_type` varchar(32) NOT NULL,
  `event_data` json DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `referrer` varchar(512) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_event` (`user_id`, `event_type`),
  KEY `idx_target` (`target_id`, `target_type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为日志表';

CREATE TABLE IF NOT EXISTS `t_user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `interest_tags` json DEFAULT NULL,
  `reading_preference` json DEFAULT NULL,
  `active_level` varchar(32) DEFAULT NULL,
  `behavior_count` int(11) NOT NULL DEFAULT '0',
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表';

CREATE TABLE IF NOT EXISTS `t_article_hot_score` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `hot_score` decimal(12,2) NOT NULL DEFAULT '0.00',
  `read_count_24h` int(11) NOT NULL DEFAULT '0',
  `interaction_count_24h` int(11) NOT NULL DEFAULT '0',
  `calculated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章热度分表';

CREATE TABLE IF NOT EXISTS `t_recommend_feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `article_id` bigint(20) NOT NULL,
  `feedback_type` varchar(32) NOT NULL,
  `scene` varchar(64) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_article` (`user_id`, `article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐反馈表';

CREATE TABLE IF NOT EXISTS `t_recommend_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `config_key` varchar(128) NOT NULL,
  `config_value` varchar(1024) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐配置表';
