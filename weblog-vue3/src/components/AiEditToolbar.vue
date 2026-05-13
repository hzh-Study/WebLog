<template>
    <div class="ai-edit-toolbar">
        <el-button type="primary" size="small" @click="handleContinue">
            <el-icon><MagicStick /></el-icon>
            AI 续写
        </el-button>
        <el-button type="warning" size="small" @click="handleRewrite">
            <el-icon><MagicStick /></el-icon>
            AI 改写
        </el-button>
        <el-button type="success" size="small" @click="handlePolish">
            <el-icon><MagicStick /></el-icon>
            AI 润色
        </el-button>

        <el-dialog
            v-model="continueDialogVisible"
            title="AI 续写"
            width="480px"
            :close-on-click-modal="false"
        >
            <el-form label-position="top">
                <el-form-item label="续写指令（可选）">
                    <el-input
                        v-model="continueInstruction"
                        type="textarea"
                        :rows="3"
                        placeholder="描述续写方向，如：介绍下一步优化方案"
                        maxlength="500"
                        show-word-limit
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="continueDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmContinue" :loading="continueLoading">
                    开始续写
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
    editorId: {
        type: String,
        default: 'publishArticleEditor'
    }
})

const emit = defineEmits(['continue-text', 'rewrite-result', 'polish-result'])

const continueDialogVisible = ref(false)
const continueInstruction = ref('')
const continueLoading = ref(false)

function handleContinue() {
    continueInstruction.value = ''
    continueDialogVisible.value = true
}

function confirmContinue() {
    continueLoading.value = true
    emit('continue-text', continueInstruction.value)
    continueDialogVisible.value = false
    continueLoading.value = false
}

function handleRewrite() {
    emit('rewrite-result')
}

function handlePolish() {
    emit('polish-result')
}
</script>

<style scoped>
.ai-edit-toolbar {
    display: flex;
    gap: 8px;
    padding: 8px 0;
}
</style>