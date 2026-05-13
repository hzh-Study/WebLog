<template>
    <div class="ai-writer-panel">
        <el-form label-position="top">
            <el-form-item label="写作要求">
                <el-input
                    :model-value="prompt"
                    @update:model-value="$emit('update:prompt', $event)"
                    type="textarea"
                    :rows="4"
                    placeholder="请描述您想要写的文章主题、风格、要点等..."
                    maxlength="2000"
                    show-word-limit
                />
            </el-form-item>
            <el-form-item label="建议标题（可选）">
                <el-input
                    :model-value="title"
                    @update:model-value="$emit('update:title', $event)"
                    placeholder="留空则由AI自动生成"
                    maxlength="100"
                />
            </el-form-item>
            <el-form-item label="文章分类（可选）">
                <el-select
                    :model-value="category"
                    @update:model-value="$emit('update:category', $event)"
                    placeholder="请选择文章分类"
                    clearable
                    style="width:100%"
                >
                    <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.name" />
                </el-select>
            </el-form-item>
            <el-form-item label="文章标签（可选）">
                <el-select
                    :model-value="tags"
                    @update:model-value="$emit('update:tags', $event)"
                    placeholder="请选择文章标签"
                    multiple
                    collapse-tags
                    collapse-tags-tooltip
                    style="width:100%"
                >
                    <el-option v-for="tag in presetTags" :key="tag" :label="tag" :value="tag" />
                </el-select>
            </el-form-item>
        </el-form>

        <div v-if="estimatedTokens > 0" class="ai-estimate-info">
            <el-icon><InfoFilled /></el-icon>
            <span>预估消耗约 <strong>{{ estimatedTokens }}</strong> tokens</span>
        </div>

        <el-button
            type="primary"
            :loading="loading"
            :disabled="disabled"
            @click="$emit('generate')"
            class="ai-generate-btn"
        >
            <el-icon><MagicStick /></el-icon>
            生成文章
        </el-button>

        <div v-if="result" class="ai-result-area">
            <div class="ai-result-header">
                <span>生成结果</span>
                <el-tag size="small" type="info">实际消耗 {{ resultTokens }} tokens</el-tag>
            </div>
            <div class="ai-result-title">{{ result.title }}</div>
            <div class="ai-result-content" v-html="resultPreview"></div>
            <div class="ai-result-actions">
                <slot name="actions" :result="result">
                    <el-button size="small" @click="$emit('copy', 'title', result.title)">
                        <el-icon><CopyDocument /></el-icon>
                        复制标题
                    </el-button>
                    <el-button size="small" @click="$emit('copy', 'content', result.content)">
                        <el-icon><CopyDocument /></el-icon>
                        复制正文
                    </el-button>
                    <el-button size="small" type="success" @click="$emit('save', 'PUBLIC')">
                        <el-icon><Promotion /></el-icon>
                        保存为文章
                    </el-button>
                    <el-button size="small" type="warning" @click="$emit('save', 'PRIVATE')">
                        <el-icon><Edit /></el-icon>
                        保存为草稿
                    </el-button>
                </slot>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    prompt: { type: String, default: '' },
    title: { type: String, default: '' },
    category: { type: String, default: '' },
    tags: { type: Array, default: () => [] },
    categories: { type: Array, default: () => [] },
    presetTags: { type: Array, default: () => [] },
    estimatedTokens: { type: Number, default: 0 },
    loading: { type: Boolean, default: false },
    disabled: { type: Boolean, default: false },
    result: { type: Object, default: null },
    resultTokens: { type: Number, default: 0 }
})

defineEmits(['update:prompt', 'update:title', 'update:category', 'update:tags', 'generate', 'copy', 'save'])

const resultPreview = computed(() => {
    if (!props.result || !props.result.content) return ''
    return props.result.content
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br>')
})
</script>

<style scoped>
.ai-writer-panel {
    display: flex;
    flex-direction: column;
}

.ai-estimate-info {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 12px;
    margin-bottom: 12px;
    background: #f0f9ff;
    border-radius: 8px;
    color: #0369a1;
    font-size: 13px;
}

.ai-generate-btn {
    width: 100%;
}

.ai-result-area {
    margin-top: 20px;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    overflow: hidden;
}

.ai-result-header {
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

.ai-result-title {
    padding: 14px 14px 0;
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
}

.ai-result-content {
    padding: 14px;
    font-size: 14px;
    line-height: 1.8;
    color: #334155;
    max-height: 400px;
    overflow-y: auto;
}

.ai-result-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding: 12px 14px;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
}
</style>