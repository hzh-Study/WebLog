<template>
    <div class="ai-creator-dialog">
        <button class="ai-creator-btn" @click="openDialog">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="btn-icon">
                <path d="M12 5v14M5 12h14"/>
                <path d="M9 3h6l2 2h3a1 1 0 011 1v14a1 1 0 01-1 1H4a1 1 0 01-1-1V6a1 1 0 011-1h3l2-2z"/>
            </svg>
            <span class="btn-text">添加文章</span>
        </button>

        <Teleport to="body">
            <Transition name="dialog">
                <div v-if="visible" class="dialog-overlay" @click.self="handleOverlayClick">
                    <div class="dialog-panel">
                        <div class="dialog-header">
                            <div class="header-left">
                                <div class="header-icon-wrap">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="header-icon">
                                        <path d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/>
                                    </svg>
                                </div>
                                <div class="header-text">
                                    <h2 class="header-title">AI 智能写作助手</h2>
                                    <p class="header-desc">从系统预定义分类和标签中选择，AI将为您生成高质量文章</p>
                                </div>
                            </div>
                            <button class="close-btn" @click="closeDialog">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                                </svg>
                            </button>
                        </div>

                        <div class="dialog-body">
                            <div class="quota-bar" v-if="quotaLoaded">
                                <div class="quota-item">
                                    <span class="quota-label">文章生成</span>
                                    <span class="quota-value" :class="{ 'quota-exhausted': quota.dailyArticleGenRemaining === 0 }">
                                        {{ quota.dailyArticleGenRemaining }}/{{ quota.dailyArticleGenLimit }}
                                    </span>
                                </div>
                                <div class="quota-divider"></div>
                                <div class="quota-item">
                                    <span class="quota-label">Token</span>
                                    <span class="quota-value" :class="{ 'quota-exhausted': quota.dailyTokenRemaining < 10000 }">
                                        {{ formatQuotaNumber(quota.dailyTokenRemaining) }}
                                    </span>
                                </div>
                                <div class="quota-divider"></div>
                                <div class="quota-item">
                                    <span class="quota-label">冷却</span>
                                    <span class="quota-value" :class="{ 'quota-exhausted': !quota.canInteractNow }">
                                        {{ quota.canInteractNow ? '就绪' : cooldownDisplay }}
                                    </span>
                                </div>
                                <button class="quota-refresh" @click="refreshQuota" :disabled="quotaRefreshing">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ 'spin': quotaRefreshing }">
                                        <polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 11-2.12-9.36L23 10"/>
                                    </svg>
                                </button>
                            </div>

                            <div class="form-section">
                                <div class="form-group">
                                    <label class="form-label">
                                        文章标题
                                        <span class="required">*</span>
                                    </label>
                                    <input
                                        ref="titleInputRef"
                                        v-model="form.title"
                                        type="text"
                                        class="form-input"
                                        :class="{ 'input-error': validation.title }"
                                        placeholder="输入文章标题..."
                                        maxlength="100"
                                        @input="clearFieldError('title')"
                                    />
                                    <Transition name="error">
                                        <p v-if="validation.title" class="field-error">{{ validation.title }}</p>
                                    </Transition>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">封面图片 <span class="hint">（可选）</span></label>
                                    <div class="image-upload">
                                        <div class="image-preview" v-if="form.titleImage">
                                            <img :src="form.titleImage" alt="封面预览" />
                                            <button class="image-remove" @click="removeImage">
                                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                                                </svg>
                                            </button>
                                        </div>
                                        <label class="image-upload-btn" v-else :class="{ 'uploading': imageUploading }">
                                            <input type="file" accept="image/*" class="image-input-hidden" @change="handleImageUpload" :disabled="imageUploading" />
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="upload-icon">
                                                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                                                <circle cx="8.5" cy="8.5" r="1.5"/>
                                                <polyline points="21 15 16 10 5 21"/>
                                            </svg>
                                            <span>{{ imageUploading ? '上传中...' : '点击上传封面图片' }}</span>
                                        </label>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <TaxonomyCategorySelect
                                        v-model="form.categoryId"
                                        label="文章分类"
                                        hint="（必选，从系统预定义分类中选择）"
                                        @change="handleCategoryChange"
                                    />
                                    <Transition name="error">
                                        <p v-if="validation.category" class="field-error">{{ validation.category }}</p>
                                    </Transition>
                                </div>

                                <div class="form-group">
                                    <TaxonomyTagSelector
                                        v-model="selectedTags"
                                        :category-id="form.categoryId"
                                        :max-tags="5"
                                        :min-tags="3"
                                        label="文章标签"
                                        hint="（需选3-5个，从系统预定义标签中选择）"
                                        @change="handleTagsChange"
                                    />
                                    <Transition name="error">
                                        <p v-if="validation.tags" class="field-error">{{ validation.tags }}</p>
                                    </Transition>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">
                                        AI 写作要求
                                        <span class="required">*</span>
                                    </label>
                                    <textarea
                                        v-model="form.aiPrompt"
                                        class="form-textarea"
                                        :class="{ 'input-error': validation.aiPrompt }"
                                        placeholder="描述您想要的文章内容、风格、结构等，AI 将根据此要求生成文章..."
                                        rows="5"
                                        maxlength="2000"
                                        @input="onPromptInput"
                                    ></textarea>
                                    <div class="textarea-footer">
                                        <span class="char-count">{{ form.aiPrompt.length }}/2000</span>
                                        <Transition name="error">
                                            <p v-if="validation.aiPrompt" class="field-error field-error-inline">{{ validation.aiPrompt }}</p>
                                        </Transition>
                                    </div>
                                </div>

                                <div v-if="tokenEstimate > 0" class="token-estimate">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="estimate-icon">
                                        <circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>
                                    </svg>
                                    <span>预估消耗约 <strong>{{ tokenEstimate }}</strong> tokens</span>
                                </div>

                                <button
                                    class="generate-btn"
                                    :class="{ 'generating': aiGenerating }"
                                    :disabled="!canGenerate"
                                    @click="handleAiGenerate"
                                >
                                    <span v-if="!aiGenerating" class="btn-content">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="btn-icon">
                                            <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                                        </svg>
                                        生成文章
                                    </span>
                                    <span v-else class="btn-content">
                                        <span class="spinner"></span>
                                        AI 正在创作中...
                                    </span>
                                </button>
                            </div>

                            <Transition name="preview">
                                <div v-if="generatedContent" class="preview-section">
                                    <div class="preview-header">
                                        <h3 class="preview-title">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="preview-icon">
                                                <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
                                                <polyline points="14 2 14 8 20 8"/>
                                            </svg>
                                            生成结果预览
                                        </h3>
                                        <span class="preview-meta">实际消耗 {{ generatedTokens }} tokens</span>
                                    </div>
                                    <div class="preview-content">
                                        <div class="preview-article-title">{{ generatedTitle }}</div>
                                        <div class="preview-article-body" v-html="renderedPreview"></div>
                                    </div>
                                </div>
                            </Transition>
                        </div>

                        <div class="dialog-footer">
                            <div class="footer-errors" v-if="footerErrors.length > 0">
                                <TransitionGroup name="error-item">
                                    <div v-for="err in footerErrors" :key="err" class="footer-error">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="error-icon">
                                            <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
                                        </svg>
                                        {{ err }}
                                    </div>
                                </TransitionGroup>
                            </div>
                            <div class="footer-actions">
                                <button class="btn-cancel" @click="closeDialog">取消</button>
                                <button
                                    class="btn-submit"
                                    :class="{ 'loading': submitting }"
                                    :disabled="submitting || !isFormValid"
                                    @click="handleSubmit"
                                >
                                    <span v-if="!submitting">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="btn-icon">
                                            <polyline points="20 6 9 17 4 12"/>
                                        </svg>
                                        发布文章
                                    </span>
                                    <span v-else>
                                        <span class="spinner spinner-sm"></span>
                                        发布中...
                                    </span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </Transition>
        </Teleport>
    </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { showMessage } from '@/composables/util'
