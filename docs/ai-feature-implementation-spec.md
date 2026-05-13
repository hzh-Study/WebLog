# WebLog AI 功能实施说明

## 1. 文档目的

本文档用于指导 AI 编码助手或开发者在当前 `WeBlog` 仓库中落地一组 AI 相关功能。

要求：

- 以现有代码为基础实施，不要推翻现有架构。
- 优先复用当前已有 AI 后端能力。
- 工作量主要体现在前端体验、页面编排、组件拆分、交互流程和状态管理上。
- 新增后端能力仅限于确有必要且接口边界清晰的部分。
- 遵循仓库现有技术栈、目录结构、接口风格和编码约定。

本文档的目标不是讨论方案，而是提供一份可直接执行的任务说明。拿到本文档后，AI 应能按顺序完成开发工作。

---

## 2. 项目上下文

### 2.1 仓库结构

- 后端：`weblog-springboot/`
- 前端：`weblog-vue3/`
- 文档目录：`docs/`

### 2.2 后端技术栈

- Java 8
- Spring Boot 2.6.3
- Maven 多模块
- MyBatis Plus
- JWT 鉴权

### 2.3 前端技术栈

- Vue 3
- Vite
- Vue Router
- Vuex
- Element Plus
- `md-editor-v3`

### 2.4 当前 AI 能力现状

当前后台已具备以下 AI 接口，位于：

- `weblog-springboot/weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/controller/AdminAiController.java`

现有接口：

- `POST /admin/ai/quota`
- `POST /admin/ai/token/estimate`
- `POST /admin/ai/article/generate`
- `POST /admin/ai/article/continue`
- `POST /admin/ai/article/rewrite`
- `POST /admin/ai/article/polish`
- `POST /admin/ai/seo/optimize`
- `POST /admin/ai/tag/recommend`
- `POST /admin/ai/category/recommend`

现有前端 AI API 封装位于：

- `weblog-vue3/src/api/admin/ai.js`

现有 AI 相关前端组件：

- `weblog-vue3/src/components/AiAssistant.vue`
- `weblog-vue3/src/components/AiArticleCreator.vue`
- `weblog-vue3/src/components/AiEditToolbar.vue`
- `weblog-vue3/src/components/AiRewriteDialog.vue`
- `weblog-vue3/src/components/AiSeoPanel.vue`

现有文章编辑页位于：

- `weblog-vue3/src/pages/admin/article-list.vue`

现有文章详情页位于：

- `weblog-vue3/src/pages/frontend/article-detail.vue`

---

## 3. 产品目标

本次 AI 功能建设分为三期：

### 一期目标

实现后台独立 `AI 工作台`，把现有零散 AI 能力产品化，让管理员可以集中完成：

- AI 写文章
- AI 改写
- AI 润色
- AI 续写
- AI SEO 优化
- AI 标签推荐
- AI 分类推荐
- Token 预估
- 配额查看

### 二期目标

将 AI 工作台能力进一步融入文章编辑流程，让文章发布/编辑页面具备更完整、更顺滑的 AI 辅助体验。

### 三期目标

给前台文章详情页增加读者向 AI 能力，包括：

- AI 摘要
- 关键观点提炼
- 基于文章内容的问答

---

## 4. 范围约束

### 4.1 必须遵守

- 优先复用现有后端接口和 VO。
- 尽量不要修改现有管理端文章发布、更新主流程逻辑。
- 新功能必须兼容现有路由、鉴权、Axios 封装和 `Response.success(...)` 返回格式。
- 代码风格保持与仓库已有风格一致。
- Java 代码必须兼容 Java 8。

### 4.2 本次不做

- 不做流式输出。
- 不做 WebSocket。
- 不接入新的第三方前端状态库。
- 不重写现有后台整体布局。
- 不改动 JWT 鉴权体系。
- 不做复杂的 AI 历史会话数据库设计。

---

## 5. 现有能力与可复用字段

### 5.1 文章生成请求

文件：

- `weblog-springboot/weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/ai/AiGenerateArticleReqVO.java`

字段：

- `prompt`
- `title`
- `categoryName`
- `tags`

### 5.2 文章生成响应

文件：

- `weblog-springboot/weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/ai/AiGenerateArticleRspVO.java`

字段：

- `title`
- `content`
- `estimatedTokens`
- `actualTokens`

