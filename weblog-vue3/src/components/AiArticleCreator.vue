<template>
    <div class="ai-creator-root">
        <button class="ai-creator-fab" @click="openCreator">
            <span class="ai-fab-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 5v14M5 12h14"/>
                    <path d="M9 3h6l2 2h3a1 1 0 011 1v14a1 1 0 01-1 1H4a1 1 0 01-1-1V6a1 1 0 011-1h3l2-2z"/>
                </svg>
            </span>
            <span class="ai-fab-text">添加文章</span>
            <span class="ai-fab-glow"></span>
        </button>

        <Teleport to="body">
            <Transition name="creator-overlay">
                <div v-if="visible" class="ai-creator-overlay" @click.self="handleOverlayClick">
                    <Transition name="creator-panel">
                        <div v-if="visible" class="ai-creator-panel" @click.stop>
                            <div class="ai-creator-header">
                                <div class="ai-creator-header-left">
                                    <div class="ai-creator-icon-wrap">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ai-creator-header-icon">
                                            <path d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/>
                                        </svg>
                                    </div>
                                    <div>
                                        <h2 class="ai-creator-title">AI 智能写作</h2>
                                        <p class="ai-creator-subtitle">填写信息，AI 将为您生成高质量文章</p>
                                    </div>
                                </div>
                                <button class="ai-creator-close" @click="closeCreator">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                                    </svg>
                                </button>
                            </div>

                            <div class="ai-creator-body">
                                <div class="ai-creator-main">
                                    <div class="ai-quota-bar" v-if="quotaLoaded">
                                        <div class="ai-quota-bar-item">
                                            <span class="ai-quota-bar-label">文章生成</span>
                                            <span class="ai-quota-bar-value" :class="{ 'ai-quota-exhausted': quota.dailyArticleGenRemaining === 0 }">
                                                {{ quota.dailyArticleGenRemaining }}/{{ quota.dailyArticleGenLimit }}
                                            </span>
                                        </div>
                                        <div class="ai-quota-bar-divider"></div>
                                        <div class="ai-quota-bar-item">
                                            <span class="ai-quota-bar-label">Token</span>
                                            <span class="ai-quota-bar-value" :class="{ 'ai-quota-exhausted': quota.dailyTokenRemaining < 10000 }">
                                                {{ formatQuotaNumber(quota.dailyTokenRemaining) }}
                                            </span>
                                        </div>
                                        <div class="ai-quota-bar-divider"></div>
                                        <div class="ai-quota-bar-item">
                                            <span class="ai-quota-bar-label">冷却</span>
                                            <span class="ai-quota-bar-value" :class="{ 'ai-quota-exhausted': !quota.canInteractNow }">
                                                {{ quota.canInteractNow ? '就绪' : cooldownDisplay }}
                                            </span>
                                        </div>
                                        <button class="ai-quota-refresh" @click="refreshQuota" :disabled="quotaRefreshing">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ 'ai-spin': quotaRefreshing }">
                                                <polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 11-2.12-9.36L23 10"/>
                                            </svg>
                                        </button>
                                    </div>

                                    <div class="ai-creator-form">
                                        <div class="ai-form-group">
                                            <label class="ai-form-label">
                                                文章标题
                                                <span class="ai-required">*</span>
                                            </label>
                                            <input
                                                ref="titleInputRef"
                                                v-model="form.title"
                                                type="text"
                                                class="ai-form-input"
                                                :class="{ 'ai-input-error': validation.title }"
                                                placeholder="输入文章标题..."
                                                maxlength="100"
                                                @input="clearFieldError('title')"
                                            />
                                            <Transition name="field-error">
                                                <p v-if="validation.title" class="ai-field-error">{{ validation.title }}</p>
                                            </Transition>
                                        </div>

                                        <div class="ai-form-group">
                                            <label class="ai-form-label">
                                                封面图片
                                                <span class="ai-tag-hint">（可选，手动上传）</span>
                                            </label>
                                            <div class="ai-image-upload">
                                                <div class="ai-image-preview" v-if="form.titleImage">
                                                    <img :src="form.titleImage" alt="封面预览" />
                                                    <button class="ai-image-remove" @click="removeImage">
                                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                            <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                                                        </svg>
                                                    </button>
                                                </div>
                                                <label class="ai-image-upload-btn" v-else :class="{ 'ai-uploading': imageUploading }">
                                                    <input
                                                        type="file"
                                                        accept="image/*"
                                                        class="ai-image-input-hidden"
                                                        @change="handleImageUpload"
                                                        :disabled="imageUploading"
                                                    />
                                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ai-upload-icon">
                                                        <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                                                        <circle cx="8.5" cy="8.5" r="1.5"/>
                                                        <polyline points="21 15 16 10 5 21"/>
                                                    </svg>
                                                    <span>{{ imageUploading ? '上传中...' : '点击上传封面图片' }}</span>
                                                </label>
                                            </div>
                                        </div>

                                        <div class="ai-form-group">
                                            <label class="ai-form-label">
                                                文章分类
                                                <span class="ai-required">*</span>
                                            </label>
                                            <div class="ai-category-section">
                                                <div class="ai-category-presets" v-if="presetCategories.length > 0">
                                                    <button
                                                        v-for="cat in presetCategories"
                                                        :key="cat.id"
                                                        class="ai-category-chip"
                                                        :class="{ 'ai-category-active': form.categoryId === cat.id }"
                                                        @click="selectCategory(cat)"
                                                    >
                                                        <span class="ai-category-dot"></span>
                                                        {{ cat.name }}
                                                    </button>
                                                </div>
                                                <div class="ai-category-custom">
                                                    <div class="ai-custom-input-wrap">
                                                        <input
                                                            v-model="customCategory"
                                                            type="text"
                                                            class="ai-form-input ai-custom-input"
                                                            :class="{ 'ai-input-error': validation.category }"
                                                            placeholder="或输入自定义分类..."
                                                            maxlength="10"
                                                            @keyup.enter="addCustomCategory"
                                                            @input="clearFieldError('category')"
                                                        />
                                                        <button
                                                            class="ai-custom-add-btn"
                                                            :disabled="!customCategory.trim()"
                                                            @click="addCustomCategory"
                                                        >
                                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
                                                                <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                                                            </svg>
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                            <Transition name="field-error">
                                                <p v-if="validation.category" class="ai-field-error">{{ validation.category }}</p>
                                            </Transition>
                                        </div>

                                        <div class="ai-form-group">
                                            <label class="ai-form-label">
                                                文章标签
                                                <span class="ai-tag-hint">（{{ selectedTags.length }}/5，需选3-5个）</span>
                                            </label>
                                            <div class="ai-tag-section">
                                                <div class="ai-tag-selected" v-if="selectedTags.length > 0">
                                                    <TransitionGroup name="tag-item">
                                                        <span
                                                            v-for="tag in selectedTags"
                                                            :key="tag"
                                                            class="ai-tag-selected-chip"
                                                        >
                                                            {{ tag }}
                                                            <button class="ai-tag-remove" @click="removeTag(tag)">
                                                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
                                                                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                                                                </svg>
                                                            </button>
                                                        </span>
                                                    </TransitionGroup>
                                                </div>
                                                <div class="ai-tag-input-area">
                                                    <div class="ai-tag-input-wrap" :class="{ 'ai-tag-limit': selectedTags.length >= 5 }">
                                                        <input
                                                            v-model="tagInput"
                                                            type="text"
                                                            class="ai-form-input ai-tag-input"
                                                            :placeholder="selectedTags.length >= 5 ? '已达到标签上限（5个）' : '输入标签名称，回车添加...'"
                                                            :disabled="selectedTags.length >= 5"
                                                            maxlength="20"
                                                            @keyup.enter="addTagFromInput"
                                                            @input="filterPresetTags"
                                                        />
                                                        <button
                                                            v-if="tagInput.trim() && selectedTags.length < 5"
                                                            class="ai-tag-add-btn"
                                                            @click="addTagFromInput"
                                                        >
                                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
                                                                <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                                                            </svg>
                                                        </button>
                                                    </div>
                                                    <Transition name="tag-suggest">
                                                        <div v-if="tagInput.trim() && filteredPresetTags.length > 0 && selectedTags.length < 5" class="ai-tag-suggestions">
                                                            <button
                                                                v-for="tag in filteredPresetTags"
                                                                :key="tag"
                                                                class="ai-tag-suggest-item"
                                                                @click="selectPresetTag(tag)"
                                                            >
                                                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ai-tag-suggest-icon">
                                                                    <path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z"/>
                                                                    <line x1="7" y1="7" x2="7.01" y2="7"/>
                                                                </svg>
                                                                {{ tag }}
                                                            </button>
                                                        </div>
                                                    </Transition>
                                                </div>
                                                <div class="ai-tag-presets" v-if="!tagInput.trim() && presetTags.length > 0">
                                                    <span class="ai-tag-presets-label">常用标签：</span>
                                                    <button
                                                        v-for="tag in presetTags"
                                                        :key="tag"
                                                        class="ai-tag-preset-chip"
                                                        :class="{ 'ai-tag-preset-disabled': selectedTags.includes(tag) || selectedTags.length >= 5 }"
                                                        :disabled="selectedTags.includes(tag) || selectedTags.length >= 5"
                                                        @click="selectPresetTag(tag)"
                                                    >
                                                        {{ tag }}
                                                    </button>
                                                </div>
                                            </div>
                                            <Transition name="field-error">
                                                <p v-if="validation.tags" class="ai-field-error">{{ validation.tags }}</p>
                                            </Transition>
                                        </div>

                                        <div class="ai-form-group">
                                            <label class="ai-form-label">
                                                AI 写作要求
                                                <span class="ai-required">*</span>
                                            </label>
                                            <textarea
                                                v-model="form.aiPrompt"
                                                class="ai-form-textarea"
                                                :class="{ 'ai-input-error': validation.aiPrompt }"
                                                placeholder="描述您想要的文章内容、风格、结构等，AI 将根据此要求生成文章..."
                                                rows="5"
                                                maxlength="2000"
                                                @input="onPromptInput"
                                            ></textarea>
                                            <div class="ai-prompt-footer">
                                                <span class="ai-prompt-count">{{ form.aiPrompt.length }}/2000</span>
                                                <Transition name="field-error">
                                                    <p v-if="validation.aiPrompt" class="ai-field-error ai-field-error-inline">{{ validation.aiPrompt }}</p>
                                                </Transition>
                                            </div>
                                        </div>

                                        <div v-if="tokenEstimate > 0" class="ai-token-estimate">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ai-estimate-icon">
                                                <circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>
                                            </svg>
                                            <span>预估消耗约 <strong>{{ tokenEstimate }}</strong> tokens</span>
                                        </div>

                                        <button
                                            class="ai-generate-btn"
                                            :class="{ 'ai-generating': aiGenerating }"
                                            :disabled="!canGenerate"
                                            @click="handleAiGenerate"
                                        >
                                            <span v-if="!aiGenerating" class="ai-generate-btn-content">
                                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ai-btn-icon">
                                                    <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                                                </svg>
                                                生成文章
                                            </span>
                                            <span v-else class="ai-generate-btn-content">
                                                <span class="ai-spinner"></span>
                                                AI 正在创作中...
                                            </span>
                                        </button>
                                    </div>

                                    <Transition name="generated-content">
                                        <div v-if="generatedContent" class="ai-creator-preview">
                                            <div class="ai-preview-header">
                                                <h3 class="ai-preview-heading">
                                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ai-preview-heading-icon">
                                                        <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
                                                        <polyline points="14 2 14 8 20 8"/>
                                                    </svg>
                                                    生成结果预览
                                                </h3>
                                                <div class="ai-preview-meta">
                                                    <span class="ai-preview-tag">实际消耗 {{ generatedTokens }} tokens</span>
                                                </div>
                                            </div>
                                            <div class="ai-preview-content">
                                                <div class="ai-preview-title">{{ generatedTitle }}</div>
                                                <div class="ai-preview-body" v-html="renderedPreview"></div>
                                            </div>
                                        </div>
                                    </Transition>
                                </div>
                            </div>

                            <div class="ai-creator-footer">
                                <div class="ai-footer-validation">
                                    <TransitionGroup name="footer-error">
                                        <div v-if="footerErrors.length > 0" class="ai-footer-errors">
                                            <div v-for="err in footerErrors" :key="err" class="ai-footer-error-item">
                                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ai-footer-error-icon">
                                                    <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
                                                </svg>
                                                {{ err }}
                                            </div>
                                        </div>
                                    </TransitionGroup>
                                </div>
                                <div class="ai-footer-actions">
                                    <button class="ai-btn-cancel" @click="closeCreator">取消</button>
                                    <button
                                        class="ai-btn-submit"
                                        :class="{ 'ai-btn-loading': submitting }"
                                        :disabled="submitting || !isFormValid"
                                        @click="handleSubmit"
                                    >
                                        <span v-if="!submitting">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="ai-btn-submit-icon">
                                                <polyline points="20 6 9 17 4 12"/>
                                            </svg>
                                            发布文章
                                        </span>
                                        <span v-else>
                                            <span class="ai-spinner ai-spinner-sm"></span>
                                            发布中...
                                        </span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </Transition>
                </div>
            </Transition>
        </Teleport>
    </div>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { showMessage } from '@/composables/util'
