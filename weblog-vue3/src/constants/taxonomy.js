/**
 * Taxonomy System Constants - 系统分类和标签预定义数据
 * 包含100个系统分类和200个系统标签
 * 用户在发布文章时只能从这些预定义选项中选择
 */

export const SYSTEM_CATEGORIES = [
    // ========== 一级分类：技术 (100) ==========
    {
        id: 100,
        name: '技术',
        code: 'tech',
        level: 1,
        children: [
            { id: 101, name: '前端', code: 'tech-frontend', level: 2 },
            { id: 102, name: '后端', code: 'tech-backend', level: 2 },
            { id: 103, name: 'DevOps', code: 'tech-devops', level: 2 },
            { id: 104, name: '数据库', code: 'tech-database', level: 2 },
            { id: 105, name: '移动开发', code: 'tech-mobile', level: 2 },
            { id: 106, name: '人工智能', code: 'tech-ai', level: 2 },
            { id: 107, name: '云计算', code: 'tech-cloud', level: 2 },
            { id: 108, name: '网络安全', code: 'tech-security', level: 2 },
            { id: 109, name: '游戏开发', code: 'tech-game', level: 2 },
            { id: 110, name: '物联网', code: 'tech-iot', level: 2 }
        ]
    },
    // ========== 一级分类：生活 (200) ==========
    {
        id: 200,
        name: '生活',
        code: 'life',
        level: 1,
        children: [
            { id: 201, name: '阅读', code: 'life-reading', level: 2 },
            { id: 202, name: '旅行', code: 'life-travel', level: 2 },
            { id: 203, name: '美食', code: 'life-food', level: 2 },
            { id: 204, name: '运动健身', code: 'life-fitness', level: 2 },
            { id: 205, name: '摄影', code: 'life-photography', level: 2 },
            { id: 206, name: '音乐', code: 'life-music', level: 2 },
            { id: 207, name: '电影', code: 'life-movie', level: 2 },
            { id: 208, name: '宠物', code: 'life-pet', level: 2 },
            { id: 209, name: '家居', code: 'life-home', level: 2 },
            { id: 210, name: '时尚', code: 'life-fashion', level: 2 },
            { id: 211, name: '美妆', code: 'life-beauty', level: 2 },
            { id: 212, name: '育儿', code: 'life-parenting', level: 2 },
            { id: 213, name: '养生', code: 'life-health', level: 2 },
            { id: 214, name: '情感', code: 'life-emotion', level: 2 },
            { id: 215, name: '手工艺', code: 'life-craft', level: 2 }
        ]
    },
    // ========== 一级分类：职场 (300) ==========
    {
        id: 300,
        name: '职场',
        code: 'work',
        level: 1,
        children: [
            { id: 301, name: '职业发展', code: 'work-career', level: 2 },
            { id: 302, name: '职场技能', code: 'work-skill', level: 2 },
            { id: 303, name: '求职面试', code: 'work-interview', level: 2 },
            { id: 304, name: '团队管理', code: 'work-management', level: 2 },
            { id: 305, name: '创业', code: 'work-startup', level: 2 },
            { id: 306, name: '远程办公', code: 'work-remote', level: 2 },
            { id: 307, name: '工作效率', code: 'work-efficiency', level: 2 },
            { id: 308, name: '职场心理', code: 'work-psychology', level: 2 }
        ]
    },
    // ========== 一级分类：财经 (400) ==========
    {
        id: 400,
        name: '财经',
        code: 'finance',
        level: 1,
        children: [
            { id: 401, name: '投资理财', code: 'finance-investment', level: 2 },
            { id: 402, name: '保险', code: 'finance-insurance', level: 2 },
            { id: 403, name: '房产', code: 'finance-real-estate', level: 2 },
            { id: 404, name: '消费', code: 'finance-consumption', level: 2 },
            { id: 405, name: '税务', code: 'finance-tax', level: 2 },
            { id: 406, name: '信用卡', code: 'finance-credit-card', level: 2 },
            { id: 407, name: '数字货币', code: 'finance-crypto', level: 2 }
        ]
    },
    // ========== 一级分类：教育 (500) ==========
    {
        id: 500,
        name: '教育',
        code: 'edu',
        level: 1,
        children: [
            { id: 501, name: '学习方法', code: 'edu-learning', level: 2 },
            { id: 502, name: '在线教育', code: 'edu-online', level: 2 },
            { id: 503, name: '语言学习', code: 'edu-language', level: 2 },
            { id: 504, name: 'K12教育', code: 'edu-k12', level: 2 },
            { id: 505, name: '高等教育', code: 'edu-higher', level: 2 },
            { id: 506, name: '职业教育', code: 'edu-vocational', level: 2 },
            { id: 507, name: '留学', code: 'edu-abroad', level: 2 }
        ]
    },
    // ========== 一级分类：创意 (600) ==========
    {
        id: 600,
        name: '创意',
        code: 'creative',
        level: 1,
        children: [
            { id: 601, name: '设计', code: 'creative-design', level: 2 },
            { id: 602, name: '写作', code: 'creative-writing', level: 2 },
            { id: 603, name: '插画', code: 'creative-illustration', level: 2 },
            { id: 604, name: '视频制作', code: 'creative-video', level: 2 },
            { id: 605, name: '动画', code: 'creative-animation', level: 2 },
            { id: 606, name: '音乐制作', code: 'creative-music-production', level: 2 },
            { id: 607, name: '产品设计', code: 'creative-product', level: 2 }
        ]
    },
    // ========== 一级分类：科技 (700) ==========
    {
        id: 700,
        name: '科技',
        code: 'tech-digital',
        level: 1,
        children: [
            { id: 701, name: '数码产品', code: 'tech-digital-products', level: 2 },
            { id: 702, name: '科技评测', code: 'tech-review', level: 2 },
            { id: 703, name: '科技资讯', code: 'tech-news', level: 2 },
            { id: 704, name: '软件应用', code: 'tech-software', level: 2 },
            { id: 705, name: '硬件DIY', code: 'tech-hardware', level: 2 }
        ]
    },
    // ========== 一级分类：文化 (800) ==========
    {
        id: 800,
        name: '文化',
        code: 'culture',
        level: 1,
        children: [
            { id: 801, name: '历史', code: 'culture-history', level: 2 },
            { id: 802, name: '哲学', code: 'culture-philosophy', level: 2 },
            { id: 803, name: '心理学', code: 'culture-psychology', level: 2 },
            { id: 804, name: '社会学', code: 'culture-sociology', level: 2 },
            { id: 805, name: '文学', code: 'culture-literature', level: 2 },
            { id: 806, name: '艺术', code: 'culture-art', level: 2 },
            { id: 807, name: '语言学', code: 'culture-linguistics', level: 2 }
        ]
    },
    // ========== 一级分类：娱乐 (900) ==========
    {
        id: 900,
        name: '娱乐',
        code: 'entertainment',
        level: 1,
        children: [
            { id: 901, name: '游戏', code: 'entertainment-game', level: 2 },
            { id: 902, name: '动漫', code: 'entertainment-anime', level: 2 },
            { id: 903, name: '综艺', code: 'entertainment-variety', level: 2 },
            { id: 904, name: '电视剧', code: 'entertainment-tv', level: 2 },
            { id: 905, name: '电影', code: 'entertainment-movie', level: 2 },
            { id: 906, name: '明星', code: 'entertainment-star', level: 2 }
        ]
    }
]

