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
                                />
                            </el-form-item>
                            <el-form-item label="建议标题（可选）">
                                <el-input v-model="articleTitle" placeholder="留空则由AI自动生成" />
                            </el-form-item>
                            <el-form-item label="文章分类（可选）">
                                <el-select
                                    v-model="articleCategory"
                                    placeholder="请选择文章分类"
                                    clearable
                                    filterable
                                    remote
                                    reserve-keyword
                                    :remote-method="searchCategories"
                                    :loading="categorySearchLoading"
                                    class="ai-full-width"
                                >
                                    <el-option
                                        v-for="cat in displayedCategoryOptions"
                                        :key="cat.id"
                                        :label="cat.name"
                                        :value="cat.id"
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
                                        :value="tag.value || tag"
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
                                />
                            </el-form-item>
                        </el-form>
                        <el-button
                            type="primary"
                            :loading="tagLoading"
                            @click="handleRecommendTags"
                            class="ai-generate-btn"
                        >
                            <el-icon><MagicStick /></el-icon>
                            获取推荐标签
                        </el-button>

                        <div v-if="tagSuggestions.length > 0 || unmatchedTagTerms.length > 0" class="ai-recommend-result">
                            <div class="ai-recommend-header">
                                <span>推荐标签</span>
                                <el-tag size="small" type="info">消耗 {{ tagTokens }} tokens</el-tag>
                            </div>
                            <div v-if="tagSuggestions.length > 0" class="ai-tag-list">
                                <div v-for="tag in tagSuggestions" :key="tag.tagId" class="ai-taxonomy-tag-item">
                                    <div class="ai-taxonomy-tag-info">
                                        <el-tag :type="acceptedTagIds.includes(tag.tagId) ? 'success' : ''">{{ tag.tagName }}</el-tag>
                                        <span class="ai-taxonomy-tag-category" v-if="tag.categoryName">{{ tag.categoryName }}</span>
                                    </div>
                                    <el-button v-if="!acceptedTagIds.includes(tag.tagId)" link type="primary" size="small" @click="acceptTag(tag)">接受</el-button>
                                    <span v-else class="added-check"><el-icon><Check /></el-icon>已接受</span>
                                </div>
                            </div>
                            <div v-if="unmatchedTagTerms.length > 0" class="ai-unmatched-section">
                                <div class="ai-unmatched-header">未匹配项（不在系统标签库中）</div>
                                <div class="ai-unmatched-list">
                                    <el-tag v-for="term in unmatchedTagTerms" :key="term" type="info" size="small" effect="plain">{{ term }}</el-tag>
                                </div>
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
                                />
                            </el-form-item>
                        </el-form>
                        <el-button
                            type="primary"
                            :loading="categoryLoading"
                            @click="handleRecommendCategories"
                            class="ai-generate-btn"
                        >
                            <el-icon><MagicStick /></el-icon>
                            获取推荐分类
                        </el-button>

                        <div v-if="categorySuggestions.length > 0 || unmatchedCategoryTerms.length > 0" class="ai-recommend-result">
                            <div class="ai-recommend-header">
                                <span>推荐分类</span>
                                <el-tag size="small" type="info">消耗 {{ categoryTokens }} tokens</el-tag>
                            </div>
                            <div v-if="categorySuggestions.length > 0" class="ai-category-list">
                                <div v-for="cat in categorySuggestions" :key="cat.categoryId" class="ai-category-item">
                                    <div class="ai-taxonomy-cat-info">
                                        <span>{{ cat.categoryName }}</span>
                                        <span v-if="cat.parentCategoryName" class="ai-taxonomy-cat-parent">{{ cat.parentCategoryName }} / {{ cat.categoryName }}</span>
                                    </div>
                                    <el-button v-if="!acceptedCategoryId || acceptedCategoryId !== cat.categoryId" link type="primary" size="small" @click="acceptCategory(cat)">接受</el-button>
                                    <span v-else class="added-check"><el-icon><Check /></el-icon>已接受</span>
                                </div>
                            </div>
                            <div v-if="unmatchedCategoryTerms.length > 0" class="ai-unmatched-section">
                                <div class="ai-unmatched-header">未匹配项（不在系统分类中）</div>
                                <div class="ai-unmatched-list">
                                    <el-tag v-for="term in unmatchedCategoryTerms" :key="term" type="info" size="small" effect="plain">{{ term }}</el-tag>
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
                                <el-input v-model="seoFormTitle" placeholder="文章标题" />
                            </el-form-item>
                            <el-form-item label="文章正文">
                                <el-input
                                    v-model="seoFormContent"
                                    type="textarea"
                                    :rows="4"
                                    placeholder="文章正文..."
                                />
                            </el-form-item>
                            <el-form-item label="当前摘要（可选）">
                                <el-input v-model="seoFormDescription" placeholder="当前摘要" />
                            </el-form-item>
                        </el-form>
                        <el-button
                            type="primary"
                            :loading="seoLoading"
                            :disabled="!seoFormTitle || !seoFormContent"
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
    recommendTags,
    recommendCategories,
    generateArticle,
    estimateTokens,
    aiSeoOptimize
} from '@/api/admin/ai'
import { publishArticle } from '@/api/admin/article'
import { getCategoryTree, searchTaxonomyTags, searchTaxonomyCategories } from '@/api/admin/taxonomy'
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

const emit = defineEmits(['update:modelValue', 'fillContent', 'applySeoTitle', 'applySeoDescription', 'applyCategory', 'applyTags'])

const visible = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
})

const activeTab = ref('article')
const generating = ref(false)
const tagLoading = ref(false)
const categoryLoading = ref(false)