import { generateArticle, estimateTokens, getAiQuota } from '@/api/admin/ai'
import { getTagSelect } from '@/api/admin/tag'
import { getCategorySelect } from '@/api/admin/category'
import { publishArticle } from '@/api/admin/article'
import { uploadFile } from '@/api/admin/file'
import { resolveAiCover } from '@/constants/ai'

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
    categoryName: '',
    aiPrompt: '',
    titleImage: ''
})

const selectedTags = ref([])
const tagInput = ref('')
const customCategory = ref('')

const presetCategories = ref([])
const presetTags = ref([])
const filteredPresetTags = ref([])
const customCategories = ref([])

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

const TAG_REGEX = /^[\u4e00-\u9fa5a-zA-Z0-9\s\+\-\.#&]{1,20}$/
const CATEGORY_REGEX = /^[\u4e00-\u9fa5a-zA-Z0-9\s\+\-\.#&]{1,10}$/

const canGenerate = computed(() => {
    return form.aiPrompt.trim().length > 0 && !aiGenerating.value
})

const isFormValid = computed(() => {
    return form.title.trim().length > 0
        && (form.categoryId || form.categoryName)
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
    if (!form.categoryId && !form.categoryName) {
        errors.push('请选择或输入文章分类')
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

async function loadPresetData() {
    try {
        const [tagRes, catRes] = await Promise.all([
            getTagSelect(),
            getCategorySelect()
        ])
        if (tagRes.success && tagRes.data) {
            presetTags.value = (tagRes.data || []).map(t => t.label || t.name || t).slice(0, 20)
        }
        if (catRes.success && catRes.data) {
            presetCategories.value = (catRes.data || []).map(c => ({
                id: c.value || c.id,
                name: c.label || c.name
            })).slice(0, 20)
        }
    } catch (e) {
        console.error('加载预设数据失败', e)
    }
}

function openCreator() {
    visible.value = true
    document.body.style.overflow = 'hidden'
    loadPresetData()
    refreshQuota()
    nextTick(() => {
        titleInputRef.value?.focus()
    })
}

function closeCreator() {
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
    closeCreator()
}

function resetForm() {
    form.title = ''
    form.categoryId = null
    form.categoryName = ''
    form.aiPrompt = ''
    form.titleImage = ''
    selectedTags.value = []
    tagInput.value = ''
    customCategory.value = ''
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

function selectCategory(cat) {
    form.categoryId = cat.id
    form.categoryName = cat.name
    customCategory.value = ''
    clearFieldError('category')
}

function addCustomCategory() {
    const name = customCategory.value.trim()
    if (!name) return
    if (!CATEGORY_REGEX.test(name)) {
        showMessage('分类名称包含无效字符', 'warning')
        return
    }
    form.categoryId = null
    form.categoryName = name
    customCategory.value = ''
    clearFieldError('category')
    if (!customCategories.value.find(c => c.name === name)) {
        customCategories.value.push({ id: null, name })
    }
}

function addTagFromInput() {
    const name = tagInput.value.trim()
    if (!name) return
    if (selectedTags.value.length >= 5) {
        showMessage('标签数量已达上限（5个）', 'warning')
        return
    }
    if (!TAG_REGEX.test(name)) {
        showMessage('标签名称包含无效字符', 'warning')
        return
    }
    if (selectedTags.value.includes(name)) {
        showMessage('该标签已选择', 'warning')
        return
    }
    selectedTags.value.push(name)
    tagInput.value = ''
    filteredPresetTags.value = []
    clearFieldError('tags')
}

function selectPresetTag(tag) {
    if (selectedTags.value.length >= 5) {
        showMessage('标签数量已达上限（5个）', 'warning')
        return
    }
    if (selectedTags.value.includes(tag)) return
    selectedTags.value.push(tag)
    tagInput.value = ''
    filteredPresetTags.value = []
    clearFieldError('tags')
}

function removeTag(tag) {
    const idx = selectedTags.value.indexOf(tag)
    if (idx > -1) {
        selectedTags.value.splice(idx, 1)
    }
    clearFieldError('tags')
}

function filterPresetTags() {
    const keyword = tagInput.value.trim().toLowerCase()
    if (!keyword) {
        filteredPresetTags.value = []
        return
    }
    filteredPresetTags.value = presetTags.value
        .filter(t => t.toLowerCase().includes(keyword) && !selectedTags.value.includes(t))
        .slice(0, 8)
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

    if (!form.categoryId && !form.categoryName) {
        validation.category = '请选择或输入文章分类'
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
            categoryName: form.categoryName || null,
            tags: selectedTags.value.length > 0 ? selectedTags.value : null
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
            categoryName: form.categoryName || null,
            tags: selectedTags.value,
            visibility: 'PUBLIC'
        })
        if (res.success) {
            showMessage('文章发布成功', 'success')
            emit('created')
            closeCreator()
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

onMounted(() => {
    loadPresetData()
})

onUnmounted(() => {
    if (cooldownTimer) clearInterval(cooldownTimer)
    if (estimateTimer) clearTimeout(estimateTimer)
})
</script>

<style scoped>
.ai-creator-root {
    position: relative;
}

/* ===== FAB 按钮 ===== */
.ai-creator-fab {
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
    overflow: hidden;
    z-index: 1;
}

.ai-creator-fab:hover {
    transform: translateY(-3px);
    box-shadow: 0 16px 48px rgba(99, 102, 241, 0.5), 0 4px 16px rgba(139, 92, 246, 0.4);
}

.ai-creator-fab:active {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(99, 102, 241, 0.35);
}

.ai-fab-glow {
    position: absolute;
    inset: -4px;
    border-radius: 20px;
    background: linear-gradient(135deg, #6366f1, #a855f7, #6366f1);
    background-size: 200% 200%;
    animation: fabGlow 3s ease-in-out infinite;
    opacity: 0;
    z-index: -1;
    filter: blur(12px);
}

.ai-creator-fab:hover .ai-fab-glow {
    opacity: 0.6;
}

@keyframes fabGlow {
    0%, 100% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
}

.ai-fab-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
}

.ai-fab-icon svg {
    width: 22px;
    height: 22px;
}

.ai-fab-text {
    white-space: nowrap;
}

/* ===== 遮罩层 ===== */
.ai-creator-overlay {
    position: fixed;
    inset: 0;
    z-index: 9999;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(15, 23, 42, 0.6);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    padding: 20px;
}

/* ===== 面板 ===== */
.ai-creator-panel {
    width: 100%;
    max-width: 820px;
    max-height: 92vh;
    background: #fff;
    border-radius: 24px;
    box-shadow: 0 32px 96px rgba(0, 0, 0, 0.25), 0 8px 24px rgba(0, 0, 0, 0.12);
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

/* ===== 头部 ===== */
.ai-creator-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24px 28px 20px;
    border-bottom: 1px solid #f1f5f9;
    flex-shrink: 0;
}

.ai-creator-header-left {
    display: flex;
    align-items: center;
    gap: 14px;
}

.ai-creator-icon-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
    border-radius: 14px;
    color: #6366f1;
}

.ai-creator-header-icon {
    width: 22px;
    height: 22px;
}

.ai-creator-title {
    font-size: 20px;
    font-weight: 700;
    color: #0f172a;
    margin: 0;
    line-height: 1.3;
}

.ai-creator-subtitle {
    font-size: 13px;
    color: #94a3b8;
    margin: 2px 0 0;
}

.ai-creator-close {
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

.ai-creator-close:hover {
    background: #fee2e2;
    color: #ef4444;
}

.ai-creator-close svg {
    width: 18px;
    height: 18px;
}

/* ===== 主体 ===== */
.ai-creator-body {
    flex: 1;
    overflow-y: auto;
    padding: 24px 28px;
    -webkit-overflow-scrolling: touch;
}

.ai-creator-main {
    display: flex;
    flex-direction: column;
    gap: 0;
}

/* ===== 表单 ===== */
.ai-creator-form {
    display: flex;
    flex-direction: column;
    gap: 22px;
}

/* ===== 额度栏 ===== */
.ai-quota-bar {
    display: flex;
    align-items: center;
    gap: 0;
    padding: 12px 16px;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border: 1px solid #e2e8f0;
    border-radius: 14px;
    margin-bottom: 20px;
}

.ai-quota-bar-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    flex: 1;
    min-width: 0;
}

.ai-quota-bar-label {
    font-size: 11px;
    color: #94a3b8;
    font-weight: 500;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.ai-quota-bar-value {
    font-size: 15px;
    font-weight: 700;
    color: #334155;
    transition: color 0.2s;
}

.ai-quota-exhausted {
    color: #ef4444;
}

.ai-quota-bar-divider {
    width: 1px;
    height: 32px;
    background: #e2e8f0;
    flex-shrink: 0;
}

.ai-quota-refresh {
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
    transition: all 0.2s;
    flex-shrink: 0;
    margin-left: 4px;
}

.ai-quota-refresh:hover:not(:disabled) {
    background: #e2e8f0;
    color: #6366f1;
}

.ai-quota-refresh:disabled {
    opacity: 0.4;
    cursor: not-allowed;
}

.ai-quota-refresh svg {
    width: 16px;
    height: 16px;
}

.ai-spin {
    animation: spin 0.7s linear infinite;
}

/* ===== 图片上传 ===== */
.ai-image-upload {
    width: 100%;
}

.ai-image-preview {
    position: relative;
    width: 100%;
    max-height: 200px;
    border-radius: 12px;
    overflow: hidden;
    border: 2px solid #e2e8f0;
}

.ai-image-preview img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    display: block;
}

.ai-image-remove {
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
    transition: all 0.2s;
}

.ai-image-remove:hover {
    background: rgba(239, 68, 68, 0.8);
}

.ai-image-remove svg {
    width: 16px;
    height: 16px;
}

.ai-image-upload-btn {
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
    transition: all 0.2s;
    color: #94a3b8;
    font-size: 14px;
}

.ai-image-upload-btn:hover {
    border-color: #818cf8;
    background: #f5f3ff;
    color: #6366f1;
}

.ai-image-upload-btn.ai-uploading {
    opacity: 0.6;
    cursor: not-allowed;
    border-color: #e2e8f0;
}

.ai-image-input-hidden {
    display: none;
}

.ai-upload-icon {
    width: 36px;
    height: 36px;
}

.ai-form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.ai-form-label {
    font-size: 14px;
    font-weight: 600;
    color: #334155;
    display: flex;
    align-items: center;
    gap: 6px;
}

.ai-required {
    color: #ef4444;
    font-weight: 700;
}

.ai-tag-hint {
    font-size: 12px;
    font-weight: 400;
    color: #94a3b8;
}

.ai-form-input {
    width: 100%;
    padding: 12px 16px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    font-size: 15px;
    color: #1e293b;
    background: #fff;
    transition: all 0.2s;
    outline: none;
    box-sizing: border-box;
    font-family: inherit;
}

.ai-form-input:focus {
    border-color: #818cf8;
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
}

.ai-form-input:disabled {
    background: #f8fafc;
    color: #94a3b8;
    cursor: not-allowed;
}

.ai-input-error {
    border-color: #fca5a5 !important;
    box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.08) !important;
}

.ai-form-textarea {
    width: 100%;
    padding: 14px 16px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    font-size: 15px;
    color: #1e293b;
    background: #fff;
    transition: all 0.2s;
    outline: none;
    resize: vertical;
    min-height: 120px;
    box-sizing: border-box;
    font-family: inherit;
    line-height: 1.6;
}

.ai-form-textarea:focus {
    border-color: #818cf8;
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
}

.ai-prompt-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 4px;
}

.ai-prompt-count {
    font-size: 12px;
    color: #94a3b8;
}

.ai-field-error {
    font-size: 13px;
    color: #ef4444;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 4px;
}

.ai-field-error-inline {
    margin: 0;
}

/* ===== 分类选择 ===== */
.ai-category-section {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.ai-category-presets {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.ai-category-chip {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 18px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    background: #fff;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    color: #475569;
    transition: all 0.2s;
    font-family: inherit;
}

.ai-category-chip:hover {
    border-color: #c7d2fe;
    background: #f5f3ff;
}

.ai-category-active {
    border-color: #6366f1;
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
    color: #4f46e5;
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(99, 102, 241, 0.15);
}

.ai-category-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #cbd5e1;
    transition: all 0.2s;
}

.ai-category-active .ai-category-dot {
    background: #6366f1;
    box-shadow: 0 0 8px rgba(99, 102, 241, 0.5);
}

.ai-category-custom {
    display: flex;
    gap: 8px;
}

.ai-custom-input-wrap {
    display: flex;
    flex: 1;
    gap: 8px;
}

.ai-custom-input {
    flex: 1;
}

.ai-custom-add-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    background: #fff;
    cursor: pointer;
    color: #6366f1;
    transition: all 0.2s;
    flex-shrink: 0;
}

.ai-custom-add-btn:hover:not(:disabled) {
    border-color: #6366f1;
    background: #eef2ff;
}

.ai-custom-add-btn:disabled {
    opacity: 0.4;
    cursor: not-allowed;
}

.ai-custom-add-btn svg {
    width: 18px;
    height: 18px;
}

/* ===== 标签选择 ===== */
.ai-tag-section {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.ai-tag-selected {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    min-height: 0;
}

.ai-tag-selected-chip {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 14px;
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
    border: 2px solid #c7d2fe;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 500;
    color: #4f46e5;
    transition: all 0.2s;
}

.ai-tag-remove {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 18px;
    height: 18px;
    border: none;
    background: transparent;
    cursor: pointer;
    color: #94a3b8;
    padding: 0;
    border-radius: 4px;
    transition: all 0.15s;
}

.ai-tag-remove:hover {
    color: #ef4444;
    background: #fee2e2;
}

.ai-tag-remove svg {
    width: 14px;
    height: 14px;
}

.ai-tag-input-area {
    position: relative;
}

.ai-tag-input-wrap {
    display: flex;
    gap: 8px;
}

.ai-tag-input {
    flex: 1;
}

.ai-tag-limit .ai-tag-input {
    border-color: #fbbf24;
    background: #fffbeb;
}

.ai-tag-add-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    background: #fff;
    cursor: pointer;
    color: #6366f1;
    transition: all 0.2s;
    flex-shrink: 0;
}

.ai-tag-add-btn:hover {
    border-color: #6366f1;
    background: #eef2ff;
}

.ai-tag-add-btn svg {
    width: 18px;
    height: 18px;
}

.ai-tag-suggestions {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    margin-top: 4px;
    background: #fff;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
    z-index: 10;
    max-height: 220px;
    overflow-y: auto;
    padding: 6px;
}

.ai-tag-suggest-item {
    display: flex;
    align-items: center;
    gap: 10px;
    width: 100%;
    padding: 10px 14px;
    border: none;
    background: transparent;
    cursor: pointer;
    font-size: 14px;
    color: #334155;
    border-radius: 8px;
    transition: all 0.15s;
    font-family: inherit;
    text-align: left;
}

.ai-tag-suggest-item:hover {
    background: #f1f5f9;
    color: #6366f1;
}

.ai-tag-suggest-icon {
    width: 16px;
    height: 16px;
    color: #94a3b8;
    flex-shrink: 0;
}

.ai-tag-presets {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 6px;
}

.ai-tag-presets-label {
    font-size: 13px;
    color: #94a3b8;
    margin-right: 4px;
}

.ai-tag-preset-chip {
    padding: 6px 14px;
    border: 1.5px solid #e2e8f0;
    border-radius: 8px;
    background: #fff;
    cursor: pointer;
    font-size: 13px;
    color: #64748b;
    transition: all 0.2s;
    font-family: inherit;
}

.ai-tag-preset-chip:hover:not(:disabled) {
    border-color: #818cf8;
    color: #6366f1;
    background: #f5f3ff;
}

.ai-tag-preset-disabled {
    opacity: 0.4;
    cursor: not-allowed;
}

/* ===== Token 预估 ===== */
.ai-token-estimate {
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

.ai-estimate-icon {
    width: 18px;
    height: 18px;
    flex-shrink: 0;
}

.ai-token-estimate strong {
    color: #15803d;
}

/* ===== 生成按钮 ===== */
.ai-generate-btn {
    width: 100%;
    padding: 16px 24px;
    border: none;
    border-radius: 14px;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
    color: #fff;
    font-size: 16px;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 4px 20px rgba(99, 102, 241, 0.3);
    font-family: inherit;
}

.ai-generate-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 32px rgba(99, 102, 241, 0.45);
}

.ai-generate-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
}

.ai-generating {
    background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}

.ai-generate-btn-content {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
}

.ai-btn-icon {
    width: 20px;
    height: 20px;
}

.ai-spinner {
    display: inline-block;
    width: 20px;
    height: 20px;
    border: 2.5px solid rgba(255, 255, 255, 0.3);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
}

.ai-spinner-sm {
    width: 16px;
    height: 16px;
    border-width: 2px;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

/* ===== 预览区域 ===== */
.ai-creator-preview {
    margin-top: 24px;
    border: 2px solid #e2e8f0;
    border-radius: 16px;
    overflow: hidden;
}

.ai-preview-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 20px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
}

.ai-preview-heading {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: #1e293b;
    margin: 0;
}

.ai-preview-heading-icon {
    width: 18px;
    height: 18px;
    color: #6366f1;
}

.ai-preview-meta {
    display: flex;
    gap: 8px;
}

.ai-preview-tag {
    font-size: 12px;
    padding: 4px 10px;
    background: #f1f5f9;
    border-radius: 6px;
    color: #64748b;
}

.ai-preview-content {
    padding: 20px;
    max-height: 360px;
    overflow-y: auto;
}

.ai-preview-title {
    font-size: 20px;
    font-weight: 700;
    color: #0f172a;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f1f5f9;
}

.ai-preview-body {
    font-size: 15px;
    line-height: 1.85;
    color: #334155;
}

.ai-preview-body :deep(h1),
.ai-preview-body :deep(h2),
.ai-preview-body :deep(h3) {
    margin-top: 20px;
    margin-bottom: 10px;
    font-weight: 600;
    color: #1e293b;
}

.ai-preview-body :deep(p) {
    margin-bottom: 10px;
}

.ai-preview-body :deep(code) {
    background: #f1f5f9;
    padding: 2px 8px;
    border-radius: 5px;
    font-size: 13px;
    color: #e11d48;
}

.ai-preview-body :deep(pre) {
    background: #1e293b;
    color: #e2e8f0;
    padding: 16px;
    border-radius: 10px;
    overflow-x: auto;
    font-size: 13px;
    line-height: 1.6;
}

/* ===== 底部 ===== */
.ai-creator-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 28px;
    border-top: 1px solid #f1f5f9;
    background: #fafbfc;
    flex-shrink: 0;
    gap: 16px;
}

.ai-footer-validation {
    flex: 1;
    min-width: 0;
}

.ai-footer-errors {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.ai-footer-error-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: #ef4444;
}

.ai-footer-error-icon {
    width: 16px;
    height: 16px;
    flex-shrink: 0;
}

.ai-footer-actions {
    display: flex;
    gap: 10px;
    flex-shrink: 0;
}

.ai-btn-cancel {
    padding: 12px 24px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    background: #fff;
    cursor: pointer;
    font-size: 15px;
    font-weight: 600;
    color: #64748b;
    transition: all 0.2s;
    font-family: inherit;
}

.ai-btn-cancel:hover {
    border-color: #cbd5e1;
    background: #f8fafc;
    color: #475569;
}

.ai-btn-submit {
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
    transition: all 0.3s;
    box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);
    font-family: inherit;
}

.ai-btn-submit:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 28px rgba(99, 102, 241, 0.45);
}

