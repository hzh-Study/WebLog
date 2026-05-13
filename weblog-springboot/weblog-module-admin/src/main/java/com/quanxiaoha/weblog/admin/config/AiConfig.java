package com.quanxiaoha.weblog.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiConfig {
    private String apiKey;
    private String apiUrl;
    private String model;

    private Integer maxTokensPerRequest = 131072;
    private Integer maxTokensPerRequestUpper = 131072;

    private Integer dailyArticleGenLimit = 5;
    private Integer dailyArticleGenLimitBase = 4;

    private Integer interactionIntervalSeconds = 60;
    private Long dailyTokenLimit = 200000L;

    private Integer tokenWarningThresholdPercent = 20;

    private Integer contentComplexityThreshold = 70;
    private Double complexityBufferPercent = 10.0;
}
