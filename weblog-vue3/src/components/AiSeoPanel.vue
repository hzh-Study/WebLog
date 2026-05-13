<template>
    <el-dialog
        :model-value="modelValue"
        title="AI SEO 优化"
        width="640px"
        :close-on-click-modal="false"
        :close-on-press-escape="!analyzing"
        :show-close="!analyzing"
        @update:model-value="emit('update:modelValue', $event)"
        class="ai-seo-dialog"
    >
        <div v-loading="analyzing" class="ai-seo-body">
            <div v-if="result" class="ai-seo-result">
                <div class="ai-seo-section">
                    <div class="ai-seo-section-header">
                        <span class="ai-seo-section-title">优化标题</span>
                        <el-button size="small" type="primary" @click="applyTitle">
                            应用
                        </el-button>
                    </div>
                    <div class="ai-seo-section-content">{{ result.optimizedTitle }}</div>
                </div>

                <div class="ai-seo-section">
                    <div class="ai-seo-section-header">
                        <span class="ai-seo-section-title">优化摘要</span>
                        <el-button size="small" type="primary" @click="applyDescription">
                            应用
                        </el-button>
                    </div>
                    <div class="ai-seo-section-content">{{ result.optimizedDescription }}</div>
                </div>

                <div class="ai-seo-section">
                    <div class="ai-seo-section-header">
                        <span class="ai-seo-section-title">关键词建议</span>
                    </div>
                    <div class="ai-seo-keywords">
                        <el-tag
                            v-for="kw in result.keywords"
                            :key="kw"
                            class="ai-seo-keyword-tag"
                        >
                            {{ kw }}
                        </el-tag>
                    </div>
                </div>

                <div class="ai-seo-meta">
                    <div class="ai-seo-meta-item">
                        <span class="ai-seo-meta-label">原标题评分</span>
                        <el-progress
                            :percentage="result.titleScore"
                            :color="scoreColor(result.titleScore)"
                            :stroke-width="8"
                            style="width: 200px"
                        />
                        <span class="ai-seo-meta-value">{{ result.titleScore }} 分</span>
                    </div>
                </div>

                <div class="ai-seo-section" v-if="result.titleSuggestion">
                    <div class="ai-seo-section-header">
                        <span class="ai-seo-section-title">标题建议</span>
                    </div>
                    <div class="ai-seo-section-content ai-seo-suggestion">{{ result.titleSuggestion }}</div>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { showMessage } from '@/composables/util'
import { aiSeoOptimize } from '@/api/admin/ai'

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false
    },
    title: {
        type: String,
        default: ''
    },
    content: {
        type: String,
        default: ''
    },
    currentDescription: {
        type: String,
        default: ''
    }
})

const emit = defineEmits(['update:modelValue', 'apply-title', 'apply-description'])

const analyzing = ref(false)
const result = ref(null)

watch(() => props.modelValue, async (val) => {
    if (val && props.title && props.content) {
        await doOptimize()
    }
})

async function doOptimize() {
    analyzing.value = true
    result.value = null
    try {
        const res = await aiSeoOptimize({
            title: props.title,
            content: props.content,
            currentDescription: props.currentDescription || null
        }, { timeout: 120000 })
        if (res.success) {
            result.value = res.data
        } else {
            showMessage(res.message || 'SEO 优化失败', 'error')
            emit('update:modelValue', false)
        }
    } catch (e) {
        console.error('SEO 优化失败', e)
        showMessage('SEO 优化请求失败，请稍后重试', 'error')
        emit('update:modelValue', false)
    } finally {
        analyzing.value = false
    }
}

function applyTitle() {
    if (result.value && result.value.optimizedTitle) {
        emit('apply-title', result.value.optimizedTitle)
        showMessage('标题已应用', 'success')
    }
}

function applyDescription() {
    if (result.value && result.value.optimizedDescription) {
        emit('apply-description', result.value.optimizedDescription)
        showMessage('摘要已应用', 'success')
    }
}

function scoreColor(score) {
    if (score >= 80) return '#22c55e'
    if (score >= 60) return '#f59e0b'
    return '#ef4444'
}
</script>

<style scoped>
.ai-seo-body {
    min-height: 160px;
}

.ai-seo-result {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.ai-seo-section {
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    overflow: hidden;
}

.ai-seo-section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 14px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
}

.ai-seo-section-title {
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
    padding: 14px 16px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
}

.ai-seo-meta-item {
    display: flex;
    align-items: center;
    gap: 12px;
}

.ai-seo-meta-label {
    font-size: 13px;
    font-weight: 600;
    color: #475569;
    white-space: nowrap;
}

.ai-seo-meta-value {
    font-size: 14px;
    font-weight: 700;
    color: #1e293b;
    white-space: nowrap;
}
</style>