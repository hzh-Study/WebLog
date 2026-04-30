# hzh

## 简介

`hzh` 的前后端分离博客项目。

- 后端采用 Spring Boot 、Mybatis Plus 、MySQL 、Spring Sericuty、JWT、Minio、Guava 等；
- 后台管理基于 Vue 3.2 + Vite + Element Plus 纯手动搭建的管理后台，未采用任何 Admin 框架；
- 支持博客 Markdown 格式发布与编辑、文章分类、文章标签的管理；
- 支持博客基本信息的设置，以及社交主页的跳转；
- 支持仪表盘数据统计，Echarts 文章发布热图统计、PV 访问量统计；

## 功能

### 前台

| 功能        | 是否完成 |
| ----------- | -------- |
| 首页        | ✅        |
| 分类列表    | ✅        |
| 标签标签    | ✅        |
| 博客详情    | ✅        |
| 站内搜索    | TODO     |
| 知识库 Wiki | TODO     |
| 博客评论    | TODO     |

### 后台

| 功能       | 是否完成 |
| ---------- | -------- |
| 后台登录页 | ✅        |
| 仪表盘     | ✅        |
| 文章管理   | ✅        |
| 分类管理   | ✅        |
| 标签管理   | ✅        |
| 博客设置   | ✅        |
| 评论管理   | TODO     |

## 模块介绍

| 项目名            | 说明                                             |
| ----------------- | ------------------------------------------------ |
| weblog-springboot | 后端项目                                         |
| weblog-vue3       | 前端项目                                         |
| deploy            | 生产 Nginx 示例与前端 `dist` 打包脚本            |

### 后端项目模块介绍

| 模块名               | 说明                 |
| -------------------- | -------------------- |
| weblog-module-admin  | 博客后台管理模块     |
| weblog-module-common | 通用模块             |
| weblog-module-jwt    | JWT 认证、授权模块   |
| weblog-web           | 博客前台（启动入口） |

## 技术栈

### 后端

| 框架                | 说明                     | 版本号      | 备注                                       |
| ------------------- | ------------------------ | ----------- | ------------------------------------------ |
| JDK                 | Java 开发工具包          | 1.8         | 它是目前企业项目比较主流的版本             |
| Spring Boot         | Web 应用开发框架         | 2.6.3       |                                            |
| Maven               | 项目构建工具             | 3.6.3       | 企业主流的构建工具                         |
| MySQL               | 数据库                   | 5.7         |                                            |
| Mybatis Plus        | Mybatis 增强版持久层框架 | 3.5.2       |                                            |
| HikariCP            | 数据库连接池             | 4.0.3       | Spring Boot 内置数据库连接池，号称性能最强 |
| Spring Security     | 安全框架                 | 2.6.3       |                                            |
| JWT                 | Web 应用令牌             | 0.11.2      |                                            |
| Lombok              | 消除冗余的样板式代码     | 1.8.22      |                                            |
| Jackson             | JSON 工具库              | 2.13.1      |                                            |
| Hibernate Validator | 参数校验组件             | 6.2.0.Final |                                            |
| Logback             | 日志组件                 | 1.2.10      |                                            |
| Guava               | Google 开源的工具库      | 18.0        |                                            |
| p6spy               | 动态监测框架             | 3.9.1       |                                            |
| Minio               | 对象存储                 | 8.2.1       | 用于存储博客中相关图片                     |
| flexmark            | Markdown 解析            | 0.62.2      |                                            |

### 前端

| 框架         | 说明                            | 版本号  |
| ------------ | ------------------------------- | ------- |
| Node         | JavaScript 运行时环境           | 18.15.0 |
| Vue 3        | Javascript 渐进式框架           | 3.2.47  |
| Vite         | 前端项目构建工具                | 4.3.9   |
| Element Plus | 饿了么基于 Vue 3 开源的组件框架 | 2.3.3   |
| vue-router   | Vue 路由管理器                  | 4.1.6   |
| vuex         | 状态存储组件                    | 4.0.2   |
| md-editor-v3 | Markdown 编辑器组件             | 3.0.1   |
| windicss     | CSS 工具类框架                  | 3.5.6   |
| axios        | 基于 Promise 的网络请求库       | 1.3.5   |
| Echarts      | 百度开源的数据可视化图表库      | 5.4.2   |

## 数据库与测试数据

先新建库 `weblog`，**表结构**由应用侧维护（如启动时由 JPA 等创建并更新），本仓库不附带 DDL。

**建表与数据**：先执行 `create_table.sql` 建库表，再执行 `create_data.sql`（`TRUNCATE` + 全量 `INSERT`）。例如：

`mysql -u 用户 -p -h 127.0.0.1 -P 3306 weblog < create_table.sql`  
`mysql -u 用户 -p -h 127.0.0.1 -P 3306 weblog < create_data.sql`

## 本地开发

1. 数据库：本机 MySQL 建好 `weblog` 库，在 `weblog-springboot/weblog-web/src/main/resources/application-dev.yaml` 中配置连接。
2. 后端：在 `weblog-springboot` 下以默认 `dev` 启动（`server.port=8081`），例如对 `weblog-web` 执行 `mvn spring-boot:run`。
3. 前端：进入 `weblog-vue3`，`pnpm install` 后 `pnpm run dev`；Vite 会把 `/api` 代理到 `http://127.0.0.1:8081` 并去掉前缀，与生产 Nginx 行为一致。

可选：打好生产包后在本机用 `pnpm run build` 与 `pnpm run preview` 预览；`vite.config.js` 已为 `preview` 配置同样的 `/api` 代理（仍指向 8081，需先启动本地 dev 后端）。

## 生产部署

1. **前端打包**：在仓库根目录，Windows 执行 `deploy\build-web.ps1`（或 `powershell -File deploy\build-web.ps1`），Linux/macOS 执行 `bash deploy/build-web.sh`；将生成的 `weblog-vue3/dist` 上传到服务器。`.env.production` 中 `VITE_APP_BASE_API='/api'`，与下方 Nginx 一致。
2. **后端**：使用 `application-prod.yaml` 中的数据源等配置，设置环境变量 `SPRING_PROFILES_ACTIVE=prod` 启动；`prod` 下监听 **8080**（与 `application-prod.yaml` 中 `server.port` 一致）。
3. **Nginx**：AcWing 等单机可整段替换为 `deploy/nginx.conf`；通用模板见 `deploy/nginx.weblog.conf`，已有站点只贴 `server` 时用 `deploy/nginx.weblog.server-snippet.conf`。`location /api/` 须 `proxy_pass http://127.0.0.1:8080/;`（**末尾是 `/` 且不要写成 `/api/`**）。