.ai-btn-submit:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
}

.ai-btn-loading {
    background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}

.ai-btn-submit-icon {
    width: 18px;
    height: 18px;
}

/* ===== 过渡动画 ===== */
.creator-overlay-enter-active { transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1); }
.creator-overlay-leave-active { transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1); }
.creator-overlay-enter-from,
.creator-overlay-leave-to { opacity: 0; }

.creator-panel-enter-active { transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1); }
.creator-panel-leave-active { transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1); }
.creator-panel-enter-from { opacity: 0; transform: scale(0.92) translateY(30px); }
.creator-panel-leave-to { opacity: 0; transform: scale(0.95) translateY(10px); }

.field-error-enter-active { transition: all 0.25s ease-out; }
.field-error-leave-active { transition: all 0.15s ease-in; }
.field-error-enter-from { opacity: 0; transform: translateY(-6px); }
.field-error-leave-to { opacity: 0; transform: translateY(-4px); }

.tag-item-enter-active { transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.tag-item-leave-active { transition: all 0.2s ease-in; position: absolute; }
.tag-item-enter-from { opacity: 0; transform: scale(0.7); }
.tag-item-leave-to { opacity: 0; transform: scale(0.7); }
.tag-item-move { transition: transform 0.3s ease; }

.tag-suggest-enter-active { transition: all 0.2s ease-out; }
.tag-suggest-leave-active { transition: all 0.15s ease-in; }
.tag-suggest-enter-from { opacity: 0; transform: translateY(-8px); }
.tag-suggest-leave-to { opacity: 0; transform: translateY(-4px); }

.generated-content-enter-active { transition: all 0.45s cubic-bezier(0.34, 1.56, 0.64, 1); }
.generated-content-leave-active { transition: all 0.2s ease-in; }
.generated-content-enter-from { opacity: 0; transform: translateY(20px); }
.generated-content-leave-to { opacity: 0; transform: translateY(10px); }

.footer-error-enter-active { transition: all 0.25s ease-out; }
.footer-error-leave-active { transition: all 0.15s ease-in; }
.footer-error-enter-from { opacity: 0; transform: translateX(-8px); }
.footer-error-leave-to { opacity: 0; transform: translateX(-4px); }

/* ===== 响应式 ===== */
@media (max-width: 768px) {
    .ai-creator-overlay {
        padding: 0;
        align-items: flex-end;
    }

    .ai-creator-panel {
        max-width: 100%;
        max-height: 96vh;
        border-radius: 24px 24px 0 0;
    }

    .ai-creator-header {
        padding: 18px 20px 16px;
    }

    .ai-creator-body {
        padding: 18px 20px;
    }

    .ai-creator-footer {
        padding: 14px 20px;
        flex-direction: column;
        gap: 12px;
    }

    .ai-footer-actions {
        width: 100%;
    }

    .ai-btn-cancel,
    .ai-btn-submit {
        flex: 1;
        justify-content: center;
    }

    .ai-creator-fab {
        padding: 12px 22px;
        font-size: 14px;
        border-radius: 14px;
    }

    .ai-creator-title {
        font-size: 18px;
    }

    .ai-category-presets {
        gap: 6px;
    }

    .ai-category-chip {
        padding: 8px 14px;
        font-size: 13px;
    }
}

@media (max-width: 480px) {
    .ai-creator-fab {
        width: 100%;
        justify-content: center;
    }

    .ai-creator-header-left {
        gap: 10px;
    }

    .ai-creator-icon-wrap {
        width: 36px;
        height: 36px;
        border-radius: 10px;
    }

    .ai-creator-header-icon {
        width: 18px;
        height: 18px;
    }

    .ai-creator-title {
        font-size: 16px;
    }

    .ai-creator-subtitle {
        font-size: 12px;
    }
}
</style>