### 5.3 AI 编辑响应

文件：

- `weblog-springboot/weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/ai/AiEditRspVO.java`

字段：

- `result`
- `estimatedTokens`
- `actualTokens`

### 5.4 配额响应

文件：

- `weblog-springboot/weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/ai/AiQuotaRspVO.java`

字段：

- `dailyArticleGenLimit`
- `dailyArticleGenUsed`
- `dailyArticleGenRemaining`
- `dailyTokenLimit`
- `dailyTokenUsed`
- `dailyTokenRemaining`
- `lastInteractionTime`
- `interactionIntervalSeconds`
- `canInteractNow`
- `remainingCooldownSeconds`

### 5.5 SEO 响应

文件：

- `weblog-springboot/weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/ai/AiSeoOptimizeRspVO.java`

字段：

- `optimizedTitle`
- `optimizedDescription`
- `keywords`
- `titleScore`
- `titleSuggestion`
- `estimatedTokens`
- `actualTokens`

### 5.6 文章发布请求

文件：

- `weblog-springboot/weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/model/vo/article/PublishArticleReqVO.java`

字段：

- `title`
- `content`
- `titleImage`
- `description`
- `categoryId`
- `categoryName`
- `tags`
- `visibility`

这些字段决定了 AI 生成后的结果如何应用到文章编辑器和文章发布表单中。

---

## 6. 一期实施目标：后台 AI 工作台

### 6.1 功能目标

新增后台页面 `AI 工作台`，统一承载现有 AI 功能，避免功能散落在文章管理页中。

### 6.2 页面入口

需要新增：

- 路由：`/admin/ai/workbench`
- 后台菜单入口：`AI 工作台`

需要修改的文件：

- `weblog-vue3/src/router/index.js`
- `weblog-vue3/src/layouts/components/AdminMenu.vue`

### 6.3 页面文件

新增页面：

- `weblog-vue3/src/pages/admin/ai-workbench.vue`

### 6.4 页面布局建议

页面建议分为以下 5 个区域：

1. 顶部概览区
2. AI 写作区
3. AI 改写/润色/续写区
4. AI SEO 优化区
5. AI 分类/标签推荐区

### 6.5 顶部概览区要求

展示内容：

- 今日文章生成额度
- 今日 Token 剩余额度
- 冷却倒计时
- 刷新额度按钮
- 当前是否可调用 AI

要求：

- 页面初始化时自动调用 `getAiQuota()`
- 支持手动刷新
- 若 `canInteractNow` 为 `false`，需显式显示冷却提示
- 若剩余额度不足，生成按钮应禁用

### 6.6 AI 写作区要求

目标：基于提示词生成完整文章。

输入项：

- 写作要求 `prompt`
- 文章标题 `title`，可选
- 分类 `categoryName`，可选
- 标签 `tags`，可选

行为要求：

- 输入变化后可手动触发 Token 预估
- 生成前需校验 `prompt` 非空
- 点击生成后调用 `generateArticle`
- 生成后展示：
  - 标题
  - 正文预览
  - 预估 token
  - 实际消耗 token

结果操作：

- 一键复制标题
- 一键复制正文
- 一键填充到文章编辑器
- 一键保存为新文章
- 一键保存为草稿

注意：

- 若工作台独立于文章编辑页，可先支持“保存文章”和“保存草稿”
- “填充到文章编辑器”可以作为二期联动能力

### 6.7 AI 改写/润色/续写区要求

目标：对已有文本进行加工。

输入项：

- 原始文本
- 操作类型：改写 / 润色 / 续写
- 附加指令，可选

界面要求：

- 左栏展示原文
- 右栏展示 AI 结果
- 保留现有对比式体验，但增强成统一工作区

行为要求：

- 改写调用 `rewriteText`
- 润色调用 `polishText`
- 续写调用 `continueArticle`

结果操作：

- 替换全文
- 复制结果
- 追加到原文末尾
- 将结果发送到写作区作为二次编辑素材

### 6.8 AI SEO 优化区要求

输入项：

- 标题
- 正文
- 当前摘要，可选

调用：

- `aiSeoOptimize`

结果展示：

- 优化标题
- 优化摘要
- 关键词
- 标题评分
- 标题建议
- token 信息

结果操作：

