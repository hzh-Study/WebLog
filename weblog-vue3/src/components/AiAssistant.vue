<template>
    <el-drawer
        v-model="visible"
        title="AI 写作助手"
        direction="rtl"
        size="480px"
        :before-close="handleClose"
        class="ai-assistant-drawer"
    >
        <div class="ai-assistant-body">
            <div class="ai-quota-card">
                <div class="ai-quota-header">
                    <el-icon><DataAnalysis /></el-icon>
                    <span>今日额度</span>
                    <el-button link type="primary" size="small" @click="refreshQuota" :loading="quotaLoading">
                        <el-icon><Refresh /></el-icon>
                    </el-button>
                </div>
                <div class="ai-quota-grid">
                    <div class="ai-quota-item">
                        <span class="ai-quota-label">文章生成</span>
                        <span class="ai-quota-value">
                            <strong :class="{ 'text-red-500': quota.dailyArticleGenRemaining === 0 }">{{ quota.dailyArticleGenRemaining }}</strong>
                            / {{ quota.dailyArticleGenLimit }}
                        </span>
                    </div>
                    <div class="ai-quota-item">
                        <span class="ai-quota-label">Token余量</span>
                        <span class="ai-quota-value">
                            <strong :class="{ 'text-red-500': quota.dailyTokenRemaining < 10000 }">{{ formatNumber(quota.dailyTokenRemaining) }}</strong>
                            / {{ formatNumber(quota.dailyTokenLimit) }}
                        </span>
                    </div>
                </div>
                <div v-if="!quota.canInteractNow && quota.lastInteractionTime" class="ai-interval-warning">
                    <el-icon><WarningFilled /></el-icon>
                    <span>请等待 {{ remainingSeconds }} 秒后再使用AI功能</span>
                </div>
            </div>

            <el-tabs v-model="activeTab" class="ai-tabs">
                <el-tab-pane label="写文章" name="article">
                    <div class="ai-tab-content">
                        <el-form label-position="top">
                            <el-form-item label="写作要求">
                                <el-input
                                    v-model="articlePrompt"
                                    type="textarea"
                                    :rows="4"
                                    placeholder="请描述您想要写的文章主题、风格、要点等..."
                                    maxlength="2000"
                                    show-word-limit
                                />
                            </el-form-item>
                            <el-form-item label="建议标题（可选）">
                                <el-input v-model="articleTitle" placeholder="留空则由AI自动生成" maxlength="100" />
                            </el-form-item>
                            <el-form-item label="文章分类（可选）">
                                <el-select
                                    v-model="articleCategory"
                                    placeholder="请选择文章分类"
                                    clearable
                                    class="ai-full-width"
                                >
                                    <el-option
                                        v-for="cat in presetCategoryOptions"
                                        :key="cat.id"
                                        :label="cat.name"
                                        :value="cat.name"
                                    />
                                </el-select>
                            </el-form-item>
                            <el-form-item label="文章标签（可选）">
                                <el-select
                                    v-model="articleTags"
                                    placeholder="请选择文章标签"
                                    multiple
                                    collapse-tags
                                    collapse-tags-tooltip
                                    class="ai-full-width"
                                >
                                    <el-option
                                        v-for="tag in presetTagOptions"
                                        :key="tag.value || tag"
                                        :label="tag.label || tag"
                                        :value="tag.label || tag"
                                    />
                                </el-select>
                            </el-form-item>
                        </el-form>

                        <div v-if="estimatedTokens > 0" class="ai-estimate-info">
                            <el-icon><InfoFilled /></el-icon>
                            <span>预估消耗约 <strong>{{ estimatedTokens }}</strong> tokens</span>
                        </div>

                        <el-button
                            type="primary"
                            :loading="generating"
                            :disabled="!canGenerate"
                            @click="handleGenerate"
                            class="ai-generate-btn"
                        >
                            <el-icon><MagicStick /></el-icon>
                            生成文章
                        </el-button>

                        <div v-if="generatedContent" class="ai-generated-content">
                            <div class="ai-generated-header">
                                <span>生成结果</span>
                                <div>
                                    <el-tag size="small" type="info">实际消耗 {{ generatedTokens }} tokens</el-tag>
                                    <el-button link type="primary" size="small" @click="handleSaveArticle" class="ml-2">
                                        <el-icon><Upload /></el-icon>
                                        保存文章
                                    </el-button>
                                    <el-button link type="primary" size="small" @click="fillToEditor" class="ml-2">
                                        <el-icon><Upload /></el-icon>
                                        填充至编辑器
                                    </el-button>
                                </div>
                            </div>
                            <div class="ai-generated-preview">
                                <div class="ai-preview-title">{{ generatedTitle }}</div>
                                <div class="ai-preview-body" v-html="renderedPreview"></div>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>

                <el-tab-pane label="标签推荐" name="tag">
                    <div class="ai-tab-content">
                        <p class="ai-tab-desc">AI将根据您的文章内容推荐合适的标签</p>
                        <el-form label-position="top">
                            <el-form-item label="推荐要求（可选）">
                                <el-input
                                    v-model="tagRequirement"
                                    type="textarea"
                                    :rows="2"
                                    placeholder="描述您想要的标签风格或方向，如：技术类标签、生活类标签..."
                                    maxlength="500"
                                    show-word-limit
                                />
                            </el-form-item>
                        </el-form>
                        <el-button
                            type="primary"
                            :loading="tagLoading"
                            :disabled="!quota.canInteractNow || quota.dailyTokenRemaining <= 0"
                            @click="handleRecommendTags"
                            class="ai-generate-btn"
                        >
                            <el-icon><MagicStick /></el-icon>
                            获取推荐标签
                        </el-button>

                        <div v-if="recommendedTags.length > 0" class="ai-recommend-result">
                            <div class="ai-recommend-header">
                                <span>推荐标签</span>
                                <el-tag size="small" type="info">消耗 {{ tagTokens }} tokens</el-tag>
                            </div>
                            <div class="ai-tag-list">
                                <el-tag
                                    v-for="tag in recommendedTags"
                                    :key="tag"
                                    class="ai-tag-item"
                                    :type="addedTags.includes(tag) ? 'success' : ''"
                                >
                                    {{ tag }}
                                    <el-button
                                        v-if="!addedTags.includes(tag)"
                                        link
                                        type="primary"
                                        size="small"
                                        @click="addTag(tag)"
                                        class="ml-1"
                                    >
                                        添加
                                    </el-button>
                                    <span v-else class="ml-1 added-check">
                                        <el-icon><Check /></el-icon>
                                    </span>
                                </el-tag>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>

                <el-tab-pane label="分类推荐" name="category">
                    <div class="ai-tab-content">
                        <p class="ai-tab-desc">AI将根据您的文章内容推荐合适的分类</p>
                        <el-form label-position="top">
                            <el-form-item label="推荐要求（可选）">
                                <el-input
                                    v-model="categoryRequirement"
                                    type="textarea"
                                    :rows="2"
                                    placeholder="描述您想要的分类风格或方向，如：技术类、生活类..."
                                    maxlength="500"
                                    show-word-limit
                                />
                            </el-form-item>
                        </el-form>
                        <el-button
                            type="primary"
                            :loading="categoryLoading"
                            :disabled="!quota.canInteractNow || quota.dailyTokenRemaining <= 0"
                            @click="handleRecommendCategories"
                            class="ai-generate-btn"
                        >
                            <el-icon><MagicStick /></el-icon>
                            获取推荐分类
                        </el-button>

                        <div v-if="recommendedCategories.length > 0" class="ai-recommend-result">
                            <div class="ai-recommend-header">
                                <span>推荐分类</span>
                                <el-tag size="small" type="info">消耗 {{ categoryTokens }} tokens</el-tag>
                            </div>
                            <div class="ai-category-list">
                                <div
                                    v-for="cat in recommendedCategories"
                                    :key="cat"
                                    class="ai-category-item"
                                >
                                    <span>{{ cat }}</span>
                                    <el-button
                                        v-if="!addedCategories.includes(cat)"
                                        link
                                        type="primary"
                                        size="small"
                                        @click="handleAddCategory(cat)"
                                    >
                                        添加
                                    </el-button>
                                    <span v-else class="added-check">
                                        <el-icon><Check /></el-icon>
                                        已添加
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>

                <el-tab-pane label="SEO优化" name="seo">
                    <div class="ai-tab-content">
                        <p class="ai-tab-desc">AI将根据当前标题和正文优化SEO</p>
                        <el-form label-position="top">
                            <el-form-item label="文章标题">
                                <el-input v-model="seoFormTitle" placeholder="文章标题" maxlength="100" />
                            </el-form-item>
                            <el-form-item label="文章正文">
                                <el-input
                                    v-model="seoFormContent"
                                    type="textarea"
                                    :rows="4"
                                    placeholder="文章正文..."
                                    maxlength="10000"
                                    show-word-limit
                                />
                            </el-form-item>
                            <el-form-item label="当前摘要（可选）">
                                <el-input v-model="seoFormDescription" placeholder="当前摘要" maxlength="200" />
                            </el-form-item>
                        </el-form>
                        <el-button
                            type="primary"
                            :loading="seoLoading"
                            :disabled="!quota.canInteractNow || quota.dailyTokenRemaining <= 0 || !seoFormTitle || !seoFormContent"
                            @click="handleSeoOptimize"
                            class="ai-generate-btn"
                        >
                            <el-icon><MagicStick /></el-icon>
                            开始优化
                        </el-button>

                        <div v-if="seoResult" class="ai-recommend-result">
                            <div class="ai-seo-section">
                                <div class="ai-seo-section-header">
                                    <span>优化标题</span>
                                    <el-button size="small" type="primary" @click="applySeoTitle">应用</el-button>
                                </div>
                                <div class="ai-seo-section-content">{{ seoResult.optimizedTitle }}</div>
                            </div>
                            <div class="ai-seo-section">
                                <div class="ai-seo-section-header">
                                    <span>优化摘要</span>
                                    <el-button size="small" type="primary" @click="applySeoDescription">应用</el-button>
                                </div>
                                <div class="ai-seo-section-content">{{ seoResult.optimizedDescription }}</div>
                            </div>
                            <div class="ai-seo-section">
                                <div class="ai-seo-section-header"><span>关键词</span></div>
                                <div class="ai-seo-keywords">
                                    <el-tag v-for="kw in seoResult.keywords" :key="kw" class="ai-seo-keyword-tag">{{ kw }}</el-tag>
                                </div>
                            </div>
                            <div class="ai-seo-meta">
                                <span>标题评分：</span>
                                <el-progress :percentage="seoResult.titleScore" :color="seoScoreColor(seoResult.titleScore)" :stroke-width="8" style="width:160px" />
                                <strong>{{ seoResult.titleScore }} 分</strong>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    </el-drawer>
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'
import { showMessage } from '@/composables/util'
import {
    getAiQuota,
    recommendTags,
    recommendCategories,
    generateArticle,
    estimateTokens,
    aiSeoOptimize
} from '@/api/admin/ai'
import { publishArticle } from '@/api/admin/article'
import { addTags } from '@/api/admin/tag'
import { addCategory } from '@/api/admin/category'
import { getTagSelect } from '@/api/admin/tag'
import { getCategorySelect } from '@/api/admin/category'
import { DEFAULT_AI_SCENERY_COVER, buildAiRecommendRequirement } from '@/constants/ai'

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false
    },
    currentTitle: {
        type: String,
        default: ''
    },
    currentContent: {
        type: String,
        default: ''
    },
    currentDescription: {
        type: String,
        default: ''
    }
})

