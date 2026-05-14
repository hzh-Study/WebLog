-- ============================================================
-- Taxonomy 重建脚本：分类 + 标签 全量重建
-- 执行前请确认已备份数据库
-- 分类：100 个（20 一级 + 80 二级）
-- 标签：200 个（每个二级分类下 2~3 个）
-- ============================================================

-- ==================== 第一步：清理 ====================

DELETE FROM t_article_category_rel;
DELETE FROM t_article_tag_rel;
DELETE FROM t_tag;
DELETE FROM t_category;

ALTER TABLE t_category AUTO_INCREMENT = 1;
ALTER TABLE t_tag AUTO_INCREMENT = 1;
ALTER TABLE t_article_category_rel AUTO_INCREMENT = 1;
ALTER TABLE t_article_tag_rel AUTO_INCREMENT = 1;

-- ==================== 第二步：插入 100 个分类 ====================

-- ---------- 一级分类（20 个，ID 1~20） ----------

INSERT INTO `t_category` (`id`, `user_id`, `parent_id`, `name`, `code`, `level`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1,  1, NULL, '技术开发',     'tech',        1,  1, 1, 1, NOW(), NOW()),
(2,  1, NULL, '编程语言',     'lang',        1,  2, 1, 1, NOW(), NOW()),
(3,  1, NULL, '前端技术',     'frontend',    1,  3, 1, 1, NOW(), NOW()),
(4,  1, NULL, '后端技术',     'backend',     1,  4, 1, 1, NOW(), NOW()),
(5,  1, NULL, '数据库',       'database',    1,  5, 1, 1, NOW(), NOW()),
(6,  1, NULL, '云计算与运维', 'cloud',       1,  6, 1, 1, NOW(), NOW()),
(7,  1, NULL, '人工智能',     'ai',          1,  7, 1, 1, NOW(), NOW()),
(8,  1, NULL, '数据科学',     'data',        1,  8, 1, 1, NOW(), NOW()),
(9,  1, NULL, '网络安全',     'security',    1,  9, 1, 1, NOW(), NOW()),
(10, 1, NULL, '产品设计',     'design',      1, 10, 1, 1, NOW(), NOW()),
(11, 1, NULL, '项目管理',     'pm',          1, 11, 1, 1, NOW(), NOW()),
(12, 1, NULL, '职场发展',     'career',      1, 12, 1, 1, NOW(), NOW()),
(13, 1, NULL, '开源生态',     'opensource',  1, 13, 1, 1, NOW(), NOW()),
(14, 1, NULL, '效率工具',     'tools',       1, 14, 1, 1, NOW(), NOW()),
(15, 1, NULL, '阅读学习',     'reading',     1, 15, 1, 1, NOW(), NOW()),
(16, 1, NULL, '生活随笔',     'life',        1, 16, 1, 1, NOW(), NOW()),
(17, 1, NULL, '创业商业',     'business',    1, 17, 1, 1, NOW(), NOW()),
(18, 1, NULL, '区块链',       'blockchain',  1, 18, 1, 1, NOW(), NOW()),
(19, 1, NULL, '物联网',       'iot',         1, 19, 1, 1, NOW(), NOW()),
(20, 1, NULL, '数学与算法',   'math',        1, 20, 1, 1, NOW(), NOW());

-- ---------- 二级分类（80 个，ID 21~100） ----------