import { generateArticle, estimateTokens, getAiQuota } from '@/api/admin/ai'
import { publishArticle } from '@/api/admin/article'
import { uploadFile } from '@/api/admin/file'
import { resolveAiCover } from '@/constants/ai'
import TaxonomyCategorySelect from './TaxonomyCategorySelect.vue'
import TaxonomyTagSelector from './TaxonomyTagSelector.vue'

const emit = defineEmits(['created'])

const visible = ref(false)
const aiGenerating = ref(false)
const submitting = ref(false)
const imageUploading = ref(false)
const quotaRefreshing = ref(false)
const quotaLoaded = ref(false)
const titleInputRef = ref(null)

const form = reactive({
    title: '',
    categoryId: null,
    aiPrompt: '',
    titleImage: ''
})

const selectedTags = ref([])

const generatedContent = ref('')
const generatedTitle = ref('')
const generatedTokens = ref(0)
const tokenEstimate = ref(0)

const quota = ref({
    dailyArticleGenLimit: 2,
    dailyArticleGenUsed: 0,
    dailyArticleGenRemaining: 2,
    dailyTokenLimit: 100000,
    dailyTokenUsed: 0,
    dailyTokenRemaining: 100000,
    lastInteractionTime: null,
    interactionIntervalSeconds: 60,
    canInteractNow: true,
    remainingCooldownSeconds: 0
})

