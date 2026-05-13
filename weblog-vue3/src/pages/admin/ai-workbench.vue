<template>
    <div class="admin-page">
        <section class="admin-page-header">
            <div>
                <span class="admin-kicker">AI Workbench</span>
                <h1 class="admin-page-title">AI 工作台</h1>
                <p class="admin-page-desc">统一使用 AI 能力：写文章、改写、润色、续写、SEO 优化，以及分类和标签推荐。</p>
            </div>
        </section>

        <div class="ai-workbench-grid">
            <div class="ai-workbench-main">
                <el-card shadow="never" class="ai-section-card" v-loading="quotaLoading">
                    <template #header>
                        <div class="ai-section-header">
                            <span class="ai-section-title">
                                <el-icon><DataAnalysis /></el-icon>
                                今日额度
                            </span>
                            <el-button link type="primary" size="small" @click="refreshQuota" :loading="quotaLoading">
                                <el-icon><Refresh /></el-icon>
                                刷新
                            </el-button>
                        </div>
                    </template>
                    <div class="ai-quota-grid">
                        <div class="ai-quota-item">
                            <span class="ai-quota-label">文章生成</span>
                            <span class="ai-quota-value">
                                <strong :class="{ 'text-red-500': quota.dailyArticleGenRemaining === 0 }">{{ quota.dailyArticleGenRemaining }}</strong>
                                / {{ quota.dailyArticleGenLimit }}
                            </span>
                        </div>
                        <div class="ai-quota-item">
                            <span class="ai-quota-label">Token 余量</span>
                            <span class="ai-quota-value">
                                <strong :class="{ 'text-red-500': quota.dailyTokenRemaining < 10000 }">{{ formatNumber(quota.dailyTokenRemaining) }}</strong>
                                / {{ formatNumber(quota.dailyTokenLimit) }}
                            </span>
                        </div>
                        <div class="ai-quota-item">
                            <span class="ai-quota-label">冷却状态</span>
                            <span class="ai-quota-value">
                                <strong :class="{ 'text-red-500': !quota.canInteractNow }">
                                    {{ quota.canInteractNow ? '就绪' : remainingSeconds + 's' }}
                                </strong>
                            </span>
                        </div>
                    </div>
                    <div v-if="!quota.canInteractNow && remainingSeconds > 0" class="ai-cooldown-warning">
                        <el-icon><WarningFilled /></el-icon>
                        <span>冷却中，请等待 {{ remainingSeconds }} 秒后再使用 AI 功能</span>
                    </div>
                </el-card>

                <el-card shadow="never" class="ai-section-card">
                    <template #header>
                        <div class="ai-section-header">
                            <span class="ai-section-title">
                                <el-icon><EditPen /></el-icon>
                                AI 写作
                            </span>
                        </div>
                    </template>
                    <el-form label-position="top">
                        <el-form-item label="写作要求">
                            <el-input
                                v-model="writePrompt"
                                type="textarea"
                                :rows="4"
                                placeholder="请描述您想要写的文章主题、风格、要点等..."
                                maxlength="2000"
                                show-word-limit
                            />
                        </el-form-item>
                        <el-form-item label="建议标题（可选）">
                            <el-input v-model="writeTitle" placeholder="留空则由 AI 自动生成" maxlength="100" />
                        </el-form-item>
                        <el-form-item label="文章分类（可选）">
                            <el-select v-model="writeCategory" placeholder="请选择文章分类" clearable style="width:100%">
                                <el-option v-for="cat in presetCategories" :key="cat.id" :label="cat.name" :value="cat.name" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="文章标签（可选）">
                            <el-select v-model="writeTags" placeholder="请选择文章标签" multiple collapse-tags collapse-tags-tooltip style="width:100%">
                                <el-option v-for="tag in presetTags" :key="tag" :label="tag" :value="tag" />
                            </el-select>
                        </el-form-item>
                    </el-form>

                    <div class="ai-write-limit-tip">
                        当前前端支持生成最长约 12000 字的文章，超长内容建议拆分为多次生成。
                    </div>
                    <div v-if="writeEstimatedTokens > 0" class="ai-estimate-info">
                        <el-icon><InfoFilled /></el-icon>
                        <span>预估消耗约 <strong>{{ writeEstimatedTokens }}</strong> tokens</span>
                    </div>

                    <el-button
                        type="primary"
                        :loading="writeLoading"
                        :disabled="!canWrite"
                        @click="handleWrite"
                        class="ai-action-btn"
                    >
                        <el-icon><MagicStick /></el-icon>
                        生成文章
                    </el-button>

                    <div v-if="writeResult" class="ai-result-area">
                        <div class="ai-result-header">
                            <span>生成结果</span>
                            <el-tag size="small" type="info">实际消耗 {{ writeResultTokens }} tokens</el-tag>
                        </div>
                        <div class="ai-result-title">{{ writeResult.title }}</div>
                        <div class="ai-result-content" v-html="writeResultPreview"></div>
                        <div class="ai-result-actions">
                            <el-button size="small" @click="copyText(writeResult.title, '标题')">
                                <el-icon><CopyDocument /></el-icon>
                                复制标题
                            </el-button>
                            <el-button size="small" @click="copyText(writeResult.content, '正文')">
                                <el-icon><CopyDocument /></el-icon>
                                复制正文
                            </el-button>
                            <el-button size="small" type="success" @click="saveAsArticle('PUBLIC')">
                                <el-icon><Promotion /></el-icon>
                                保存为文章                            </el-button>
                            <el-button size="small" type="warning" @click="saveAsArticle('PRIVATE')">
                                <el-icon><Edit /></el-icon>
                                保存为草稿                            </el-button>
                        </div>
                    </div>
                </el-card>

                <el-card shadow="never" class="ai-section-card">
                    <template #header>
                        <div class="ai-section-header">
                            <span class="ai-section-title">
                                <el-icon><Operation /></el-icon>
                                AI 改写 / 润色 / 续写
                            </span>
                        </div>
                    </template>
                    <el-form label-position="top">
                        <el-form-item label="原始文本">
                            <el-input
                                v-model="editText"
                                type="textarea"
                                :rows="5"
                                placeholder="请输入或粘贴需要处理的文本..."
                                maxlength="5000"
                                show-word-limit
                            />
                        </el-form-item>
                        <el-form-item label="操作类型">
                            <el-radio-group v-model="editType">
                                <el-radio-button value="rewrite">改写</el-radio-button>
                                <el-radio-button value="polish">润色</el-radio-button>
                                <el-radio-button value="continue">续写</el-radio-button>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="附加指令（可选）">
                            <el-input
                                v-model="editInstruction"
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
                        :loading="editLoading"
                        :disabled="!canEdit"
                        @click="handleEdit"
                        class="ai-action-btn"
                    >
                        <el-icon><MagicStick /></el-icon>
                        {{ editTypeLabel }}
                    </el-button>

                    <div v-if="editResult" class="ai-result-area">
                        <div class="ai-compare-container">
                            <div class="ai-compare-panel">
                                <div class="ai-compare-header">原文</div>
                                <div class="ai-compare-content"><pre class="ai-compare-text">{{ editText }}</pre></div>
                            </div>
                            <div class="ai-compare-divider">
                                <el-icon><DArrowRight /></el-icon>
                            </div>
                            <div class="ai-compare-panel">
                                <div class="ai-compare-header ai-compare-header--result">结果</div>
                                <div class="ai-compare-content"><pre class="ai-compare-text">{{ editResult }}</pre></div>
                            </div>
                        </div>
                        <div class="ai-result-actions">
                            <el-button size="small" @click="copyText(editResult, '结果')">
                                <el-icon><CopyDocument /></el-icon>
                                复制结果
                            </el-button>
                            <el-button size="small" type="primary" @click="replaceEditText">
                                <el-icon><Switch /></el-icon>
                                替换原文
                            </el-button>
                            <el-button size="small" @click="appendToEditText">
                                <el-icon><Bottom /></el-icon>
                                追加到原文                            </el-button>
                            <el-button size="small" type="warning" @click="sendToWriter">
                                <el-icon><Promotion /></el-icon>
                                发送到写作区                            </el-button>
                        </div>
                    </div>
                </el-card>

                <el-card shadow="never" class="ai-section-card">
                    <template #header>
                        <div class="ai-section-header">
                            <span class="ai-section-title">
                                <el-icon><TrendCharts /></el-icon>
                                AI SEO 优化
                            </span>
                        </div>
                    </template>
                    <el-form label-position="top">
                        <el-form-item label="文章标题">
                            <el-input v-model="seoTitle" placeholder="请输入文章标题" maxlength="100" />
                        </el-form-item>
                        <el-form-item label="鏂囩珷姝ｆ枃">
                            <el-input
                                v-model="seoContent"
                                type="textarea"
                                :rows="5"
                                placeholder="请输入文章正文..."
                                maxlength="10000"
                                show-word-limit
                            />
                        </el-form-item>
                        <el-form-item label="当前摘要（可选）">
                            <el-input v-model="seoDescription" placeholder="当前文章摘要" maxlength="200" />
                        </el-form-item>
                    </el-form>

                    <el-button
                        type="primary"
                        :loading="seoLoading"
                        :disabled="!canSeo"
                        @click="handleSeo"
                        class="ai-action-btn"
                    >
                        <el-icon><MagicStick /></el-icon>
                        开始优化                    </el-button>

                    <div v-if="seoResult" class="ai-result-area">
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
                            <div class="ai-seo-section-header">
                                <span>关键词</span>
                                <el-button size="small" @click="copyText(seoResult.keywords.join(', '), '关键词')">
                                    <el-icon><CopyDocument /></el-icon>
                                    复制
                                </el-button>
                            </div>
                            <div class="ai-seo-keywords">
                                <el-tag v-for="kw in seoResult.keywords" :key="kw" class="ai-seo-keyword-tag">{{ kw }}</el-tag>
                            </div>
                        </div>
                        <div class="ai-seo-meta">
                            <span>标题评分</span>
                            <el-progress :percentage="seoResult.titleScore" :color="scoreColor(seoResult.titleScore)" :stroke-width="8" style="width:200px" />
                            <strong>{{ seoResult.titleScore }} 分</strong>
                        </div>
                        <div v-if="seoResult.titleSuggestion" class="ai-seo-section">
                            <div class="ai-seo-section-header"><span>标题建议</span></div>
                            <div class="ai-seo-section-content ai-seo-suggestion">{{ seoResult.titleSuggestion }}</div>
                        </div>
                    </div>
                </el-card>

                <el-card shadow="never" class="ai-section-card">
                    <template #header>
                        <div class="ai-section-header">
                            <span class="ai-section-title">
                                <el-icon><CollectionTag /></el-icon>
                                AI 分类 / 标签推荐
                            </span>
                        </div>
                    </template>
                    <el-form label-position="top">
                        <el-form-item label="文章正文">
                            <el-input
                                v-model="recommendContent"
                                type="textarea"
                                :rows="5"
                                placeholder="请输入文章正文，AI 将基于此推荐分类和标签..."
                                maxlength="10000"
                                show-word-limit
                            />
                        </el-form-item>
                        <el-form-item label="推荐要求（可选）">
                            <el-input
                                v-model="recommendRequirement"
                                type="textarea"
                                :rows="2"
                                placeholder="描述您想要的风格或方向..."
                                maxlength="500"
                                show-word-limit
                            />
                        </el-form-item>
                    </el-form>

                    <div class="ai-recommend-btns">
                        <el-button
                            type="primary"
                            :loading="categoryRecLoading"
                            :disabled="!canRecommend"
                            @click="handleRecommendCategories"
                        >
                            <el-icon><MagicStick /></el-icon>
                            推荐分类
                        </el-button>
                        <el-button
                            type="success"
                            :loading="tagRecLoading"
                            :disabled="!canRecommend"
                            @click="handleRecommendTags"
                        >
                            <el-icon><MagicStick /></el-icon>
                            推荐标签
                        </el-button>
                    </div>

                    <div v-if="recommendedCategories.length > 0" class="ai-result-area">
                        <div class="ai-result-header">
                            <span>推荐分类</span>
                            <el-tag size="small" type="info">消耗 {{ categoryRecTokens }} tokens</el-tag>
                        </div>
                        <div class="ai-recommend-list">
                            <div v-for="cat in recommendedCategories" :key="cat" class="ai-recommend-item">
                                <span>{{ cat }}</span>
                                <el-button
                                    v-if="!addedCategories.includes(cat)"
                                    link type="primary" size="small"
                                    @click="addCategoryItem(cat)"
                                >添加</el-button>
                                <span v-else class="added-check">
                                    <el-icon><Check /></el-icon> 已添加                                </span>
                            </div>
                        </div>
                    </div>

                    <div v-if="recommendedTags.length > 0" class="ai-result-area">
                        <div class="ai-result-header">
                            <span>推荐标签</span>
                            <el-tag size="small" type="info">消耗 {{ tagRecTokens }} tokens</el-tag>
                        </div>
                        <div class="ai-recommend-list ai-tag-list">
                            <el-tag
                                v-for="tag in recommendedTags" :key="tag"
                                class="ai-tag-item"
                                :type="addedTags.includes(tag) ? 'success' : ''"
                            >
                                {{ tag }}
                                <el-button v-if="!addedTags.includes(tag)" link type="primary" size="small" @click="addTagItem(tag)" class="ml-1">添加</el-button>
                                <span v-else class="ml-1 added-check"><el-icon><Check /></el-icon></span>
                            </el-tag>
                        </div>
                    </div>
                </el-card>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import { showMessage } from '@/composables/util'