INSERT INTO `t_category` (`id`, `user_id`, `parent_id`, `name`, `code`, `level`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
-- 技术开发(1) 的子分类
(21, 1, 1,  '前端开发', 'tech-frontend', 2, 1, 1, 1, NOW(), NOW()),
(22, 1, 1,  '后端开发', 'tech-backend',  2, 2, 1, 1, NOW(), NOW()),
(23, 1, 1,  '移动开发', 'tech-mobile',   2, 3, 1, 1, NOW(), NOW()),
(24, 1, 1,  '桌面开发', 'tech-desktop',  2, 4, 1, 1, NOW(), NOW()),
(25, 1, 1,  '游戏开发', 'tech-game',     2, 5, 1, 1, NOW(), NOW()),

-- 编程语言(2) 的子分类
(26, 1, 2,  'Java',       'lang-java',      2, 1, 1, 1, NOW(), NOW()),
(27, 1, 2,  'Python',     'lang-python',    2, 2, 1, 1, NOW(), NOW()),
(28, 1, 2,  'JavaScript', 'lang-javascript',2, 3, 1, 1, NOW(), NOW()),
(29, 1, 2,  'Go',         'lang-go',        2, 4, 1, 1, NOW(), NOW()),
(30, 1, 2,  'C/C++',      'lang-cpp',       2, 5, 1, 1, NOW(), NOW()),

-- 前端技术(3) 的子分类
(31, 1, 3,  'Vue生态',    'frontend-vue',        2, 1, 1, 1, NOW(), NOW()),
(32, 1, 3,  'React生态',  'frontend-react',      2, 2, 1, 1, NOW(), NOW()),
(33, 1, 3,  'CSS与样式',  'frontend-css',        2, 3, 1, 1, NOW(), NOW()),
(34, 1, 3,  '构建工具',   'frontend-build',      2, 4, 1, 1, NOW(), NOW()),
(35, 1, 3,  '小程序',     'frontend-miniprogram',2, 5, 1, 1, NOW(), NOW()),

-- 后端技术(4) 的子分类
(36, 1, 4,  'Spring生态', 'backend-spring',       2, 1, 1, 1, NOW(), NOW()),
(37, 1, 4,  'Node.js',    'backend-nodejs',       2, 2, 1, 1, NOW(), NOW()),
(38, 1, 4,  '微服务',     'backend-microservice', 2, 3, 1, 1, NOW(), NOW()),
(39, 1, 4,  'API设计',    'backend-api',          2, 4, 1, 1, NOW(), NOW()),
(40, 1, 4,  '消息队列',   'backend-mq',           2, 5, 1, 1, NOW(), NOW()),

-- 数据库(5) 的子分类
(41, 1, 5,  'MySQL',       'database-mysql',  2, 1, 1, 1, NOW(), NOW()),
(42, 1, 5,  'PostgreSQL',  'database-pgsql',  2, 2, 1, 1, NOW(), NOW()),
(43, 1, 5,  'MongoDB',     'database-mongo',  2, 3, 1, 1, NOW(), NOW()),
(44, 1, 5,  'Redis',       'database-redis',  2, 4, 1, 1, NOW(), NOW()),
(45, 1, 5,  '数据库设计',  'database-design', 2, 5, 1, 1, NOW(), NOW()),

-- 云计算与运维(6) 的子分类
(46, 1, 6,  '容器化',     'cloud-container', 2, 1, 1, 1, NOW(), NOW()),
(47, 1, 6,  'CI/CD',      'cloud-cicd',      2, 2, 1, 1, NOW(), NOW()),
(48, 1, 6,  '云服务',     'cloud-service',   2, 3, 1, 1, NOW(), NOW()),
(49, 1, 6,  '监控与日志', 'cloud-monitor',   2, 4, 1, 1, NOW(), NOW()),
(50, 1, 6,  'Linux',      'cloud-linux',     2, 5, 1, 1, NOW(), NOW()),

-- 人工智能(7) 的子分类
(51, 1, 7,  '机器学习',   'ai-ml', 2, 1, 1, 1, NOW(), NOW()),
(52, 1, 7,  '深度学习',   'ai-dl', 2, 2, 1, 1, NOW(), NOW()),
(53, 1, 7,  '大语言模型', 'ai-llm',2, 3, 1, 1, NOW(), NOW()),
(54, 1, 7,  '计算机视觉', 'ai-cv', 2, 4, 1, 1, NOW(), NOW()),
(55, 1, 7,  'NLP',        'ai-nlp',2, 5, 1, 1, NOW(), NOW()),

-- 数据科学(8) 的子分类
(56, 1, 8,  '数据分析',     'data-analysis',     2, 1, 1, 1, NOW(), NOW()),
(57, 1, 8,  '数据可视化',   'data-visualization',2, 2, 1, 1, NOW(), NOW()),
(58, 1, 8,  '大数据',       'data-bigdata',      2, 3, 1, 1, NOW(), NOW()),
(59, 1, 8,  '数据工程',     'data-engineering',  2, 4, 1, 1, NOW(), NOW()),

-- 网络安全(9) 的子分类
(60, 1, 9,  'Web安全',   'security-web',    2, 1, 1, 1, NOW(), NOW()),
(61, 1, 9,  '渗透测试',  'security-pentest', 2, 2, 1, 1, NOW(), NOW()),
(62, 1, 9,  '密码学',    'security-crypto',  2, 3, 1, 1, NOW(), NOW()),
(63, 1, 9,  '安全运维',  'security-ops',     2, 4, 1, 1, NOW(), NOW()),

-- 产品设计(10) 的子分类
(64, 1, 10, 'UI设计',    'design-ui',         2, 1, 1, 1, NOW(), NOW()),
(65, 1, 10, 'UX设计',    'design-ux',         2, 2, 1, 1, NOW(), NOW()),
(66, 1, 10, '交互设计',  'design-interaction', 2, 3, 1, 1, NOW(), NOW()),
(67, 1, 10, '设计工具',  'design-tools',      2, 4, 1, 1, NOW(), NOW()),

-- 项目管理(11) 的子分类
(68, 1, 11, '敏捷开发',  'pm-agile',      2, 1, 1, 1, NOW(), NOW()),
(69, 1, 11, '需求分析',  'pm-requirement', 2, 2, 1, 1, NOW(), NOW()),
(70, 1, 11, '团队协作',  'pm-team',       2, 3, 1, 1, NOW(), NOW()),
(71, 1, 11, '质量管理',  'pm-quality',    2, 4, 1, 1, NOW(), NOW()),

-- 职场发展(12) 的子分类
(72, 1, 12, '面试求职',  'career-interview',  2, 1, 1, 1, NOW(), NOW()),
(73, 1, 12, '职业规划',  'career-planning',   2, 2, 1, 1, NOW(), NOW()),
(74, 1, 12, '技术管理',  'career-management', 2, 3, 1, 1, NOW(), NOW()),
(75, 1, 12, '远程办公',  'career-remote',     2, 4, 1, 1, NOW(), NOW()),

-- 开源生态(13) 的子分类
(76, 1, 13, '开源项目',  'opensource-project',   2, 1, 1, 1, NOW(), NOW()),
(77, 1, 13, '开源贡献',  'opensource-contribute', 2, 2, 1, 1, NOW(), NOW()),
(78, 1, 13, '开源协议',  'opensource-license',   2, 3, 1, 1, NOW(), NOW()),
(79, 1, 13, 'GitHub',    'opensource-github',    2, 4, 1, 1, NOW(), NOW()),

-- 效率工具(14) 的子分类
(80, 1, 14, '开发工具',  'tools-dev',       2, 1, 1, 1, NOW(), NOW()),
(81, 1, 14, 'AI工具',    'tools-ai',        2, 2, 1, 1, NOW(), NOW()),
(82, 1, 14, '写作工具',  'tools-writing',   2, 3, 1, 1, NOW(), NOW()),
(83, 1, 14, '自动化',    'tools-automation', 2, 4, 1, 1, NOW(), NOW()),

-- 阅读学习(15) 的子分类
(84, 1, 15, '技术书籍',  'reading-tech',  2, 1, 1, 1, NOW(), NOW()),
(85, 1, 15, '在线课程',  'reading-course', 2, 2, 1, 1, NOW(), NOW()),
(86, 1, 15, '学习方法',  'reading-method', 2, 3, 1, 1, NOW(), NOW()),
(87, 1, 15, '认证考试',  'reading-cert',   2, 4, 1, 1, NOW(), NOW()),

-- 生活随笔(16) 的子分类
(88, 1, 16, '旅行见闻',  'life-travel', 2, 1, 1, 1, NOW(), NOW()),
(89, 1, 16, '美食分享',  'life-food',   2, 2, 1, 1, NOW(), NOW()),
(90, 1, 16, '摄影记录',  'life-photo',  2, 3, 1, 1, NOW(), NOW()),
(91, 1, 16, '生活感悟',  'life-thought', 2, 4, 1, 1, NOW(), NOW()),

-- 创业商业(17) 的子分类
(92, 1, 17, '创业心得',  'business-startup',  2, 1, 1, 1, NOW(), NOW()),
(93, 1, 17, '产品运营',  'business-operation', 2, 2, 1, 1, NOW(), NOW()),
(94, 1, 17, '商业模式',  'business-model',    2, 3, 1, 1, NOW(), NOW()),
(95, 1, 17, '投融资',    'business-finance',  2, 4, 1, 1, NOW(), NOW()),

-- 区块链(18) 的子分类
(96, 1, 18, '智能合约',  'blockchain-contract', 2, 1, 1, 1, NOW(), NOW()),
(97, 1, 18, 'Web3',      'blockchain-web3',     2, 2, 1, 1, NOW(), NOW()),
(98, 1, 18, 'DeFi',      'blockchain-defi',     2, 3, 1, 1, NOW(), NOW()),

-- 物联网(19) 的子分类
(99, 1, 19, '嵌入式',    'iot-embedded', 2, 1, 1, 1, NOW(), NOW()),

-- 数学与算法(20) 的子分类
(100, 1, 20, '算法与数据结构', 'math-algorithm', 2, 1, 1, 1, NOW(), NOW());

-- ==================== 第三步：插入 200 个标签 ====================

-- ---------- 技术开发/前端开发(21) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 21, 'HTML5',     'tech-frontend-html5', '["H5","HTML"]',           1, 1, 1, NOW(), NOW()),
(1, 21, 'SPA',       'tech-frontend-spa',   '["单页应用","Single Page Application"]', 2, 1, 1, NOW(), NOW()),
(1, 21, 'PWA',       'tech-frontend-pwa',   '["渐进式Web应用","Progressive Web App"]', 3, 1, 1, NOW(), NOW());

