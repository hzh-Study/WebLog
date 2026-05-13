<template>
    <div class="ai-rewrite-panel">
        <el-form label-position="top">
            <el-form-item label="原始文本">
                <el-input
                    :model-value="text"
                    @update:model-value="$emit('update:text', $event)"
                    type="textarea"
                    :rows="5"
                    placeholder="请输入或粘贴需要处理的文本..."
                    maxlength="5000"
                    show-word-limit
                />
            </el-form-item>
            <el-form-item label="操作类型">
                <el-radio-group :model-value="editType" @update:model-value="$emit('update:editType', $event)">
                    <el-radio-button value="rewrite">改写</el-radio-button>
                    <el-radio-button value="polish">润色</el-radio-button>
                    <el-radio-button value="continue">续写</el-radio-button>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="附加指令（可选）">
                <el-input
                    :model-value="instruction"
                    @update:model-value="$emit('update:instruction', $event)"
                    type="textarea"
                    :rows="2"
                    placeholder="描述额外要求，如：更正式、更口语化..."
                    maxlength="500"
                    show-word-limit
                />
            </el-form-item>
        </el-form>

        <el-button
            type="primary"
            :loading="loading"
            :disabled="disabled"
            @click="$emit('execute')"
            class="ai-execute-btn"
        >
            <el-icon><MagicStick /></el-icon>
            {{ editTypeLabel }}
        </el-button>

        <div v-if="result" class="ai-result-area">
            <div class="ai-compare-container">
                <div class="ai-compare-panel">
                    <div class="ai-compare-header">原文</div>
                    <div class="ai-compare-content"><pre class="ai-compare-text">{{ text }}</pre></div>
                </div>
                <div class="ai-compare-divider">
                    <el-icon><DArrowRight /></el-icon>
                </div>
                <div class="ai-compare-panel">
                    <div class="ai-compare-header ai-compare-header--result">结果</div>
                    <div class="ai-compare-content"><pre class="ai-compare-text">{{ result }}</pre></div>
                </div>
            </div>
            <div class="ai-result-actions">
                <slot name="actions" :result="result" :text="text">
                    <el-button size="small" @click="$emit('copy', result)">
                        <el-icon><CopyDocument /></el-icon>
                        复制结果
                    </el-button>
                    <el-button size="small" type="primary" @click="$emit('replace')">
                        <el-icon><Switch /></el-icon>
                        替换原文
                    </el-button>
                    <el-button size="small" @click="$emit('append')">
                        <el-icon><Bottom /></el-icon>
                        追加到原文
                    </el-button>
                </slot>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    text: { type: String, default: '' },
    editType: { type: String, default: 'rewrite' },
    instruction: { type: String, default: '' },
    loading: { type: Boolean, default: false },
    disabled: { type: Boolean, default: false },
    result: { type: String, default: '' }
})

defineEmits(['update:text', 'update:editType', 'update:instruction', 'execute', 'copy', 'replace', 'append'])

const editTypeLabel = computed(() => {
    const map = { rewrite: '开始改写', polish: '开始润色', continue: '开始续写' }
    return map[props.editType] || '执行'
})
</script>

<style scoped>
.ai-rewrite-panel {
    display: flex;
    flex-direction: column;
}

.ai-execute-btn {
    width: 100%;
}

.ai-result-area {
    margin-top: 20px;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    overflow: hidden;
}

.ai-compare-container {
    display: flex;
    gap: 0;
    min-height: 200px;
}

.ai-compare-panel {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

.ai-compare-header {
    padding: 8px 14px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    font-size: 12px;
    font-weight: 600;
    color: #64748b;
}

.ai-compare-header--result {
    background: #f0fdf4;
    border-bottom-color: #bbf7d0;
    color: #166534;
}

.ai-compare-content {
    flex: 1;
    overflow-y: auto;
    padding: 12px 14px;
    max-height: 300px;
}

.ai-compare-text {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-all;
    font-size: 13px;
    line-height: 1.7;
    color: #334155;
    font-family: inherit;
}

.ai-compare-divider {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    flex-shrink: 0;
    color: #94a3b8;
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