- 应用优化标题
- 应用优化摘要
- 复制关键词

### 6.9 AI 分类/标签推荐区要求

输入项：

- 正文内容
- 推荐要求，可选

分类推荐：

- 调用 `recommendCategories`
- 展示推荐列表
- 可一键加入当前草稿分类候选

标签推荐：

- 调用 `recommendTags`
- 展示推荐标签
- 可一键加入当前标签列表

交互要求：

- 已添加项需明确标识
- 重复项不能重复加入

---

## 7. 一期建议的前端目录拆分

为了避免继续堆积在单文件中，建议将 AI 组件拆到独立目录。

新增目录：

- `weblog-vue3/src/components/ai/`

建议新增组件：

- `AiQuotaCard.vue`
- `AiWriterPanel.vue`
- `AiRewritePanel.vue`
- `AiSeoWorkbenchPanel.vue`
- `AiRecommendPanel.vue`
- `AiResultPreview.vue`

建议新增 composable：

- `weblog-vue3/src/composables/useAiTask.js`

该 composable 统一处理：

- 请求 loading
- 超时提示
- 失败提示
- token 信息提取
- 配额刷新
- 冷却判断

---

## 8. 二期实施目标：文章编辑器 AI 体验升级

### 8.1 目标

将 `article-list.vue` 中现有零散 AI 入口升级为统一 AI 助手。

当前已存在：

- 顶部 `AI 写作` 按钮
- `AiEditToolbar`
- `AiRewriteDialog`
- `AiSeoPanel`

二期需要完成的事情：

- 将发布文章弹窗和编辑文章弹窗中的 AI 能力统一到一个侧边面板
- 对正文内容进行上下文感知
- 支持当前标题、摘要、正文与 AI 工作区联动

### 8.2 需要修改的页面

- `weblog-vue3/src/pages/admin/article-list.vue`

### 8.3 主要交互改造点

- 点击 `AI 写作` 按钮，打开统一侧边面板
- 面板自动读取当前表单中的：
  - 标题
  - 正文
  - 摘要
  - 分类
  - 标签
- AI 结果可直接写回当前表单

### 8.4 必做能力

- 依据当前正文推荐标签
- 依据当前正文推荐分类
- 依据当前标题+正文生成 SEO 建议
- 依据当前正文做续写
- 依据当前选中文本做改写或润色

### 8.5 可选增强

- 摘要自动生成
- 根据标题生成大纲
- 根据大纲扩写段落

这部分如果后端当前不支持，可先在文档中预留，不作为必须交付。

---

## 9. 三期实施目标：前台文章详情页 AI 能力

### 9.1 目标

面向读者新增文章理解辅助能力，而不是继续只服务管理员。

### 9.2 目标页面

- `weblog-vue3/src/pages/frontend/article-detail.vue`

### 9.3 需要新增的读者能力

1. AI 摘要
2. 关键观点提炼
3. 文章问答

### 9.4 页面建议布局

建议在文章正文上方或正文右侧新增 `AI 阅读助手` 区块。

区块内容：

- 摘要卡片
- 关键观点卡片
- 问答输入框和回答区

### 9.5 前端体验要求

- 摘要支持折叠/展开
- 关键观点展示为列表或卡片
- 问答区域需展示提问和回答
- 回答失败时可重试
- 加载中状态要明显

---

## 10. 三期后端最小新增范围

三期允许新增少量面向前台的 AI 接口，但必须控制范围。

建议新增 controller：

- `weblog-springboot/weblog-web/src/main/java/com/quanxiaoha/weblog/web/controller/ArticleAiController.java`

建议新增 service：

- `weblog-springboot/weblog-web/src/main/java/com/quanxiaoha/weblog/web/service/ArticleAiService.java`
- `weblog-springboot/weblog-web/src/main/java/com/quanxiaoha/weblog/web/service/impl/ArticleAiServiceImpl.java`

建议新增接口：

- `POST /article/ai/summary`
- `POST /article/ai/key-points`
- `POST /article/ai/qa`

### 10.1 请求方式建议

为避免前端传整篇文章正文，建议接口统一按 `articleId` 工作。

推荐请求结构：

- `articleId`
- `question`，仅问答接口需要

### 10.2 响应结构建议

摘要接口：

- `summary`

关键观点接口：

- `points`