-- ---------- 技术开发/后端开发(22) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 22, 'RESTful',   'tech-backend-restful', '["REST API","REST"]',     1, 1, 1, NOW(), NOW()),
(1, 22, 'GraphQL',   'tech-backend-graphql', '["GQL"]',                2, 1, 1, NOW(), NOW()),
(1, 22, 'RPC',       'tech-backend-rpc',     '["远程过程调用","Remote Procedure Call"]', 3, 1, 1, NOW(), NOW());

-- ---------- 技术开发/移动开发(23) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 23, 'iOS',       'tech-mobile-ios',     '["iPhone","iPad"]',       1, 1, 1, NOW(), NOW()),
(1, 23, 'Android',   'tech-mobile-android', '["安卓"]',                2, 1, 1, NOW(), NOW()),
(1, 23, 'Flutter',   'tech-mobile-flutter', '["Dart","跨平台"]',       3, 1, 1, NOW(), NOW());

-- ---------- 技术开发/桌面开发(24) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 24, 'Electron',  'tech-desktop-electron', '["Electron.js"]',        1, 1, 1, NOW(), NOW()),
(1, 24, 'Tauri',     'tech-desktop-tauri',    '["Rust桌面框架"]',       2, 1, 1, NOW(), NOW()),
(1, 24, 'Qt',        'tech-desktop-qt',       '["Qt框架"]',             3, 1, 1, NOW(), NOW());

-- ---------- 技术开发/游戏开发(25) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 25, 'Unity',     'tech-game-unity',   '["Unity3D","Unity引擎"]', 1, 1, 1, NOW(), NOW()),
(1, 25, 'Unreal',    'tech-game-unreal',  '["UE","虚幻引擎"]',       2, 1, 1, NOW(), NOW()),
(1, 25, 'Cocos',     'tech-game-cocos',   '["Cocos2d","CocosCreator"]', 3, 1, 1, NOW(), NOW());

-- ---------- 编程语言/Java(26) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 26, 'Spring Boot', 'lang-java-springboot', '["SpringBoot","Boot"]', 1, 1, 1, NOW(), NOW()),
(1, 26, 'JVM',         'lang-java-jvm',        '["Java虚拟机"]',        2, 1, 1, NOW(), NOW()),
(1, 26, 'Maven',       'lang-java-maven',      '["MVN"]',               3, 1, 1, NOW(), NOW());

-- ---------- 编程语言/Python(27) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 27, 'Django',   'lang-python-django',   '["Django框架"]',         1, 1, 1, NOW(), NOW()),
(1, 27, 'Flask',    'lang-python-flask',    '["Flask框架"]',          2, 1, 1, NOW(), NOW()),
(1, 27, 'FastAPI',  'lang-python-fastapi',  '["Fast API"]',           3, 1, 1, NOW(), NOW());

-- ---------- 编程语言/JavaScript(28) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 28, 'ES6+',       'lang-javascript-es6',       '["ES6","ES2015","ECMAScript"]', 1, 1, 1, NOW(), NOW()),
(1, 28, 'TypeScript',  'lang-javascript-typescript', '["TS"]',                      2, 1, 1, NOW(), NOW()),
(1, 28, 'Deno',       'lang-javascript-deno',       '["Deno运行时"]',               3, 1, 1, NOW(), NOW());

-- ---------- 编程语言/Go(29) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 29, 'Gin',        'lang-go-gin',     '["Gin框架"]',            1, 1, 1, NOW(), NOW()),
(1, 29, 'GORM',       'lang-go-gorm',    '["Go ORM"]',             2, 1, 1, NOW(), NOW()),
(1, 29, 'Go Modules', 'lang-go-modules',  '["Go模块","gomod"]',     3, 1, 1, NOW(), NOW());