export const SYSTEM_TAGS = {
    // ========== 前端标签 (20个) ==========
    101: [
        { id: 1001, name: 'Vue3', code: 'vue3', aliases: ['Vue.js 3', 'Vue 3', 'Vue'] },
        { id: 1002, name: 'React', code: 'react', aliases: ['React.js', 'ReactJS', 'React Fiber'] },
        { id: 1003, name: 'TypeScript', code: 'typescript', aliases: ['TS', 'TypeScript'] },
        { id: 1004, name: 'JavaScript', code: 'javascript', aliases: ['JS', 'ECMAScript'] },
        { id: 1005, name: 'CSS3', code: 'css3', aliases: ['CSS', 'Stylesheet', '级联样式表'] },
        { id: 1006, name: 'HTML5', code: 'html5', aliases: ['HTML', 'HyperText Markup Language'] },
        { id: 1007, name: 'Vite', code: 'vite', aliases: ['Vite'] },
        { id: 1008, name: 'Webpack', code: 'webpack', aliases: ['Webpack'] },
        { id: 1009, name: 'Tailwind CSS', code: 'tailwindcss', aliases: ['Tailwind', 'TailwindCSS'] },
        { id: 1010, name: 'Sass', code: 'sass', aliases: ['SCSS', 'Sass'] },
        { id: 1011, name: 'Next.js', code: 'nextjs', aliases: ['NextJS', 'Next'] },
        { id: 1012, name: 'Nuxt', code: 'nuxt', aliases: ['Nuxt.js', 'NuxtJS'] },
        { id: 1013, name: 'Angular', code: 'angular', aliases: ['AngularJS', 'Angular'] },
        { id: 1014, name: 'Svelte', code: 'svelte', aliases: ['Svelte'] },
        { id: 1015, name: 'UI框架', code: 'ui-framework', aliases: ['UI库', 'UI框架'] },
        { id: 1016, name: '小程序', code: 'mini-program', aliases: ['微信小程序', '小程序开发'] },
        { id: 1017, name: '移动端适配', code: 'mobile-adaptation', aliases: ['响应式', 'Responsive'] },
        { id: 1018, name: '性能优化', code: 'performance-optimization', aliases: ['Performance', '优化'] },
        { id: 1019, name: '前端工程化', code: 'frontend-engineering', aliases: ['工程化', '前端架构'] },
        { id: 1020, name: 'WebGL', code: 'webgl', aliases: ['Three.js', 'WebGL'] }
    ],
    // ========== 后端标签 (25个) ==========
    102: [
        { id: 1021, name: 'Java', code: 'java', aliases: ['JDK', 'Java'] },
        { id: 1022, name: 'Spring Boot', code: 'spring-boot', aliases: ['SpringBoot'] },
        { id: 1023, name: 'Spring Cloud', code: 'spring-cloud', aliases: ['SpringCloud'] },
        { id: 1024, name: 'Python', code: 'python', aliases: ['Python'] },
        { id: 1025, name: 'Django', code: 'django', aliases: ['Django'] },
        { id: 1026, name: 'Flask', code: 'flask', aliases: ['Flask'] },
        { id: 1027, name: 'Go', code: 'go', aliases: ['Golang', 'Go语言'] },
        { id: 1028, name: 'Node.js', code: 'nodejs', aliases: ['NodeJS', 'Node'] },
        { id: 1029, name: 'Express', code: 'express', aliases: ['Express.js'] },
        { id: 1030, name: 'NestJS', code: 'nestjs', aliases: ['NestJS'] },
        { id: 1031, name: 'PHP', code: 'php', aliases: ['PHP'] },
        { id: 1032, name: 'Laravel', code: 'laravel', aliases: ['Laravel'] },
        { id: 1033, name: 'Ruby', code: 'ruby', aliases: ['Ruby'] },
        { id: 1034, name: 'Rails', code: 'rails', aliases: ['Ruby on Rails'] },
        { id: 1035, name: 'RESTful API', code: 'restful', aliases: ['REST API', 'RESTful'] },
        { id: 1036, name: 'GraphQL', code: 'graphql', aliases: ['GraphQL'] },
        { id: 1037, name: 'gRPC', code: 'grpc', aliases: ['gRPC'] },
        { id: 1038, name: '微服务', code: 'microservice', aliases: ['Microservice', '微服务架构'] },
        { id: 1039, name: '分布式', code: 'distributed', aliases: ['Distributed', '分布式系统'] },
        { id: 1040, name: '高并发', code: 'high-concurrency', aliases: ['并发', 'Concurrency'] },
        { id: 1041, name: '消息队列', code: 'message-queue', aliases: ['MQ', 'Message Queue'] },
        { id: 1042, name: '缓存', code: 'cache', aliases: ['Cache', 'Redis'] },
        { id: 1043, name: '认证授权', code: 'auth', aliases: ['OAuth', 'JWT', '权限'] },
        { id: 1044, name: '设计模式', code: 'design-pattern', aliases: ['Design Pattern'] },
        { id: 1045, name: '架构设计', code: 'architecture', aliases: ['Architecture', '系统设计'] }
    ],
    // ========== DevOps标签 (20个) ==========
    103: [
        { id: 1046, name: 'Docker', code: 'docker', aliases: ['Container', '容器'] },
        { id: 1047, name: 'Kubernetes', code: 'kubernetes', aliases: ['K8s', 'K8S'] },
        { id: 1048, name: 'CI/CD', code: 'cicd', aliases: ['Jenkins', 'GitHub Actions', 'GitLab CI'] },
        { id: 1049, name: 'Linux', code: 'linux', aliases: ['Ubuntu', 'CentOS', 'Shell'] },
        { id: 1050, name: 'Nginx', code: 'nginx', aliases: ['Nginx'] },
        { id: 1051, name: 'Apache', code: 'apache', aliases: ['Apache'] },
        { id: 1052, name: 'Shell脚本', code: 'shell-script', aliases: ['Bash', 'Shell'] },
        { id: 1053, name: 'Ansible', code: 'ansible', aliases: ['Ansible'] },
        { id: 1054, name: 'Terraform', code: 'terraform', aliases: ['Terraform'] },
        { id: 1055, name: 'Prometheus', code: 'prometheus', aliases: ['Prometheus'] },
        { id: 1056, name: 'Grafana', code: 'grafana', aliases: ['Grafana'] },
        { id: 1057, name: 'ELK日志', code: 'elk', aliases: ['Elasticsearch', 'Logstash', 'Kibana'] },
        { id: 1058, name: '容器编排', code: 'container-orchestration', aliases: ['编排', 'Orchestration'] },
        { id: 1059, name: '监控告警', code: 'monitoring', aliases: ['监控', 'Alerting'] },
        { id: 1060, name: '自动化运维', code: 'automated-ops', aliases: ['AIOps', '自动化'] },
        { id: 1061, name: '负载均衡', code: 'load-balancing', aliases: ['LB', 'Load Balancer'] },
        { id: 1062, name: '反向代理', code: 'reverse-proxy', aliases: ['Proxy', '反向代理'] },
        { id: 1063, name: '服务网格', code: 'service-mesh', aliases: ['Istio', 'Service Mesh'] },
        { id: 1064, name: 'Git', code: 'git', aliases: ['Git'] },
        { id: 1065, name: '版本控制', code: 'version-control', aliases: ['VCS', '版本管理'] }
    ],
    // ========== 数据库标签 (20个) ==========
    104: [
        { id: 1066, name: 'MySQL', code: 'mysql', aliases: ['MariaDB'] },
        { id: 1067, name: 'PostgreSQL', code: 'postgresql', aliases: ['Postgres', 'PG'] },
        { id: 1068, name: 'Redis', code: 'redis', aliases: ['Redis'] },
        { id: 1069, name: 'MongoDB', code: 'mongodb', aliases: ['Mongo'] },
        { id: 1070, name: 'Elasticsearch', code: 'elasticsearch', aliases: ['ES', 'Elastic'] },
        { id: 1071, name: 'Oracle', code: 'oracle', aliases: ['Oracle DB'] },
        { id: 1072, name: 'SQL Server', code: 'sql-server', aliases: ['MSSQL', 'SQLServer'] },
        { id: 1073, name: 'SQLite', code: 'sqlite', aliases: ['SQLite'] },
        { id: 1074, name: 'Cassandra', code: 'cassandra', aliases: ['Cassandra'] },
        { id: 1075, name: 'Neo4j', code: 'neo4j', aliases: ['Neo4j', '图数据库'] },
        { id: 1076, name: '数据库优化', code: 'db-optimization', aliases: ['性能优化', 'SQL优化'] },
        { id: 1077, name: '索引优化', code: 'index-optimization', aliases: ['索引', 'Index'] },
        { id: 1078, name: '分库分表', code: 'sharding', aliases: ['Sharding', '分片'] },
        { id: 1079, name: '主从复制', code: 'replication', aliases: ['Replication', '读写分离'] },
        { id: 1080, name: '事务', code: 'transaction', aliases: ['Transaction', 'ACID'] },
        { id: 1081, name: 'NoSQL', code: 'nosql', aliases: ['NoSQL'] },
        { id: 1082, name: '数据库设计', code: 'db-design', aliases: ['Schema', '数据建模'] },
        { id: 1083, name: 'ORM', code: 'orm', aliases: ['Hibernate', 'MyBatis'] },
        { id: 1084, name: '数据迁移', code: 'data-migration', aliases: ['Migration', '迁移'] },
        { id: 1085, name: '备份恢复', code: 'backup-recovery', aliases: ['Backup', '恢复'] }
    ],
    // ========== 移动开发标签 (15个) ==========
    105: [
        { id: 1086, name: 'Android', code: 'android', aliases: ['安卓', 'Android开发'] },
        { id: 1087, name: 'iOS', code: 'ios', aliases: ['苹果', 'Apple开发'] },
        { id: 1088, name: 'Flutter', code: 'flutter', aliases: ['Flutter'] },
        { id: 1089, name: 'React Native', code: 'react-native', aliases: ['RN', 'ReactNative'] },
        { id: 1090, name: 'Kotlin', code: 'kotlin', aliases: ['Kotlin'] },
        { id: 1091, name: 'Swift', code: 'swift', aliases: ['Swift'] },
        { id: 1092, name: '跨平台开发', code: 'cross-platform', aliases: ['跨平台', 'Hybrid'] },
        { id: 1093, name: 'App Store', code: 'app-store', aliases: ['AppStore', '应用市场'] },
        { id: 1094, name: '应用发布', code: 'app-release', aliases: ['上架', '发布'] },
        { id: 1095, name: '移动安全', code: 'mobile-security', aliases: ['App安全', '加固'] },
        { id: 1096, name: '推送通知', code: 'push-notification', aliases: ['Push', '消息推送'] },
        { id: 1097, name: '热更新', code: 'hot-update', aliases: ['热修复', 'Hot Fix'] },
        { id: 1098, name: '性能监控', code: 'mobile-monitoring', aliases: ['APM', '性能分析'] },
        { id: 1099, name: '移动测试', code: 'mobile-testing', aliases: ['测试', 'Monkey'] }
    ],
    // ========== 人工智能标签 (20个) ==========
    106: [
        { id: 1100, name: '机器学习', code: 'machine-learning', aliases: ['ML', 'Machine Learning'] },
        { id: 1101, name: '深度学习', code: 'deep-learning', aliases: ['DL', 'Deep Learning'] },
        { id: 1102, name: 'TensorFlow', code: 'tensorflow', aliases: ['TF', 'TensorFlow'] },
        { id: 1103, name: 'PyTorch', code: 'pytorch', aliases: ['PyTorch'] },
        { id: 1104, name: '神经网络', code: 'neural-network', aliases: ['NN', 'Neural Network'] },
        { id: 1105, name: '自然语言处理', code: 'nlp', aliases: ['NLP', '文本处理'] },
        { id: 1106, name: '计算机视觉', code: 'cv', aliases: ['CV', '图像处理'] },
        { id: 1107, name: '大语言模型', code: 'llm', aliases: ['LLM', 'GPT', 'ChatGPT'] },
        { id: 1108, name: '强化学习', code: 'reinforcement-learning', aliases: ['RL', '强化学习'] },
        { id: 1109, name: '迁移学习', code: 'transfer-learning', aliases: ['Transfer Learning'] },
        { id: 1110, name: '模型训练', code: 'model-training', aliases: ['训练', 'Training'] },
        { id: 1111, name: '模型部署', code: 'model-deployment', aliases: ['部署', 'Serving'] },
        { id: 1112, name: '数据预处理', code: 'data-preprocessing', aliases: ['ETL', '数据清洗'] },
        { id: 1113, name: '特征工程', code: 'feature-engineering', aliases: ['特征', 'Feature'] },
        { id: 1114, name: '模型优化', code: 'model-optimization', aliases: ['压缩', 'Quantization'] },
        { id: 1115, name: 'AI应用', code: 'ai-application', aliases: ['落地', '应用'] },
        { id: 1116, name: 'AI工具', code: 'ai-tool', aliases: ['工具', 'Platform'] },
        { id: 1117, name: 'Prompt工程', code: 'prompt-engineering', aliases: ['Prompt', '提示词'] },
        { id: 1118, name: 'AI伦理', code: 'ai-ethics', aliases: ['伦理', '安全'] },
        { id: 1119, name: 'AI评测', code: 'ai-evaluation', aliases: ['Benchmark', '评测'] }
    ],
    // ========== 云计算标签 (15个) ==========
    107: [
        { id: 1120, name: 'AWS', code: 'aws', aliases: ['Amazon Web Services'] },
        { id: 1121, name: '阿里云', code: 'aliyun', aliases: ['Alibaba Cloud'] },
        { id: 1122, name: '腾讯云', code: 'tencent-cloud', aliases: ['Tencent Cloud'] },
        { id: 1123, name: '华为云', code: 'huawei-cloud', aliases: ['Huawei Cloud'] },
        { id: 1124, name: 'Azure', code: 'azure', aliases: ['Microsoft Azure'] },
        { id: 1125, name: 'Google Cloud', code: 'gcp', aliases: ['GCP', 'Google Cloud Platform'] },
        { id: 1126, name: 'Serverless', code: 'serverless', aliases: ['无服务器', '函数计算'] },
        { id: 1127, name: '容器服务', code: 'container-service', aliases: ['ECS', '云容器'] },
        { id: 1128, name: '对象存储', code: 'object-storage', aliases: ['OSS', 'COS', '云存储'] },
        { id: 1129, name: 'CDN', code: 'cdn', aliases: ['内容分发', 'CDN'] },
        { id: 1130, name: 'VPC', code: 'vpc', aliases: ['私有网络', 'Virtual Private Cloud'] },
        { id: 1131, name: '负载均衡', code: 'cloud-load-balancing', aliases: ['SLB', 'CLB'] },
        { id: 1132, name: '云数据库', code: 'cloud-database', aliases: ['RDS', '云数据库'] },
        { id: 1133, name: '弹性伸缩', code: 'auto-scaling', aliases: ['ASG', '伸缩'] },
        { id: 1134, name: '成本优化', code: 'cost-optimization', aliases: ['省钱', '优化'] }
    ],
    // ========== 网络安全标签 (15个) ==========
    108: [
        { id: 1135, name: 'Web安全', code: 'web-security', aliases: ['Web安全', 'XSS', 'CSRF'] },
        { id: 1136, name: '渗透测试', code: 'penetration-testing', aliases: ['渗透', 'Pen Test'] },
        { id: 1137, name: '密码学', code: 'cryptography', aliases: ['Crypto', '加密'] },
        { id: 1138, name: '网络安全', code: 'network-security', aliases: ['Firewall', '防火墙'] },
        { id: 1139, name: '数据安全', code: 'data-security', aliases: ['安全', 'Data Security'] },
        { id: 1140, name: '身份认证', code: 'authentication', aliases: ['Auth', '身份验证'] },
        { id: 1141, name: '漏洞扫描', code: 'vulnerability-scanning', aliases: ['扫描', 'Scanner'] },
        { id: 1142, name: '应急响应', code: 'incident-response', aliases: ['IR', '应急'] },
        { id: 1143, name: '安全运维', code: 'security-ops', aliases: ['SecOps', '安全运营'] },
        { id: 1144, name: '等级保护', code: 'security-compliance', aliases: ['等保', '合规'] },
        { id: 1145, name: 'WAF', code: 'waf', aliases: ['Web应用防火墙'] },
        { id: 1146, name: 'DDoS防护', code: 'ddos-protection', aliases: ['DDoS', '抗D'] },
        { id: 1147, name: '加密算法', code: 'encryption-algorithm', aliases: ['AES', 'RSA'] },
        { id: 1148, name: '安全开发', code: 'secure-development', aliases: ['SDL', '安全编码'] },
        { id: 1149, name: '漏洞修复', code: 'vulnerability-fix', aliases: ['补丁', 'Patch'] }
    ],
    // ========== 阅读生活标签 (15个) ==========
    201: [
        { id: 1150, name: '读书笔记', code: 'reading-notes', aliases: ['读后感', '书评'] },
        { id: 1151, name: '技术书籍', code: 'tech-books', aliases: ['编程书', '技术书'] },
        { id: 1152, name: '小说推荐', code: 'novel-recommendation', aliases: ['小说', 'Book'] },
        { id: 1153, name: '书单', code: 'book-list', aliases: ['书单', 'Reading List'] },
        { id: 1154, name: '学习方法', code: 'reading-method', aliases: ['阅读方法', '技巧'] },
        { id: 1155, name: '电子书', code: 'ebook', aliases: ['E-book', '电子书'] },
        { id: 1156, name: '纸质书', code: 'paper-book', aliases: ['纸质书', '实体书'] },
        { id: 1157, name: '阅读工具', code: 'reading-tools', aliases: ['Kindle', '阅读器'] },
        { id: 1158, name: '读书会', code: 'book-club', aliases: ['读书分享', 'Reading Club'] },
        { id: 1159, name: '自我提升', code: 'self-improvement', aliases: ['成长', '个人发展'] },
        { id: 1160, name: '心理学书籍', code: 'psychology-books', aliases: ['心理学', 'Psychology'] },
        { id: 1161, name: '商业书籍', code: 'business-books', aliases: ['商业', 'Business'] },
        { id: 1162, name: '历史书籍', code: 'history-books', aliases: ['历史', 'History'] },
        { id: 1163, name: '文学作品', code: 'literary-works', aliases: ['文学', 'Literature'] },
        { id: 1164, name: '科普读物', code: 'science-books', aliases: ['科普', 'Popular Science'] }
    ],
    // ========== 旅行生活标签 (10个) ==========
    202: [
        { id: 1165, name: '国内旅行', code: 'domestic-travel', aliases: ['国内游', '国内旅游'] },
        { id: 1166, name: '海外旅行', code: 'overseas-travel', aliases: ['出国游', '出境游'] },
        { id: 1167, name: '旅行攻略', code: 'travel-guide', aliases: ['攻略', 'Guide'] },
        { id: 1168, name: '旅行摄影', code: 'travel-photography', aliases: ['旅拍', 'Tourism Photo'] },
        { id: 1169, name: '旅行装备', code: 'travel-gear', aliases: ['装备', 'Gear'] },
        { id: 1170, name: '背包客', code: 'backpacking', aliases: ['Backpacking', '穷游'] },
        { id: 1171, name: '自驾游', code: 'road-trip', aliases: ['自驾', 'Road Trip'] },
        { id: 1172, name: '出境签证', code: 'visa', aliases: ['签证', 'Visa'] },
        { id: 1173, name: '酒店推荐', code: 'hotel-recommendation', aliases: ['酒店', 'Hotel'] },
        { id: 1174, name: '景点打卡', code: 'attraction', aliases: ['打卡', 'Scenic Spot'] }
    ],
    // ========== 美食生活标签 (10个) ==========
    203: [
        { id: 1175, name: '食谱分享', code: 'recipe', aliases: ['菜谱', 'Recipe'] },
        { id: 1176, name: '美食探店', code: 'food-exploration', aliases: ['探店', '餐厅'] },
        { id: 1177, name: '烘焙', code: 'baking', aliases: ['Bake', '糕点'] },
        { id: 1178, name: '健康饮食', code: 'healthy-eating', aliases: ['养生', '健康'] },
        { id: 1179, name: '咖啡', code: 'coffee', aliases: ['Coffee', '咖啡'] },
        { id: 1180, name: '茶道', code: 'tea', aliases: ['Tea', '茶'] },
        { id: 1181, name: '烹饪技巧', code: 'cooking-tips', aliases: ['技巧', 'Tips'] },
        { id: 1182, name: '食材推荐', code: 'ingredients', aliases: ['食材', 'Ingredients'] },
        { id: 1183, name: '小吃甜点', code: 'snacks-desserts', aliases: ['小吃', '甜点'] },
        { id: 1184, name: '地方美食', code: 'local-cuisine', aliases: ['特色', 'Cuisine'] }
    ],
    // ========== 运动健身标签 (10个) ==========
    204: [
        { id: 1185, name: '健身', code: 'fitness', aliases: ['训练', 'Workout'] },
        { id: 1186, name: '跑步', code: 'running', aliases: ['Running', 'Jogging'] },
        { id: 1187, name: '瑜伽', code: 'yoga', aliases: ['Yoga'] },
        { id: 1188, name: '游泳', code: 'swimming', aliases: ['Swimming'] },
        { id: 1189, name: '篮球', code: 'basketball', aliases: ['Basketball'] },
        { id: 1190, name: '足球', code: 'football', aliases: ['Football', 'Soccer'] },
        { id: 1191, name: '羽毛球', code: 'badminton', aliases: ['Badminton'] },
        { id: 1192, name: '健身计划', code: 'fitness-plan', aliases: ['计划', 'Plan'] },
        { id: 1193, name: '减脂', code: 'fat-loss', aliases: ['Loss', '减脂'] },
        { id: 1194, name: '增肌', code: 'muscle-gain', aliases: ['Gain', '增肌'] }
    ],
    // ========== 摄影标签 (5个) ==========
    205: [
        { id: 1195, name: '摄影技巧', code: 'photography-tips', aliases: ['技巧', 'Tips'] },
        { id: 1196, name: '器材评测', code: 'gear-review', aliases: ['评测', 'Review'] },
        { id: 1197, name: '人像摄影', code: 'portrait-photography', aliases: ['人像', 'Portrait'] },
        { id: 1198, name: '风光摄影', code: 'landscape-photography', aliases: ['风光', 'Landscape'] },
        { id: 1199, name: '手机摄影', code: 'mobile-photography', aliases: ['手机', 'Phone'] }
    ],
    // ========== 音乐标签 (5个) ==========
    206: [
        { id: 1200, name: '乐器演奏', code: 'instrument', aliases: ['乐器', 'Guitar', '钢琴'] },
        { id: 1201, name: '音乐推荐', code: 'music-recommendation', aliases: ['推荐', 'Playlist'] },
        { id: 1202, name: '音乐制作', code: 'music-production', aliases: ['DAW', '编曲'] },
        { id: 1203, name: '音乐理论', code: 'music-theory', aliases: ['乐理', 'Theory'] },
        { id: 1204, name: '独立音乐', code: 'indie-music', aliases: ['Indie', '独立'] }
    ],
    // ========== 电影标签 (5个) ==========
    207: [
        { id: 1205, name: '电影推荐', code: 'movie-recommendation', aliases: ['推荐', 'Recommendation'] },
        { id: 1206, name: '影评', code: 'movie-review', aliases: ['Review', '影评'] },
        { id: 1207, name: '科幻电影', code: 'sci-fi', aliases: ['Sci-Fi', 'Science Fiction'] },
        { id: 1208, name: '动作电影', code: 'action-movie', aliases: ['Action'] },
        { id: 1209, name: '剧情电影', code: 'drama-movie', aliases: ['Drama'] }
    ],
    // ========== 宠物标签 (5个) ==========
    208: [
        { id: 1210, name: '猫咪', code: 'cat', aliases: ['Cat', '猫'] },
        { id: 1211, name: '狗狗', code: 'dog', aliases: ['Dog', '狗'] },
        { id: 1212, name: '宠物饲养', code: 'pet-keeping', aliases: ['养宠', '饲养'] },
        { id: 1213, name: '宠物健康', code: 'pet-health', aliases: ['健康', 'Health'] },
        { id: 1214, name: '宠物用品', code: 'pet-products', aliases: ['用品', 'Products'] }
    ],
    // ========== 家居标签 (5个) ==========
    209: [
        { id: 1215, name: '装修', code: 'renovation', aliases: ['Decoration', '装潢'] },
        { id: 1216, name: '家居好物', code: 'home-products', aliases: ['好物', 'Products'] },
        { id: 1217, name: '收纳整理', code: 'organization', aliases: ['整理', 'Organization'] },
        { id: 1218, name: '智能家居', code: 'smart-home', aliases: ['Smart Home', 'IoT Home'] },
        { id: 1219, name: '绿植', code: 'plants', aliases: ['Plant', '植物'] }
    ],
    // ========== 职场标签 (10个) ==========
    301: [
        { id: 1220, name: '简历优化', code: 'resume-optimization', aliases: ['简历', 'Resume'] },
        { id: 1221, name: '薪资谈判', code: 'salary-negotiation', aliases: ['Salary', '薪酬'] },
        { id: 1222, name: '职场人脉', code: 'networking', aliases: ['Network', '人脉'] },
        { id: 1223, name: '跳槽', code: 'job-hopping', aliases: ['转职', 'Change'] },
        { id: 1224, name: '自由职业', code: 'freelance', aliases: ['Freelancer', 'SOHO'] }
    ],
    302: [
        { id: 1225, name: '沟通技巧', code: 'communication', aliases: ['沟通', 'Communication'] },
        { id: 1226, name: '时间管理', code: 'time-management', aliases: ['Time', '时间'] },
        { id: 1227, name: '演讲表达', code: 'presentation', aliases: ['Presentation', '演讲'] },
        { id: 1228, name: '项目管理', code: 'project-management', aliases: ['PM', 'Project Management'] },
        { id: 1229, name: '办公软件', code: 'office-software', aliases: ['Office', 'Excel'] }
    ],
    // ========== 财经标签 (10个) ==========
    401: [
        { id: 1230, name: '基金投资', code: 'fund-investment', aliases: ['基金', 'Fund'] },
        { id: 1231, name: '股票投资', code: 'stock-investment', aliases: ['股票', 'Stock'] },
        { id: 1232, name: '理财规划', code: 'financial-planning', aliases: ['理财', 'Planning'] },
        { id: 1233, name: '省钱技巧', code: 'money-saving', aliases: ['省钱', 'Save Money'] },
        { id: 1234, name: '被动收入', code: 'passive-income', aliases: ['Income', '睡后收入'] }
    ],
    // ========== 教育标签 (10个) ==========
    501: [
        { id: 1235, name: '学习方法论', code: 'learning-methodology', aliases: ['方法论', 'Methodology'] },
        { id: 1236, name: '记忆技巧', code: 'memory-techniques', aliases: ['记忆', 'Memory'] },
        { id: 1237, name: '思维导图', code: 'mind-map', aliases: ['Mind Map', 'Thinking Map'] },
        { id: 1238, name: '费曼学习法', code: 'feynman-technique', aliases: ['费曼', 'Feynman'] },
        { id: 1239, name: '专注力训练', code: 'focus-training', aliases: ['专注', 'Focus'] }
    ],
    // ========== 创意设计标签 (10个) ==========
    601: [
        { id: 1240, name: 'UI设计', code: 'ui-design', aliases: ['UI', '界面设计'] },
        { id: 1241, name: 'UX设计', code: 'ux-design', aliases: ['UX', '用户体验'] },
        { id: 1242, name: '平面设计', code: 'graphic-design', aliases: ['Graphic', '平面'] },
        { id: 1243, name: '品牌设计', code: 'branding', aliases: ['Brand', '品牌'] },
        { id: 1244, name: '配色方案', code: 'color-scheme', aliases: ['配色', 'Color'] }
    ],
    604: [
        { id: 1245, name: '剪辑技巧', code: 'editing-tips', aliases: ['剪辑', 'Editing'] },
        { id: 1246, name: 'Premiere', code: 'premiere', aliases: ['Pr', 'Premiere Pro'] },
        { id: 1247, name: 'Final Cut Pro', code: 'final-cut-pro', aliases: ['FCP', 'Final Cut'] },
        { id: 1248, name: '达芬奇调色', code: 'davinci-resolve', aliases: ['调色', 'Color Grading'] },
        { id: 1249, name: '特效合成', code: 'vfx', aliases: ['VFX', '特效'] }
    ],
    // ========== 游戏开发标签 (10个) ==========
    109: [
        { id: 1250, name: 'Unity', code: 'unity', aliases: ['Unity3D', 'Unity'] },
        { id: 1251, name: 'Unreal Engine', code: 'unreal-engine', aliases: ['UE', 'UE4', 'UE5'] },
        { id: 1252, name: '游戏策划', code: 'game-design', aliases: ['策划', 'Game Design'] },
        { id: 1253, name: '游戏美术', code: 'game-art', aliases: ['美术', 'Art'] },
        { id: 1254, name: '游戏运营', code: 'game-operation', aliases: ['运营', 'Operation'] }
    ],
    // ========== 时尚美妆标签 (5个) ==========
    210: [
        { id: 1255, name: '穿搭', code: 'fashion-style', aliases: ['搭配', 'Style'] },
        { id: 1256, name: '品牌推荐', code: 'brand-recommendation', aliases: ['品牌', 'Brand'] }
    ],
    211: [
        { id: 1257, name: '护肤', code: 'skincare', aliases: ['护肤', 'Skincare'] },
        { id: 1258, name: '彩妆', code: 'makeup', aliases: ['彩妆', 'Makeup'] }
    ],
    // ========== 育儿标签 (5个) ==========
    212: [
        { id: 1259, name: '育儿经验', code: 'parenting-experience', aliases: ['经验', 'Experience'] },
        { id: 1260, name: '早教', code: 'early-education', aliases: ['Early Education'] }
    ],
    // ========== 情感标签 (5个) ==========
    214: [
        { id: 1261, name: '人际关系', code: 'interpersonal', aliases: ['关系', 'Relationship'] },
        { id: 1262, name: '情感沟通', code: 'emotional-communication', aliases: ['沟通', 'Communication'] }
    ],
    // ========== 物联网标签 (5个) ==========
    110: [
        { id: 1263, name: '传感器', code: 'sensors', aliases: ['Sensor', '传感'] },
        { id: 1264, name: '嵌入式开发', code: 'embedded-development', aliases: ['Embedded', '单片机'] },
        { id: 1265, name: 'Arduino', code: 'arduino', aliases: ['Arduino'] },
        { id: 1266, name: ' Raspberry Pi', code: 'raspberry-pi', aliases: ['Pi', '树莓派'] },
        { id: 1267, name: '智能硬件', code: 'smart-hardware', aliases: ['硬件', 'Hardware'] }
    ]
}