const cooldownDisplay = ref('')
let cooldownTimer = null

const validation = reactive({
    title: '',
    category: '',
    tags: '',
    aiPrompt: ''
})

let estimateTimer = null

const canGenerate = computed(() => {
    return form.aiPrompt.trim().length > 0 && !aiGenerating.value
})

const selectedTagIds = computed(() => selectedTags.value.map(t => t.id))

const isFormValid = computed(() => {
    return form.title.trim().length > 0
        && form.categoryId
        && selectedTags.value.length >= 3
        && selectedTags.value.length <= 5
        && generatedContent.value.length > 0
})

const footerErrors = computed(() => {
    const errors = []
    if (!generatedContent.value) {
        return errors
    }
    if (!form.title.trim()) {
        errors.push('请输入文章标题')
    }
    if (!form.categoryId) {
        errors.push('请选择文章分类')
    }
    if (selectedTags.value.length < 3) {
        errors.push(`请至少选择3个标签（当前${selectedTags.value.length}个）`)
    } else if (selectedTags.value.length > 5) {
        errors.push('标签数量不能超过5个')
    }
    return errors
})

const renderedPreview = computed(() => {
    if (!generatedContent.value) return ''
    return generatedContent.value
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br>')
})

function formatQuotaNumber(num) {
    if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
    }
    return num.toLocaleString()
}

async function refreshQuota() {
    quotaRefreshing.value = true
    try {
        const res = await getAiQuota()
        if (res.success && res.data) {
            quota.value = res.data
            quotaLoaded.value = true
            updateCooldown()
        }
    } catch (e) {
        console.error('获取额度失败', e)
    } finally {
        quotaRefreshing.value = false
    }
}

function updateCooldown() {
    if (cooldownTimer) clearInterval(cooldownTimer)
    if (!quota.value.canInteractNow && quota.value.remainingCooldownSeconds > 0) {
        let remaining = quota.value.remainingCooldownSeconds
        cooldownDisplay.value = formatCooldown(remaining)
        cooldownTimer = setInterval(() => {
            remaining--
            if (remaining <= 0) {
                clearInterval(cooldownTimer)
                quota.value.canInteractNow = true
                cooldownDisplay.value = '就绪'
            } else {
                cooldownDisplay.value = formatCooldown(remaining)
            }
        }, 1000)
    } else {
        cooldownDisplay.value = '就绪'
    }
}