-- ---------- 编程语言/C/C++(30) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 30, 'STL',   'lang-cpp-stl',   '["标准模板库","Standard Template Library"]', 1, 1, 1, NOW(), NOW()),
(1, 30, 'Boost', 'lang-cpp-boost', '["Boost库"]',                                2, 1, 1, NOW(), NOW()),
(1, 30, 'CMake', 'lang-cpp-cmake', '["CMake构建"]',                               3, 1, 1, NOW(), NOW());

-- ---------- 前端技术/Vue生态(31) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 31, 'Vue3',  'frontend-vue-vue3',  '["Vue.js 3","Vue 3"]',  1, 1, 1, NOW(), NOW()),
(1, 31, 'Pinia', 'frontend-vue-pinia', '["Vue状态管理"]',        2, 1, 1, NOW(), NOW()),
(1, 31, 'Nuxt',  'frontend-vue-nuxt',  '["Nuxt.js","Nuxt3"]',   3, 1, 1, NOW(), NOW());

-- ---------- 前端技术/React生态(32) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 32, 'Redux',   'frontend-react-redux',  '["React状态管理"]',     1, 1, 1, NOW(), NOW()),
(1, 32, 'Next.js', 'frontend-react-nextjs', '["NextJS","Nuxt"]',     2, 1, 1, NOW(), NOW()),
(1, 32, 'Hooks',   'frontend-react-hooks',  '["React Hooks","钩子"]', 3, 1, 1, NOW(), NOW());

-- ---------- 前端技术/CSS与样式(33) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 33, 'Tailwind', 'frontend-css-tailwind', '["Tailwind CSS"]', 1, 1, 1, NOW(), NOW()),
(1, 33, 'Sass',     'frontend-css-sass',     '["SCSS","Syntactically Awesome Style Sheets"]', 2, 1, 1, NOW(), NOW()),
(1, 33, 'CSS3',     'frontend-css-css3',     '["CSS","层叠样式表"]', 3, 1, 1, NOW(), NOW());

-- ---------- 前端技术/构建工具(34) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 34, 'Webpack', 'frontend-build-webpack', '["Webpack打包"]',  1, 1, 1, NOW(), NOW()),
(1, 34, 'Vite',    'frontend-build-vite',    '["Vite构建"]',     2, 1, 1, NOW(), NOW()),
(1, 34, 'Babel',   'frontend-build-babel',   '["Babel转译"]',    3, 1, 1, NOW(), NOW());

-- ---------- 前端技术/小程序(35) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 35, '微信小程序',   'frontend-miniprogram-wechat',  '["WeChat Mini Program"]', 1, 1, 1, NOW(), NOW()),
(1, 35, '支付宝小程序', 'frontend-miniprogram-alipay',  '["Alipay Mini Program"]', 2, 1, 1, NOW(), NOW()),
(1, 35, 'Taro',         'frontend-miniprogram-taro',    '["Taro框架","多端统一"]',  3, 1, 1, NOW(), NOW());

-- ---------- 后端技术/Spring生态(36) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 36, 'Spring Cloud',   'backend-spring-cloud',   '["微服务框架","SC"]',    1, 1, 1, NOW(), NOW()),
(1, 36, 'Spring Security','backend-spring-security', '["安全框架"]',           2, 1, 1, NOW(), NOW()),
(1, 36, 'Spring Batch',   'backend-spring-batch',    '["批处理框架"]',         3, 1, 1, NOW(), NOW());

-- ---------- 后端技术/Node.js(37) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 37, 'Express', 'backend-nodejs-express', '["Express.js"]', 1, 1, 1, NOW(), NOW()),
(1, 37, 'Koa',     'backend-nodejs-koa',     '["Koa.js"]',     2, 1, 1, NOW(), NOW()),
(1, 37, 'NestJS',  'backend-nodejs-nestjs',  '["Nest"]',       3, 1, 1, NOW(), NOW());

-- ---------- 后端技术/微服务(38) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 38, 'Dubbo',    'backend-microservice-dubbo', '["Apache Dubbo"]', 1, 1, 1, NOW(), NOW()),
(1, 38, 'gRPC',     'backend-microservice-grpc',  '["Google RPC"]',   2, 1, 1, NOW(), NOW()),
(1, 38, '服务网格', 'backend-microservice-mesh',  '["Service Mesh","Istio"]', 3, 1, 1, NOW(), NOW());

-- ---------- 后端技术/API设计(39) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 39, 'Swagger', 'backend-api-swagger', '["Swagger UI","OpenAPI文档"]', 1, 1, 1, NOW(), NOW()),
(1, 39, 'OpenAPI', 'backend-api-openapi', '["OpenAPI 3.0"]',              2, 1, 1, NOW(), NOW()),
(1, 39, 'GraphQL','backend-api-graphql',  '["GQL","图查询语言"]',          3, 1, 1, NOW(), NOW());

-- ---------- 后端技术/消息队列(40) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 40, 'Kafka',     'backend-mq-kafka',     '["Apache Kafka"]', 1, 1, 1, NOW(), NOW()),
(1, 40, 'RabbitMQ',  'backend-mq-rabbitmq',  '["RMQ"]',          2, 1, 1, NOW(), NOW()),
(1, 40, 'RocketMQ',  'backend-mq-rocketmq',  '["Apache RocketMQ"]', 3, 1, 1, NOW(), NOW());

-- ---------- 数据库/MySQL(41) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 41, '索引优化',  'database-mysql-index',       '["MySQL索引","Index优化"]', 1, 1, 1, NOW(), NOW()),
(1, 41, '分库分表',  'database-mysql-sharding',     '["Sharding","分片"]',       2, 1, 1, NOW(), NOW()),
(1, 41, '主从复制',  'database-mysql-replication',  '["MySQL复制","Replication"]', 3, 1, 1, NOW(), NOW());

