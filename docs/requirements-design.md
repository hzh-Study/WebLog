# WeBlog 功能拓展需求设计文档

> **版本**: v1.0  
> **日期**: 2026-05-11  
> **选型方案**: A1 + A4 + C3 + C4 + D1  
> **主题**: 写作体验 + 数据分析 + 社交互动

---

## 目录

1. [项目概述与背景](#1-项目概述与背景)
2. [A1: AI 文章续写/改写/润色](#2-a1-ai-文章续写改写润色)
3. [A4: AI SEO 元数据优化](#3-a4-ai-seo-元数据优化)
4. [C3: 数据分析大屏](#4-c3-数据分析大屏)
5. [C4: 草稿箱 & 版本管理](#5-c4-草稿箱--版本管理)
6. [D1: 点赞/收藏/分享](#6-d1-点赞收藏分享)
7. [数据库设计汇总](#7-数据库设计汇总)
8. [API 接口设计汇总](#8-api-接口设计汇总)
9. [前端路由与组件规划](#9-前端路由与组件规划)
10. [实施优先级与里程碑](#10-实施优先级与里程碑)

---

## 1. 项目概述与背景

### 1.1 现有系统概况

WeBlog 是一个基于 **Java 8 + Spring Boot 2.6.3 + Vue 3 + Element Plus** 的个人博客系统。当前已具备以下核心能力：

| 模块 | 功能 |
|------|------|
| 博客前台 | 首页、文章详情、分类/标签浏览、归档、搜索、用户主页 |
| 管理后台 | 仪表盘、文章 CRUD、分类管理、标签管理、博客设置 |
| AI 写作 | AI 文章生成、AI 标签推荐、AI 分类推荐、Token 额度管理 |
| 数据统计 | 基础仪表盘（文章/分类/标签/PV 总数、发布趋势图、PV 趋势图） |

### 1.2 技术架构

```
weblog-vue3/          → Vue 3 + Vite + Element Plus（前后端分离前端）
weblog-springboot/    → Spring Boot 2.6.3 / Java 8 / MyBatis Plus
  ├── weblog-module-common/   → DO、Mapper、Response、异常、工具类
  ├── weblog-module-admin/    → 管理端 API、Service、DAO、AI、安全
  ├── weblog-module-jwt/      → JWT 认证过滤器
  └── weblog-web/             → 启动入口 + 公开博客 API
```

### 1.3 拓展目标

本次拓展聚焦三个方向：

- **写作体验提升**（A1 + A4）：AI 深度辅助写作全流程，从续写到 SEO 优化
- **数据洞察增强**（C3）：将简陋仪表盘升级为专业数据分析大屏
- **社交互动激活**（D1 + C4）：为读者提供互动能力，为作者提供版本管理

---

## 2. A1: AI 文章续写/改写/润色

### 2.1 功能概述

在现有 AI 文章生成的基础上，增加三种精准的编辑辅助能力：

| 子功能 | 说明 | 使用场景 |
|--------|------|----------|
| **AI 续写** | 基于光标位置/选中文本，AI 继续撰写后续内容 | 写到一半卡壳，让 AI 帮忙往下写 |
| **AI 改写** | 选中段落，AI 用不同表达方式重写，保持原意 | 对某段表达不满意，换一种说法 |
| **AI 润色** | 选中段落，AI 优化措辞、修正语法、提升文采 | 文章写完后的打磨 |

### 2.2 后端设计

#### 2.2.1 新增接口

在 `AdminAiController` 中新增三个接口：

```
POST /admin/ai/article/continue    → AI 续写
POST /admin/ai/article/rewrite     → AI 改写
POST /admin/ai/article/polish      → AI 润色
```

#### 2.2.2 请求 VO

```java
// AiContinueReqVO.java
public class AiContinueReqVO {
    @NotBlank(message = "文章上下文不能为空")
    private String context;          // 已有文章内容（用于 AI 理解上下文）

    private String selectedText;     // 光标附近选中的文本（可选，用于精确定位续写起点）

    private String instruction;      // 续写方向指令（可选，如"继续论证这个观点"）
}

// AiRewriteReqVO.java
public class AiRewriteReqVO {
    @NotBlank(message = "待改写文本不能为空")
    private String text;             // 要改写的段落

    private String style;            // 改写风格：more_formal / more_casual / shorter / longer
}

// AiPolishReqVO.java
public class AiPolishReqVO {
    @NotBlank(message = "待润色文本不能为空")
    private String text;             // 要润色的段落
}
```

#### 2.2.3 响应 VO

```java
// AiEditRspVO.java（统一响应，三种操作共用）
public class AiEditRspVO {
    private String result;           // AI 处理后的文本
    private Long estimatedTokens;
    private Long actualTokens;
}
```

#### 2.2.4 Service 层设计

在 [AdminAiService](file:///d:/xm/WeBlog/weblog-springboot/weblog-module-admin/src/main/java/com/quanxiaoha/weblog/admin/service/AdminAiService.java) 中新增方法签名：

```java
Response<AiEditRspVO> continueArticle(AiContinueReqVO reqVO);
Response<AiEditRspVO> rewriteText(AiRewriteReqVO reqVO);
Response<AiEditRspVO> polishText(AiPolishReqVO reqVO);
```

在 `AdminAiServiceImpl` 中实现，复用现有的：
- `callAiApi()` — AI API 调用
- `estimateTokens()` — Token 估算
- `checkTokenLimit()` / `recordUsage()` — 额度控制
- `acquireRequestLock()` / `releaseRequestLock()` — 并发锁

**Prompt 设计要点：**

- **续写**：System Prompt `"你是一个博客写作续写助手。根据已有内容和用户的续写指令，自然流畅地继续写下去，保持文风一致。"`，User Prompt 携带 context + selectedText + instruction
- **改写**：System Prompt `"你是一个文章改写助手。将用户提供的文本换一种表达方式重写，保持原意不变。"`，User Prompt 携带 text + style
- **润色**：System Prompt `"你是一个文章润色助手。优化用户提供的文本的措辞、语法和表达，提升文采，但不改变核心内容。"`，User Prompt 携带 text

#### 2.2.5 额度策略

- **续写**：计入每日 Token 消耗，不占用文章生成次数
- **改写**：计入每日 Token 消耗，不占用文章生成次数
- **润色**：计入每日 Token 消耗，不占用文章生成次数

> 这三种操作为高频使用场景，仅受 Token 总量限制（默认 10 万/天），不受文章生成次数限制（默认 2 次/天）。

### 2.3 前端设计

#### 2.3.1 交互入口

在文章编辑器的工具栏区域增加 AI 辅助按钮组，位于 [article-list.vue](file:///d:/xm/WeBlog/weblog-vue3/src/pages/admin/article-list.vue) 的两个编辑器弹窗内。

**方案：在 MdEditor 上方增加 AI 快捷工具栏**

```
┌─────────────────────────────────────────────────┐
│ [AI 续写] [AI 改写] [AI 润色] [AI SEO优化]      │  ← 新增工具栏
├─────────────────────────────────────────────────┤
│                                                   │
│   MdEditor（markdown 编辑器）                     │
│                                                   │
└─────────────────────────────────────────────────┘
```

#### 2.3.2 交互流程

**AI 续写流程：**
1. 用户在编辑器中定位光标
2. 点击「AI 续写」按钮
3. 弹出小窗输入续写方向指令（可选）
4. 点击「开始续写」，显示 loading
5. AI 返回续写内容 → 自动插入到光标位置

**AI 改写/润色流程：**
1. 用户在编辑器中选中一段文字
2. 点击「AI 改写」或「AI 润色」
3. 对于改写：弹出风格选择（更正式 / 更轻松 / 更简短 / 更详细）
4. 显示 loading，AI 返回结果
5. 以 Diff 对比形式展示原稿和改写结果
6. 用户点击「替换」→ 替换选中文本；点击「保留原稿」→ 取消

#### 2.3.3 新增组件

| 组件 | 文件路径 | 说明 |
|------|----------|------|
| `AiEditToolbar.vue` | `src/components/AiEditToolbar.vue` | 编辑器上方 AI 辅助工具栏 |
| `AiRewriteDialog.vue` | `src/components/AiRewriteDialog.vue` | 改写/润色的风格选择与 Diff 对比弹窗 |

#### 2.3.4 API 封装

在 `src/api/admin/ai.js` 中新增：

```javascript
export function aiContinue(data) {
    return axios.post("/admin/ai/article/continue", data)
}
export function aiRewrite(data) {
    return axios.post("/admin/ai/article/rewrite", data)
}
export function aiPolish(data) {
    return axios.post("/admin/ai/article/polish", data)
}
```

---

## 3. A4: AI SEO 元数据优化

### 3.1 功能概述

一键分析文章内容，AI 自动生成 SEO 优化建议：

| 输出项 | 说明 |
|--------|------|
| **优化标题** | 更具搜索吸引力的标题建议 |
| **优化摘要** | 精准、含关键词的 description（≤160 字） |
| **关键词建议** | 5~10 个 SEO 关键词 |
| **标题评分** | 对原标题的 SEO 评分和改进建议 |

### 3.2 后端设计

#### 3.2.1 新增接口

```
POST /admin/ai/seo/optimize    → AI SEO 优化
```

#### 3.2.2 请求/响应 VO

```java
// AiSeoOptimizeReqVO.java
public class AiSeoOptimizeReqVO {
    @NotBlank(message = "文章标题不能为空")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String currentDescription;  // 当前摘要（可选）
}

// AiSeoOptimizeRspVO.java
public class AiSeoOptimizeRspVO {
    private String optimizedTitle;          // 优化后的标题
    private String optimizedDescription;    // 优化后的摘要
    private List<String> keywords;          // 推荐关键词
    private Integer titleScore;             // 原标题评分（1-100）
    private String titleSuggestion;         // 标题改进建议
    private Long estimatedTokens;
    private Long actualTokens;
}
```

#### 3.2.3 Service 层

在 `AdminAiService` 中新增：

```java
Response<AiSeoOptimizeRspVO> seoOptimize(AiSeoOptimizeReqVO reqVO);
```

**Prompt 要点：**
- System Prompt: `"你是一个 SEO 优化专家。根据博客文章标题和内容，提供 SEO 优化建议。返回 JSON 格式结果……"`
- 要求 AI 返回结构化 JSON，便于解析
- 标题评分基于：长度（15-40 字最优）、关键词密度、吸引力

#### 3.2.4 额度策略

- 计入每日 Token 消耗，不占用文章生成次数

### 3.3 前端设计

#### 3.3.1 交互入口

在文章编辑弹窗的「摘要」表单项旁边增加「AI SEO 优化」按钮：

```
┌──────────────────────────────────────────┐
│ 摘要 *  [________________] [AI SEO 优化] │
└──────────────────────────────────────────┘
```

点击后：
1. 获取当前已填写的标题 + 正文内容
2. 调用 `/admin/ai/seo/optimize`
3. 展示优化建议面板（右侧滑出或弹窗）：
   - 优化标题（可一键替换）
   - 优化摘要（可一键填入摘要输入框）
   - 关键词建议列表
   - 原标题评分

#### 3.3.2 新增组件

| 组件 | 文件路径 | 说明 |
|------|----------|------|
| `AiSeoPanel.vue` | `src/components/AiSeoPanel.vue` | SEO 优化建议面板 |

#### 3.3.3 API 封装

```javascript
export function aiSeoOptimize(data) {
    return axios.post("/admin/ai/seo/optimize", data)
}
```

---

## 4. C3: 数据分析大屏

### 4.1 功能概述

将现有仪表盘（仅 4 张统计卡片 + 2 张图表）升级为专业数据分析大屏：

| 模块 | 内容 |
|------|------|
| **概览卡片**（保留增强） | 文章数、分类/标签数、总 PV、总 UV、今日 PV/UV |
| **PV/UV 趋势图** | 日/周/月维度切换，双折线图（PV + UV） |
| **热门文章 Top 10** | 按浏览量排行，展示标题 + PV + 占比 |
| **访客地域分布** | 中国地图热力图/IP 归属地统计（基于 t_visitor_record） |
| **访问来源分析** | 直接访问 / 搜索引擎 / 社交平台占比（解析 Referer） |
| **发布节奏日历图** | GitHub 风格热力图：一年中每天发文数量 |
| **分类/标签占比** | 饼图展示各分类/标签下的文章数量占比 |

### 4.2 后端设计

#### 4.2.1 新增接口

```
POST /admin/dashboard/statistics/overview      → 仪表盘总览（增强版）
POST /admin/dashboard/statistics/pvuv/trend    → PV/UV 趋势
POST /admin/dashboard/statistics/article/rank  → 热门文章排行
POST /admin/dashboard/statistics/visitor/region → 访客地域分布
POST /admin/dashboard/statistics/publish/heatmap → 发布日历热力图
POST /admin/dashboard/statistics/category/ratio  → 分类占比
POST /admin/dashboard/statistics/tag/ratio       → 标签占比
```

#### 4.2.2 请求/响应 VO

```java
// DashboardOverviewRspVO.java
public class DashboardOverviewRspVO {
    private Long articleTotalCount;
    private Long categoryTotalCount;
    private Long tagTotalCount;
    private Long pvTotalCount;
    private Long uvTotalCount;        // 新增：总 UV
    private Long todayPv;             // 新增：今日 PV
    private Long todayUv;             // 新增：今日 UV
}

// PvUvTrendRspVO.java
public class PvUvTrendRspVO {
    private List<String> dates;               // 日期列表
    private List<Long> pvData;                // PV 数据
    private List<Long> uvData;                // UV 数据
}

// ArticleRankRspVO.java
public class ArticleRankRspVO {
    private List<ArticleRankItemVO> items;

    @Data
    @Builder
    public static class ArticleRankItemVO {
        private Long articleId;
        private String title;
        private Long pv;
        private Double percentage;    // 占比
    }
}

// VisitorRegionRspVO.java
public class VisitorRegionRspVO {
    private List<RegionItemVO> items;

    @Data
    @Builder
    public static class RegionItemVO {
        private String region;        // 省份/城市
        private Long count;           // 访客数
    }
}

// PublishHeatmapRspVO.java
public class PublishHeatmapRspVO {
    // 返回近一年每天的文章发布数
    // 格式：[{date: "2026-01-01", count: 2}, ...]
    private List<PublishHeatmapItemVO> items;

    @Data
    @Builder
    public static class PublishHeatmapItemVO {
        private String date;
        private Integer count;
    }
}

// CategoryRatioRspVO.java / TagRatioRspVO.java
public class CategoryRatioRspVO {
    private List<RatioItemVO> items;

    @Data
    @Builder
    public static class RatioItemVO {
        private String name;
        private Long count;
        private Double percentage;
    }
}
```

#### 4.2.3 Service 层

新建 `AdminStatisticsService` 及 `AdminStatisticsServiceImpl`，不与现有 `AdminDashboardService` 耦合（现有接口继续保留）。

- PV/UV 数据来源：`t_statistics_article_pv`、`t_statistics_user_pv`
- 访客地域：`t_visitor_record.ip_region` 字段聚合
- 热门文章：`t_article.read_num` 排序取 Top 10

### 4.3 前端设计

#### 4.3.1 页面布局

将现有 `admin/index.vue` 改造为分析大屏，采用 **Tab 切换** 或 **全屏滚动** 布局：

```
┌──────────────────────────────────────────────────┐
│  仪表盘  │  PV/UV趋势  │  访客分析  │  内容分析  │  ← Tab 导航
├──────────────────────────────────────────────────┤
│                                                    │
│   [概览卡片 × 6]                                   │
│   文章 | 分类 | 标签 | 总PV | 今日PV | 今日UV      │
│                                                    │
│   [趋势图 - 左]     [热门文章TOP10 - 右]           │
│   双折线 PV/UV       排行榜表格                     │
│                                                    │
│   [访客地域地图 - 左]  [分类饼图 - 右]              │
│   中国热力图           环形饼图                      │
│                                                    │
│   [发布日历热力图 - 全宽]                           │
│   GitHub风格 365天格子                              │
│                                                    │
└──────────────────────────────────────────────────┘
```

#### 4.3.2 新增/改造组件

| 组件 | 文件路径 | 说明 |
|------|----------|------|
| `DashboardOverview.vue` | `src/components/DashboardOverview.vue` | 增强版概览卡片（6 张） |
| `PvUvTrendChart.vue` | `src/components/PvUvTrendChart.vue` | PV/UV 双折线趋势图 |
| `ArticleRankTable.vue` | `src/components/ArticleRankTable.vue` | 热门文章 Top 10 表格 |
| `VisitorRegionMap.vue` | `src/components/VisitorRegionMap.vue` | 访客地域热力图，使用 ECharts 中国地图 |
| `PublishHeatmap.vue` | `src/components/PublishHeatmap.vue` | 发布日历热力图，使用 ECharts Calendar |
| `CategoryPieChart.vue` | `src/components/CategoryPieChart.vue` | 分类占比饼图 |
| `TagPieChart.vue` | `src/components/TagPieChart.vue` | 标签占比饼图 |

> 注意：项目已使用 ECharts（`ArticlePublishChart.vue`、`PVChart.vue`），新图表沿用 ECharts。

#### 4.3.3 图表库依赖

项目已在 `package.json` 中使用 `echarts`，无需新增依赖。

---

## 5. C4: 草稿箱 & 版本管理

### 5.1 功能概述

| 子功能 | 说明 |
|--------|------|
| **自动保存草稿** | 编辑文章时每 30 秒自动保存一次草稿 |
| **手动保存草稿** | 「保存草稿」按钮，随时手动保存 |
| **草稿箱列表** | 管理后台新增「草稿箱」菜单，展示所有草稿 |
| **草稿继续编辑** | 从草稿箱恢复编辑 |
| **版本快照** | 每次「发布」或「更新」时自动创建版本快照 |
| **版本历史** | 查看文章的所有历史版本 |
| **版本对比** | 对比两个版本的差异（Diff 视图） |
| **版本回滚** | 将文章内容回滚到指定版本 |

### 5.2 数据库设计

#### 5.2.1 t_article_draft（草稿表）

```sql
CREATE TABLE `t_article_draft` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `article_id` bigint(20) DEFAULT NULL COMMENT '关联的文章ID（已发布文章编辑时关联，新文章草稿为null）',
  `title` varchar(256) DEFAULT NULL,
  `content` longtext,
  `title_image` varchar(512) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `tags` varchar(1024) DEFAULT NULL COMMENT '标签ID，逗号分隔',
  `visibility` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章草稿表';
```

- 与文章唯一绑定：同一 `user_id` + `article_id`（null 表示新文章草稿）只保留一条，upsert 逻辑
- 新文章草稿：`article_id = NULL`，每用户最多 1 条新文章草稿

#### 5.2.2 t_article_version（版本快照）

```sql
CREATE TABLE `t_article_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `title` varchar(256) NOT NULL,
  `content` longtext NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `change_summary` varchar(256) DEFAULT NULL COMMENT '变更摘要',
  `version_num` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_article_ver` (`article_id`, `version_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章版本快照表';
```

- 每次「发布」或「更新提交」时自动创建快照
- `version_num` 自增，从 1 开始
- 为防止无限膨胀，每篇文章最多保留 50 个版本（超出删除最旧版本）

### 5.3 后端设计

#### 5.3.1 草稿接口

```
POST   /admin/article/draft/save        → 保存草稿（自动/手动通用）
POST   /admin/article/draft/list        → 草稿列表
POST   /admin/article/draft/detail      → 获取草稿详情（用于恢复编辑）
DELETE /admin/article/draft/delete      → 删除草稿
```

#### 5.3.2 版本接口

```
POST /admin/article/version/list        → 获取某文章的所有版本列表
POST /admin/article/version/detail      → 获取某个版本的完整内容
POST /admin/article/version/diff        → 对比两个版本的差异
POST /admin/article/version/rollback    → 回滚到指定版本
```

#### 5.3.3 VO 设计

```java
// SaveDraftReqVO.java
public class SaveDraftReqVO {
    private Long articleId;       // 已有文章ID，新文章为null
    private String title;
    private String content;
    private String titleImage;
    private String description;
    private Long categoryId;
    private List<String> tags;
    private String visibility;
}

// DraftListRspVO.java
public class DraftListRspVO {
    private List<DraftItemVO> items;

    @Data
    @Builder
    public static class DraftItemVO {
        private Long draftId;
        private Long articleId;         // null 表示新文章草稿
        private String title;
        private String updateTime;
    }
}

// ArticleVersionListReqVO.java
public class ArticleVersionListReqVO {
    @NotNull
    private Long articleId;
}

// ArticleVersionListRspVO.java
public class ArticleVersionListRspVO {
    private List<VersionItemVO> items;

    @Data
    @Builder
    public static class VersionItemVO {
        private Long versionId;
        private Integer versionNum;
        private String title;
        private String changeSummary;
        private String createTime;
    }
}

// ArticleVersionDiffReqVO.java
public class ArticleVersionDiffReqVO {
    private Long versionIdA;      // 版本A ID
    private Long versionIdB;      // 版本B ID（为null则对比当前版本）
}

// ArticleVersionDiffRspVO.java
public class ArticleVersionDiffRspVO {
    private String titleA;
    private String titleB;
    private String contentA;
    private String contentB;
}
```

#### 5.3.4 自动保存机制

- **前端主导**：编辑器 `watch` 表单数据，30 秒无操作或内容变化超过阈值时触发 `POST /admin/article/draft/save`
- **后端处理**：upsert 逻辑 — 同 `user_id + article_id` 存在则更新，否则插入

### 5.4 前端设计

#### 5.4.1 草稿自动保存

在 [article-list.vue](file:///d:/xm/WeBlog/weblog-vue3/src/pages/admin/article-list.vue) 的两个编辑器弹窗中：

- 新增 `useAutoSaveDraft()` composable
- 监听 `form.title` 和 `form.content` 变化
- 30 秒防抖后自动调用草稿保存 API
- Toast 提示「草稿已自动保存」（仅 3 秒显示，不打断编辑）

```
composables/useAutoSaveDraft.js  → 自动保存草稿 composable
```

#### 5.4.2 草稿箱页面

新增管理后台页面 `src/pages/admin/draft-list.vue`：

- 表格展示：标题（新文章草稿显示「未命名草稿」）、关联文章、最后保存时间、操作（继续编辑、删除）
- 点击「继续编辑」→ 打开发布编辑器并回填草稿数据

#### 5.4.3 版本历史

在文章列表操作列增加「版本」按钮：

- 点击 → 弹出版本历史面板
- 时间线展示版本列表
- 选中两个版本 → 点击「对比」→ Diff 视图
- 选中某版本 → 点击「回滚到此版本」→ 确认弹窗 → 执行回滚

#### 5.4.4 新增路由

```javascript
// src/router/index.js 新增
{
    path: '/admin/draft/list',
    component: AdminDraftList,
    meta: { title: '草稿箱' }
}
```

#### 5.4.5 菜单更新

在 [AdminMenu.vue](file:///d:/xm/WeBlog/weblog-vue3/src/layouts/components/AdminMenu.vue) 中新增「草稿箱」菜单项，位于「文章管理」下方。

---

## 6. D1: 点赞/收藏/分享

### 6.1 功能概述

| 子功能 | 说明 | 场景 |
|--------|------|------|
| **文章点赞** | 读者可点赞文章，显示点赞数 | 文章详情页底部 |
| **文章收藏** | 读者收藏文章，可在个人主页查看收藏列表 | 文章详情页 + 个人主页 |
| **一键分享** | 复制链接、生成分享卡片、二维码分享 | 文章详情页底部 |

### 6.2 数据库设计

#### 6.2.1 t_article_like（点赞记录）

```sql
CREATE TABLE `t_article_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL COMMENT '登录用户ID，匿名用户为null',
  `visitor_id` varchar(64) DEFAULT NULL COMMENT '匿名访客标识（基于IP+UA哈希）',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_article_visitor` (`article_id`, `visitor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞记录表';
```

#### 6.2.2 t_article_favorite（收藏记录）

```sql
CREATE TABLE `t_article_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL COMMENT '仅登录用户可收藏',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章收藏记录表';
```

#### 6.2.3 t_article 表新增字段

```sql
ALTER TABLE `t_article`
  ADD COLUMN `like_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '点赞数' AFTER `read_num`,
  ADD COLUMN `favorite_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '收藏数' AFTER `like_num`;
```

### 6.3 后端设计

#### 6.3.1 接口

```
POST /article/like          → 点赞/取消点赞（幂等：有则删，无则增）
POST /article/like/status   → 查询用户对文章的点赞状态
POST /article/favorite      → 收藏/取消收藏
POST /article/favorite/list → 当前用户收藏列表（分页）
POST /article/share/record  → 记录分享行为（可选统计分析）
```

#### 6.3.2 VO 设计

```java
// ArticleLikeReqVO.java
public class ArticleLikeReqVO {
    @NotNull
    private Long articleId;
}

// ArticleLikeRspVO.java
public class ArticleLikeRspVO {
    private Boolean liked;        // 当前操作后是否已点赞
    private Long likeCount;       // 最新点赞总数
}

// ArticleFavoriteReqVO.java
public class ArticleFavoriteReqVO {
    @NotNull
    private Long articleId;
}

// ArticleFavoriteRspVO.java
public class ArticleFavoriteRspVO {
    private Boolean favorited;
    private Long favoriteCount;
}

// ArticleFavoritePageReqVO.java
public class ArticleFavoritePageReqVO {
    private Long current = 1L;
    private Long size = 10L;
}

// ArticleFavoritePageRspVO.java
public class ArticleFavoritePageRspVO {
    private List<FavoriteItemVO> records;
    private Long current;
    private Long size;
    private Long total;

    @Data
    @Builder
    public static class FavoriteItemVO {
        private Long articleId;
        private String title;
        private String description;
        private String titleImage;
        private String createTime;
        private Long readNum;
        private Long likeNum;
    }
}
```

#### 6.3.3 点赞策略

- **登录用户**：以 `user_id` 唯一约束，幂等操作
- **匿名用户**：以 `visitor_id`（IP + User-Agent 哈希）唯一约束，24 小时内同一访客不能重复点赞
- 点赞计数存储在 `t_article.like_num` 冗余字段，定时任务校准

### 6.4 前端设计

#### 6.4.1 文章详情页 - 底部互动栏

在 [article-detail.vue](file:///d:/xm/WeBlog/weblog-vue3/src/pages/frontend/article-detail.vue) 文章正文下方新增互动工具栏：

```
┌──────────────────────────────────────────────┐
│                                               │
│  [ ❤ 点赞 128 ]   [ ⭐ 收藏 56 ]   [ 🔗 分享 ] │
│                                               │
└──────────────────────────────────────────────┘
```

**分享功能：**
- 点击「分享」→ 弹出 Popover
- 选项：复制链接、生成二维码（qrcode.js）、分享到微信/微博（使用平台分享 URL scheme）

#### 6.4.2 文章列表卡片增强

在首页/分类/标签的文章列表中：

- 每个文章卡片底部显示 `❤128  ⭐56  👁1.2k` 小图标+数字

#### 6.4.3 收藏列表页

新建前台页面 `src/pages/frontend/favorite-list.vue`：

- 展示当前用户的收藏文章列表
- 每项显示：封面图、标题、摘要、点赞/收藏/阅读数、收藏时间
- 支持取消收藏
- 路由：`/u/:username/favorites`

#### 6.4.4 新增/改造组件

| 组件 | 文件路径 | 说明 |
|------|----------|------|
| `ArticleInteraction.vue` | `src/components/ArticleInteraction.vue` | 文章互动工具栏（点赞/收藏/分享） |
| `FavoriteList.vue` | `src/pages/frontend/favorite-list.vue` | 收藏列表页面 |

#### 6.4.5 新增路由

```javascript
{
    path: '/u/:username/favorites',
    component: FavoriteList,
    meta: { title: '收藏列表' }
}
```

---

## 7. 数据库设计汇总

### 7.1 新增表

| 表名 | 说明 | 所属模块 |
|------|------|----------|
| `t_article_draft` | 文章草稿 | C4 |
| `t_article_version` | 文章版本快照 | C4 |
| `t_article_like` | 点赞记录 | D1 |
| `t_article_favorite` | 收藏记录 | D1 |

### 7.2 修改表

| 表名 | 修改内容 | 所属模块 |
|------|----------|----------|
| `t_article` | 新增 `like_num`、`favorite_num` 字段 | D1 |

### 7.3 完整 DDL

```sql
-- ========== C4: 草稿箱 & 版本管理 ==========

CREATE TABLE `t_article_draft` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章版本快照表';

-- ========== D1: 点赞/收藏/分享 ==========

ALTER TABLE `t_article`
  ADD COLUMN `like_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '点赞数',
  ADD COLUMN `favorite_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '收藏数';

CREATE TABLE `t_article_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL COMMENT '登录用户ID',
  `visitor_id` varchar(64) DEFAULT NULL COMMENT '匿名访客标识',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_article_visitor` (`article_id`, `visitor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞记录表';

CREATE TABLE `t_article_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章收藏记录表';
```

---

## 8. API 接口设计汇总

### 8.1 AI 增强接口（A1 + A4）

| 方法 | 路径 | 说明 | 模块 |
|------|------|------|------|
| POST | `/admin/ai/article/continue` | AI 续写 | A1 |
| POST | `/admin/ai/article/rewrite` | AI 改写 | A1 |
| POST | `/admin/ai/article/polish` | AI 润色 | A1 |
| POST | `/admin/ai/seo/optimize` | AI SEO 优化 | A4 |

### 8.2 数据分析接口（C3）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/admin/dashboard/statistics/overview` | 增强版总览 |
| POST | `/admin/dashboard/statistics/pvuv/trend` | PV/UV 趋势 |
| POST | `/admin/dashboard/statistics/article/rank` | 热门文章排行 |
| POST | `/admin/dashboard/statistics/visitor/region` | 访客地域分布 |
| POST | `/admin/dashboard/statistics/publish/heatmap` | 发布日历热力图 |
| POST | `/admin/dashboard/statistics/category/ratio` | 分类占比 |
| POST | `/admin/dashboard/statistics/tag/ratio` | 标签占比 |

### 8.3 草稿 & 版本接口（C4）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/admin/article/draft/save` | 保存草稿 |
| POST | `/admin/article/draft/list` | 草稿列表 |
| POST | `/admin/article/draft/detail` | 草稿详情 |
| DELETE | `/admin/article/draft/delete` | 删除草稿 |
| POST | `/admin/article/version/list` | 版本列表 |
| POST | `/admin/article/version/detail` | 版本详情 |
| POST | `/admin/article/version/diff` | 版本 Diff |
| POST | `/admin/article/version/rollback` | 版本回滚 |

### 8.4 互动接口（D1）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/article/like` | 点赞/取消点赞 |
| POST | `/article/like/status` | 查询点赞状态 |
| POST | `/article/favorite` | 收藏/取消收藏 |
| POST | `/article/favorite/list` | 收藏列表（分页） |

> 注：`/article/*` 为前台公开接口路径，不需要 `/admin` 前缀。

### 8.5 错误码扩展

在 [ResponseCodeEnum](file:///d:/xm/WeBlog/weblog-springboot/weblog-module-common/src/main/java/com/quanxiaoha/weblog/common/enums/ResponseCodeEnum.java) 中新增：

```java
// ----------- 草稿 & 版本 -----------
DRAFT_NOT_FOUND("40001", "草稿不存在"),
VERSION_NOT_FOUND("40002", "版本不存在"),
VERSION_ROLLBACK_FAILED("40003", "版本回滚失败"),

// ----------- 互动 -----------
LIKE_DUPLICATE("50001", "已点赞，不可重复"),
FAVORITE_DUPLICATE("50002", "已收藏，不可重复"),
ARTICLE_NOT_PUBLIC("50003", "该文章未公开，无法互动"),
```

---

## 9. 前端路由与组件规划

### 9.1 新增路由

```javascript
// 管理后台
{
    path: '/admin/draft/list',
    component: () => import('@/pages/admin/draft-list.vue'),
    meta: { title: '草稿箱' }
}

// 前台
{
    path: '/u/:username/favorites',
    component: () => import('@/pages/frontend/favorite-list.vue'),
    meta: { title: '收藏列表' }
}
```

### 9.2 组件清单

| 组件 | 路径 | 说明 | 模块 |
|------|------|------|------|
| `AiEditToolbar.vue` | `src/components/` | 编辑器 AI 辅助工具栏 | A1 |
| `AiRewriteDialog.vue` | `src/components/` | AI 改写 Diff 对比弹窗 | A1 |
| `AiSeoPanel.vue` | `src/components/` | SEO 优化建议面板 | A4 |
| `PvUvTrendChart.vue` | `src/components/` | PV/UV 趋势图 | C3 |
| `ArticleRankTable.vue` | `src/components/` | 热门文章排行表格 | C3 |
| `VisitorRegionMap.vue` | `src/components/` | 访客地域热力图 | C3 |
| `PublishHeatmap.vue` | `src/components/` | 发布日历热力图 | C3 |
| `ArticleInteraction.vue` | `src/components/` | 点赞/收藏/分享工具栏 | D1 |
| `draft-list.vue` | `src/pages/admin/` | 草稿箱页面 | C4 |
| `favorite-list.vue` | `src/pages/frontend/` | 收藏列表页 | D1 |
| `useAutoSaveDraft.js` | `src/composables/` | 自动保存草稿 composable | C4 |

### 9.3 菜单更新

在 [AdminMenu.vue](file:///d:/xm/WeBlog/weblog-vue3/src/layouts/components/AdminMenu.vue) 中新增：

- 「草稿箱」→ `/admin/draft/list`（位于文章管理下方）

### 9.4 API 文件更新

| 文件 | 新增内容 |
|------|----------|
| `src/api/admin/ai.js` | `aiContinue`, `aiRewrite`, `aiPolish`, `aiSeoOptimize` |
| `src/api/admin/dashboard.js` | `getOverview`, `getPvUvTrend`, `getArticleRank`, `getVisitorRegion`, `getPublishHeatmap`, `getCategoryRatio`, `getTagRatio` |
| `src/api/admin/article.js` | `saveDraft`, `getDraftList`, `getDraftDetail`, `deleteDraft`, `getVersionList`, `getVersionDetail`, `getVersionDiff`, `rollbackVersion` |
| `src/api/frontend/article.js`（新建） | `likeArticle`, `getLikeStatus`, `favoriteArticle`, `getFavoriteList` |

---

## 10. 实施优先级与里程碑

### 10.1 优先级矩阵

| 优先级 | 模块 | 工作量估计 | 依赖 |
|--------|------|-----------|------|
| **P0** | A1: AI 续写/改写/润色 | 后端 2d + 前端 2d | 现有 AI 基础设施 |
| **P0** | A4: AI SEO 优化 | 后端 1d + 前端 1d | A1 完成后 |
| **P1** | D1: 点赞/收藏/分享 | 后端 2d + 前端 2d | 无 |
| **P1** | C4: 草稿箱 & 版本管理 | 后端 3d + 前端 2d | 无 |
| **P2** | C3: 数据分析大屏 | 后端 3d + 前端 3d | 无 |

### 10.2 里程碑

```
Milestone 1 (Week 1-2): AI 写作增强
  ✅ A1: AI 续写/改写/润色
  ✅ A4: AI SEO 元数据优化

Milestone 2 (Week 3-4): 社交互动 + 版本管理
  ✅ D1: 点赞/收藏/分享
  ✅ C4: 草稿箱 & 版本管理

Milestone 3 (Week 5-6): 数据可视化
  ✅ C3: 数据分析大屏

Milestone 4 (Week 7): 集成测试 & 上线
  ✅ 全功能回归测试
  ✅ 性能优化（索引检查、慢查询优化）
  ✅ 部署文档更新
```

### 10.3 风险与注意事项

| 风险 | 应对措施 |
|------|----------|
| AI Token 消耗过大 | A1/A4 均受现有日 Token 限额约束；可单独配置更低的 `maxTokensPerRequest` |
| 版本表膨胀 | 限制每篇文章最多 50 个版本，定时清理 |
| 点赞并发计数 | `like_num` 冗余字段 + 定时任务校准；前端乐观更新 |
| 数据分析大屏性能 | 趋势数据按天预聚合到 `t_statistics_article_pv`；热门排行加缓存 |
| ECharts 地图数据 | 引入中国 GeoJSON（~300KB），按需加载 |

---

> **文档结束**  
> 本文档基于 WeBlog 现有架构编写，所有新增表/接口/组件均与现有代码风格保持一致。  
> 具体实施时，后端沿用 `@Autowired` 字段注入 + `Response.success/fail` + `BizException` + MyBatis Plus 模式；  
> 前端沿用 Vue 3 Composition API + Element Plus + ECharts + `src/axios.js` Axios 封装。