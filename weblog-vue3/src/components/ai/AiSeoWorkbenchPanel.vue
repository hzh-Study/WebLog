<template>
    <div class="ai-seo-panel">
        <el-form label-position="top">
            <el-form-item label="文章标题">
                <el-input
                    :model-value="seoTitle"
                    @update:model-value="$emit('update:seoTitle', $event)"
                    placeholder="请输入文章标题"
                    maxlength="100"
                />
            </el-form-item>
            <el-form-item label="文章正文">
                <el-input
                    :model-value="seoContent"
                    @update:model-value="$emit('update:seoContent', $event)"
                    type="textarea"
                    :rows="5"
                    placeholder="请输入文章正文..."
                    maxlength="10000"
                    show-word-limit
                />
            </el-form-item>
            <el-form-item label="当前摘要（可选）">
                <el-input
                    :model-value="seoDescription"
                    @update:model-value="$emit('update:seoDescription', $event)"
                    placeholder="当前文章摘要"
                    maxlength="200"
                />
            </el-form-item>
        </el-form>

        <el-button
            type="primary"
            :loading="loading"
            :disabled="disabled"
            @click="$emit('optimize')"
            class="ai-optimize-btn"
        >
            <el-icon><MagicStick /></el-icon>
            开始优化
        </el-button>

        <div v-if="result" class="ai-result-area">
            <div class="ai-seo-section">
                <div class="ai-seo-section-header">
                    <span>优化标题</span>
                    <el-button size="small" type="primary" @click="$emit('applyTitle')">应用</el-button>
                </div>
                <div class="ai-seo-section-content">{{ result.optimizedTitle }}</div>
            </div>
            <div class="ai-seo-section">
                <div class="ai-seo-section-header">
                    <span>优化摘要</span>
                    <el-button size="small" type="primary" @click="$emit('applyDescription')">应用</el-button>
                </div>
                <div class="ai-seo-section-content">{{ result.optimizedDescription }}</div>
            </div>
            <div class="ai-seo-section">
                <div class="ai-seo-section-header">
                    <span>关键词</span>
                    <el-button size="small" @click="$emit('copyKeywords', result.keywords)">
                        <el-icon><CopyDocument /></el-icon>
                        复制
                    </el-button>
                </div>
                <div class="ai-seo-keywords">
                    <el-tag v-for="kw in result.keywords" :key="kw" class="ai-seo-keyword-tag">{{ kw }}</el-tag>
                </div>
            </div>
            <div class="ai-seo-meta">
                <span>标题评分：</span>
                <el-progress :percentage="result.titleScore" :color="scoreColor(result.titleScore)" :stroke-width="8" style="width:200px" />
                <strong>{{ result.titleScore }} 分</strong>
            </div>
            <div v-if="result.titleSuggestion" class="ai-seo-section">
                <div class="ai-seo-section-header"><span>标题建议</span></div>
                <div class="ai-seo-section-content ai-seo-suggestion">{{ result.titleSuggestion }}</div>
            </div>
        </div>
    </div>
</template>

<script setup>
defineProps({
    seoTitle: { type: String, default: '' },
    seoContent: { type: String, default: '' },
    seoDescription: { type: String, default: '' },
    loading: { type: Boolean, default: false },
    disabled: { type: Boolean, default: false },
    result: { type: Object, default: null }
})

defineEmits(['update:seoTitle', 'update:seoContent', 'update:seoDescription', 'optimize', 'applyTitle', 'applyDescription', 'copyKeywords'])

function scoreColor(score) {
    if (score >= 80) return '#22c55e'
    if (score >= 60) return '#f59e0b'
    return '#ef4444'
}
</script>

<style scoped>
.ai-seo-panel {
    display: flex;
    flex-direction: column;
}

.ai-optimize-btn {
    width: 100%;
}

.ai-result-area {
    margin-top: 20px;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    overflow: hidden;
}

.ai-seo-section {
    border-bottom: 1px solid #e2e8f0;
}

.ai-seo-section:last-child {
    border-bottom: none;
}

.ai-seo-section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 14px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    font-size: 13px;
    font-weight: 600;
    color: #475569;
}

.ai-seo-section-content {
    padding: 14px;
    font-size: 14px;
    line-height: 1.7;
    color: #334155;
}

.ai-seo-suggestion {
    color: #64748b;
    font-style: italic;
}

.ai-seo-keywords {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding: 14px;
}

.ai-seo-keyword-tag {
    font-size: 13px;
}

.ai-seo-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    font-size: 13px;
    color: #475569;
}
</style>