function formatCooldown(seconds) {
    if (seconds <= 0) return '就绪'
    const m = Math.floor(seconds / 60)
    const s = seconds % 60
    if (m > 0) {
        return `${m}分${s}秒`
    }
    return `${s}秒`
}

async function handleImageUpload(e) {
    const file = e.target.files[0]
    if (!file) return

    if (!file.type.startsWith('image/')) {
        showMessage('请选择图片文件', 'warning')
        return
    }

    if (file.size > 10 * 1024 * 1024) {
        showMessage('图片大小不能超过10MB', 'warning')
        return
    }

    imageUploading.value = true
    try {
        const formData = new FormData()
        formData.append('file', file)
        const res = await uploadFile(formData)
        if (res.success && res.data) {
            form.titleImage = res.data.url
            showMessage('图片上传成功', 'success')
        }
    } catch (e) {
        console.error('图片上传失败', e)
        showMessage('图片上传失败', 'error')
    } finally {
        imageUploading.value = false
        e.target.value = ''
    }
}

function removeImage() {
    form.titleImage = ''
}

function openDialog() {
    visible.value = true
    document.body.style.overflow = 'hidden'
    refreshQuota()
    nextTick(() => {
        titleInputRef.value?.focus()
    })
}

function closeDialog() {
    visible.value = false
    document.body.style.overflow = ''
    if (cooldownTimer) clearInterval(cooldownTimer)
    resetForm()
}

function handleOverlayClick() {
    if (generatedContent.value) {
        showMessage('请先取消或发布文章', 'warning')
        return
    }
    closeDialog()
}

function resetForm() {
    form.title = ''
    form.categoryId = null
    form.aiPrompt = ''
    form.titleImage = ''
    selectedTags.value = []
    generatedContent.value = ''
    generatedTitle.value = ''
    generatedTokens.value = 0
    tokenEstimate.value = 0
    clearAllErrors()
}

function clearAllErrors() {
    validation.title = ''
    validation.category = ''
    validation.tags = ''
    validation.aiPrompt = ''
}

function clearFieldError(field) {
    validation[field] = ''
}

function handleCategoryChange(category) {
    if (category) {
        selectedTags.value = []
        clearFieldError('category')
    }
}

function handleTagsChange(tags) {
    clearFieldError('tags')
}

function onPromptInput() {
    clearFieldError('aiPrompt')
    if (estimateTimer) clearTimeout(estimateTimer)
    const val = form.aiPrompt.trim()
    if (val.length > 10) {
        estimateTimer = setTimeout(async () => {
            try {
                const res = await estimateTokens({ prompt: val })
                if (res.success) {
                    tokenEstimate.value = res.data.estimatedTokens
                }
            } catch (e) { /* ignore */ }
        }, 600)
    } else {
        tokenEstimate.value = 0
    }
}

function validateForGeneration() {
    let valid = true
    clearAllErrors()
    if (!form.aiPrompt.trim()) {
        validation.aiPrompt = '请输入AI写作要求'
        valid = false
    }
    return valid
}

function validateForSubmit() {
    let valid = true
    clearAllErrors()

    if (!generatedContent.value) {
        validation.aiPrompt = '请先生成文章内容'
        valid = false
    } else if (!form.title.trim()) {
        validation.title = '请输入文章标题'
        valid = false
    }

    if (!form.categoryId) {
        validation.category = '请选择文章分类'
        valid = false
    }
    if (selectedTags.value.length < 3) {
        validation.tags = `请至少选择3个标签（当前${selectedTags.value.length}个）`
        valid = false
    } else if (selectedTags.value.length > 5) {
        validation.tags = '标签数量不能超过5个'
        valid = false
    }
    return valid
}