import {
    getAiQuota,
    generateArticle,
    estimateTokens,
    continueArticle,
    rewriteText,
    polishText,
    aiSeoOptimize,
    recommendTags,
    recommendCategories
} from '@/api/admin/ai'
import { publishArticle } from '@/api/admin/article'
import { addTags } from '@/api/admin/tag'
import { addCategory } from '@/api/admin/category'
import { getTagSelect } from '@/api/admin/tag'
import { getCategorySelect } from '@/api/admin/category'
import { DEFAULT_AI_SCENERY_COVER, buildAiRecommendRequirement } from '@/constants/ai'

const quotaLoading = ref(false)
const quota = reactive({
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
const remainingSeconds = ref(0)

function updateRemainingSeconds() {
    if (intervalTimer) clearInterval(intervalTimer)
    if (!quota.canInteractNow && quota.remainingCooldownSeconds > 0) {
        remainingSeconds.value = quota.remainingCooldownSeconds
        intervalTimer = setInterval(() => {
            remainingSeconds.value = Math.max(0, remainingSeconds.value - 1)
            if (remainingSeconds.value <= 0) {
                clearInterval(intervalTimer)
                quota.canInteractNow = true
                refreshQuota()
            }
        }, 1000)
    } else {
        remainingSeconds.value = 0
    }
}

async function refreshQuota() {
    quotaLoading.value = true
    try {
        const res = await getAiQuota()
        if (res.success) {
            Object.assign(quota, res.data)
            updateRemainingSeconds()
        }
    } catch (e) {
        console.error('获取额度失败', e)
    } finally {
        quotaLoading.value = false
    }
}

function formatNumber(num) {
    if (num == null) return '0'
    if (num >= 10000) return (num / 10000).toFixed(1) + '万'
    return num.toLocaleString()
}

const presetCategories = ref([])
const presetTags = ref([])

async function loadPresets() {
    try {
        const [tagRes, catRes] = await Promise.all([getTagSelect(), getCategorySelect()])
        if (tagRes.success && tagRes.data) {
            presetTags.value = (tagRes.data || []).map(t => t.label || t.name || t).slice(0, 30)
        }
        if (catRes.success && catRes.data) {
            presetCategories.value = (catRes.data || []).map(c => ({ id: c.value || c.id, name: c.label || c.name })).slice(0, 20)
        }
    } catch (e) {
        console.error('加载预设选项失败', e)
    }
}

onMounted(() => {
    refreshQuota()
    loadPresets()
})

onUnmounted(() => {
    if (intervalTimer) clearInterval(intervalTimer)
    if (writeEstimateTimer) clearTimeout(writeEstimateTimer)
})

// ===== AI 写作 =====
const writePrompt = ref('')
const writeTitle = ref('')
const writeCategory = ref('')
const writeTags = ref([])
const writeEstimatedTokens = ref(0)
const writeLoading = ref(false)
const writeResult = ref(null)
const writeResultTokens = ref(0)

let writeEstimateTimer = null

const canWrite = computed(() => {
    return writePrompt.value.trim().length > 0
        && quota.dailyArticleGenRemaining > 0
        && quota.canInteractNow
        && quota.dailyTokenRemaining > 0
})

const writeResultPreview = computed(() => {
    if (!writeResult.value || !writeResult.value.content) return ''
    return writeResult.value.content
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\n/g, '<br>')
})

watch(writePrompt, (val) => {
    if (writeEstimateTimer) clearTimeout(writeEstimateTimer)
    if (val.trim().length > 10) {
        writeEstimateTimer = setTimeout(async () => {
            try {
                const res = await estimateTokens({ prompt: val.trim() })
                if (res.success) writeEstimatedTokens.value = res.data.estimatedTokens
            } catch (e) { /* ignore */ }
        }, 600)
    } else {
        writeEstimatedTokens.value = 0
    }
})

async function handleWrite() {
    if (!writePrompt.value.trim()) {
        showMessage('请输入写作要求', 'warning')
        return
    }
    writeLoading.value = true
    try {
        const res = await generateArticle({
            prompt: writePrompt.value.trim(),
            title: writeTitle.value.trim() || null,
            categoryName: writeCategory.value || null,
            tags: writeTags.value.length > 0 ? writeTags.value : null
        }, { timeout: 120000 })
        if (res.success) {
            writeResult.value = res.data
            writeResultTokens.value = res.data.actualTokens
            showMessage('文章生成成功', 'success')
            await refreshQuota()
        } else {
            showMessage(res.message || '文章生成失败', 'error')
        }
    } catch (e) {
        console.error('文章生成失败', e)
        showMessage('文章生成失败，请稍后重试', 'error')
    } finally {
        writeLoading.value = false
    }
}

function generateDescription(content, maxLen = 200) {
    if (!content) return ''
    const text = content
        .replace(/#{1,6}\s/g, '')
        .replace(/[*_~`>|]/g, '')
        .replace(/\[([^\]]*)\]\([^)]*\)/g, '$1')
        .replace(/!\[([^\]]*)\]\([^)]*\)/g, '')
        .replace(/\n+/g, ' ')
        .trim()
    return text.length > maxLen ? text.slice(0, maxLen) + '...' : text
}

function resolveCategoryId(name) {
    if (!name) return null
    const found = presetCategories.value.find(c => c.name === name)
    return found ? Number(found.id) : null
}

async function saveAsArticle(visibility) {
    if (!writeResult.value) {
        showMessage('没有可保存的文章内容', 'warning')
        return
    }
    const title = writeResult.value.title || '无标题文章'
    const content = writeResult.value.content
    const description = generateDescription(content)
    const tags = writeTags.value.length > 0 ? writeTags.value : []

    try {
        const res = await publishArticle({
            title,
            content,
            titleImage: DEFAULT_AI_SCENERY_COVER,
            description,
            categoryName: writeCategory.value || null,
            tags,
            visibility
        })
        if (res.success) {
            showMessage(visibility === 'PUBLIC' ? '文章发布成功' : '私有文章发布成功', 'success')
        } else {
            showMessage(res.message || '保存失败', 'error')
        }
    } catch (e) {
        console.error('保存文章失败', e)
        showMessage('保存文章失败，请稍后重试', 'error')
    }
}

// ===== AI 改写/润色/续写 =====
const editText = ref('')
const editType = ref('rewrite')
const editInstruction = ref('')
const editLoading = ref(false)
const editResult = ref('')

const editTypeLabel = computed(() => {
    const map = { rewrite: '开始改写', polish: '开始润色', continue: '开始续写' }
    return map[editType.value] || '执行'
})

const canEdit = computed(() => {
    return editText.value.trim().length > 0
        && quota.canInteractNow
        && quota.dailyTokenRemaining > 0
})

async function handleEdit() {
    if (!editText.value.trim()) {
        showMessage('请输入原始文本', 'warning')
        return
    }
    editLoading.value = true
    editResult.value = ''
    try {
        let res
        const text = editText.value.trim()
        const instruction = editInstruction.value.trim() || null
        if (editType.value === 'rewrite') {
            res = await rewriteText({ text, style: instruction }, { timeout: 120000 })
        } else if (editType.value === 'polish') {
            res = await polishText({ text }, { timeout: 120000 })
        } else {
            res = await continueArticle({ context: text, instruction }, { timeout: 120000 })
        }
        if (res.success) {
            editResult.value = res.data.result
            showMessage('处理完成', 'success')
            await refreshQuota()
        } else {
            showMessage(res.message || '处理失败', 'error')
        }
    } catch (e) {
        console.error('AI 处理失败', e)
        showMessage('AI 处理失败，请稍后重试', 'error')
    } finally {
        editLoading.value = false
    }
}

function replaceEditText() {
    editText.value = editResult.value
    showMessage('已替换原文', 'success')
}

function appendToEditText() {
    editText.value = editText.value + '\n\n' + editResult.value
    showMessage('已追加到原文末尾', 'success')
}

function sendToWriter() {
    writePrompt.value = '基于以下内容进行扩展：\n' + editResult.value.slice(0, 500)
    showMessage('已发送到写作区', 'success')
}

// ===== AI SEO =====
const seoTitle = ref('')
const seoContent = ref('')
const seoDescription = ref('')
const seoLoading = ref(false)
const seoResult = ref(null)

const canSeo = computed(() => {
    return seoTitle.value.trim().length > 0
        && seoContent.value.trim().length > 0
        && quota.canInteractNow
        && quota.dailyTokenRemaining > 0
})

async function handleSeo() {
    seoLoading.value = true
    seoResult.value = null
    try {
        const res = await aiSeoOptimize({
            title: seoTitle.value.trim(),
            content: seoContent.value.trim(),
            currentDescription: seoDescription.value.trim() || null
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
        seoTitle.value = seoResult.value.optimizedTitle
        showMessage('标题已应用', 'success')
    }
}

function applySeoDescription() {
    if (seoResult.value && seoResult.value.optimizedDescription) {
        seoDescription.value = seoResult.value.optimizedDescription
        showMessage('摘要已应用', 'success')
    }
}

function scoreColor(score) {
    if (score >= 80) return '#22c55e'
    if (score >= 60) return '#f59e0b'
    return '#ef4444'
}

// ===== AI 推荐 =====
const recommendContent = ref('')
const recommendRequirement = ref('')
const categoryRecLoading = ref(false)
const tagRecLoading = ref(false)
const recommendedCategories = ref([])
const recommendedTags = ref([])
const addedCategories = ref([])
const addedTags = ref([])
const categoryRecTokens = ref(0)
const tagRecTokens = ref(0)

const canRecommend = computed(() => {
    return recommendContent.value.trim().length > 0
        && quota.canInteractNow
        && quota.dailyTokenRemaining > 0
})

async function handleRecommendCategories() {
    categoryRecLoading.value = true
    recommendedCategories.value = []
    try {
        const res = await recommendCategories({
            content: recommendContent.value.trim(),
            requirement: buildAiRecommendRequirement(recommendRequirement.value, recommendContent.value)
        }, { timeout: 120000 })
        if (res.success) {
            recommendedCategories.value = res.data.categories
            categoryRecTokens.value = res.data.actualTokens
            addedCategories.value = []
            await refreshQuota()
        } else {
            showMessage(res.message || '分类推荐失败', 'error')
        }
    } catch (e) {
        console.error('分类推荐失败', e)
        showMessage('分类推荐失败，请稍后重试', 'error')
    } finally {
        categoryRecLoading.value = false
    }
}

async function handleRecommendTags() {
    tagRecLoading.value = true
    recommendedTags.value = []
    try {
        const res = await recommendTags({
            content: recommendContent.value.trim(),
            requirement: buildAiRecommendRequirement(recommendRequirement.value, recommendContent.value)
        }, { timeout: 120000 })
        if (res.success) {
            recommendedTags.value = res.data.tags
            tagRecTokens.value = res.data.actualTokens
            addedTags.value = []
            await refreshQuota()
        } else {
            showMessage(res.message || '标签推荐失败', 'error')
        }
    } catch (e) {
        console.error('标签推荐失败', e)
        showMessage('标签推荐失败，请稍后重试', 'error')
    } finally {
        tagRecLoading.value = false
    }
}

async function addCategoryItem(name) {
    try {
        await addCategory({ name })
        addedCategories.value.push(name)
        showMessage(`分类“${name}”已添加`, 'success')
    } catch (e) {
        console.error('添加分类失败', e)
        showMessage('添加分类失败，请稍后重试', 'error')
    }
}

async function addTagItem(name) {
    try {
        await addTags({ tags: [name] })
        addedTags.value.push(name)
        showMessage(`标签“${name}”已添加`, 'success')
    } catch (e) {
        console.error('添加标签失败', e)
        showMessage('添加标签失败，请稍后重试', 'error')
    }
}

// ===== 通用工具 =====
async function copyText(text, label) {
    try {
        await navigator.clipboard.writeText(text)
        showMessage(`${label}已复制到剪贴板`, 'success')
    } catch (e) {
        const textarea = document.createElement('textarea')
        textarea.value = text
        document.body.appendChild(textarea)
        textarea.select()
        document.execCommand('copy')
        document.body.removeChild(textarea)
        showMessage(`${label}已复制到剪贴板`, 'success')
    }
}
</script>

<style scoped>
.ai-workbench-grid {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.ai-workbench-main {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.ai-section-card {
    border-radius: 12px;
}

.ai-section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.ai-section-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 700;
    font-size: 15px;
    color: #1e293b;
}

.ai-quota-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
}

.ai-quota-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding: 12px;
    background: #f8fafc;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
}

.ai-quota-label {
    font-size: 12px;
    color: #64748b;
    font-weight: 600;
}

.ai-quota-value {
    font-size: 14px;
    color: #334155;
}

.ai-quota-value strong {
    font-size: 18px;
    font-weight: 800;
    color: #1e293b;
}

.ai-cooldown-warning {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 12px;
    padding: 10px 14px;
    background: #fef3c7;
    border-radius: 8px;
    color: #92400e;
    font-size: 13px;
    font-weight: 600;
}

.ai-write-limit-tip {
    margin-bottom: 12px;
    padding: 10px 12px;
    border: 1px solid #fde68a;
    border-radius: 8px;
    background: #fffbeb;
    color: #92400e;
    font-size: 13px;
    line-height: 1.6;
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

.ai-action-btn {
    width: 100%;
    margin-top: 8px;
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
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    font-size: 13px;
    color: #475569;
}

.ai-recommend-btns {
    display: flex;
    gap: 12px;
}

.ai-recommend-list {
    padding: 14px;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.ai-recommend-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 12px;
    background: #f8fafc;
    border-radius: 8px;
    font-size: 14px;
    color: #334155;
}

.ai-tag-list {
    flex-direction: row;
    flex-wrap: wrap;
}

.ai-tag-item {
    font-size: 13px;
}

.added-check {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    color: #16a34a;
    font-size: 12px;
    font-weight: 600;
}

.text-red-500 {
    color: #ef4444 !important;
}
</style>