问答接口：

- `answer`
- `references`，可选

### 10.3 实施要求

- 后端根据 `articleId` 查询文章正文
- AI 回答严格限制在文章上下文内
- 问答回答中禁止编造成文外信息
- 所有接口返回统一 `Response.success(...)`

---

## 11. 文件级任务拆分

以下任务应按顺序执行。

### 任务 A：新增后台 AI 工作台入口

修改：

- `weblog-vue3/src/router/index.js`
- `weblog-vue3/src/layouts/components/AdminMenu.vue`

要求：

- 新增 `/admin/ai/workbench`
- 菜单项文案为 `AI 工作台`
- 图标使用现有 Element Plus 图标

验收：

- 登录后台后可以从左侧菜单进入 AI 工作台

### 任务 B：新增后台 AI 工作台页面

新增：

- `weblog-vue3/src/pages/admin/ai-workbench.vue`

要求：

- 页面具备完整布局
- 能正常调用现有 `src/api/admin/ai.js` 接口
- 具备加载态、错误态、空态

验收：

- 页面可独立使用，不依赖文章管理页也能完成主要 AI 操作

### 任务 C：抽离 AI 通用组件

新增：

- `weblog-vue3/src/components/ai/*`

要求：

- 不再让所有 AI 逻辑都堆在一个页面文件中
- 组件 props / emits 设计清晰
- 可复用于工作台和文章编辑页

验收：

- `ai-workbench.vue` 和 `article-list.vue` 都能复用这些组件

### 任务 D：新增 AI 通用逻辑 composable

新增：

- `weblog-vue3/src/composables/useAiTask.js`

要求：

- 封装通用请求状态
- 封装 quota 拉取与刷新
- 封装统一提示

验收：

- 页面中不再充斥重复的 loading/error 逻辑

### 任务 E：改造文章管理页 AI 体验

修改：

- `weblog-vue3/src/pages/admin/article-list.vue`

要求：

- 统一 AI 助手入口
- AI 结果可回填标题、摘要、正文、分类、标签
- 保持原有文章发布、更新功能可用

验收：

- 发布文章和编辑文章流程均可正常调用 AI
- 不引入已有功能回归

### 任务 F：新增前台 AI API 封装

新增：

- `weblog-vue3/src/api/frontend/ai.js`

要求：

- 封装摘要、关键观点、问答接口

验收：

- 前台页面无需直接写 Axios 请求

### 任务 G：新增前台文章 AI 助手

修改：

- `weblog-vue3/src/pages/frontend/article-detail.vue`

新增建议组件：

- `weblog-vue3/src/components/frontend/ArticleAiAssistant.vue`

要求：

- 页面展示 AI 摘要、关键观点、问答
- 保持现有文章详情页布局风格一致

验收：

- 文章详情页中出现 AI 阅读助手
- 问答可用

### 任务 H：新增前台 AI 后端接口

新增后端文件建议：

- `weblog-springboot/weblog-web/src/main/java/com/quanxiaoha/weblog/web/controller/ArticleAiController.java`
- `weblog-springboot/weblog-web/src/main/java/com/quanxiaoha/weblog/web/service/ArticleAiService.java`
- `weblog-springboot/weblog-web/src/main/java/com/quanxiaoha/weblog/web/service/impl/ArticleAiServiceImpl.java`
- 对应请求/响应 VO

要求：

- 沿用项目统一 `Response` 返回
- 出错时抛 `BizException` 或返回项目统一错误结构

验收：

- 前台 `AI 摘要`、`关键观点`、`问答` 均可联调成功

---

## 12. 推荐执行顺序

按以下顺序实施：

1. 完成任务 A
2. 完成任务 B
3. 完成任务 C
4. 完成任务 D
5. 完成任务 E
6. 自测后台 AI 工作台与文章编辑联动
7. 完成任务 F
8. 完成任务 H
9. 完成任务 G
10. 自测前台文章 AI 阅读助手

原因：

- 先完成后台工作台，可以最大化复用现有能力
- 再升级文章编辑页，减少重复造轮子
- 最后做前台读者功能，避免早期引入不必要的后端改造

---

## 13. 详细验收标准

### 13.1 一期验收标准