async function handleAiGenerate() {
    if (!validateForGeneration()) return

    try {
        const quotaRes = await getAiQuota()
        if (quotaRes.success && quotaRes.data) {
            const remaining = quotaRes.data.dailyArticleGenRemaining
            const limit = quotaRes.data.dailyArticleGenLimit
            if (remaining <= 0) {
                showMessage(`今日AI文章生成次数已用完（${limit}/${limit}），请明天再试`, 'warning')
                return
            }
            if (!quotaRes.data.canInteractNow) {
                showMessage('AI 请求间隔未到，请稍后再试', 'warning')
                return
            }
        }
    } catch (e) {
        console.warn('查询AI额度失败，继续尝试生成', e)
    }

    aiGenerating.value = true
    try {
        const res = await generateArticle({
            prompt: form.aiPrompt.trim(),
            title: form.title.trim() || null,
            categoryId: form.categoryId || null,
            tagIds: selectedTags.value.map(t => t.id)
        }, { timeout: 120000 })
        if (res.success) {
            generatedContent.value = res.data.content
            generatedTitle.value = res.data.title
            generatedTokens.value = res.data.actualTokens
            showMessage('文章生成成功', 'success')
            await refreshQuota()
        } else {
            showMessage(res.message || '文章生成失败', 'error')
        }
    } catch (e) {
        console.error('文章生成失败', e)
        const msg = e && e.code === 'ECONNABORTED' ? '请求超时，AI 生成时间较长，请稍后重试' : (e && e.response && e.response.data && e.response.data.message || '文章生成失败')
        showMessage(msg, 'error')
    } finally {
        aiGenerating.value = false
    }
}

async function handleSubmit() {
    if (!validateForSubmit()) return

    submitting.value = true
    try {
        const res = await publishArticle({
            title: generatedTitle.value || form.title,
            content: generatedContent.value,
            titleImage: resolveAiCover(form.titleImage),
            categoryId: form.categoryId || null,
            tagIds: selectedTags.value.map(t => t.id),
            visibility: 'PUBLIC'
        })
        if (res.success) {
            showMessage('文章发布成功', 'success')
            emit('created')
            closeDialog()
        } else {
            showMessage(res.message || '文章发布失败，请稍后重试', 'error')
        }
    } catch (e) {
        console.error('文章发布失败', e)
        showMessage('文章发布失败，请检查网络或稍后重试', 'error')
    } finally {
        submitting.value = false
    }
}

onUnmounted(() => {
    if (cooldownTimer) clearInterval(cooldownTimer)
    if (estimateTimer) clearTimeout(estimateTimer)
})
</script>

<style scoped>
/* Button */
.ai-creator-btn {
    position: relative;
    display: inline-flex;
    align-items: center;
    gap: 10px;
    padding: 14px 28px;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a855f7 100%);
    color: #fff;
    border: none;
    border-radius: 16px;
    cursor: pointer;
    font-size: 16px;
    font-weight: 700;
    letter-spacing: 0.02em;
    box-shadow: 0 8px 32px rgba(99, 102, 241, 0.4), 0 2px 8px rgba(139, 92, 246, 0.3);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.ai-creator-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 16px 48px rgba(99, 102, 241, 0.5), 0 4px 16px rgba(139, 92, 246, 0.4);
}

.btn-icon {
    width: 22px;
    height: 22px;
}

.btn-text {
    white-space: nowrap;
}

/* Overlay */
.dialog-overlay {
    position: fixed;
    inset: 0;
    z-index: 9999;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(15, 23, 42, 0.6);
    backdrop-filter: blur(8px);
    padding: 20px;
}

/* Panel */
.dialog-panel {
    width: 100%;
    max-width: 820px;
    max-height: 92vh;
    background: #fff;
    border-radius: 24px;
    box-shadow: 0 32px 96px rgba(0, 0, 0, 0.25);
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

/* Header */
.dialog-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24px 28px 20px;
    border-bottom: 1px solid #f1f5f9;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 14px;
}