-- ---------- 数据库/PostgreSQL(42) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 42, 'JSONB',      'database-pgsql-jsonb',     '["JSON存储","PG JSON"]', 1, 1, 1, NOW(), NOW()),
(1, 42, '扩展插件',   'database-pgsql-extension', '["PostgreSQL扩展","PG扩展"]', 2, 1, 1, NOW(), NOW()),
(1, 42, '性能调优',   'database-pgsql-tuning',    '["PG调优","PostgreSQL优化"]', 3, 1, 1, NOW(), NOW());

-- ---------- 数据库/MongoDB(43) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 43, '聚合管道',  'database-mongo-aggregation', '["Aggregation Pipeline"]', 1, 1, 1, NOW(), NOW()),
(1, 43, '副本集',    'database-mongo-replicaset',  '["Replica Set","MongoDB复制"]', 2, 1, 1, NOW(), NOW()),
(1, 43, '分片',      'database-mongo-sharding',    '["Sharding","MongoDB分片"]', 3, 1, 1, NOW(), NOW());

-- ---------- 数据库/Redis(44) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 44, '缓存策略',  'database-redis-cache',          '["Redis缓存","Cache Strategy"]', 1, 1, 1, NOW(), NOW()),
(1, 44, '分布式锁',  'database-redis-lock',           '["Redis锁","Distributed Lock"]',  2, 1, 1, NOW(), NOW()),
(1, 44, '数据结构',  'database-redis-datastructure',  '["Redis数据类型"]',               3, 1, 1, NOW(), NOW());

-- ---------- 数据库/数据库设计(45) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 45, 'ER建模',  'database-design-er',            '["实体关系模型","ER Model"]', 1, 1, 1, NOW(), NOW()),
(1, 45, '范式',    'database-design-normalization',  '["数据库范式","Normal Form"]', 2, 1, 1, NOW(), NOW()),
(1, 45, '分区表',  'database-design-partition',      '["表分区","Partition Table"]', 3, 1, 1, NOW(), NOW());

-- ---------- 云计算与运维/容器化(46) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 46, 'Docker',     'cloud-container-docker', '["容器","Container"]', 1, 1, 1, NOW(), NOW()),
(1, 46, 'Kubernetes', 'cloud-container-k8s',    '["K8s","容器编排"]',    2, 1, 1, NOW(), NOW()),
(1, 46, 'Helm',       'cloud-container-helm',   '["K8s包管理"]',        3, 1, 1, NOW(), NOW());

-- ---------- 云计算与运维/CI/CD(47) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 47, 'Jenkins',        'cloud-cicd-jenkins',        '["CI服务器"]',     1, 1, 1, NOW(), NOW()),
(1, 47, 'GitHub Actions', 'cloud-cicd-github-actions', '["GHA","Actions"]', 2, 1, 1, NOW(), NOW()),
(1, 47, 'GitLab CI',      'cloud-cicd-gitlab-ci',      '["GitLab Pipeline"]', 3, 1, 1, NOW(), NOW());

-- ---------- 云计算与运维/云服务(48) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 48, 'AWS',    'cloud-service-aws',     '["Amazon Web Services","亚马逊云"]', 1, 1, 1, NOW(), NOW()),
(1, 48, '阿里云', 'cloud-service-aliyun',   '["Alibaba Cloud","ECS"]',            2, 1, 1, NOW(), NOW()),
(1, 48, '腾讯云', 'cloud-service-tencent',  '["Tencent Cloud","CVM"]',            3, 1, 1, NOW(), NOW());

-- ---------- 云计算与运维/监控与日志(49) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 49, 'Prometheus', 'cloud-monitor-prometheus', '["Prom监控"]', 1, 1, 1, NOW(), NOW()),
(1, 49, 'ELK',        'cloud-monitor-elk',        '["Elasticsearch+Logstash+Kibana"]', 2, 1, 1, NOW(), NOW()),
(1, 49, 'Grafana',    'cloud-monitor-grafana',     '["可视化监控"]', 3, 1, 1, NOW(), NOW());

-- ---------- 云计算与运维/Linux(50) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 50, 'Shell',     'cloud-linux-shell',  '["Bash","Shell脚本"]', 1, 1, 1, NOW(), NOW()),
(1, 50, '系统管理',  'cloud-linux-admin',  '["Linux运维","系统运维"]', 2, 1, 1, NOW(), NOW()),
(1, 50, '性能调优',  'cloud-linux-tuning', '["Linux优化","系统调优"]', 3, 1, 1, NOW(), NOW());

-- ---------- 人工智能/机器学习(51) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 51, 'Scikit-learn', 'ai-ml-sklearn',     '["sklearn","机器学习库"]', 1, 1, 1, NOW(), NOW()),
(1, 51, '特征工程',     'ai-ml-feature',      '["Feature Engineering"]', 2, 1, 1, NOW(), NOW()),
(1, 51, '模型评估',     'ai-ml-evaluation',   '["Model Evaluation","模型评价"]', 3, 1, 1, NOW(), NOW());

-- ---------- 人工智能/深度学习(52) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 52, 'PyTorch',     'ai-dl-pytorch',     '["Torch"]',           1, 1, 1, NOW(), NOW()),
(1, 52, 'TensorFlow',  'ai-dl-tensorflow',  '["TF","谷歌深度学习"]', 2, 1, 1, NOW(), NOW()),
(1, 52, 'CNN',         'ai-dl-cnn',         '["卷积神经网络","Convolutional Neural Network"]', 3, 1, 1, NOW(), NOW());

-- ---------- 人工智能/大语言模型(53) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 53, 'GPT',          'ai-llm-gpt',    '["ChatGPT","Generative Pre-trained Transformer"]', 1, 1, 1, NOW(), NOW()),
(1, 53, 'BERT',         'ai-llm-bert',   '["Bidirectional Encoder Representations"]',       2, 1, 1, NOW(), NOW()),
(1, 53, 'Prompt工程',   'ai-llm-prompt', '["Prompt Engineering","提示词工程"]',               3, 1, 1, NOW(), NOW());

