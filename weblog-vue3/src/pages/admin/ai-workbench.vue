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
                            />
                        </el-form-item>
                        <el-form-item label="建议标题（可选）">
                            <el-input v-model="writeTitle" placeholder="留空则由 AI 自动生成" />
                        </el-form-item>
                        <el-form-item label="文章分类（可选）">
                            <el-select v-model="writeCategory" placeholder="请选择文章分类" clearable filterable remote reserve-keyword :remote-method="searchCategories" :loading="categorySearchLoading" style="width:100%">
                                <el-option v-for="cat in displayedCategories" :key="cat.id" :label="cat.name" :value="cat.id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="文章标签（可选）">
                            <el-select v-model="writeTags" placeholder="请选择文章标签" multiple collapse-tags collapse-tags-tooltip style="width:100%">
                                <el-option v-for="tag in presetTags" :key="tag.value" :label="tag.label" :value="tag.value" />
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
                            />
                        </el-form-item>
                        <el-form-item label="推荐要求（可选）">
                            <el-input
                                v-model="recommendRequirement"
                                type="textarea"
                                :rows="2"
                                placeholder="描述您想要的风格或方向..."
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

                    <div v-if="categorySuggestions.length > 0 || unmatchedCategoryTerms.length > 0" class="ai-result-area">
                        <div class="ai-result-header">
                            <span>推荐分类</span>
                            <el-tag size="small" type="info">消耗 {{ categoryRecTokens }} tokens</el-tag>
                        </div>
                        <div v-if="categorySuggestions.length > 0" class="ai-recommend-list">
                            <div v-for="cat in categorySuggestions" :key="cat.categoryId" class="ai-recommend-item">
                                <div>
                                    <span>{{ cat.categoryName }}</span>
                                    <span v-if="cat.parentCategoryName" style="font-size:12px;color:#64748b;margin-left:8px">{{ cat.parentCategoryName }} / {{ cat.categoryName }}</span>
                                </div>
                                <el-button
                                    v-if="!acceptedCategoryId || acceptedCategoryId !== cat.categoryId"
                                    link type="primary" size="small"
                                    @click="acceptCategory(cat)"
                                >接受</el-button>
                                <span v-else class="added-check">
                                    <el-icon><Check /></el-icon> 已接受
                                </span>
                            </div>
                        </div>
                        <div v-if="unmatchedCategoryTerms.length > 0" style="padding:12px 14px;border-top:1px solid #e2e8f0">
                            <div style="font-size:12px;color:#94a3b8;margin-bottom:8px">未匹配项（不在系统分类中）</div>
                            <div style="display:flex;flex-wrap:wrap;gap:6px">
                                <el-tag v-for="term in unmatchedCategoryTerms" :key="term" type="info" size="small" effect="plain">{{ term }}</el-tag>
                            </div>
                        </div>
                    </div>

                    <div v-if="tagSuggestions.length > 0 || unmatchedTagTerms.length > 0" class="ai-result-area">
                        <div class="ai-result-header">
                            <span>推荐标签</span>
                            <el-tag size="small" type="info">消耗 {{ tagRecTokens }} tokens</el-tag>
                        </div>
                        <div v-if="tagSuggestions.length > 0" class="ai-recommend-list">
                            <div v-for="tag in tagSuggestions" :key="tag.tagId" class="ai-recommend-item">
                                <div style="display:flex;align-items:center;gap:8px">
                                    <el-tag :type="acceptedTagIds.includes(tag.tagId) ? 'success' : ''">{{ tag.tagName }}</el-tag>
                                    <span v-if="tag.categoryName" style="font-size:12px;color:#64748b;background:#f1f5f9;padding:2px 8px;border-radius:4px">{{ tag.categoryName }}</span>
                                </div>
                                <el-button v-if="!acceptedTagIds.includes(tag.tagId)" link type="primary" size="small" @click="acceptTag(tag)">接受</el-button>
                                <span v-else class="added-check"><el-icon><Check /></el-icon> 已接受</span>
                            </div>
                        </div>
                        <div v-if="unmatchedTagTerms.length > 0" style="padding:12px 14px;border-top:1px solid #e2e8f0">
                            <div style="font-size:12px;color:#94a3b8;margin-bottom:8px">未匹配项（不在系统标签库中）</div>
                            <div style="display:flex;flex-wrap:wrap;gap:6px">
                                <el-tag v-for="term in unmatchedTagTerms" :key="term" type="info" size="small" effect="plain">{{ term }}</el-tag>
                            </div>
                        </div>
                    </div>
                </el-card>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'
import { showMessage } from '@/composables/util'
import {
    generateArticle,
    estimateTokens,
    continueArticle,
    rewriteText,
    polishText,
    recommendTags,
    recommendCategories
} from '@/api/admin/ai'
import { publishArticle } from '@/api/admin/article'
import { getCategoryTree, searchTaxonomyTags, searchTaxonomyCategories } from '@/api/admin/taxonomy'
import { DEFAULT_AI_SCENERY_COVER, buildAiRecommendRequirement } from '@/constants/ai'

