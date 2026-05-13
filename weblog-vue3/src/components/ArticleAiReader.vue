<template>
    <section class="front-card front-card-solid">
        <div class="front-card-body">
            <div class="front-section-title">AI 阅读助手</div>
            <h2 class="sidebar-title">智能阅读</h2>

            <div v-if="!summary && !loading && !error" class="ai-reader-empty">
                <p class="ai-reader-desc">AI 帮您快速了解本文核心内容</p>
                <el-button
                    type="primary"
                    size="small"
                    :loading="loading"
                    @click="fetchSummary"
                    class="ai-reader-btn"
                >
                    <el-icon><MagicStick /></el-icon>
                    生成摘要
                </el-button>
            </div>

            <div v-if="loading" class="ai-reader-loading">
                <el-icon class="is-loading"><Loading /></el-icon>
                <span>AI 正在阅读文章...</span>
            </div>

            <div v-if="error" class="ai-reader-error">
                <p>{{ error }}</p>
                <el-button size="small" @click="fetchSummary">重试</el-button>
            </div>

            <div v-if="summary" class="ai-reader-result">
                <div class="ai-reader-section">
                    <div class="ai-reader-label">文章摘要</div>
                    <p class="ai-reader-text">{{ summary.summary }}</p>
                </div>

                <div v-if="summary.keyPoints" class="ai-reader-section">
                    <div class="ai-reader-label">核心要点</div>
                    <ul class="ai-reader-points">
                        <li v-for="(point, idx) in keyPointList" :key="idx">{{ point }}</li>
                    </ul>
                </div>

                <div class="ai-reader-meta-row">
                    <span v-if="summary.readingTime" class="ai-reader-meta">
                        <el-icon><Timer /></el-icon>
                        {{ summary.readingTime }}
                    </span>
                    <span v-if="summary.difficulty" class="ai-reader-meta">
                        <el-icon><DataAnalysis /></el-icon>
                        {{ summary.difficulty }}
                    </span>
                </div>

                <el-button size="small" @click="resetSummary" class="ai-reader-reset-btn">
                    重新生成
                </el-button>
            </div>
        </div>
    </section>
</template>

<script setup>
import { computed, ref } from 'vue'
import { summarizeArticle } from '@/api/frontend/ai'

const props = defineProps({
    articleId: {
        type: Number,
        default: null
    }
})

const loading = ref(false)
const error = ref('')
const summary = ref(null)

const keyPointList = computed(() => {
    if (!summary.value || !summary.value.keyPoints) return []
    return summary.value.keyPoints
        .split(/[；;、\n]/)
        .map((point) => point.trim())
        .filter(Boolean)
})

async function fetchSummary() {
    if (!props.articleId) return
    loading.value = true
    error.value = ''
    summary.value = null
    try {
        const res = await summarizeArticle(props.articleId)
        if (res.success) {
            summary.value = res.data
        } else {
            error.value = res.message || 'AI 摘要生成失败'
        }
    } catch (e) {
        console.error('AI 摘要生成失败', e)
        error.value = 'AI 服务暂时不可用，请稍后重试'
    } finally {
        loading.value = false
    }
}

function resetSummary() {
    summary.value = null
    error.value = ''
}
</script>

<style scoped>
.ai-reader-empty {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.ai-reader-desc {
    font-size: 13px;
    color: #64748b;
    line-height: 1.6;
    margin: 0;
}

.ai-reader-btn {
    width: 100%;
}

.ai-reader-loading {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px 0;
    font-size: 13px;
    color: #64748b;
}

.ai-reader-error {
    display: flex;
    flex-direction: column;
    gap: 8px;
    font-size: 13px;
    color: #ef4444;
}

.ai-reader-error p {
    margin: 0;
}

.ai-reader-result {
    display: flex;
    flex-direction: column;
    gap: 14px;
}

.ai-reader-section {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.ai-reader-label {
    font-size: 12px;
    font-weight: 700;
    color: #0369a1;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.ai-reader-text {
    font-size: 13px;
    color: #334155;
    line-height: 1.7;
    margin: 0;
}

.ai-reader-points {
    margin: 0;
    padding-left: 18px;
    font-size: 13px;
    color: #334155;
    line-height: 1.8;
}

.ai-reader-points li {
    margin: 2px 0;
}

.ai-reader-meta-row {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
}

.ai-reader-meta {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: #64748b;
}

.ai-reader-reset-btn {
    width: 100%;
}
</style>