.header-icon-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
    border-radius: 14px;
    color: #6366f1;
}

.header-icon {
    width: 22px;
    height: 22px;
}

.header-title {
    font-size: 20px;
    font-weight: 700;
    color: #0f172a;
    margin: 0;
}

.header-desc {
    font-size: 13px;
    color: #94a3b8;
    margin: 2px 0 0;
}

.close-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border: none;
    background: #f8fafc;
    border-radius: 10px;
    cursor: pointer;
    color: #94a3b8;
    transition: all 0.2s;
}

.close-btn:hover {
    background: #fee2e2;
    color: #ef4444;
}

.close-btn svg {
    width: 18px;
    height: 18px;
}

/* Body */
.dialog-body {
    flex: 1;
    overflow-y: auto;
    padding: 24px 28px;
}

/* Quota Bar */
.quota-bar {
    display: flex;
    align-items: center;
    gap: 0;
    padding: 12px 16px;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border: 1px solid #e2e8f0;
    border-radius: 14px;
    margin-bottom: 20px;
}

.quota-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    flex: 1;
}

.quota-label {
    font-size: 11px;
    color: #94a3b8;
    font-weight: 500;
}

.quota-value {
    font-size: 15px;
    font-weight: 700;
    color: #334155;
}

.quota-exhausted {
    color: #ef4444;
}

.quota-divider {
    width: 1px;
    height: 32px;
    background: #e2e8f0;
}

.quota-refresh {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border: none;
    background: transparent;
    cursor: pointer;
    color: #94a3b8;
    border-radius: 8px;
    margin-left: 4px;
}

.quota-refresh:hover:not(:disabled) {
    background: #e2e8f0;
    color: #6366f1;
}

.quota-refresh svg {
    width: 16px;
    height: 16px;
}

.spin {
    animation: spin 0.7s linear infinite;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

/* Form */
.form-section {
    display: flex;
    flex-direction: column;
    gap: 22px;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.form-label {
    font-size: 14px;
    font-weight: 600;
    color: #334155;
}

.required {
    color: #ef4444;
}

.hint {
    font-size: 12px;
    font-weight: 400;
    color: #94a3b8;
}

.form-input, .form-textarea {
    width: 100%;
    padding: 12px 16px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    font-size: 15px;
    color: #1e293b;
    background: #fff;
    outline: none;
    box-sizing: border-box;
    font-family: inherit;
    transition: all 0.2s;
}

.form-input:focus, .form-textarea:focus {
    border-color: #818cf8;
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
}

.input-error {
    border-color: #fca5a5 !important;
}

.field-error {
    font-size: 13px;
    color: #ef4444;
    margin: 0;
}

.field-error-inline {
    margin: 0;
}

/* Image Upload */
.image-upload {
    width: 100%;
}

.image-preview {
    position: relative;
    width: 100%;
    max-height: 200px;
    border-radius: 12px;
    overflow: hidden;
    border: 2px solid #e2e8f0;
}

.image-preview img {
    width: 100%;
    height: 200px;
    object-fit: cover;
}

.image-remove {
    position: absolute;
    top: 8px;
    right: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    border: none;
    background: rgba(0, 0, 0, 0.5);
    border-radius: 8px;
    cursor: pointer;
    color: #fff;
}

.image-remove svg {
    width: 16px;
    height: 16px;
}

.image-upload-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    width: 100%;
    padding: 28px 20px;
    border: 2px dashed #cbd5e1;
    border-radius: 12px;
    background: #fafbfc;
    cursor: pointer;
    color: #94a3b8;
    font-size: 14px;
    transition: all 0.2s;
}

.image-upload-btn:hover {
    border-color: #818cf8;
    background: #f5f3ff;
    color: #6366f1;
}

.upload-icon {
    width: 36px;
    height: 36px;
}

.image-input-hidden {
    display: none;
}

.textarea-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 4px;
}

.char-count {
    font-size: 12px;
    color: #94a3b8;
}

