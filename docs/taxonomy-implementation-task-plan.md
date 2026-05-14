# WeBlog Taxonomy 分类标签系统实施任务总文档

> 版本：v1.0  
> 日期：2026-05-14  
> 主题：预定义 taxonomy 分类标签系统 + AI 推荐受控化改造  
> 目标：将当前“用户可自由创建分类/标签”的模式，升级为“固定分类体系 + 预定义标签池 + AI 推荐受控映射 + 支持 AI 多线程并行开发”的可执行方案

---

## 目录

1. 项目背景与目标
2. 当前系统现状与差距
3. 总体实施范围
4. 核心设计决策
5. 全量任务拆解
6. AI 多线程并行开发拆包方案
7. 任务依赖关系与执行顺序
8. 文件级改动清单
9. API 与数据库落地清单
10. 验收标准
11. 风险与回滚策略
12. 建议的开发协作方式

---

## 1. 项目背景与目标

### 1.1 背景

当前 WeBlog 已具备：

- 后台文章发布与编辑
- 后台分类管理、标签管理
- 标签远程搜索
- AI 标签推荐、AI 分类推荐
- 前台分类/标签浏览

但现有实现仍然是“用户自定义分类/标签”模型，无法满足以下目标：

- 分类体系固定，不允许普通用户随意创建或修改
- 标签必须属于预定义 taxonomy
- 用户只能从批准集合中选择标签
- AI 推荐结果必须映射到 taxonomy 内，不能直接产生系统外标签
- 支持后续由多个 AI/多个子任务并行开发

### 1.2 本次改造目标

本次改造要实现：

1. 预定义分类树
2. 预定义标签池，且标签从属于分类
3. 后台文章编辑页使用 taxonomy 选择器
4. 后端文章发布/更新接口只接受合法 `categoryId + tagIds`
5. AI 标签推荐输出结构化候选，并受 taxonomy 约束
6. 所有工作整理为单一任务文档，便于 AI 多线程并行开发

---

## 2. 当前系统现状与差距

### 2.1 现有基础能力

当前仓库已有以下能力：

- 数据表：`t_category`、`t_tag`、`t_article_category_rel`、`t_article_tag_rel`
- 管理端标签模糊搜索接口
- 管理端分类下拉接口
- AI 标签推荐 / AI 分类推荐接口
- 文章发布和更新能力

### 2.2 当前关键问题

#### 问题 1：发布文章仍支持自动创建分类
现有发布逻辑支持：
- 传 `categoryId`
- 或不传 `categoryId`，传 `categoryName` 后自动创建分类

这与 taxonomy 固定体系冲突。

#### 问题 2：发布文章仍支持自动创建标签
现有文章发布/更新链路中，标签是 `List<String>`，并允许：
- 提交标签 ID 字符串
- 提交标签名字符串
- 标签不存在时自动新建标签

这与“只能选择预定义标签”冲突。

#### 问题 3：前端标签选择器允许自由创建
编辑页当前标签选择使用 Element Plus 的远程多选，并开启了 `allow-create`，这会让用户直接输入新标签。

#### 问题 4：AI 推荐结果未经过 taxonomy 收敛
现有 AI 推荐更像“文本建议”，缺少：
- taxonomy 映射层
- 分类上下文
- 合法性校验
- 接受/拒绝审核流程

---

## 3. 总体实施范围

### 3.1 本次必须完成

1. taxonomy 数据结构扩展
2. taxonomy 查询与搜索接口
3. 文章发布/更新接口改造
4. 文章编辑页改用 taxonomy 选择方式
5. AI 推荐结果 taxonomy 化
6. 单文档任务拆解，适配 AI 并行开发

### 3.2 本次可以后补

1. taxonomy 后台完整管理台
2. AI 采纳反馈持久化
3. 推荐准确率分析面板
4. 高级同义词/别名治理工具
5. taxonomy 运维导入后台

---

## 4. 核心设计决策

### 4.1 采用现有表扩展方案

不新建独立 taxonomy 表，优先扩展：