const emit = defineEmits(['update:modelValue', 'fillContent', 'applySeoTitle', 'applySeoDescription'])

const visible = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
})

const activeTab = ref('article')
const quotaLoading = ref(false)
const generating = ref(false)
const tagLoading = ref(false)
const categoryLoading = ref(false)

const articlePrompt = ref('')
const articleTitle = ref('')
const articleCategory = ref('')
const articleTags = ref([])
const estimatedTokens = ref(0)
const generatedContent = ref('')
const generatedTitle = ref('')
const generatedTokens = ref(0)

const recommendedTags = ref([])
const addedTags = ref([])
const tagTokens = ref(0)
const tagRequirement = ref('')

const recommendedCategories = ref([])
const addedCategories = ref([])
const categoryTokens = ref(0)
const categoryRequirement = ref('')

const seoLoading = ref(false)
const seoFormTitle = ref('')
const seoFormContent = ref('')
const seoFormDescription = ref('')
const seoResult = ref(null)

const presetCategoryOptions = ref([])
const presetTagOptions = ref([])

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

let intervalTimer = null
let estimateTimer = null

const remainingSeconds = ref(0)

const canGenerate = computed(() => {
    return articlePrompt.value.trim().length > 0
        && quota.value.dailyArticleGenRemaining > 0
        && quota.value.canInteractNow
        && quota.value.dailyTokenRemaining > 0
})

