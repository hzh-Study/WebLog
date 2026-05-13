package com.quanxiaoha.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quanxiaoha.weblog.admin.config.AiConfig;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.constant.ArticleVisibilityConstants;
import com.quanxiaoha.weblog.common.domain.dos.ArticleContentDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleContentMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.web.model.vo.article.ArticleAiSummarizeReqVO;
import com.quanxiaoha.weblog.web.model.vo.article.ArticleAiSummarizeRspVO;
import com.quanxiaoha.weblog.web.service.ArticleAiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ArticleAiServiceImpl implements ArticleAiService {

    @Autowired
    private AiConfig aiConfig;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleContentMapper articleContentMapper;

    private final RestTemplate restTemplate = createRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static RestTemplate createRestTemplate() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(20);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(5000)
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
    public Response<ArticleAiSummarizeRspVO> summarizeArticle(ArticleAiSummarizeReqVO reqVO) {
        Long articleId = reqVO.getArticleId();

        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (articleDO == null) {
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }
        if (!ArticleVisibilityConstants.PUBLIC.equalsIgnoreCase(articleDO.getVisibility())) {
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_PUBLIC);
        }

        ArticleContentDO contentDO = articleContentMapper.selectOne(
                new LambdaQueryWrapper<ArticleContentDO>().eq(ArticleContentDO::getArticleId, articleId));
        String content = contentDO != null ? contentDO.getContent() : "";

        String systemPrompt = "你是一个专业的文章阅读助手。请根据提供的文章内容，生成以下信息，以JSON格式返回：{\"summary\":\"文章摘要（200字以内）\",\"keyPoints\":\"核心要点（3-5条，用分号分隔）\",\"readingTime\":\"预计阅读时间（如：约5分钟）\",\"difficulty\":\"阅读难度（简单/中等/较难）\"}";

        StringBuilder userPromptBuilder = new StringBuilder();
        userPromptBuilder.append("文章标题：").append(articleDO.getTitle()).append("\n\n");
        userPromptBuilder.append("文章内容：\n").append(truncateContent(content, 8000));
        String userPrompt = userPromptBuilder.toString();

        try {
            String aiResponse = callAiApi(systemPrompt, userPrompt);

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

            String summary = "";
            String keyPoints = "";
            String readingTime = "";
            String difficulty = "";

            try {
                JsonNode root = objectMapper.readTree(cleanedJson);
                if (root.has("summary")) {
                    summary = root.get("summary").asText();
                }
                if (root.has("keyPoints")) {
                    keyPoints = root.get("keyPoints").asText();
                }
                if (root.has("readingTime")) {
                    readingTime = root.get("readingTime").asText();
                }
                if (root.has("difficulty")) {
                    difficulty = root.get("difficulty").asText();
                }
            } catch (Exception e) {
                log.warn("文章摘要JSON解析失败，使用原始响应: {}", e.getMessage());
                summary = aiResponse;
            }

            return Response.success(ArticleAiSummarizeRspVO.builder()
                    .summary(summary)
                    .keyPoints(keyPoints)
                    .readingTime(readingTime)
                    .difficulty(difficulty)
                    .build());
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("文章AI摘要生成失败", e);
            throw new BizException("AI_ERROR", "AI服务暂时不可用，请稍后重试");
        }
    }

    private String callAiApi(String systemPrompt, String userPrompt) {
        try {
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
            requestBody.put("max_tokens", aiConfig.getMaxTokensPerRequest());
            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiConfig.getApiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("前台AI API请求 - model: {}, prompt长度: {}", aiConfig.getModel(), userPrompt.length());

            ResponseEntity<String> response = restTemplate.exchange(
                    aiConfig.getApiUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            if (responseBody == null) {
                log.error("前台AI API返回空响应体");
                throw new BizException("AI_ERROR", "AI服务返回空响应");
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(responseBody);

                JsonNode error = root.get("error");
                if (error != null) {
                    String errorMsg = error.has("message") ? error.get("message").asText() : error.toString();
                    log.error("前台AI API业务错误: {}", errorMsg);
                    throw new BizException("AI_ERROR", "AI服务错误: " + errorMsg);
                }

                JsonNode choices = root.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode message = choices.get(0).get("message");
                    if (message != null) {
                        String content = message.get("content").asText();
                        log.info("前台AI API响应成功 - 响应长度: {}", content.length());
                        return content;
                    }
                }

                log.error("前台AI API返回格式异常");
                throw new BizException("AI_ERROR", "AI服务返回格式异常");
            }

            log.error("前台AI API返回非200 - status: {}", response.getStatusCode());
            throw new BizException("AI_ERROR", "AI服务返回异常(" + response.getStatusCodeValue() + ")");
        } catch (BizException e) {
            throw e;
        } catch (RestClientResponseException e) {
            log.error("前台AI API请求失败 - status: {}", e.getRawStatusCode());
            throw new BizException("AI_ERROR", "AI服务返回错误(" + e.getRawStatusCode() + ")");
        } catch (Exception e) {
            log.error("前台AI API调用失败", e);
            throw new BizException("AI_ERROR", "AI服务调用失败: " + e.getMessage());
        }
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "\n...(内容已截断)";
    }
}