- `t_category`
- `t_tag`

新增字段支持：
- 层级
- 归属分类
- 系统预定义标记
- 启停状态
- 排序
- 编码
- 标签别名

这样对现有仓库侵入最小。

### 4.2 taxonomy 的最小模型

第一阶段采用两层正式模型：

- 分类 category
- 标签 tag

其中：
- category 支持 `parent_id`，可表达多层级
- tag 必须有 `category_id`

### 4.3 后端强约束优先于前端约束

前端只能辅助限制，真正的安全边界必须在后端：

- 发布文章时只接受合法 `categoryId`
- 发布文章时只接受合法 `tagIds`
- 不再接受自由文本 `tags`
- 不再接受 `categoryName` 自动建类

### 4.4 AI 推荐采用“两段式”

1. AI 输出语义候选
2. 服务端映射到 taxonomy

映射不到的项单独输出 `unmatchedTerms`，不直接入库。

---

## 5. 全量任务拆解

下面按模块拆全量任务。

---

### 5.1 数据库任务

#### DB-1 扩展 `t_category`
新增字段：
- `parent_id`
- `code`
- `level`
- `sort`
- `is_system`
- `status`

#### DB-2 扩展 `t_tag`
新增字段：
- `category_id`
- `code`
- `alias_json`
- `sort`
- `is_system`
- `status`

#### DB-3 新增索引
包括但不限于：
- 分类：`parent_id`、`status`、`code`
- 标签：`category_id`、`status`、`code`、`name`

#### DB-4 初始化 taxonomy 数据
准备最小可用分类树与标签池：
- 技术 / 前端 / Vue3、Vite
- 技术 / 后端 / Spring Boot
- 生活 / 阅读 / 书单 等

#### DB-5 旧数据兼容与迁移
- 给旧分类补默认层级
- 给旧标签补默认状态
- 导出现有标签，准备人工归类
- 统计 `category_id IS NULL` 的标签，作为待治理清单

---

### 5.2 DO / DAO / Mapper 层任务

#### DAO-1 扩展 `CategoryDO`
新增字段：
- `parentId`
- `code`
- `level`
- `sort`
- `isSystem`
- `status`

#### DAO-2 扩展 `TagDO`
新增字段：
- `categoryId`
- `code`
- `aliasJson`
- `sort`
- `isSystem`
- `status`

#### DAO-3 新建 `AdminTaxonomyDao`
需要提供：
- 查询全部分类
- 查询启用分类
- 按 ID 查询分类
- 按分类查询标签
- taxonomy 搜索标签
- 批量按 ID 查询标签

#### DAO-4 新建 `AdminTaxonomyDaoImpl`
按现有 MyBatis Plus + QueryWrapper 风格实现。

---

### 5.3 VO / DTO 层任务

#### VO-1 taxonomy 查询 VO
新增：
- `QueryCategoryTreeReqVO`
- `QueryTagByCategoryReqVO`
- `SearchTaxonomyTagReqVO`
- `ValidateTagReqVO`

#### VO-2 taxonomy 响应 DTO
新增：
- `CategoryTreeItemRspVO`
- `TaxonomyTagItemRspVO`
- `ValidateTagRspVO`

#### VO-3 文章请求 VO 改造
改造：
- `PublishArticleReqVO`
- `UpdateArticleReqVO`

替换内容：
- 删除 `categoryName`
- 删除 `List<String> tags`
- 改成 `List<Long> tagIds`

#### VO-4 文章详情回显 DTO 增强
新增或扩展：
- `ArticleTagItemRspVO`
- `ArticleCategoryRspVO`
- `ArticleDetailRspVO`（如项目已有对应 VO，则做字段增强）

#### VO-5 AI 推荐返回 DTO 增强
新增：
- `AiRecommendTaxonomyTagItemRspVO`
- 扩展 `AiRecommendTagRspVO`

---

### 5.4 Controller 层任务