const articlePrompt = ref('')
const articleTitle = ref('')
const articleCategory = ref(null)
const articleTags = ref([])
const estimatedTokens = ref(0)
const generatedContent = ref('')
const generatedTitle = ref('')
const generatedTokens = ref(0)

const tagSuggestions = ref([])
const unmatchedTagTerms = ref([])
const acceptedTagIds = ref([])
const tagTokens = ref(0)
const tagRequirement = ref('')

const categorySuggestions = ref([])
const unmatchedCategoryTerms = ref([])
const acceptedCategoryId = ref(null)
const categoryTokens = ref(0)
const categoryRequirement = ref('')

const seoLoading = ref(false)
const seoFormTitle = ref('')
const seoFormContent = ref('')
const seoFormDescription = ref('')
const seoResult = ref(null)

const presetCategoryOptions = ref([])
const presetTagOptions = ref([])
const categorySearchLoading = ref(false)
const categorySearchResults = ref(null)

const displayedCategoryOptions = computed(() => {
    return categorySearchResults.value !== null ? categorySearchResults.value : presetCategoryOptions.value
})

let estimateTimer = null

const canGenerate = computed(() => {
    return articlePrompt.value.trim().length > 0
})

const renderedPreview = computed(() => {
    if (!generatedContent.value) return ''
    return generatedContent.value
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br>')
})

watch(() => props.modelValue, async (val) => {
    if (val) {
        seoFormTitle.value = props.currentTitle || ''
        seoFormContent.value = props.currentContent || ''
        seoFormDescription.value = props.currentDescription || ''
        await loadPresetOptions()
    }
})

onUnmounted(() => {
    if (estimateTimer) clearTimeout(estimateTimer)
})

async function loadPresetOptions() {
    try {
        const catRes = await getCategoryTree()
        if (catRes.success && catRes.data) {
            const tree = catRes.data
            let allCategories = []
            for (const parent of tree) {
                allCategories.push({ id: parent.id, name: parent.name })
                if (parent.children) {
                    for (const child of parent.children) {
                        allCategories.push({ id: child.id, name: child.name })
                    }
                }
            }
            presetCategoryOptions.value = allCategories.slice(0, 50)
        }
        const tagRes = await searchTaxonomyTags({ key: '' })
        if (tagRes.success && tagRes.data) {
            presetTagOptions.value = (tagRes.data || []).map(t => ({
                value: t.id,
                label: t.name
            })).slice(0, 50)
        }
    } catch (e) {
        console.error('加载预设选项失败', e)
    }
}

async function searchCategories(query) {
    if (!query) {
        categorySearchResults.value = null
        return
    }
    categorySearchLoading.value = true
    try {
        const res = await searchTaxonomyCategories({ key: query })
        if (res.success && res.data) {
            categorySearchResults.value = (res.data || []).map(c => ({
                id: c.id,
                name: c.name
            })).slice(0, 50)
        }
    } catch (e) {
        console.error('搜索分类失败', e)
    } finally {
        categorySearchLoading.value = false
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
            categoryId: articleCategory.value || null,
            tagIds: articleTags.value.length > 0 ? articleTags.value : null
        }, { timeout: 120000 })
        if (res.success) {
            generatedContent.value = res.data.content
            generatedTitle.value = res.data.title
            generatedTokens.value = res.data.actualTokens
            showMessage('文章生成成功', 'success')
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
            tagSuggestions.value = res.data.suggestions || []
            unmatchedTagTerms.value = res.data.unmatchedTerms || []
            tagTokens.value = res.data.actualTokens
            acceptedTagIds.value = []
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
            categorySuggestions.value = res.data.suggestions || []
            unmatchedCategoryTerms.value = res.data.unmatchedTerms || []
            categoryTokens.value = res.data.actualTokens
            acceptedCategoryId.value = null
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

function acceptTag(tag) {
    const exists = presetTagOptions.value.some(t => t.value === tag.tagId)
    if (!exists) {
        showMessage(`标签「${tag.tagName}」不在系统标签库中，无法接受`, 'warning')
        return
    }
    if (!acceptedTagIds.value.includes(tag.tagId)) {
        acceptedTagIds.value.push(tag.tagId)
        emit('applyTags', { tagId: tag.tagId, tagName: tag.tagName, categoryId: tag.categoryId, categoryName: tag.categoryName })
        showMessage(`标签「${tag.tagName}」已接受`, 'success')
    }
}

function acceptCategory(cat) {
    const exists = presetCategoryOptions.value.some(c => c.id === cat.categoryId)
    if (!exists) {
        showMessage(`分类「${cat.categoryName}」不在系统分类中，无法接受`, 'warning')
        return
    }
    acceptedCategoryId.value = cat.categoryId
    emit('applyCategory', { categoryId: cat.categoryId, categoryName: cat.categoryName })
    showMessage(`分类「${cat.categoryName}」已接受`, 'success')
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
            categoryId: articleCategory.value || null,
            tagIds: articleTags.value.length > 0 ? articleTags.value : [],
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

.ai-taxonomy-tag-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    margin-bottom: 6px;
}

.ai-taxonomy-tag-info {
    display: flex;
    align-items: center;
    gap: 8px;
}

.ai-taxonomy-tag-category {
    font-size: 12px;
    color: #64748b;
    background: #f1f5f9;
    padding: 2px 8px;
    border-radius: 4px;
}

.ai-taxonomy-cat-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.ai-taxonomy-cat-parent {
    font-size: 12px;
    color: #64748b;
}

.ai-unmatched-section {
    padding: 12px 16px;
    border-top: 1px solid #e2e8f0;
}

.ai-unmatched-header {
    font-size: 12px;
    color: #94a3b8;
    margin-bottom: 8px;
}

.ai-unmatched-list {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
}
</style>