-- ---------- 人工智能/计算机视觉(54) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 54, '目标检测',  'ai-cv-detection',   '["Object Detection","物体检测"]', 1, 1, 1, NOW(), NOW()),
(1, 54, '图像分割',  'ai-cv-segmentation', '["Image Segmentation","语义分割"]', 2, 1, 1, NOW(), NOW()),
(1, 54, 'OCR',       'ai-cv-ocr',         '["文字识别","光学字符识别"]',       3, 1, 1, NOW(), NOW());

-- ---------- 人工智能/NLP(55) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 55, '文本分类',      'ai-nlp-classification', '["Text Classification"]',       1, 1, 1, NOW(), NOW()),
(1, 55, '命名实体识别',  'ai-nlp-ner',            '["NER","Named Entity Recognition"]', 2, 1, 1, NOW(), NOW()),
(1, 55, '情感分析',      'ai-nlp-sentiment',      '["Sentiment Analysis","观点挖掘"]', 3, 1, 1, NOW(), NOW());

-- ---------- 数据科学/数据分析(56) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 56, 'Excel',      'data-analysis-excel',      '["电子表格","Spreadsheet"]', 1, 1, 1, NOW(), NOW()),
(1, 56, 'SQL分析',    'data-analysis-sql',         '["SQL数据分析"]',            2, 1, 1, NOW(), NOW()),
(1, 56, '统计分析',   'data-analysis-statistics',  '["Statistics","统计方法"]',   3, 1, 1, NOW(), NOW());

-- ---------- 数据科学/数据可视化(57) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 57, 'ECharts', 'data-visualization-echarts', '["百度图表库"]',  1, 1, 1, NOW(), NOW()),
(1, 57, 'D3.js',   'data-visualization-d3',      '["D3","Data-Driven Documents"]', 2, 1, 1, NOW(), NOW()),
(1, 57, 'Tableau', 'data-visualization-tableau',  '["Tableau可视化"]', 3, 1, 1, NOW(), NOW());

-- ---------- 数据科学/大数据(58) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 58, 'Hadoop', 'data-bigdata-hadoop', '["Apache Hadoop"]', 1, 1, 1, NOW(), NOW()),
(1, 58, 'Spark',  'data-bigdata-spark',  '["Apache Spark"]',  2, 1, 1, NOW(), NOW()),
(1, 58, 'Flink',  'data-bigdata-flink',  '["Apache Flink"]',  3, 1, 1, NOW(), NOW());

-- ---------- 数据科学/数据工程(59) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 59, 'ETL',       'data-engineering-etl',          '["Extract Transform Load","数据抽取"]', 1, 1, 1, NOW(), NOW()),
(1, 59, '数据湖',    'data-engineering-datalake',      '["Data Lake","湖仓一体"]',              2, 1, 1, NOW(), NOW()),
(1, 59, '数据仓库',  'data-engineering-datawarehouse', '["Data Warehouse","数仓"]',             3, 1, 1, NOW(), NOW());

-- ---------- 网络安全/Web安全(60) — 3 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 60, 'XSS',      'security-web-xss',  '["跨站脚本","Cross-Site Scripting"]', 1, 1, 1, NOW(), NOW()),
(1, 60, 'CSRF',     'security-web-csrf', '["跨站请求伪造","Cross-Site Request Forgery"]', 2, 1, 1, NOW(), NOW()),
(1, 60, 'SQL注入',  'security-web-sqli', '["SQL Injection","SQLi"]',            3, 1, 1, NOW(), NOW());

-- ---------- 网络安全/渗透测试(61) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 61, 'Burp Suite', 'security-pentest-burp', '["Burp","渗透工具"]', 1, 1, 1, NOW(), NOW()),
(1, 61, '漏洞挖掘',   'security-pentest-vuln', '["Vulnerability","0day"]', 2, 1, 1, NOW(), NOW());

-- ---------- 网络安全/密码学(62) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 62, 'RSA', 'security-crypto-rsa', '["非对称加密","RSA算法"]', 1, 1, 1, NOW(), NOW()),
(1, 62, 'AES', 'security-crypto-aes', '["对称加密","AES算法"]',   2, 1, 1, NOW(), NOW());

-- ---------- 网络安全/安全运维(63) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 63, 'WAF',       'security-ops-waf',     '["Web应用防火墙","Web Application Firewall"]', 1, 1, 1, NOW(), NOW()),
(1, 63, '应急响应',  'security-ops-incident', '["Incident Response","安全事件响应"]',          2, 1, 1, NOW(), NOW());

-- ---------- 产品设计/UI设计(64) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 64, 'Figma',     'design-ui-figma',    '["Figma设计"]',  1, 1, 1, NOW(), NOW()),
(1, 64, '设计规范',  'design-ui-guideline', '["Design System","设计系统"]', 2, 1, 1, NOW(), NOW());

-- ---------- 产品设计/UX设计(65) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 65, '用户研究',    'design-ux-research',   '["User Research","用研"]', 1, 1, 1, NOW(), NOW()),
(1, 65, '可用性测试',  'design-ux-usability',  '["Usability Testing"]',    2, 1, 1, NOW(), NOW());

-- ---------- 产品设计/交互设计(66) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 66, '原型设计',  'design-interaction-prototype', '["Prototype","交互原型"]', 1, 1, 1, NOW(), NOW()),
(1, 66, '动效设计',  'design-interaction-motion',    '["Motion Design","动画设计"]', 2, 1, 1, NOW(), NOW());

-- ---------- 产品设计/设计工具(67) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 67, 'Photoshop', 'design-tools-photoshop', '["PS","Adobe PS"]', 1, 1, 1, NOW(), NOW()),
(1, 67, '蓝湖',      'design-tools-lanhu',     '["Lanhu","设计协作"]', 2, 1, 1, NOW(), NOW());

-- ---------- 项目管理/敏捷开发(68) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 68, 'Scrum',  'pm-agile-scrum',  '["敏捷框架","Sprint"]', 1, 1, 1, NOW(), NOW()),
(1, 68, 'Kanban', 'pm-agile-kanban', '["看板方法","看板"]',    2, 1, 1, NOW(), NOW());