- 后台菜单中存在 `AI 工作台`
- 可以进入独立页面
- 页面能显示 AI 配额
- 页面能执行文章生成
- 页面能执行改写、润色、续写
- 页面能执行 SEO 优化
- 页面能推荐标签和分类
- 页面在额度不足、冷却中、请求失败时有明确提示

### 13.2 二期验收标准

- 文章发布弹窗中可使用统一 AI 助手
- 文章编辑弹窗中可使用统一 AI 助手
- AI 生成内容可回填表单
- 标签、分类推荐可直接应用
- SEO 结果可直接应用到标题和摘要

### 13.3 三期验收标准

- 文章详情页展示 AI 阅读助手
- AI 摘要可成功生成
- 关键观点可成功生成
- 用户可以对当前文章提问并拿到回答
- 回答过程有加载态和失败重试

---

## 14. UI 与交互要求

### 14.1 管理端

- 风格应与当前管理后台已有视觉一致
- 使用 Element Plus 组件优先
- 页面不要只堆按钮，要有明确分区
- AI 结果区要突出“可应用”和“非自动覆盖”

### 14.2 前台

- 风格必须与当前文章详情页现有设计一致
- 不要做成生硬的客服聊天窗口
- AI 助手区需要更像阅读增强模块

### 14.3 通用交互原则

- 所有 AI 行为必须有 loading 状态
- 所有异常必须有错误提示
- 所有结果应用动作要显式触发
- 默认不自动覆盖用户原文

---

## 15. 测试建议

### 15.1 前端自测

后台：

- 进入 AI 工作台
- 拉取 quota 成功
- quota 不足时按钮禁用
- prompt 为空时不能生成
- 生成结果展示正常
- 改写/润色/续写都能正常返回
- SEO 优化正常返回
- 推荐标签/分类正常返回

编辑页：

- 发布文章时 AI 回填不破坏现有表单
- 编辑文章时 AI 回填不破坏现有内容

前台：

- 文章详情页 AI 模块正常展示
- 摘要、关键观点、问答正常工作

### 15.2 后端自测

- 现有 `/admin/ai/*` 接口回归测试
- 新增 `/article/ai/*` 接口测试
- 空文章、无效文章 ID、AI 请求失败的异常处理测试

---

## 16. 风险与注意事项

### 16.1 风险一：前端逻辑继续堆积

避免方式：

- 必须拆组件
- 必须抽 composable

### 16.2 风险二：文章编辑页回归

避免方式：

- 不重写原有发布/更新提交流程
- AI 只作为辅助输入来源

### 16.3 风险三：前台问答幻觉

避免方式：

- 问答仅基于文章正文
- 不允许回答文外信息

### 16.4 风险四：配额与冷却交互不清晰

避免方式：

- 页面显式显示剩余额度和倒计时
- 调用前提前禁用按钮

### 16.5 风险五：AI 配置泄露

注意：

- 后端 `application.yaml` 当前存在默认 `AI_API_KEY`
- 上线前必须改为仅通过环境变量配置
- 前端禁止直接接触任何真实密钥

---

## 17. 交付完成定义

当以下条件全部满足时，可视为本任务完成：

- 后台 AI 工作台可用
- 文章编辑页 AI 辅助升级完成
- 前台文章 AI 阅读助手可用
- 主要路径完成自测
- 无明显功能回归

---

## 18. 给执行 AI 的具体指令

如果你是接手实施该任务的 AI，请按以下原则执行：

- 先阅读本文档，再阅读相关现有文件
- 优先复用已有 AI 接口，不重复造后端
- 优先做一期，再做二期，再做三期
- 修改代码时保持小步提交思路
- 每完成一个阶段就自测一次
- 不要在未确认现有结构前大规模重构
- 不要擅自引入与当前项目不一致的新框架

推荐先阅读这些文件：

- `weblog-vue3/src/pages/admin/article-list.vue`
- `weblog-vue3/src/api/admin/ai.js`
- `weblog-vue3/src/components/AiAssistant.vue`
- `weblog-vue3/src/components/AiRewriteDialog.vue`
- `weblog-vue3/src/components/AiSeoPanel.vue`
- `weblog-vue3/src/router/index.js`
- `weblog-vue3/src/layouts/components/AdminMenu.vue`
- `weblog-vue3/src/pages/frontend/article-detail.vue`
- `weblog-springboot/weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/controller/AdminAiController.java`

