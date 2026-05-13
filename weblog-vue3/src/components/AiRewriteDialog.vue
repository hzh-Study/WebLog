<template>
    <el-dialog
        :model-value="modelValue"
        :title="title"
        width="800px"
        :close-on-click-modal="false"
        :close-on-press-escape="!loading"
        :show-close="!loading"
        @update:model-value="emit('update:modelValue', $event)"
        class="ai-rewrite-dialog"
    >
        <div v-loading="loading" class="ai-rewrite-body">
            <div class="ai-compare-container">
                <div class="ai-compare-panel">
                    <div class="ai-compare-header">
                        <el-icon><Document /></el-icon>
                        <span>原文</span>
                    </div>
                    <div class="ai-compare-content">
                        <pre class="ai-compare-text">{{ originalText }}</pre>
                    </div>
                </div>
                <div class="ai-compare-divider">
                    <el-icon><DArrowRight /></el-icon>
                </div>
                <div class="ai-compare-panel">
                    <div class="ai-compare-header ai-compare-header--result">
                        <el-icon><MagicStick /></el-icon>
                        <span>结果</span>
                    </div>
                    <div class="ai-compare-content">
                        <pre class="ai-compare-text">{{ resultText }}</pre>
                    </div>
                </div>
            </div>
        </div>
        <template #footer>
            <el-button @click="emit('cancel')">保留原稿</el-button>
            <el-button type="primary" @click="emit('replace')" :disabled="!resultText">
                替换
            </el-button>
        </template>
    </el-dialog>
</template>

<script setup>
const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false
    },
    originalText: {
        type: String,
        default: ''
    },
    resultText: {
        type: String,
        default: ''
    },
    title: {
        type: String,
        default: 'AI 改写'
    },
    loading: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['update:modelValue', 'replace', 'cancel'])
</script>

<style scoped>
.ai-rewrite-body {
    min-height: 200px;
}

.ai-compare-container {
    display: flex;
    gap: 0;
    min-height: 300px;
}

.ai-compare-panel {
    flex: 1;
    min-width: 0;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.ai-compare-header {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 10px 14px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    font-size: 13px;
    font-weight: 600;
    color: #475569;
}

.ai-compare-header--result {
    background: #f0fdf4;
    border-bottom-color: #bbf7d0;
    color: #166534;
}

.ai-compare-content {
    flex: 1;
    overflow-y: auto;
    padding: 14px;
}

.ai-compare-text {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-all;
    font-size: 14px;
    line-height: 1.7;
    color: #334155;
    font-family: inherit;
}

.ai-compare-divider {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    flex-shrink: 0;
    color: #94a3b8;
}
</style>