-- ---------- 项目管理/需求分析(69) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 69, '用户故事', 'pm-requirement-userstory', '["User Story","需求故事"]', 1, 1, 1, NOW(), NOW()),
(1, 69, 'PRD',      'pm-requirement-prd',       '["产品需求文档","Product Requirements Document"]', 2, 1, 1, NOW(), NOW());

-- ---------- 项目管理/团队协作(70) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 70, 'Code Review', 'pm-team-codereview', '["代码审查","CR"]',  1, 1, 1, NOW(), NOW()),
(1, 70, '知识管理',    'pm-team-knowledge',   '["Knowledge Management","Wiki"]', 2, 1, 1, NOW(), NOW());

-- ---------- 项目管理/质量管理(71) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 71, '单元测试',  'pm-quality-unittest',      '["Unit Test","UT"]', 1, 1, 1, NOW(), NOW()),
(1, 71, '代码规范',  'pm-quality-codestandard',  '["Code Standard","编码规范"]', 2, 1, 1, NOW(), NOW());

-- ---------- 职场发展/面试求职(72) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 72, '算法题',    'career-interview-algorithm',      '["LeetCode","刷题"]', 1, 1, 1, NOW(), NOW()),
(1, 72, '系统设计',  'career-interview-systemdesign',   '["System Design","架构设计面试"]', 2, 1, 1, NOW(), NOW());

-- ---------- 职场发展/职业规划(73) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 73, '技术路线',    'career-planning-techpath',  '["技术栈规划","Tech Path"]', 1, 1, 1, NOW(), NOW()),
(1, 73, '架构师成长',  'career-planning-architect', '["Architect","架构师之路"]',  2, 1, 1, NOW(), NOW());

-- ---------- 职场发展/技术管理(74) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 74, '技术选型', 'career-management-techselection', '["Tech Selection","技术决策"]', 1, 1, 1, NOW(), NOW()),
(1, 74, 'OKR',      'career-management-okr',           '["Objectives and Key Results","目标与关键成果"]', 2, 1, 1, NOW(), NOW());

-- ---------- 职场发展/远程办公(75) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 75, '协作工具',  'career-remote-collaboration', '["远程协作","Remote Tools"]', 1, 1, 1, NOW(), NOW()),
(1, 75, '异步沟通',  'career-remote-async',         '["Async Communication","异步交流"]', 2, 1, 1, NOW(), NOW());

-- ---------- 开源生态/开源项目(76) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 76, 'Apache项目', 'opensource-project-apache', '["Apache Foundation","ASF"]', 1, 1, 1, NOW(), NOW()),
(1, 76, 'Linux内核',  'opensource-project-linux',  '["Linux Kernel","内核开发"]',  2, 1, 1, NOW(), NOW());

-- ---------- 开源生态/开源贡献(77) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 77, 'PR提交',    'opensource-contribute-pr',     '["Pull Request","合并请求"]', 1, 1, 1, NOW(), NOW()),
(1, 77, '代码审查',  'opensource-contribute-review',  '["Code Review","开源审查"]',  2, 1, 1, NOW(), NOW());

-- ---------- 开源生态/开源协议(78) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 78, 'MIT',         'opensource-license-mit',     '["MIT License","MIT协议"]',     1, 1, 1, NOW(), NOW()),
(1, 78, 'Apache 2.0',  'opensource-license-apache',  '["Apache License","Apache协议"]', 2, 1, 1, NOW(), NOW());

-- ---------- 开源生态/GitHub(79) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 79, 'Actions', 'opensource-github-actions', '["GitHub Actions","GHA"]', 1, 1, 1, NOW(), NOW()),
(1, 79, 'Copilot', 'opensource-github-copilot', '["GitHub Copilot","AI编程助手"]', 2, 1, 1, NOW(), NOW());

-- ---------- 效率工具/开发工具(80) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 80, 'VS Code',        'tools-dev-vscode', '["Visual Studio Code","VSC"]', 1, 1, 1, NOW(), NOW()),
(1, 80, 'IntelliJ IDEA',  'tools-dev-idea',   '["IDEA","JetBrains IDEA"]',    2, 1, 1, NOW(), NOW());

-- ---------- 效率工具/AI工具(81) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 81, 'ChatGPT', 'tools-ai-chatgpt', '["OpenAI ChatGPT","GPT"]', 1, 1, 1, NOW(), NOW()),
(1, 81, 'Cursor',  'tools-ai-cursor',  '["Cursor IDE","AI编辑器"]', 2, 1, 1, NOW(), NOW());

-- ---------- 效率工具/写作工具(82) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 82, 'Markdown', 'tools-writing-markdown', '["MD","Markdown语法"]', 1, 1, 1, NOW(), NOW()),
(1, 82, 'Notion',   'tools-writing-notion',   '["Notion笔记","知识库"]', 2, 1, 1, NOW(), NOW());

-- ---------- 效率工具/自动化(83) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 83, '脚本编写', 'tools-automation-script', '["Scripting","自动化脚本"]', 1, 1, 1, NOW(), NOW()),
(1, 83, 'RPA',      'tools-automation-rpa',    '["Robotic Process Automation","流程自动化"]', 2, 1, 1, NOW(), NOW());

-- ---------- 阅读学习/技术书籍(84) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 84, '设计模式',  'reading-tech-designpattern', '["Design Patterns","GoF"]', 1, 1, 1, NOW(), NOW()),
(1, 84, '重构',      'reading-tech-refactoring',   '["Refactoring","代码重构"]', 2, 1, 1, NOW(), NOW());

-- ---------- 阅读学习/在线课程(85) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 85, '极客时间', 'reading-course-geektime', '["极客时间专栏"]', 1, 1, 1, NOW(), NOW()),
(1, 85, 'Coursera', 'reading-course-coursera', '["在线教育","MOOC"]', 2, 1, 1, NOW(), NOW());