/* Token Estimate */
.token-estimate {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background: #f0fdf4;
    border: 1px solid #bbf7d0;
    border-radius: 12px;
    font-size: 14px;
    color: #166534;
}

.estimate-icon {
    width: 18px;
    height: 18px;
}

/* Generate Button */
.generate-btn {
    width: 100%;
    padding: 16px 24px;
    border: none;
    border-radius: 14px;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
    color: #fff;
    font-size: 16px;
    font-weight: 700;
    cursor: pointer;
    box-shadow: 0 4px 20px rgba(99, 102, 241, 0.3);
    font-family: inherit;
    transition: all 0.3s;
}

.generate-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 32px rgba(99, 102, 241, 0.45);
}

.generate-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.generating {
    background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}

.btn-content {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
}

.btn-content svg {
    width: 20px;
    height: 20px;
}

.spinner {
    display: inline-block;
    width: 20px;
    height: 20px;
    border: 2.5px solid rgba(255, 255, 255, 0.3);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
}

.spinner-sm {
    width: 16px;
    height: 16px;
    border-width: 2px;
}

/* Preview */
.preview-section {
    margin-top: 24px;
    border: 2px solid #e2e8f0;
    border-radius: 16px;
    overflow: hidden;
}

.preview-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 20px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
}

.preview-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: #1e293b;
    margin: 0;
}

.preview-icon {
    width: 18px;
    height: 18px;
    color: #6366f1;
}

.preview-meta {
    font-size: 12px;
    padding: 4px 10px;
    background: #f1f5f9;
    border-radius: 6px;
    color: #64748b;
}

.preview-content {
    padding: 20px;
    max-height: 360px;
    overflow-y: auto;
}

.preview-article-title {
    font-size: 20px;
    font-weight: 700;
    color: #0f172a;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f1f5f9;
}

.preview-article-body {
    font-size: 15px;
    line-height: 1.85;
    color: #334155;
}

/* Footer */
.dialog-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 28px;
    border-top: 1px solid #f1f5f9;
    background: #fafbfc;
}

.footer-errors {
    flex: 1;
}

.footer-error {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #ef4444;
    margin-bottom: 4px;
}

.error-icon {
    width: 16px;
    height: 16px;
}

.footer-actions {
    display: flex;
    gap: 10px;
}

.btn-cancel {
    padding: 12px 24px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    background: #fff;
    cursor: pointer;
    font-size: 15px;
    font-weight: 600;
    color: #64748b;
    font-family: inherit;
    transition: all 0.2s;
}

.btn-cancel:hover {
    border-color: #cbd5e1;
    background: #f8fafc;
}

.btn-submit {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 28px;
    border: none;
    border-radius: 12px;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
    color: #fff;
    font-size: 15px;
    font-weight: 700;
    cursor: pointer;
    box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);
    font-family: inherit;
    transition: all 0.3s;
}

.btn-submit:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 28px rgba(99, 102, 241, 0.45);
}

.btn-submit:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.btn-submit.loading {
    background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}

/* Transitions */
.dialog-enter-active { transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1); }
.dialog-leave-active { transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1); }
.dialog-enter-from, .dialog-leave-to { opacity: 0; }

.error-enter-active { transition: all 0.25s ease-out; }
.error-leave-active { transition: all 0.15s ease-in; }
.error-enter-from, .error-leave-to { opacity: 0; }

.preview-enter-active { transition: all 0.45s cubic-bezier(0.34, 1.56, 0.64, 1); }
.preview-leave-active { transition: all 0.2s ease-in; }
.preview-enter-from { opacity: 0; transform: translateY(20px); }
.preview-leave-to { opacity: 0; }

.error-item-enter-active { transition: all 0.25s ease-out; }
.error-item-leave-active { transition: all 0.15s ease-in; }
.error-item-enter-from { opacity: 0; transform: translateX(-8px); }
.error-item-leave-to { opacity: 0; }
</style>