const presetCategories = ref([])
const presetTags = ref([])
const categorySearchLoading = ref(false)
const categorySearchResults = ref(null)

const displayedCategories = computed(() => {
    return categorySearchResults.value !== null ? categorySearchResults.value : presetCategories.value
})

async function loadPresets() {
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
            presetCategories.value = allCategories.slice(0, 50)
        }
        const tagRes = await searchTaxonomyTags({ key: '' })
        if (tagRes.success && tagRes.data) {
            presetTags.value = (tagRes.data || []).map(t => ({
                value: t.id,
                label: t.name
            })).slice(0, 50)
        }
    } catch (e) {
        console.error('加载预设选项失败', e)
    }
}

loadPresets()

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

onUnmounted(() => {
    if (writeEstimateTimer) clearTimeout(writeEstimateTimer)
})

// ===== AI 写作 =====
const writePrompt = ref('')
const writeTitle = ref('')
const writeCategory = ref(null)
const writeTags = ref([])
const writeEstimatedTokens = ref(0)
const writeLoading = ref(false)
const writeResult = ref(null)
const writeResultTokens = ref(0)

let writeEstimateTimer = null

const canWrite = computed(() => {
    return writePrompt.value.trim().length > 0
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
            categoryId: writeCategory.value || null,
            tagIds: writeTags.value.length > 0 ? writeTags.value : null
        }, { timeout: 120000 })
        if (res.success) {
            writeResult.value = res.data
            writeResultTokens.value = res.data.actualTokens
            showMessage('文章生成成功', 'success')
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

async function saveAsArticle(visibility) {
    if (!writeResult.value) {
        showMessage('没有可保存的文章内容', 'warning')
        return
    }
    const title = writeResult.value.title || '无标题文章'
    const content = writeResult.value.content
    const description = generateDescription(content)

    try {
        const res = await publishArticle({
            title,
            content,
            titleImage: DEFAULT_AI_SCENERY_COVER,
            description,
            categoryId: writeCategory.value || null,
            tagIds: writeTags.value.length > 0 ? writeTags.value : [],
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

// ===== AI 推荐 =====
const recommendContent = ref('')
const recommendRequirement = ref('')
const categoryRecLoading = ref(false)
const tagRecLoading = ref(false)
const categorySuggestions = ref([])
const unmatchedCategoryTerms = ref([])
const acceptedCategoryId = ref(null)
const tagSuggestions = ref([])
const unmatchedTagTerms = ref([])
const acceptedTagIds = ref([])
const categoryRecTokens = ref(0)
const tagRecTokens = ref(0)

const canRecommend = computed(() => {
    return recommendContent.value.trim().length > 0
})

async function handleRecommendCategories() {
    categoryRecLoading.value = true
    categorySuggestions.value = []
    unmatchedCategoryTerms.value = []
    try {
        const res = await recommendCategories({
            content: recommendContent.value.trim(),
            requirement: buildAiRecommendRequirement(recommendRequirement.value, recommendContent.value)
        }, { timeout: 120000 })
        if (res.success) {
            categorySuggestions.value = res.data.suggestions || []
            unmatchedCategoryTerms.value = res.data.unmatchedTerms || []
            categoryRecTokens.value = res.data.actualTokens
            acceptedCategoryId.value = null
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
    tagSuggestions.value = []
    unmatchedTagTerms.value = []
    try {
        const res = await recommendTags({
            content: recommendContent.value.trim(),
            requirement: buildAiRecommendRequirement(recommendRequirement.value, recommendContent.value)
        }, { timeout: 120000 })
        if (res.success) {
            tagSuggestions.value = res.data.suggestions || []
            unmatchedTagTerms.value = res.data.unmatchedTerms || []
            tagRecTokens.value = res.data.actualTokens
            acceptedTagIds.value = []
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

function acceptCategory(cat) {
    const exists = presetCategories.value.some(c => c.id === cat.categoryId)
    if (!exists) {
        showMessage(`分类「${cat.categoryName}」不在系统分类中，无法接受`, 'warning')
        return
    }
    acceptedCategoryId.value = cat.categoryId
    showMessage(`分类「${cat.categoryName}」已接受`, 'success')
}

function acceptTag(tag) {
    const exists = presetTags.value.some(t => t.value === tag.tagId)
    if (!exists) {
        showMessage(`标签「${tag.tagName}」不在系统标签库中，无法接受`, 'warning')
        return
    }
    if (!acceptedTagIds.value.includes(tag.tagId)) {
        acceptedTagIds.value.push(tag.tagId)
        showMessage(`标签「${tag.tagName}」已接受`, 'success')
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
</style>