#### API-1 新建 `AdminTaxonomyController`
新增接口：
- `POST /admin/taxonomy/category/tree`
- `POST /admin/taxonomy/tag/list-by-category`
- `POST /admin/taxonomy/tag/search`
- `POST /admin/taxonomy/tag/validate`

#### API-2 保持旧标签接口并逐步弱化
旧接口：
- `/admin/tag/search`
- `/admin/tag/select/list`

处理策略：
- 兼容期保留
- 新编辑页切换到 taxonomy 接口
- 后续逐步废弃

---

### 5.5 Service 层任务

#### SVC-1 新建 `AdminTaxonomyService`
职责：
- 分类树查询
- 分类下标签分页
- taxonomy 搜索标签
- 批量校验标签

#### SVC-2 新建 `AdminTaxonomyServiceImpl`
实现以上查询能力。

#### SVC-3 新建 `ArticleTaxonomyValidateService`
职责：
- 校验分类存在且启用
- 校验标签存在且启用
- 校验标签属于指定分类
- 提供 `validateArticleTaxonomy(categoryId, tagIds)` 封装方法

#### SVC-4 新建 `ArticleTaxonomyValidateServiceImpl`
为文章发布/更新提供统一校验入口。

#### SVC-5 可选新建 `TaxonomyAiMappingService`
职责：
- 把 AI 生成的关键词候选映射到 taxonomy 中的标签
- 产出结构化推荐结果
- 识别未命中项

---

### 5.6 文章发布/更新链路改造任务

#### ART-1 改造 `PublishArticleReqVO`
要求：
- 必须传 `categoryId`
- 必须传 `tagIds`
- 不允许 `categoryName`
- 不允许自由 `tags`

#### ART-2 改造 `UpdateArticleReqVO`
要求：
- `tagIds` 替代 `tags`

#### ART-3 改造 `AdminArticleServiceImpl#publishArticle`
替换逻辑：
- 删除 `resolveCategoryId(...)`
- 删除分类自动创建逻辑
- 删除标签自动创建逻辑
- 新增 `validateArticleTaxonomy(...)`
- 新增 `saveArticleCategoryRelation(...)`
- 新增 `saveArticleTagRelations(...)`

#### ART-4 改造 `AdminArticleServiceImpl#updateArticle`
替换逻辑：
- 更新前校验 categoryId/tagIds
- 删除旧标签关系后重建
- 不允许自动创建标签

#### ART-5 废弃旧私有方法
重点处理：
- `resolveCategoryId(...)`
- `handleTagBiz(...)`

---

### 5.7 AI 标签/分类推荐任务

#### AI-1 扩展 `AiRecommendTagRspVO`
新增字段：
- `suggestions`
- `unmatchedTerms`
- `estimatedTokens`
- `actualTokens`

#### AI-2 推荐结果结构化
每条标签建议包含：
- `tagId`
- `tagName`
- `categoryId`
- `categoryName`
- `categoryPath`
- `confidence`
- `reason`
- `status`

#### AI-3 服务端 taxonomy 映射层
把 AI 原始候选映射到：
- 标签名
- 标签 code
- 标签 alias_json
- 可选分类上下文

#### AI-4 前端推荐审核能力
支持：
- 接受
- 拒绝
- 批量接受
- 未匹配项提示

---

### 5.8 前端任务

#### FE-1 改造文章编辑页标签选择器
目标：
- 去掉 `allow-create`
- 改为 taxonomy 选择方式

#### FE-2 新建 taxonomy 标签选择组件
建议：
- `TaxonomyTagSelector.vue`
- `TaxonomyCategorySelect.vue`

能力包括：
- 分类树浏览
- 分类下标签展示
- 搜索
- 自动补全
- 已选标签展示

#### FE-3 改造 AI 助手推荐区
增强：
- 展示标签所属分类
- 接受/拒绝
- 已接受同步进已选标签

#### FE-4 文章详情回显适配
使用新的 `tagIds + tags[]` 回显结构。

---

### 5.9 测试任务

#### QA-1 数据层测试
- 新字段写入正常
- taxonomy 初始化成功
- 旧数据兼容