-- ---------- 阅读学习/学习方法(86) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 86, '刻意练习',  'reading-method-deliberate', '["Deliberate Practice"]', 1, 1, 1, NOW(), NOW()),
(1, 86, '费曼技巧',  'reading-method-feynman',    '["Feynman Technique","费曼学习法"]', 2, 1, 1, NOW(), NOW());

-- ---------- 阅读学习/认证考试(87) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 87, 'PMP',   'reading-cert-pmp',    '["项目管理专业人士","Project Management Professional"]', 1, 1, 1, NOW(), NOW()),
(1, 87, '软考',  'reading-cert-ruankao', '["计算机软考","全国计算机技术与软件资格考试"]', 2, 1, 1, NOW(), NOW());

-- ---------- 生活随笔/旅行见闻(88) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 88, '国内旅行',  'life-travel-domestic',  '["国内游","国内攻略"]',  1, 1, 1, NOW(), NOW()),
(1, 88, '海外旅行',  'life-travel-overseas',  '["出国游","海外攻略"]',  2, 1, 1, NOW(), NOW());

-- ---------- 生活随笔/美食分享(89) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 89, '探店',  'life-food-discover', '["美食探店","餐厅推荐"]', 1, 1, 1, NOW(), NOW()),
(1, 89, '菜谱',  'life-food-recipe',   '["食谱","烹饪"]',         2, 1, 1, NOW(), NOW());

-- ---------- 生活随笔/摄影记录(90) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 90, '风光摄影',  'life-photo-landscape', '["风景摄影","Landscape Photography"]', 1, 1, 1, NOW(), NOW()),
(1, 90, '后期修图',  'life-photo-editing',   '["修图","Photo Editing","Lightroom"]', 2, 1, 1, NOW(), NOW());

-- ---------- 生活随笔/生活感悟(91) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 91, '随笔',      'life-thought-essay',  '["杂文","随想"]',   1, 1, 1, NOW(), NOW()),
(1, 91, '成长记录',  'life-thought-growth', '["个人成长","成长日记"]', 2, 1, 1, NOW(), NOW());

-- ---------- 创业商业/创业心得(92) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 92, '从0到1',    'business-startup-zerotoone', '["Zero to One","从零到一"]', 1, 1, 1, NOW(), NOW()),
(1, 92, '商业计划',  'business-startup-plan',      '["Business Plan","BP"]',     2, 1, 1, NOW(), NOW());

-- ---------- 创业商业/产品运营(93) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 93, '用户增长',  'business-operation-growth',  '["Growth Hacking","增长黑客"]', 1, 1, 1, NOW(), NOW()),
(1, 93, '内容运营',  'business-operation-content', '["Content Operation","内容营销"]', 2, 1, 1, NOW(), NOW());

-- ---------- 创业商业/商业模式(94) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 94, 'SaaS',     'business-model-saas',         '["Software as a Service","软件即服务"]', 1, 1, 1, NOW(), NOW()),
(1, 94, '订阅制',   'business-model-subscription', '["Subscription","订阅模式"]',            2, 1, 1, NOW(), NOW());

-- ---------- 创业商业/投融资(95) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 95, '天使投资',  'business-finance-angel',  '["Angel Investment","种子轮"]', 1, 1, 1, NOW(), NOW()),
(1, 95, '股权分配',  'business-finance-equity', '["Equity Distribution","期权"]', 2, 1, 1, NOW(), NOW());

-- ---------- 区块链/智能合约(96) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 96, 'Solidity', 'blockchain-contract-solidity', '["智能合约语言","Solidity语言"]', 1, 1, 1, NOW(), NOW()),
(1, 96, '以太坊',   'blockchain-contract-ethereum', '["Ethereum","ETH"]',              2, 1, 1, NOW(), NOW());

-- ---------- 区块链/Web3(97) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 97, 'DApp', 'blockchain-web3-dapp', '["去中心化应用","Decentralized Application"]', 1, 1, 1, NOW(), NOW()),
(1, 97, 'DAO',  'blockchain-web3-dao',  '["去中心化自治组织","Decentralized Autonomous Organization"]', 2, 1, 1, NOW(), NOW());

-- ---------- 区块链/DeFi(98) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 98, '流动性挖矿', 'blockchain-defi-liquidity', '["Liquidity Mining","Yield Farming"]', 1, 1, 1, NOW(), NOW()),
(1, 98, '稳定币',     'blockchain-defi-stablecoin', '["Stablecoin","USDT","USDC"]',         2, 1, 1, NOW(), NOW());

-- ---------- 物联网/嵌入式(99) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 99, 'Arduino',   'iot-embedded-arduino',     '["开源硬件","Arduino开发"]', 1, 1, 1, NOW(), NOW()),
(1, 99, '树莓派',    'iot-embedded-raspberrypi', '["Raspberry Pi","RPi"]',     2, 1, 1, NOW(), NOW());

-- ---------- 数学与算法/算法与数据结构(100) — 2 个标签 ----------

INSERT INTO `t_tag` (`user_id`, `category_id`, `name`, `code`, `alias_json`, `sort`, `is_system`, `status`, `create_time`, `update_time`)
VALUES
(1, 100, '动态规划',  'math-algorithm-dp',    '["DP","Dynamic Programming"]', 1, 1, 1, NOW(), NOW()),
(1, 100, '图算法',    'math-algorithm-graph', '["Graph Algorithm","图论算法"]', 2, 1, 1, NOW(), NOW());

-- ==================== 验证查询 ====================

-- SELECT '分类总数' AS item, COUNT(*) AS cnt FROM t_category
-- UNION ALL
-- SELECT '一级分类', COUNT(*) FROM t_category WHERE level = 1
-- UNION ALL
-- SELECT '二级分类', COUNT(*) FROM t_category WHERE level = 2
-- UNION ALL
-- SELECT '标签总数', COUNT(*) FROM t_tag;