export function getCategoryTree() {
    return SYSTEM_CATEGORIES
}

export function getCategoryById(id) {
    for (const parent of SYSTEM_CATEGORIES) {
        if (parent.id === id) {
            return parent
        }
        if (parent.children) {
            const child = parent.children.find(c => c.id === id)
            if (child) {
                return { ...child, parentName: parent.name }
            }
        }
    }
    return null
}

export function getTagsByCategoryId(categoryId) {
    return SYSTEM_TAGS[categoryId] || []
}

export function searchCategories(keyword) {
    const results = []
    for (const parent of SYSTEM_CATEGORIES) {
        if (parent.name.includes(keyword)) {
            results.push(parent)
        }
        if (parent.children) {
            for (const child of parent.children) {
                if (child.name.includes(keyword) || child.code.includes(keyword)) {
                    results.push({ ...child, parentName: parent.name })
                }
            }
        }
    }
    return results
}

export function searchTags(keyword) {
    const results = []
    for (const [categoryId, tags] of Object.entries(SYSTEM_TAGS)) {
        for (const tag of tags) {
            if (tag.name.includes(keyword) || 
                tag.code.includes(keyword) || 
                tag.aliases.some(alias => alias.includes(keyword))) {
                results.push({ ...tag, categoryId: parseInt(categoryId) })
            }
        }
    }
    return results
}

export function validateCategoryId(id) {
    return getCategoryById(id) !== null
}

export function validateTagId(id) {
    for (const tags of Object.values(SYSTEM_TAGS)) {
        if (tags.find(tag => tag.id === id)) {
            return true
        }
    }
    return false
}

export function validateTagIds(ids) {
    return ids.every(id => validateTagId(id))
}