#### QA-2 后端接口测试
- 分类树接口
- 分类下标签接口
- taxonomy 搜索接口
- 批量校验接口
- 发布/更新文章校验接口

#### QA-3 前端交互测试
- 无法自由创建标签
- 标签搜索正常
- 标签分类归属显示正常
- AI 推荐接受/拒绝流程正常

#### QA-4 回归测试
- 前台标签页不异常
- 前台分类页不异常
- 已有文章详情正常

---

## 6. AI 多线程并行开发拆包方案

这一节是本文件最重要的部分之一，用于支持多个 AI / 多个子会话并行开发。

原则：
- 每个工作包尽量避免同时改同一文件
- 若必须改同一文件，安排串行
- 先做底座，再做依赖层

---

### 工作包 A：数据库与 DO 底座

#### 任务范围
- SQL 迁移脚本
- `CategoryDO`
- `TagDO`
- 数据初始化 SQL

#### 主要文件
- `create_table.sql` 或新迁移 SQL 文件
- `CategoryDO.java`
- `TagDO.java`

#### 是否适合并行
可以并行，但应避免与工作包 C 同时改 DO。

#### 产出物
- SQL 初稿
- DO 扩展版代码

---

### 工作包 B：taxonomy 查询链路

#### 任务范围
- taxonomy VO
- `AdminTaxonomyController`
- `AdminTaxonomyService`
- `AdminTaxonomyServiceImpl`
- `AdminTaxonomyDao`
- `AdminTaxonomyDaoImpl`

#### 主要文件
- `admin/model/vo/taxonomy/*`
- `AdminTaxonomyController.java`
- `AdminTaxonomyService.java`
- `AdminTaxonomyServiceImpl.java`
- `AdminTaxonomyDao.java`
- `AdminTaxonomyDaoImpl.java`

#### 依赖
依赖工作包 A 中的 DO 字段准备好。

#### 是否适合并行
非常适合单独并行开发。

---

### 工作包 C：文章发布/更新链路改造

#### 任务范围
- `PublishArticleReqVO`
- `UpdateArticleReqVO`
- `ArticleTaxonomyValidateService`
- `ArticleTaxonomyValidateServiceImpl`
- `AdminArticleServiceImpl` 改造

#### 主要文件
- `PublishArticleReqVO.java`
- `UpdateArticleReqVO.java`
- `ArticleTaxonomyValidateService.java`
- `ArticleTaxonomyValidateServiceImpl.java`
- `AdminArticleServiceImpl.java`

#### 依赖
依赖工作包 A 的 DO 字段；可依赖工作包 B 的 `AdminTaxonomyDao`。

#### 并行注意
这个包会改核心发布逻辑，不建议再让别的包同时改 `AdminArticleServiceImpl.java`。

---

### 工作包 D：AI taxonomy 推荐链路

#### 任务范围
- 扩展 AI 推荐 DTO
- `TaxonomyAiMappingService`
- `AdminAiServiceImpl` 中标签推荐逻辑改造

#### 主要文件
- `AiRecommendTagRspVO.java`
- `AiRecommendTaxonomyTagItemRspVO.java`
- `TaxonomyAiMappingService.java`
- `TaxonomyAiMappingServiceImpl.java`
- `AdminAiServiceImpl.java`

#### 依赖
依赖工作包 B 的 taxonomy 查询能力。

#### 是否适合并行
适合并行，但不要与其他人同时修改 `AdminAiServiceImpl.java`。

---

### 工作包 E：前端 taxonomy 选择器

#### 任务范围
- taxonomy 选择器组件
- taxonomy API 封装
- 编辑页标签交互改造

#### 主要文件
- `src/api/admin/taxonomy.js`（建议新增）
- `TaxonomyTagSelector.vue`
- `TaxonomyCategorySelect.vue`
- `article-list.vue`

#### 依赖
依赖工作包 B 提供新接口。

#### 是否适合并行
适合独立前端并行开发。

---

### 工作包 F：前端 AI 推荐交互改造

