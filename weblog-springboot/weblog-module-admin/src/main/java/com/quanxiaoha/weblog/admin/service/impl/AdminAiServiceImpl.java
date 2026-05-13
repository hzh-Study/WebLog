package com.quanxiaoha.weblog.admin.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quanxiaoha.weblog.admin.config.AiConfig;
import com.quanxiaoha.weblog.admin.dao.*;
import com.quanxiaoha.weblog.admin.model.vo.ai.*;
import com.quanxiaoha.weblog.admin.service.AdminAiService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.*;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminAiServiceImpl implements AdminAiService {

    @Autowired
    private AiConfig aiConfig;

    @Autowired
    private AdminAiUsageRecordDao aiUsageRecordDao;

    @Autowired
    private AdminArticleDao articleDao;

    @Autowired
    private AdminTagDao tagDao;

    @Autowired
    private AdminCategoryDao categoryDao;

    @Autowired
    private AdminUserDao userDao;

    private final RestTemplate restTemplate = createRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentHashMap<Long, String> userRequestLock = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> userRequestLockTime = new ConcurrentHashMap<>();
    private static final long LOCK_TIMEOUT_MS = 120_000L;

    private static RestTemplate createRestTemplate() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(20);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(15000)
                .setSocketTimeout(120000)
                .setConnectionRequestTimeout(10000)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(60, TimeUnit.SECONDS)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    @Override
    public Response<AiRecommendTagRspVO> recommendTags(AiRecommendTagReqVO reqVO) {
        UserDO currentUser = getCurrentUser();
        String lockToken = null;
        boolean locked = false;
        try {
            lockToken = acquireRequestLock(currentUser.getId());
            locked = true;
            checkInteractionInterval(currentUser.getId());

            String userRequirement = sanitizeInput(reqVO != null ? reqVO.getRequirement() : null);

            List<TagDO> existingTags = tagDao.selectAllByUserId(currentUser.getId());
            List<String> existingTagNames = existingTags.stream()
                    .map(TagDO::getName)
                    .collect(Collectors.toList());

            List<ArticleDO> articles = queryUserArticles(currentUser.getId());
            String articleContext = buildArticleContext(articles);

            String systemPrompt = "你是一个博客标签推荐助手。根据用户已有的文章内容、现有标签以及用户的具体要求，推荐3-5个新的、合适的标签。只返回标签名称列表，每行一个，不要有其他内容。不要推荐已存在的标签。";
            StringBuilder userPromptBuilder = new StringBuilder();
            userPromptBuilder.append("现有标签：").append(String.join("、", existingTagNames)).append("\n\n");
            if (userRequirement != null && !userRequirement.isEmpty()) {
                userPromptBuilder.append("用户要求：").append(userRequirement).append("\n\n");
            }
            userPromptBuilder.append("文章内容摘要：\n").append(articleContext);
            String userPrompt = userPromptBuilder.toString();

            long estimatedTokens = estimateTokens(systemPrompt + userPrompt);
            checkTokenLimit(currentUser.getId(), estimatedTokens + aiConfig.getMaxTokensPerRequest());

            String aiResponse = callAiApi(systemPrompt, userPrompt, aiConfig.getMaxTokensPerRequest());

            List<String> recommendedTags = parseTagList(aiResponse, existingTagNames);
            long actualTokens = estimateTokens(systemPrompt + userPrompt + aiResponse);

            recordUsage(currentUser.getId(), false, actualTokens);

            return Response.success(AiRecommendTagRspVO.builder()
                    .tags(recommendedTags)
                    .estimatedTokens(estimatedTokens)
                    .actualTokens(actualTokens)
                    .build());
        } finally {
            if (locked) {
                releaseRequestLock(currentUser.getId(), lockToken);
            }
        }
    }

    @Override
    public Response<AiRecommendCategoryRspVO> recommendCategories(AiRecommendCategoryReqVO reqVO) {
        UserDO currentUser = getCurrentUser();
        String lockToken = null;
        boolean locked = false;
        try {
            lockToken = acquireRequestLock(currentUser.getId());
            locked = true;
            checkInteractionInterval(currentUser.getId());

            String userRequirement = sanitizeInput(reqVO != null ? reqVO.getRequirement() : null);

            List<CategoryDO> existingCategories = categoryDao.selectAllCategory(currentUser.getId());
            List<String> existingCategoryNames = existingCategories.stream()
                    .map(CategoryDO::getName)
                    .collect(Collectors.toList());

            List<ArticleDO> articles = queryUserArticles(currentUser.getId());
            String articleContext = buildArticleContext(articles);

            String systemPrompt = "你是一个博客分类推荐助手。根据用户已有的文章内容、现有分类以及用户的具体要求，推荐2-3个新的、合适的分类名称。只返回分类名称列表，每行一个，不要有其他内容。不要推荐已存在的分类。";
            StringBuilder userPromptBuilder = new StringBuilder();
            userPromptBuilder.append("现有分类：").append(String.join("、", existingCategoryNames)).append("\n\n");
            if (userRequirement != null && !userRequirement.isEmpty()) {
                userPromptBuilder.append("用户要求：").append(userRequirement).append("\n\n");
            }
            userPromptBuilder.append("文章内容摘要：\n").append(articleContext);
            String userPrompt = userPromptBuilder.toString();

            long estimatedTokens = estimateTokens(systemPrompt + userPrompt);
            checkTokenLimit(currentUser.getId(), estimatedTokens + aiConfig.getMaxTokensPerRequest());

            String aiResponse = callAiApi(systemPrompt, userPrompt, aiConfig.getMaxTokensPerRequest());

            List<String> recommendedCategories = parseCategoryList(aiResponse, existingCategoryNames);
            long actualTokens = estimateTokens(systemPrompt + userPrompt + aiResponse);

            recordUsage(currentUser.getId(), false, actualTokens);

            return Response.success(AiRecommendCategoryRspVO.builder()
                    .categories(recommendedCategories)
                    .estimatedTokens(estimatedTokens)
                    .actualTokens(actualTokens)
                    .build());
        } finally {
            if (locked) {
                releaseRequestLock(currentUser.getId(), lockToken);
            }
        }
    }

    @Override
    public Response<AiGenerateArticleRspVO> generateArticle(AiGenerateArticleReqVO reqVO) {
        UserDO currentUser = getCurrentUser();
        String lockToken = null;
        boolean locked = false;
        try {
            lockToken = acquireRequestLock(currentUser.getId());
            locked = true;
            checkArticleGenLimit(currentUser.getId());
            checkInteractionInterval(currentUser.getId());

            String sanitizedPrompt = sanitizeInput(reqVO.getPrompt());
            String sanitizedTitle = sanitizeInput(reqVO.getTitle());
            String sanitizedCategory = sanitizeInput(reqVO.getCategoryName());
            List<String> sanitizedTags = reqVO.getTags() != null
                    ? reqVO.getTags().stream().map(this::sanitizeInput).filter(s -> !s.isEmpty()).collect(Collectors.toList())
                    : Collections.emptyList();

            int requestedChars = extractRequestedCharCount(sanitizedPrompt);
            String systemPrompt = buildArticleGenSystemPrompt(sanitizedPrompt);
            String userPrompt = buildArticleGenPrompt(sanitizedPrompt, sanitizedTitle, sanitizedCategory, sanitizedTags);

            long estimatedTokens = estimateTokens(systemPrompt + userPrompt);
            int dynamicMaxTokens = calculateDynamicMaxTokens(sanitizedPrompt, sanitizedTitle);
            checkTokenLimit(currentUser.getId(), estimatedTokens + dynamicMaxTokens);

            String aiResponse = callAiApi(systemPrompt, userPrompt, dynamicMaxTokens);
            String title = extractTitle(aiResponse, reqVO.getTitle());
            String content = cleanContent(aiResponse);
            long actualTokens = estimateTokens(systemPrompt + userPrompt + aiResponse);

            if (requestedChars >= 5000 && content.length() < requestedChars * 0.5) {
                log.info("文章长度{}不足目标{}的50%，启动续写模式", content.length(), requestedChars);
                StringBuilder fullContent = new StringBuilder(content);
                int maxContinuations = Math.min(5, (int) Math.ceil(requestedChars / 2000.0));
                for (int i = 0; i < maxContinuations && fullContent.length() < requestedChars * 0.8; i++) {
                    checkInteractionInterval(currentUser.getId());
                    String continuePrompt = "请继续续写以下内容，保持Markdown格式和文风一致，继续深入展开：\n\n" +
                            fullContent.substring(Math.max(0, fullContent.length() - 500));
                    String continueSystem = "你是一个专业的博客写作助手。请继续续写用户提供的文章片段，保持Markdown格式和文风一致，内容要详实深入。";
                    String continued = callAiApi(continueSystem, continuePrompt, dynamicMaxTokens);
                    String cleanContinued = cleanContent(continued);
                    if (cleanContinued.length() > 100) {
                        fullContent.append("\n\n").append(cleanContinued);
                        actualTokens += estimateTokens(continueSystem + continuePrompt + continued);
                        log.info("续写第{}次，当前总长度: {}", i + 1, fullContent.length());
                    } else {
                        break;
                    }
                }
                content = fullContent.toString();
            }

            recordUsage(currentUser.getId(), true, actualTokens);

            return Response.success(AiGenerateArticleRspVO.builder()
                    .title(title)
                    .content(content)
                    .estimatedTokens(estimatedTokens)
                    .actualTokens(actualTokens)
                    .build());
        } finally {
            if (locked) {
                releaseRequestLock(currentUser.getId(), lockToken);
            }
        }
    }

    @Override
    public Response<AiQuotaRspVO> getQuota() {
        UserDO currentUser = getCurrentUser();
        Date today = getToday();
        AiUsageRecordDO record = aiUsageRecordDao.selectByUserIdAndDate(currentUser.getId(), today);

        int articleGenUsed = record != null ? record.getArticleGenCount() : 0;
        long tokenUsed = record != null ? record.getTokenUsed() : 0L;
        Long lastInteractionTime = record != null && record.getLastInteractionTime() != null
                ? record.getLastInteractionTime().getTime() : null;

        int baseLimit = aiConfig.getDailyArticleGenLimitBase() != null ? aiConfig.getDailyArticleGenLimitBase() : 4;
        int totalLimit = aiConfig.getDailyArticleGenLimit() != null ? aiConfig.getDailyArticleGenLimit() : 5;
        int 弹性额度 = totalLimit - baseLimit;

        int articleGenRemaining = Math.max(0, totalLimit - articleGenUsed);
        long tokenRemaining = Math.max(0, aiConfig.getDailyTokenLimit() - tokenUsed);
        long 弹性剩余 = Math.max(0, aiConfig.getDailyTokenLimit() - tokenUsed - (long) baseLimit * aiConfig.getMaxTokensPerRequest());

        int standard写作次数 = (int) (tokenRemaining / aiConfig.getMaxTokensPerRequest());
        boolean need弹性分配 = tokenRemaining < (long) baseLimit * aiConfig.getMaxTokensPerRequest();

        boolean canInteractNow = true;
        long remainingCooldownSeconds = 0;
        if (lastInteractionTime != null) {
            long elapsed = System.currentTimeMillis() - lastInteractionTime;
            long required = aiConfig.getInteractionIntervalSeconds() * 1000L;
            if (elapsed < required) {
                canInteractNow = false;
                remainingCooldownSeconds = (long) Math.ceil((required - elapsed) / 1000.0);
            }
        }

        int warningThreshold = aiConfig.getTokenWarningThresholdPercent() != null ? aiConfig.getTokenWarningThresholdPercent() : 20;
        long tokenLimit = aiConfig.getDailyTokenLimit();
        boolean token预警触发 = tokenLimit > 0 && (tokenRemaining * 100 / tokenLimit) < warningThreshold;

        return Response.success(AiQuotaRspVO.builder()
                .dailyArticleGenLimit(totalLimit)
                .dailyArticleGenLimitBase(baseLimit)
                .dailyArticleGenUsed(articleGenUsed)
                .dailyArticleGenRemaining(articleGenRemaining)
                .dailyArticleGen弹性额度(弹性额度)
                .dailyTokenLimit(tokenLimit)
                .dailyTokenUsed(tokenUsed)
                .dailyTokenRemaining(tokenRemaining)
                .dailyToken弹性剩余(弹性剩余)
                .standard写作次数(standard写作次数)
                .need弹性分配(need弹性分配)
                .lastInteractionTime(lastInteractionTime)
                .interactionIntervalSeconds(aiConfig.getInteractionIntervalSeconds())
                .canInteractNow(canInteractNow)
                .remainingCooldownSeconds(remainingCooldownSeconds)
                .token预警触发(token预警触发)
                .token预警阈值Percent(warningThreshold)
                .build());
    }

    @Override
    public Response<AiEstimateTokenRspVO> estimateTokens(AiEstimateTokenReqVO reqVO) {
        String sanitized = sanitizeInput(reqVO.getPrompt());
        long estimated = estimateTokens(sanitized);
        return Response.success(AiEstimateTokenRspVO.builder()
                .estimatedTokens(estimated)
                .build());
    }

    @Override
    public Response<AiEditRspVO> continueArticle(AiContinueReqVO reqVO) {
        UserDO currentUser = getCurrentUser();
        String lockToken = null;
        boolean locked = false;
        try {
            lockToken = acquireRequestLock(currentUser.getId());
            locked = true;
            checkInteractionInterval(currentUser.getId());

            String context = sanitizeInput(reqVO != null ? reqVO.getContext() : null);
            String selectedText = sanitizeInput(reqVO != null ? reqVO.getSelectedText() : null);
            String instruction = sanitizeInput(reqVO != null ? reqVO.getInstruction() : null);
            if (context.isEmpty() && selectedText.isEmpty()) {
                throw new BizException(ResponseCodeEnum.PARAM_ERROR);
            }

            String systemPrompt = "你是一个博客写作续写助手。根据已有内容和用户的续写指令，自然流畅地继续写下去，保持文风一致。";
            StringBuilder userPromptBuilder = new StringBuilder();
            if (context != null && !context.isEmpty()) {
                userPromptBuilder.append("已有内容：\n").append(context).append("\n\n");
            }
            if (selectedText != null && !selectedText.isEmpty()) {
                userPromptBuilder.append("选中文本：").append(selectedText).append("\n\n");
            }
            userPromptBuilder.append("续写指令：").append(instruction);
            String userPrompt = userPromptBuilder.toString();

            long estimatedTokens = estimateTokens(systemPrompt + userPrompt);
            int dynamicMaxTokens = calculateDynamicMaxTokens(context + selectedText, instruction);
            checkTokenLimit(currentUser.getId(), estimatedTokens + dynamicMaxTokens);

            String aiResponse = callAiApi(systemPrompt, userPrompt, dynamicMaxTokens);

            long actualTokens = estimateTokens(systemPrompt + userPrompt + aiResponse);

            recordUsage(currentUser.getId(), false, actualTokens);

            return Response.success(AiEditRspVO.builder()
                    .result(aiResponse)
                    .estimatedTokens(estimatedTokens)
                    .actualTokens(actualTokens)
                    .build());
        } finally {
            if (locked) {
                releaseRequestLock(currentUser.getId(), lockToken);
            }
        }
    }

    @Override
    public Response<AiEditRspVO> rewriteText(AiRewriteReqVO reqVO) {
        UserDO currentUser = getCurrentUser();
        String lockToken = null;
        boolean locked = false;
        try {
            lockToken = acquireRequestLock(currentUser.getId());
            locked = true;
            checkInteractionInterval(currentUser.getId());

            String text = sanitizeInput(reqVO != null ? reqVO.getText() : null);
            String style = sanitizeInput(reqVO != null ? reqVO.getStyle() : null);
            requireNonBlank(text, "待改写文本不能为空");

            String systemPrompt = "你是一个文章改写助手。将用户提供的文本换一种表达方式重写，保持原意不变。";
            StringBuilder userPromptBuilder = new StringBuilder();
            userPromptBuilder.append("原文：\n").append(text).append("\n");
            if (style != null && !style.isEmpty()) {
                userPromptBuilder.append("\n风格要求：").append(style);
            }
            String userPrompt = userPromptBuilder.toString();

            long estimatedTokens = estimateTokens(systemPrompt + userPrompt);
            int dynamicMaxTokens = calculateDynamicMaxTokens(text, style);
            checkTokenLimit(currentUser.getId(), estimatedTokens + dynamicMaxTokens);

            String aiResponse = callAiApi(systemPrompt, userPrompt, dynamicMaxTokens);

            long actualTokens = estimateTokens(systemPrompt + userPrompt + aiResponse);

            recordUsage(currentUser.getId(), false, actualTokens);

            return Response.success(AiEditRspVO.builder()
                    .result(aiResponse)
                    .estimatedTokens(estimatedTokens)
                    .actualTokens(actualTokens)
                    .build());
        } finally {
            if (locked) {
                releaseRequestLock(currentUser.getId(), lockToken);
            }
        }
    }

    @Override
    public Response<AiEditRspVO> polishText(AiPolishReqVO reqVO) {
        UserDO currentUser = getCurrentUser();
        String lockToken = null;
        boolean locked = false;
        try {
            lockToken = acquireRequestLock(currentUser.getId());
            locked = true;
            checkInteractionInterval(currentUser.getId());

            String text = sanitizeInput(reqVO != null ? reqVO.getText() : null);
            requireNonBlank(text, "待润色文本不能为空");

            String systemPrompt = "你是一个文章润色助手。优化用户提供的文本的措辞、语法和表达，提升文采，但不改变核心内容。";
            String userPrompt = "请润色以下文本：\n" + text;

            long estimatedTokens = estimateTokens(systemPrompt + userPrompt);
            checkTokenLimit(currentUser.getId(), estimatedTokens + aiConfig.getMaxTokensPerRequest());

            String aiResponse = callAiApi(systemPrompt, userPrompt, aiConfig.getMaxTokensPerRequest());

            long actualTokens = estimateTokens(systemPrompt + userPrompt + aiResponse);

            recordUsage(currentUser.getId(), false, actualTokens);

            return Response.success(AiEditRspVO.builder()
                    .result(aiResponse)
                    .estimatedTokens(estimatedTokens)
                    .actualTokens(actualTokens)
                    .build());
        } finally {
            if (locked) {
                releaseRequestLock(currentUser.getId(), lockToken);
            }
        }
    }

    @Override
    public Response<AiSeoOptimizeRspVO> seoOptimize(AiSeoOptimizeReqVO reqVO) {
        UserDO currentUser = getCurrentUser();
        String lockToken = null;
        boolean locked = false;
        try {
            lockToken = acquireRequestLock(currentUser.getId());
            locked = true;
            checkInteractionInterval(currentUser.getId());

            String title = sanitizeInput(reqVO != null ? reqVO.getTitle() : null);
            String content = sanitizeInput(reqVO != null ? reqVO.getContent() : null);
            String currentDescription = sanitizeInput(reqVO != null ? reqVO.getCurrentDescription() : null);
            requireNonBlank(title, "标题不能为空");
            requireNonBlank(content, "内容不能为空");

            String systemPrompt = "你是一个 SEO 优化专家。根据博客文章标题和内容，提供 SEO 优化建议。请返回JSON格式结果：{\"optimizedTitle\":\"优化标题\",\"optimizedDescription\":\"优化摘要（≤160字）\",\"keywords\":[\"关键词1\",\"关键词2\",...],\"titleScore\":85,\"titleSuggestion\":\"标题改进建议\"}";
            StringBuilder userPromptBuilder = new StringBuilder();
            userPromptBuilder.append("文章标题：").append(title).append("\n");
            userPromptBuilder.append("文章内容：").append(content);
            if (currentDescription != null && !currentDescription.isEmpty()) {
                userPromptBuilder.append("\n当前摘要：").append(currentDescription);
            }
            String userPrompt = userPromptBuilder.toString();

            long estimatedTokens = estimateTokens(systemPrompt + userPrompt);
            int dynamicMaxTokens = calculateDynamicMaxTokens(title, content);
            checkTokenLimit(currentUser.getId(), estimatedTokens + dynamicMaxTokens);

            String aiResponse = callAiApi(systemPrompt, userPrompt, dynamicMaxTokens);

            long actualTokens = estimateTokens(systemPrompt + userPrompt + aiResponse);

            recordUsage(currentUser.getId(), false, actualTokens);

            String cleanedJson = aiResponse.trim();
            if (cleanedJson.startsWith("```json")) {
                cleanedJson = cleanedJson.substring(7);
            }
            if (cleanedJson.startsWith("```")) {
                cleanedJson = cleanedJson.substring(3);
            }
            if (cleanedJson.endsWith("```")) {
                cleanedJson = cleanedJson.substring(0, cleanedJson.length() - 3);
            }
            cleanedJson = cleanedJson.trim();

            String optimizedTitle = title;
            String optimizedDescription = "";
            Integer titleScore = 0;
            String titleSuggestion = "";
            List<String> keywords = new ArrayList<>();

            try {
                JsonNode root = objectMapper.readTree(cleanedJson);
                if (root.has("optimizedTitle")) {
                    optimizedTitle = root.get("optimizedTitle").asText();
                }
                if (root.has("optimizedDescription")) {
                    optimizedDescription = root.get("optimizedDescription").asText();
                }
                if (root.has("titleScore")) {
                    titleScore = root.get("titleScore").asInt();
                }
                if (root.has("titleSuggestion")) {
                    titleSuggestion = root.get("titleSuggestion").asText();
                }
                if (root.has("keywords") && root.get("keywords").isArray()) {
                    for (JsonNode kw : root.get("keywords")) {
                        keywords.add(kw.asText());
                    }
                }
            } catch (Exception e) {
                log.warn("SEO优化JSON解析失败，返回原始响应: {}", e.getMessage());
                optimizedDescription = aiResponse;
            }

            return Response.success(AiSeoOptimizeRspVO.builder()
                    .optimizedTitle(optimizedTitle)
                    .optimizedDescription(optimizedDescription)
                    .keywords(keywords)
                    .titleScore(titleScore)
                    .titleSuggestion(titleSuggestion)
                    .estimatedTokens(estimatedTokens)
                    .actualTokens(actualTokens)
                    .build());
        } finally {
            if (locked) {
                releaseRequestLock(currentUser.getId(), lockToken);
            }
        }
    }

    private String acquireRequestLock(Long userId) {
        long now = System.currentTimeMillis();
        Long lockTime = userRequestLockTime.get(userId);
        if (lockTime != null && (now - lockTime) > LOCK_TIMEOUT_MS) {
            log.warn("用户 {} 的AI请求锁已超时，强制释放", userId);
            userRequestLock.remove(userId);
            userRequestLockTime.remove(userId);
        }

        String lockToken = UUID.randomUUID().toString();
        String previous = userRequestLock.putIfAbsent(userId, lockToken);
        if (previous != null) {
            throw new BizException("AI_LOCKED", "您有一个AI请求正在处理中，请等待完成后再试");
        }
        userRequestLockTime.put(userId, now);
        return lockToken;
    }

    private void releaseRequestLock(Long userId, String lockToken) {
        userRequestLock.remove(userId, lockToken);
        userRequestLockTime.remove(userId);
    }

    private UserDO getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        UserDO userDO = userDao.selectByUsername(username);
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }
        return userDO;
    }

    private void checkArticleGenLimit(Long userId) {
        Date today = getToday();
        AiUsageRecordDO record = aiUsageRecordDao.selectByUserIdAndDate(userId, today);
        int used = record != null ? record.getArticleGenCount() : 0;
        if (used >= aiConfig.getDailyArticleGenLimit()) {
            throw new BizException("AI_QUOTA_EXCEEDED", "今日AI文章生成次数已用完（" + aiConfig.getDailyArticleGenLimit() + "次/天）");
        }
    }

    private void checkInteractionInterval(Long userId) {
        Date today = getToday();
        AiUsageRecordDO record = aiUsageRecordDao.selectByUserIdAndDate(userId, today);
        if (record != null && record.getLastInteractionTime() != null) {
            long elapsed = System.currentTimeMillis() - record.getLastInteractionTime().getTime();
            long required = aiConfig.getInteractionIntervalSeconds() * 1000L;
            if (elapsed < required) {
                long waitSeconds = (long) Math.ceil((required - elapsed) / 1000.0);
                throw new BizException("AI_INTERVAL", "请等待" + waitSeconds + "秒后再使用AI功能");
            }
        }
    }

    private void checkTokenLimit(Long userId, long newTokens) {
        Date today = getToday();
        AiUsageRecordDO record = aiUsageRecordDao.selectByUserIdAndDate(userId, today);
        long used = record != null ? record.getTokenUsed() : 0L;
        if (used + newTokens > aiConfig.getDailyTokenLimit()) {
            throw new BizException("AI_TOKEN_EXCEEDED", "今日AI Token额度不足，已使用" + used + "/" + aiConfig.getDailyTokenLimit());
        }
    }

    private void recordUsage(Long userId, boolean isArticleGen, long tokens) {
        Date today = getToday();
        AiUsageRecordDO record = aiUsageRecordDao.selectByUserIdAndDate(userId, today);

        if (record == null) {
            record = AiUsageRecordDO.builder()
                    .userId(userId)
                    .usageDate(today)
                    .articleGenCount(isArticleGen ? 1 : 0)
                    .tokenUsed(tokens)
                    .lastInteractionTime(new Date())
                    .build();
            aiUsageRecordDao.insert(record);
        } else {
            record.setTokenUsed(record.getTokenUsed() + tokens);
            if (isArticleGen) {
                record.setArticleGenCount(record.getArticleGenCount() + 1);
            }
            record.setLastInteractionTime(new Date());
            aiUsageRecordDao.updateById(record);
        }
    }

    private String callAiApi(String systemPrompt, String userPrompt, int maxTokens) {
        int maxRetries = 3;
        long retryDelayMs = 2000;
        Exception lastException = null;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    long delay = retryDelayMs * (long) Math.pow(2, attempt - 1);
                    log.info("AI API第{}次重试, 等待{}ms...", attempt, delay);
                    Thread.sleep(delay);
                }

                return doCallAiApi(systemPrompt, userPrompt, maxTokens);
            } catch (BizException e) {
                String msg = e.getMessage();
                if (msg != null && (msg.contains("429") || msg.contains("速率限制") || msg.contains("rate"))) {
                    log.warn("AI API速率限制 (attempt {}/{}), 将重试...", attempt + 1, maxRetries);
                    lastException = e;
                    continue;
                }
                throw e;
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null && (msg.contains("timeout") || msg.contains("timed out") || msg.contains("Read timed out"))) {
                    log.warn("AI API超时 (attempt {}/{}), 将重试...", attempt + 1, maxRetries);
                    lastException = e;
                    continue;
                }
                log.error("AI API调用失败", e);
                throw new BizException("AI_ERROR", "AI服务调用失败: " + e.getMessage());
            }
        }
        throw new BizException("AI_ERROR", "AI服务调用失败(已重试" + maxRetries + "次): " + (lastException != null ? lastException.getMessage() : "未知错误"));
    }

    private String doCallAiApi(String systemPrompt, String userPrompt, int maxTokens) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + aiConfig.getApiKey());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        log.info("AI API请求 - model: {}, prompt长度: {}, maxTokens: {}", aiConfig.getModel(), userPrompt.length(), maxTokens);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    aiConfig.getApiUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );
        } catch (RestClientResponseException e) {
            String responseBody = e.getResponseBodyAsString();
            String errorDetail = extractApiErrorDetail(responseBody);
            log.error("AI API请求失败 - status: {}, detail: {}", e.getRawStatusCode(), errorDetail);
            throw new BizException("AI_ERROR", "AI服务返回错误(" + e.getRawStatusCode() + "): " + errorDetail);
        }

        String responseBody = response.getBody();
        if (responseBody == null) {
            log.error("AI API返回空响应体 - status: {}", response.getStatusCode());
            throw new BizException("AI_ERROR", "AI服务返回空响应");
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            String errorDetail = extractApiErrorDetail(responseBody);
            log.error("AI API返回非200 - status: {}, detail: {}", response.getStatusCode(), errorDetail);
            throw new BizException("AI_ERROR", "AI服务返回异常(" + response.getStatusCodeValue() + "): " + errorDetail);
        }

        JsonNode root;
        try {
            root = objectMapper.readTree(responseBody);
        } catch (Exception e) {
            log.error("AI API响应JSON解析失败 - body: {}", responseBody.substring(0, Math.min(500, responseBody.length())));
            throw new BizException("AI_ERROR", "AI服务返回数据格式异常");
        }

        JsonNode error = root.get("error");
        if (error != null) {
            String errorMsg = error.has("message") ? error.get("message").asText() : error.toString();
            log.error("AI API业务错误 - code: {}, message: {}", error.has("code") ? error.get("code").asText() : "unknown", errorMsg);
            throw new BizException("AI_ERROR", "AI服务错误: " + errorMsg);
        }

        JsonNode usage = root.get("usage");
        if (usage != null) {
            log.info("AI API用量 - prompt_tokens: {}, completion_tokens: {}, total_tokens: {}",
                    usage.get("prompt_tokens"), usage.get("completion_tokens"), usage.get("total_tokens"));
        }

        JsonNode choices = root.get("choices");
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).get("message");
            if (message != null) {
                String content = message.get("content").asText();
                log.info("AI API响应成功 - 响应长度: {}", content.length());
                return content;
            }
        }

        log.error("AI API返回格式异常 - body: {}", responseBody.substring(0, Math.min(500, responseBody.length())));
        throw new BizException("AI_ERROR", "AI服务返回格式异常");
    }

    private String extractApiErrorDetail(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode error = root.get("error");
            if (error != null && error.has("message")) {
                return error.get("message").asText();
            }
        } catch (Exception ignored) {
        }
        return responseBody != null && responseBody.length() > 200
                ? responseBody.substring(0, 200) + "..."
                : (responseBody != null ? responseBody : "无详情");
    }

    private long estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0L;
        }
        long cjkChars = 0;
        long asciiWordChars = 0;
        long otherChars = 0;

        boolean inWord = false;
        for (int i = 0; i < text.length(); i++) {
            int cp = text.codePointAt(i);
            if (Character.isSupplementaryCodePoint(cp)) {
                i++;
            }
            Character.UnicodeScript script = Character.UnicodeScript.of(cp);
            if (script == Character.UnicodeScript.HAN
                    || script == Character.UnicodeScript.HIRAGANA
                    || script == Character.UnicodeScript.KATAKANA
                    || script == Character.UnicodeScript.HANGUL) {
                cjkChars++;
                inWord = false;
            } else if ((cp >= 'a' && cp <= 'z') || (cp >= 'A' && cp <= 'Z')) {
                if (!inWord) {
                    asciiWordChars++;
                    inWord = true;
                }
            } else if (cp >= '0' && cp <= '9') {
                if (!inWord) {
                    asciiWordChars++;
                    inWord = true;
                }
            } else {
                if (!Character.isWhitespace(cp)) {
                    otherChars++;
                }
                inWord = false;
            }
        }

        double estimated = cjkChars * 1.8 + asciiWordChars * 1.3 + otherChars * 0.5;
        return (long) Math.ceil(estimated * 1.1);
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "").trim();
    }

    private int calculateDynamicMaxTokens(String text1, String text2) {
        int baseMax = aiConfig.getMaxTokensPerRequest() != null ? aiConfig.getMaxTokensPerRequest() : 131072;
        int upperMax = aiConfig.getMaxTokensPerRequestUpper() != null ? aiConfig.getMaxTokensPerRequestUpper() : 131072;

        log.info("AI配置读取 - baseMax={}, upperMax={}, model={}", baseMax, upperMax, aiConfig.getModel());

        String combined = (text1 != null ? text1 : "") + (text2 != null ? text2 : "");
        if (combined.isEmpty()) {
            return baseMax;
        }

        int requestedChars = extractRequestedCharCount(combined);
        int tokensNeeded = (int) Math.ceil(requestedChars * 1.8);
        int result = Math.max(baseMax, Math.min(tokensNeeded, upperMax));

        log.info("根据字数要求动态调整maxTokens: base={}, upper={}, requestedChars={}, tokensNeeded={}, result={}",
                baseMax, upperMax, requestedChars, tokensNeeded, result);
        return result;
    }

    private int extractRequestedCharCount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "([\\d\\.]+)[\\s]*(?:万|w|W)?[\\s]*(?:字|字符|char|字左右|个字)");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        int maxChars = 0;
        while (matcher.find()) {
            try {
                double num = Double.parseDouble(matcher.group(1));
                String unit = matcher.group(0);
                int chars;
                if (unit.contains("万") || unit.contains("w") || unit.contains("W")) {
                    chars = (int) (num * 10000);
                } else {
                    chars = (int) num;
                }
                maxChars = Math.max(maxChars, chars);
                log.info("提取到字数要求: num={}, unit={}, chars={}", num, unit, chars);
            } catch (NumberFormatException ignored) {
            }
        }
        return maxChars;
    }

    private double calculateContentComplexity(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }

        int totalChars = text.length();
        int longSentenceCount = 0;
        int technicalTermCount = 0;

        String[] sentences = text.split("[。！？.!?]");
        for (String sentence : sentences) {
            if (sentence.length() > 50) {
                longSentenceCount++;
            }
        }

        String[] words = text.split("\\s+");
        for (String word : words) {
            if (word.length() > 6) {
                technicalTermCount++;
            }
        }

        double longSentenceRatio = sentences.length > 0 ? (double) longSentenceCount / sentences.length : 0;
        double technicalTermRatio = words.length > 0 ? (double) technicalTermCount / words.length : 0;

        double complexity = longSentenceRatio * 40 + technicalTermRatio * 40 + (1 - Math.min(totalChars / 10000.0, 1.0)) * 20;

        return Math.min(complexity, 100);
    }

    private List<ArticleDO> queryUserArticles(Long userId) {
        return articleDao.queryArticlePageList(1L, 20L, null, null, null, userId).getRecords();
    }

    private String buildArticleContext(List<ArticleDO> articles) {
        if (CollectionUtils.isEmpty(articles)) {
            return "暂无文章";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(articles.size(), 10); i++) {
            ArticleDO article = articles.get(i);
            sb.append("标题：").append(article.getTitle()).append("\n");
            if (article.getDescription() != null) {
                sb.append("摘要：").append(article.getDescription()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private List<String> parseTagList(String aiResponse, List<String> existingTags) {
        Set<String> existingSet = new HashSet<>(existingTags);
        return Arrays.stream(aiResponse.split("\\n"))
                .map(String::trim)
                .map(line -> line.replaceAll("^[\\d]+[\\.\\、\\s]+", ""))
                .filter(line -> !line.isEmpty())
                .filter(line -> !existingSet.contains(line))
                .filter(line -> line.length() <= 20)
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<String> parseCategoryList(String aiResponse, List<String> existingCategories) {
        Set<String> existingSet = new HashSet<>(existingCategories);
        return Arrays.stream(aiResponse.split("\\n"))
                .map(String::trim)
                .map(line -> line.replaceAll("^[\\d]+[\\.\\、\\s]+", ""))
                .filter(line -> !line.isEmpty())
                .filter(line -> !existingSet.contains(line))
                .filter(line -> line.length() <= 10)
                .limit(3)
                .collect(Collectors.toList());
    }

    private String buildArticleGenSystemPrompt(String userPrompt) {
        int requestedChars = extractRequestedCharCount(userPrompt);
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个专业的博客写作助手。根据用户的写作要求生成一篇高质量的博客文章。使用Markdown格式。");
        if (requestedChars >= 5000) {
            sb.append("用户要求文章字数约").append(requestedChars).append("字，请务必充分展开每个章节，每个小节至少写800-1500字，内容要详实、有深度、有案例。");
        }
        sb.append("文章应包含标题、引言、正文（分多个小节）和总结。");
        return sb.toString();
    }

    private String buildArticleGenPrompt(String prompt, String title, String categoryName, List<String> tags) {
        StringBuilder sb = new StringBuilder();
        sb.append("写作要求：").append(prompt).append("\n");
        if (title != null && !title.isEmpty()) {
            sb.append("建议标题：").append(title).append("\n");
        }
        if (categoryName != null && !categoryName.isEmpty()) {
            sb.append("文章分类：").append(categoryName).append("\n");
        }
        if (!CollectionUtils.isEmpty(tags)) {
            sb.append("文章标签：").append(String.join("、", tags)).append("\n");
        }
        int requestedChars = extractRequestedCharCount(prompt);
        if (requestedChars >= 5000) {
            sb.append("\n重要：用户明确要求文章约").append(requestedChars).append("字。请确保内容足够充实，每个章节都要详细展开，不要草草结束。");
        }
        sb.append("\n请生成一篇完整的博客文章。");
        return sb.toString();
    }

    private String extractTitle(String content, String fallbackTitle) {
        String[] lines = content.split("\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("# ") && !trimmed.startsWith("## ")) {
                return trimmed.substring(2).trim();
            }
        }
        return fallbackTitle != null && !fallbackTitle.isEmpty() ? fallbackTitle : "未命名文章";
    }

    private String cleanContent(String content) {
        return content.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
    }

    private void requireNonBlank(String text, String message) {
        if (text == null || text.trim().isEmpty()) {
            throw new BizException(ResponseCodeEnum.PARAM_ERROR.getErrorCode(), message);
        }
    }

    private Date getToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