const renderedPreview = computed(() => {
    if (!generatedContent.value) return ''
    return generatedContent.value
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br>')
})

function formatNumber(num) {
    if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
    }
    return num.toLocaleString()
}

async function refreshQuota() {
    quotaLoading.value = true
    try {
        const res = await getAiQuota()
        if (res.success) {
            quota.value = res.data
            updateRemainingSeconds()
        }
    } catch (e) {
        console.error('获取额度失败', e)
    } finally {
        quotaLoading.value = false
    }
}

function updateRemainingSeconds() {
    if (intervalTimer) clearInterval(intervalTimer)
    if (!quota.value.canInteractNow && quota.value.remainingCooldownSeconds > 0) {
        remainingSeconds.value = quota.value.remainingCooldownSeconds
        intervalTimer = setInterval(() => {
            remainingSeconds.value = Math.max(0, remainingSeconds.value - 1)
            if (remainingSeconds.value <= 0) {
                clearInterval(intervalTimer)
                quota.value.canInteractNow = true
                refreshQuota()
            }
        }, 1000)
    } else {
        remainingSeconds.value = 0
    }
}

watch(() => props.modelValue, async (val) => {
    if (val) {
        seoFormTitle.value = props.currentTitle || ''
        seoFormContent.value = props.currentContent || ''
        seoFormDescription.value = props.currentDescription || ''
        await refreshQuota()
        await loadPresetOptions()
    } else {
        if (intervalTimer) clearInterval(intervalTimer)
    }
})