#### 任务范围
- `AiAssistant.vue` 推荐区改造
- AI 推荐结果接入 taxonomy 展示
- 接受/拒绝交互

#### 主要文件
- `AiAssistant.vue`
- `src/api/admin/ai.js`

#### 依赖
依赖工作包 D 的 AI 接口返回结构。

#### 是否适合并行
适合并行，但不要与其他人同时改 `AiAssistant.vue`。

---

### 工作包 G：测试与回归

#### 任务范围
- 后端接口联调测试
- 前端交互验证
- 回归已有标签/分类/文章详情
- 产出验收清单

#### 依赖
依赖前面各包基本完成。

#### 是否适合并行
适合在后半程并行开展。

---

## 7. 任务依赖关系与执行顺序

### 串行依赖主链

1. 工作包 A：数据库与 DO 底座
2. 工作包 B：taxonomy 查询链路
3. 工作包 C：文章发布/更新链路改造
4. 工作包 D：AI taxonomy 推荐链路
5. 工作包 E：前端 taxonomy 选择器
6. 工作包 F：前端 AI 推荐交互改造
7. 工作包 G：测试与回归

### 可并行建议

#### 第一批并行
- A：数据库与 DO 底座
- 文档整理 / 验收标准准备

#### 第二批并行
- B：taxonomy 查询链路
- E：前端 taxonomy 选择器的静态组件骨架

#### 第三批并行
- C：文章发布/更新链路改造
- D：AI taxonomy 推荐链路

#### 第四批并行
- F：前端 AI 推荐交互改造
- G：测试用例与回归清单准备

---

## 8. 文件级改动清单

### 后端新增文件

- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/taxonomy/QueryCategoryTreeReqVO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/taxonomy/CategoryTreeItemRspVO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/taxonomy/QueryTagByCategoryReqVO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/taxonomy/SearchTaxonomyTagReqVO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/taxonomy/TaxonomyTagItemRspVO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/taxonomy/ValidateTagReqVO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/taxonomy/ValidateTagRspVO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/controller/AdminTaxonomyController.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/service/AdminTaxonomyService.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/service/ArticleTaxonomyValidateService.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/service/TaxonomyAiMappingService.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/service/impl/AdminTaxonomyServiceImpl.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/service/impl/ArticleTaxonomyValidateServiceImpl.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/service/impl/TaxonomyAiMappingServiceImpl.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/dao/AdminTaxonomyDao.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/dao/impl/AdminTaxonomyDaoImpl.java`

### 后端改造文件

- `weblog-module-common/src/main/java/com/quanxiaoha/weblog/common/domain/dos/CategoryDO.java`
- `weblog-module-common/src/main/java/com/quanxiaoha/weblog/common/domain/dos/TagDO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/article/PublishArticleReqVO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/article/UpdateArticleReqVO.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/service/impl/AdminArticleServiceImpl.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/service/impl/AdminAiServiceImpl.java`
- `weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/ai/AiRecommendTagRspVO.java`
- `weblog-module-common/src/main/java/com/quanxiaoha/weblog/common/enums/ResponseCodeEnum.java`

### 前端新增文件

- `weblog-vue3/src/api/admin/taxonomy.js`
- `weblog-vue3/src/components/TaxonomyTagSelector.vue`
- `weblog-vue3/src/components/TaxonomyCategorySelect.vue`

### 前端改造文件

- `weblog-vue3/src/pages/admin/article-list.vue`
- `weblog-vue3/src/components/AiAssistant.vue`
- `weblog-vue3/src/api/admin/ai.js`

---

## 9. API 与数据库落地清单

### 9.1 API 清单

#### taxonomy API
- `POST /admin/taxonomy/category/tree`
- `POST /admin/taxonomy/tag/list-by-category`
- `POST /admin/taxonomy/tag/search`
- `POST /admin/taxonomy/tag/validate`

#### 文章 API 调整
- `POST /admin/article/publish` 请求体调整为 `categoryId + tagIds`
- `POST /admin/article/update` 请求体调整为 `categoryId + tagIds`

#### AI API 调整
- `POST /admin/ai/tag/recommend` 响应结构调整
- `POST /admin/ai/category/recommend` 可选增强 categoryPath / confidence / reason

### 9.2 数据库落地清单

#### `t_category`
新增：
- `parent_id`
- `code`
- `level`
- `sort`
- `is_system`
- `status`

#### `t_tag`
新增：
- `category_id`
- `code`
- `alias_json`
- `sort`
- `is_system`
- `status`

---

## 10. 验收标准

### 10.1 后端验收

1. taxonomy 分类树接口可用
2. taxonomy 标签搜索接口可用
3. 非法 `tagIds` 提交发布失败
4. 停用标签提交发布失败
5. 标签不属于分类时提交失败
6. 不再自动创建分类/标签

### 10.2 前端验收

1. 编辑页不能自由创建标签
2. 可按分类浏览标签
3. 可按关键字搜索标签
4. 搜索结果能显示分类归属
5. AI 推荐能接受/拒绝
6. 接受后可同步进入已选标签

### 10.3 AI 验收

1. 推荐结果结构化
2. 推荐结果在 taxonomy 内
3. 映射不到的项不会直接进系统

### 10.4 回归验收

1. 旧文章正常读取
2. 前台标签页正常
3. 前台分类页正常
4. 文章详情正常

---

## 11. 风险与回滚策略

### 风险 1：旧数据里存在大量脏标签

处理：
- 先做统计清单
- 先不强制一次清洗完
- 对新文章强约束，对旧文章逐步迁移

### 风险 2：核心发布链路改动大

处理：
- 先保留旧接口兼容开发分支验证
- 待前端切换后再完全关闭旧逻辑

### 风险 3：多 AI 并行开发冲突

处理：
- 明确文件边界
- `AdminArticleServiceImpl.java`、`AdminAiServiceImpl.java`、`article-list.vue` 这类核心文件只允许一个工作包同时修改

### 风险 4：AI 推荐准确率不稳定

处理：
- 第一版优先保证“合法性”，其次再提升“准确率”

---

## 12. 建议的开发协作方式

### 12.1 适合 AI 多线程开发的推荐分工

#### AI-Worker-1
负责：工作包 A
- SQL
- DO
- 初始化数据

#### AI-Worker-2
负责：工作包 B
- taxonomy 后端查询链路

#### AI-Worker-3
负责：工作包 C
- 发布/更新接口与 `AdminArticleServiceImpl`

#### AI-Worker-4
负责：工作包 D
- AI taxonomy 映射与推荐返回结构

#### AI-Worker-5
负责：工作包 E
- taxonomy 前端选择器与编辑页改造

#### AI-Worker-6
负责：工作包 F + G
- AI 推荐前端交互
- 联调与回归清单

### 12.2 合并顺序建议

1. 先合并 A
2. 再合并 B
3. 再合并 C
4. 再合并 D
5. 再合并 E
6. 再合并 F
7. 最后做 G 验收

### 12.3 每个工作包的完成定义

每个工作包提交前必须满足：

1. 代码能编译
2. 本包新增接口可自测
3. 不引入自由标签创建回归
4. 更新本文档中的状态与产出清单

---

## 附录：建议的第一轮执行顺序

### 第一天
- 工作包 A：SQL + DO
- 工作包 B：taxonomy 查询骨架

### 第二天
- 工作包 C：文章发布/更新改造
- 工作包 E：前端 taxonomy 选择器骨架

### 第三天
- 工作包 D：AI taxonomy 映射
- 工作包 F：AI 推荐前端接入

### 第四天
- 工作包 G：联调、回归、修正

---

## 结论

这份文档的用途不是“说明想法”，而是直接作为 taxonomy 分类标签系统改造的总任务单。

它同时满足：

1. 单文档汇总所有任务
2. 明确数据库 / 后端 / 前端 / AI / 测试分工
3. 支持 AI 多线程并行开发
4. 明确依赖关系与合并顺序
5. 可直接作为后续执行蓝图
