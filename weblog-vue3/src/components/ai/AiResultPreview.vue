<template>
    <div class="ai-result-preview">
        <div class="ai-result-preview-header">
            <span>{{ title }}</span>
            <div class="ai-result-preview-meta">
                <el-tag v-if="tokens > 0" size="small" type="info">消耗 {{ tokens }} tokens</el-tag>
                <slot name="header-actions"></slot>
            </div>
        </div>
        <div class="ai-result-preview-body">
            <slot>
                <div class="ai-result-preview-text">{{ content }}</div>
            </slot>
        </div>
        <div v-if="$slots.actions" class="ai-result-preview-actions">
            <slot name="actions"></slot>
        </div>
    </div>
</template>

<script setup>
defineProps({
    title: { type: String, default: '结果' },
    content: { type: String, default: '' },
    tokens: { type: Number, default: 0 }
})
</script>

<style scoped>
.ai-result-preview {
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    overflow: hidden;
}

.ai-result-preview-header {
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

.ai-result-preview-meta {
    display: flex;
    align-items: center;
    gap: 8px;
}

.ai-result-preview-body {
    padding: 14px;
    font-size: 14px;
    line-height: 1.8;
    color: #334155;
    max-height: 400px;
    overflow-y: auto;
}

.ai-result-preview-text {
    white-space: pre-wrap;
    word-break: break-all;
}

.ai-result-preview-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding: 12px 14px;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
}
</style>