onUnmounted(() => {
    if (intervalTimer) clearInterval(intervalTimer)
    if (estimateTimer) clearTimeout(estimateTimer)
})

async function loadPresetOptions() {
    try {
        const [tagRes, catRes] = await Promise.all([
            getTagSelect(),
            getCategorySelect()
        ])
        if (tagRes.success && tagRes.data) {
            presetTagOptions.value = (tagRes.data || []).map(t => t.label || t.name || t).slice(0, 30)
        }
        if (catRes.success && catRes.data) {
            presetCategoryOptions.value = (catRes.data || []).map(c => ({
                id: c.value || c.id,
                name: c.label || c.name
            })).slice(0, 20)
        }
    } catch (e) {
        console.error('加载预设选项失败', e)
    }
}

async function handleGenerate() {
    if (!articlePrompt.value.trim()) {
        showMessage('请输入写作要求', 'warning')
        return
    }

    generating.value = true
    try {
        const res = await generateArticle({
            prompt: articlePrompt.value.trim(),
            title: articleTitle.value.trim() || null,
            categoryName: articleCategory.value || null,
            tags: articleTags.value.length > 0 ? articleTags.value : null
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
        showMessage('文章生成失败，请稍后重试', 'error')
    } finally {
        generating.value = false
    }
}

async function handleRecommendTags() {
    tagLoading.value = true
    try {
        const res = await recommendTags({
            content: props.currentContent || seoFormContent.value || '',
            requirement: buildAiRecommendRequirement(tagRequirement.value, props.currentContent || seoFormContent.value || '')
        }, { timeout: 120000 })
        if (res.success) {
            recommendedTags.value = res.data.tags
            tagTokens.value = res.data.actualTokens
            addedTags.value = []
            await refreshQuota()
        } else {
            showMessage(res.message || '标签推荐失败', 'error')
        }
    } catch (e) {
        console.error('标签推荐失败', e)
        showMessage('标签推荐失败，请稍后重试', 'error')
    } finally {
        tagLoading.value = false
    }
}

async function handleRecommendCategories() {
    categoryLoading.value = true
    try {
        const res = await recommendCategories({
            content: props.currentContent || seoFormContent.value || '',
            requirement: buildAiRecommendRequirement(categoryRequirement.value, props.currentContent || seoFormContent.value || '')
        }, { timeout: 120000 })
        if (res.success) {
            recommendedCategories.value = res.data.categories
            categoryTokens.value = res.data.actualTokens
            addedCategories.value = []
            await refreshQuota()
        } else {
            showMessage(res.message || '分类推荐失败', 'error')
        }
    } catch (e) {
        console.error('分类推荐失败', e)
        showMessage('分类推荐失败，请稍后重试', 'error')
    } finally {
        categoryLoading.value = false
    }
}

async function addTag(tagName) {
    try {
        await addTags({ tags: [tagName] })
        addedTags.value.push(tagName)
        showMessage(`标签「${tagName}」已添加`, 'success')
    } catch (e) {
        console.error('添加标签失败', e)
        showMessage('添加标签失败，请稍后重试', 'error')
    }
}

async function handleAddCategory(categoryName) {
    try {
        await addCategory({ name: categoryName })
        addedCategories.value.push(categoryName)
        showMessage(`分类「${categoryName}」已添加`, 'success')
    } catch (e) {
        console.error('添加分类失败', e)
        showMessage('添加分类失败，请稍后重试', 'error')
    }
}

async function handleSaveArticle() {
    if (!generatedContent.value) {
        showMessage('没有可保存的文章内容', 'warning')
        return
    }
    try {
        const res = await publishArticle({
            title: generatedTitle.value || '无标题文章',
            content: generatedContent.value,
            titleImage: DEFAULT_AI_SCENERY_COVER,
            description: '',
            categoryName: articleCategory.value || null,
            tags: articleTags.value.length > 0 ? articleTags.value : [],
            visibility: 'PUBLIC'
        })
        if (res.success) {
            showMessage('文章保存成功', 'success')
            visible.value = false
        } else {
            showMessage(res.message || '保存失败', 'error')
        }
    } catch (e) {
        console.error('保存文章失败', e)
        showMessage('保存文章失败，请稍后重试', 'error')
    }
}

function fillToEditor() {
    emit('fillContent', {
        title: generatedTitle.value,
        content: generatedContent.value
    })
    showMessage('内容已填充至编辑器', 'success')
}

async function handleSeoOptimize() {
    seoLoading.value = true
    seoResult.value = null
    try {
        const res = await aiSeoOptimize({
            title: seoFormTitle.value.trim(),
            content: seoFormContent.value.trim(),
            currentDescription: seoFormDescription.value.trim() || null
        }, { timeout: 120000 })
        if (res.success) {
            seoResult.value = res.data
            showMessage('SEO 优化完成', 'success')
            await refreshQuota()
        } else {
            showMessage(res.message || 'SEO 优化失败', 'error')
        }
    } catch (e) {
        console.error('SEO 优化失败', e)
        showMessage('SEO 优化失败，请稍后重试', 'error')
    } finally {
        seoLoading.value = false
    }
}

function applySeoTitle() {
    if (seoResult.value && seoResult.value.optimizedTitle) {
        emit('applySeoTitle', seoResult.value.optimizedTitle)
        showMessage('标题已应用', 'success')
    }
}

function applySeoDescription() {
    if (seoResult.value && seoResult.value.optimizedDescription) {
        emit('applySeoDescription', seoResult.value.optimizedDescription)
        showMessage('摘要已应用', 'success')
    }
}

function seoScoreColor(score) {
    if (score >= 80) return '#22c55e'
    if (score >= 60) return '#f59e0b'
    return '#ef4444'
}

function handleClose() {
    visible.value = false
}

watch(articlePrompt, (val) => {
    if (estimateTimer) clearTimeout(estimateTimer)
    if (val.trim().length > 10) {
        estimateTimer = setTimeout(async () => {
            try {
                const res = await estimateTokens({ prompt: val.trim() })
                if (res.success) {
                    estimatedTokens.value = res.data.estimatedTokens
                }
            } catch (e) {
                // ignore
            }
        }, 600)
    } else {
        estimatedTokens.value = 0
    }
})
</script>

<style scoped>
.ai-assistant-body {
    padding: 0 4px;
}

.ai-full-width {
    width: 100%;
}

.ai-quota-card {
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 16px;
    border: 1px solid #bae6fd;
}

.ai-quota-header {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 700;
    color: #0369a1;
    margin-bottom: 12px;
    font-size: 14px;
}

.ai-quota-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
}

.ai-quota-item {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.ai-quota-label {
    font-size: 12px;
    color: #64748b;
}

.ai-quota-value {
    font-size: 14px;
    color: #334155;
}

.ai-quota-value strong {
    font-size: 18px;
    color: #0369a1;
}

.ai-interval-warning {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 10px;
    padding: 8px 12px;
    background: #fef3c7;
    border-radius: 8px;
    font-size: 13px;
    color: #92400e;
}

.ai-tabs {
    margin-top: 4px;
}

.ai-tab-content {
    padding-top: 12px;
}

.ai-tab-desc {
    font-size: 13px;
    color: #64748b;
    margin-bottom: 16px;
}

.ai-estimate-info {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 12px;
    background: #f0fdf4;
    border-radius: 8px;
    font-size: 13px;
    color: #166534;
    margin-bottom: 12px;
}

.ai-generate-btn {
    width: 100%;
    margin-top: 8px;
}

.ai-generated-content {
    margin-top: 20px;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    overflow: hidden;
}

.ai-generated-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    font-weight: 600;
    font-size: 14px;
}

.ai-generated-preview {
    padding: 16px;
    max-height: 400px;
    overflow-y: auto;
}

.ai-preview-title {
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 12px;
    color: #1e293b;
}

.ai-preview-body {
    font-size: 14px;
    line-height: 1.8;
    color: #334155;
}

.ai-preview-body :deep(h1),
.ai-preview-body :deep(h2),
.ai-preview-body :deep(h3) {
    margin-top: 16px;
    margin-bottom: 8px;
    font-weight: 600;
}

.ai-preview-body :deep(p) {
    margin-bottom: 8px;
}

.ai-preview-body :deep(code) {
    background: #f1f5f9;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 13px;
}

.ai-preview-body :deep(pre) {
    background: #1e293b;
    color: #e2e8f0;
    padding: 12px;
    border-radius: 8px;
    overflow-x: auto;
}

.ai-recommend-result {
    margin-top: 16px;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    overflow: hidden;
}

.ai-recommend-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    font-weight: 600;
    font-size: 14px;
}

.ai-tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding: 16px;
}

.ai-tag-item {
    display: inline-flex;
    align-items: center;
    font-size: 13px;
}

.ai-category-list {
    padding: 16px;
}

.ai-category-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    margin-bottom: 8px;
    font-size: 14px;
}

.added-check {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    color: #16a34a;
    font-size: 13px;
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
    font-size: 13px;
    color: #475569;
}
</style>
