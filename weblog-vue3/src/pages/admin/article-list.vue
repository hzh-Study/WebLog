<template>
    <div class="admin-page">
    <section class="admin-page-header">
        <div>
            <span class="admin-kicker">Article Manager</span>
            <h1 class="admin-page-title">文章管理</h1>
            <p class="admin-page-desc">筛选、编辑、发布和预览博客文章，统一使用更完整的后台卡片与操作区布局。</p>
        </div>
        <div class="admin-page-actions">
            <span class="admin-summary-chip"><strong>{{ total }}</strong> 篇文章</span>
        </div>
    </section>

    <el-card shadow="never" class="admin-filter-card mb-5">
        <div class="admin-filter-grid">
            <div class="admin-field">
                <span class="admin-field-label">文章标题</span>
                <el-input v-model="searchTitle" placeholder="请输入（模糊查询）" />
            </div>
            <div class="admin-field">
                <span class="admin-field-label">发布日期</span>
                <el-date-picker v-model="pickDate" type="daterange" range-separator="至" start-placeholder="开始时间"
                    end-placeholder="结束时间" :shortcuts="shortcuts" size="default" @change="datepickerChange" />
            </div>
            <div class="admin-field">
                <span class="admin-field-label">文章分类</span>
                <el-select v-model="filterCategoryId" clearable placeholder="全部分类" size="default" @change="getTableData">
                    <el-option v-for="cat in flatCategories" :key="cat.value" :label="cat.label" :value="cat.value" />
                </el-select>
            </div>
            <div class="admin-filter-actions">
                <el-button type="primary" :icon="Search" @click="getTableData">查询</el-button>
                <el-button :icon="RefreshRight" @click="reset">重置</el-button>
            </div>
        </div>
    </el-card>


    <el-card shadow="never" class="admin-table-card">
        <div class="admin-table-toolbar">
            <div>
                <div class="admin-table-title">文章列表</div>
                <div class="admin-table-desc">分类与标签由系统统一管理，发文时请从预定义选项中选择。</div>
            </div>
            <el-button type="primary" :icon="Edit" @click="openPublishDialog">写文章</el-button>
        </div>

        <el-table
            :data="tableData"
            stripe
            class="mt-4 admin-article-table"
            v-loading="tableLoading"
            table-layout="fixed"
        >
            <el-table-column
                prop="title"
                min-width="200"
                show-overflow-tooltip
                class-name="admin-col-title"
            >
                <template #header>
                    <el-tooltip content="文章标题" placement="top">
                        <span class="admin-th-icon" role="text" aria-label="标题列">
                            <el-icon><Document /></el-icon>
                        </span>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column width="92" align="center">
                <template #header>
                    <el-tooltip content="可见性" placement="top">
                        <span class="admin-th-icon" role="text" aria-label="可见性列">
                            <el-icon><Key /></el-icon>
                        </span>
                    </el-tooltip>
                </template>
                <template #default="scope">
                    <el-tag v-if="scope.row.visibility === 'PUBLIC'" type="success" size="small" effect="light" class="vis-tag">公开</el-tag>
                    <el-tag v-else type="info" size="small" effect="light" class="vis-tag">私有</el-tag>
                </template>
            </el-table-column>
            <el-table-column width="100" align="center">
                <template #header>
                    <el-tooltip content="分类" placement="top">
                        <span class="admin-th-icon" role="text" aria-label="分类列">
                            <el-icon><FolderOpened /></el-icon>
                        </span>
                    </el-tooltip>
                </template>
                <template #default="scope">
                    <span v-if="scope.row.categoryName" class="admin-cell-text">{{ scope.row.categoryName }}</span>
                    <span v-else class="admin-cell-muted">—</span>
                </template>
            </el-table-column>
            <el-table-column min-width="140" align="center">
                <template #header>
                    <el-tooltip content="标签" placement="top">
                        <span class="admin-th-icon" role="text" aria-label="标签列">
                            <el-icon><PriceTag /></el-icon>
                        </span>
                    </el-tooltip>
                </template>
                <template #default="scope">
                    <div v-if="scope.row.tagNames && scope.row.tagNames.length" class="admin-tag-row">
                        <el-tag v-for="name in scope.row.tagNames" :key="name" size="small" effect="plain" class="admin-table-tag">{{ name }}</el-tag>
                    </div>
                    <span v-else class="admin-cell-muted">—</span>
                </template>
            </el-table-column>
            <el-table-column width="96" align="center">
                <template #header>
                    <el-tooltip content="预览图" placement="top">
                        <span class="admin-th-icon" role="text" aria-label="预览图列">
                            <el-icon><Picture /></el-icon>
                        </span>
                    </el-tooltip>
                </template>
                <template #default="scope">
                    <el-image class="admin-table-image" :src="resolveAiCover(scope.row.titleImage)" fit="cover" />
                </template>
            </el-table-column>
            <el-table-column prop="createTime" width="166" align="center">
                <template #header>
                    <el-tooltip content="发布时间" placement="top">
                        <span class="admin-th-icon" role="text" aria-label="发布时间列">
                            <el-icon><Calendar /></el-icon>
                        </span>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column width="120" align="center" fixed="right">
                <template #header>
                    <el-tooltip content="操作" placement="top">
                        <span class="admin-th-icon" role="text" aria-label="操作列">
                            <el-icon><Operation /></el-icon>
                        </span>
                    </el-tooltip>
                </template>
                <template #default="scope">
                    <div class="admin-row-actions">
                        <el-tooltip content="编辑" placement="top">
                            <el-button link type="primary" :icon="Edit" @click="showArticleUpdateEditorShow(scope.row)" />
                        </el-tooltip>
                        <el-tooltip content="预览" placement="top">
                            <el-button link :icon="View" @click="previewArticle(scope.row)" />
                        </el-tooltip>
                        <el-tooltip content="删除" placement="top">
                            <el-button link type="danger" :icon="Delete" @click="deleteArticleSubmit(scope.row)" />
                        </el-tooltip>
                        <el-tooltip content="版本" placement="top">
                            <el-button link :icon="Clock" @click="openVersionPanel(scope.row)" />
                        </el-tooltip>
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <div class="admin-pagination-wrap">
            <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]" :small="small"
                :disabled="disabled" background="true" layout="total, sizes, prev, pager, next, jumper" :total="total"
                @size-change="handleSizeChange" @current-change="getTableData" />
        </div>
    </el-card>

    <!-- 写博客 -->
    <el-dialog v-model="isArticlePublishEditorShow" destroy-on-close fullscreen="true" :show-close="false" :modal="false" class="admin-article-dialog">

        <template #header="{ close, titleId, titleClass }">
            <div class="">
                <div class="my-header flex justify-between admin-dialog-header">
                        <h4 class="font-bold">写文章</h4>
                        <div class="admin-dialog-footer">
                            <el-button @click="hidePublishEditor">取消</el-button>
                            <el-button type="primary" @click="onSubmit">
                                <el-icon class="mr-1">
                                    <Promotion />
                                </el-icon>
                                发布
                            </el-button>
                        </div>
                    </div>
            </div>
        </template>
        <el-form v-if="isArticlePublishEditorShow" :model="form" ref="publishArticleFormRef" label-position="top" :size="large" :rules="rules" class="admin-form-layout">
            <el-form-item label="标题" prop="title">
                <div class="admin-title-row">
                    <el-input v-model="form.title" autocomplete="off" size="large" maxlength="40" show-word-limit clearable class="admin-title-input" />
                    <el-button type="warning" size="large" @click="openSeoDialog" :loading="seoLoading" :disabled="!form.title.trim() || !form.content.trim()">
                        <el-icon><TrendCharts /></el-icon>
                        AI SEO
                    </el-button>
                </div>
            </el-form-item>
            <el-form-item label="内容" prop="content">
                <MdEditor v-model="form.content" @onUploadImg="onUploadImg" editorId="publishArticleEditor" />
            </el-form-item>
            <el-form-item label="封面" prop="titleImage">
                <el-upload class="avatar-uploader admin-upload-box" action="#" :on-change="handleTitleImageChange" :auto-upload="false"
                    :show-file-list="false" :on-success="handleAvatarSuccess">
                    <img v-if="form.titleImage" :src="form.titleImage" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </el-form-item>
            <el-form-item label="摘要" prop="description">
                <el-input v-model="form.description" :rows="3" type="textarea" placeholder="请输入文章摘要" />
            </el-form-item>
            <el-form-item label="分类" prop="categoryId">
                <el-select v-model="form.categoryId" clearable placeholder="---请选择---" size="large" class="admin-wide-select" @change="handleCategoryChange" filterable remote reserve-keyword :remote-method="searchCategories" :loading="categorySearchLoading">
                    <template v-if="!categorySearchActive">
                        <el-option v-for="cat in flatCategories" :key="cat.value" :label="cat.label" :value="cat.value" />
                    </template>
                    <template v-else>
                        <el-option v-for="cat in categorySearchResults" :key="cat.id" :label="cat.name" :value="cat.id" />
                    </template>
                </el-select>
            </el-form-item>
            <el-form-item label="标签" prop="tagIds">
                <el-select v-model="form.tagIds" multiple :multiple-limit="3" filterable remote reserve-keyword placeholder="输入关键字搜索标签（最多3个）" class="admin-wide-select"
                    :remote-method="searchTags" @visible-change="handleTagDropdownVisible"
                    :loading="tagSelectLoading" size="large">
                    <el-option v-for="item in tagOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="可见性" prop="visibility">
                <el-select v-model="form.visibility" size="large" class="admin-wide-select">
                    <el-option label="公开" value="PUBLIC" />
                    <el-option label="私有" value="PRIVATE" />
                </el-select>
            </el-form-item>

        </el-form>
    </el-dialog>

    <!-- 编辑博客 -->
    <el-dialog v-model="isArticleUpdateEditorShow" destroy-on-close fullscreen="true" :show-close="false" :modal="false" class="admin-article-dialog">
        <template #header="{ close, titleId, titleClass }">
            <div class="my-header flex items-center justify-between admin-dialog-header">
                <h4 class="font-bold">编辑文章</h4>
                <div class="admin-dialog-footer">
                    <el-button @click="hideArticleUpdateEditor">取消</el-button>
                    <el-button type="primary" @click="updateSubmit">
                        <el-icon class="mr-1">
                            <Promotion />
                        </el-icon>
                        提交
                    </el-button>
                </div>
            </div>
        </template>
        <el-form v-if="isArticleUpdateEditorShow" :model="form" ref="updateArticleFormRef" label-position="top" :size="large" :rules="rules" class="admin-form-layout">
            <el-form-item label="标题" prop="title">
                <div class="admin-title-row">
                    <el-input v-model="form.title" autocomplete="off" size="large" maxlength="40" show-word-limit clearable class="admin-title-input" />
                    <el-button type="warning" size="large" @click="openSeoDialog" :loading="seoLoading" :disabled="!form.title.trim() || !form.content.trim()">
                        <el-icon><TrendCharts /></el-icon>
                        AI SEO
                    </el-button>
                </div>
            </el-form-item>
            <el-form-item label="内容" prop="content">
                <MdEditor
                    v-model="form.content"
                    :key="articleEditKey"
                    @onUploadImg="onUploadImg"
                    editorId="updateArticleEditor"
                />
            </el-form-item>
            <el-form-item label="封面" prop="titleImage">
                <el-upload class="avatar-uploader admin-upload-box" action="#" :on-change="handleTitleImageChange" :auto-upload="false"
                    :show-file-list="false" :on-success="handleAvatarSuccess">
                    <img v-if="form.titleImage" :src="form.titleImage" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </el-form-item>
            <el-form-item label="摘要" prop="description">
                <el-input v-model="form.description" :rows="3" type="textarea" placeholder="请输入文章摘要" />
            </el-form-item>
            <el-form-item label="分类" prop="categoryId">
                <el-select v-model="form.categoryId" clearable placeholder="---请选择---" size="large" class="admin-wide-select" @change="handleCategoryChange" filterable remote reserve-keyword :remote-method="searchCategories" :loading="categorySearchLoading">
                    <template v-if="!categorySearchActive">
                        <el-option v-for="cat in flatCategories" :key="cat.value" :label="cat.label" :value="cat.value" />
                    </template>
                    <template v-else>
                        <el-option v-for="cat in categorySearchResults" :key="cat.id" :label="cat.name" :value="cat.id" />
                    </template>
                </el-select>
            </el-form-item>
            <el-form-item label="标签" prop="tagIds">
                <el-select v-model="form.tagIds" multiple :multiple-limit="3" filterable remote reserve-keyword placeholder="输入关键字搜索标签（最多3个）" class="admin-wide-select"
                    :remote-method="searchTags" @visible-change="handleTagDropdownVisible"
                    :loading="tagSelectLoading" size="large">
                    <el-option v-for="item in tagOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="可见性" prop="visibility">
                <el-select v-model="form.visibility" size="large" class="admin-wide-select">
                    <el-option label="公开" value="PUBLIC" />
                    <el-option label="私有" value="PRIVATE" />
                </el-select>
            </el-form-item>
        </el-form>
    </el-dialog>
</div>

<ArticleVersionPanel v-model="showVersionPanel" :article-id="versionArticleId" />

<!-- AI SEO 优化弹窗 -->
<el-dialog v-model="seoDialogVisible" title="AI SEO 优化" width="600px" destroy-on-close>
    <div v-if="seoLoading" class="seo-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>AI 正在分析文章，请稍候...</span>
    </div>
    <div v-else-if="seoResult" class="seo-result">
        <div class="seo-section">
            <div class="seo-section-header">
                <span>优化标题</span>
                <el-button size="small" type="primary" @click="applySeoTitle">应用到表单</el-button>
            </div>
            <div class="seo-section-content">{{ seoResult.optimizedTitle }}</div>
        </div>
        <div class="seo-section">
            <div class="seo-section-header">
                <span>优化摘要</span>
                <el-button size="small" type="primary" @click="applySeoDescription">应用到表单</el-button>
            </div>
            <div class="seo-section-content">{{ seoResult.optimizedDescription }}</div>
        </div>
        <div class="seo-section">
            <div class="seo-section-header"><span>关键词</span></div>
            <div class="seo-keywords">
                <el-tag v-for="kw in seoResult.keywords" :key="kw" class="seo-keyword-tag">{{ kw }}</el-tag>
            </div>
        </div>
        <div class="seo-score-row">
            <span>标题评分</span>
            <el-progress :percentage="seoResult.titleScore" :color="seoScoreColor(seoResult.titleScore)" :stroke-width="8" style="flex:1;max-width:200px" />
            <strong>{{ seoResult.titleScore }} 分</strong>
        </div>
        <div v-if="seoResult.titleSuggestion" class="seo-section">
            <div class="seo-section-header"><span>标题建议</span></div>
            <div class="seo-section-content seo-suggestion">{{ seoResult.titleSuggestion }}</div>
        </div>
    </div>
    <div v-else class="seo-empty">
        <span>点击"AI SEO"按钮开始优化</span>
    </div>
    <template #footer>
        <el-button @click="seoDialogVisible = false">关闭</el-button>
        <el-button type="warning" @click="handleSeoOptimize" :loading="seoLoading" :disabled="!form.title.trim() || !form.content.trim()">
            <el-icon><TrendCharts /></el-icon>
            重新优化
        </el-button>
    </template>
</el-dialog>
</template>

<script setup>
// import { ElMessage, ElMessageBox } from 'element-plus'
import { ref, reactive, nextTick, onMounted, onUnmounted } from 'vue'
// import MDEditor from '@/components/MDEditor.vue'
import { publishArticle, getArticlePageList, deleteArticle, getArticleDetail, updateArticle } from '@/api/admin/article'
import { uploadFile } from '@/api/admin/file'
import { aiSeoOptimize } from '@/api/admin/ai'
import MdEditor from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { showMessage } from '@/composables/util'
import { useRouter, useRoute } from 'vue-router'
import { getCategoryTree, searchTaxonomyTags, getTagsByCategory, searchTaxonomyCategories } from '@/api/admin/taxonomy'
import moment from 'moment';
import ArticleVersionPanel from '@/components/ArticleVersionPanel.vue'
import { resolveAiCover } from '@/constants/ai'
import {
    Search,
    RefreshRight,
    Edit,
    View,
    Delete,
    Document,
    Key,
    Picture,
    Calendar,
    Operation,
    Clock,
    TrendCharts,
    Loading,
    FolderOpened,
    PriceTag,
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

function onArticleCreated() {
    getTableData()
}

const showVersionPanel = ref(false)
const versionArticleId = ref(null)

function openVersionPanel(row) {
    versionArticleId.value = row.id
    showVersionPanel.value = true
}

const form = reactive({
    id: null,
    title: '',
    content: '',
    titleImage: '',
    categoryId: null,
    tagIds: [],
    description: "",
    visibility: 'PUBLIC',
})

const isArticlePublishEditorShow = ref(false)
const isArticleUpdateEditorShow = ref(false)
const tableLoading = ref(false)
/** 每次成功加载待编辑文章后递增，使 MdEditor 与表单随数据重挂载 */
const articleEditKey = ref(0)
const articleEditLoading = ref(false)

const searchTitle = ref('')
const pickDate = ref('')
const filterCategoryId = ref(null)
const startDate = ref(null)
const endDate = ref(null)

const reset = () => {
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    searchTitle.value = ''
    filterCategoryId.value = null
    current.value = 1
    getTableData()
}

const datepickerChange = (e) => {
    if (!e) {
        startDate.value = null
        endDate.value = null
        return
    }
    startDate.value = moment(e[0]).format('YYYY-MM-DD HH:mm:ss')
    endDate.value = moment(e[1]).format('YYYY-MM-DD HH:mm:ss')
}

const shortcuts = [
    {
        text: '最近一周',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        },
    },
    {
        text: '最近一个月',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            return [start, end]
        },
    },
    {
        text: '最近三个月',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            return [start, end]
        },
    },
]

const handleTitleImageChange = (file) => {
    console.log('开始上传文件')
    console.log(file)
    let formData = new FormData()
    formData.append("file", file.raw);
    uploadFile(formData).then((e) => {
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'warning', 'message')
            return
        }
        form.titleImage = e.data.url
        showMessage('文章题图上传成功', 'success', 'message')
    })
}

const resetArticleForm = () => {
    form.id = null
    form.title = ''
    form.content = ''
    form.titleImage = ''
    form.categoryId = null
    form.tagIds = []
    form.description = ''
    form.visibility = 'PUBLIC'
}

/** 打开发布弹窗前清空表单，避免与「编辑」共用 reactive 时带上旧稿或空白被另一路 MdEditor 污染 */
const openPublishDialog = () => {
    resetArticleForm()
    isArticlePublishEditorShow.value = true
}

const hidePublishEditor = () => {
    isArticlePublishEditorShow.value = false
    resetArticleForm()
}

const hideArticleUpdateEditor = () => {
    isArticleUpdateEditorShow.value = false
    resetArticleForm()
}

const handleCategoryChange = (categoryId) => {
    form.tagIds = []
    loadTagsByCategory(categoryId)
}

const applyArticleDetailToForm = (d) => {
    if (!d) return
    form.id = d.id
    form.title = d.title ?? ''
    form.content = d.content != null ? String(d.content) : ''
    form.titleImage = d.titleImage ?? ''
    const cid = d.categoryId
    form.categoryId = cid == null || cid === '' ? null : Number(cid)
    const raw = d.tagIds
    if (Array.isArray(raw) && raw.length) {
        form.tagIds = raw.map((t) => Number(t))
    } else {
        form.tagIds = []
    }
    if (form.categoryId) {
        loadTagsByCategory(form.categoryId)
    }
    form.description = d.description ?? ''
    form.visibility = d.visibility != null && d.visibility !== '' ? d.visibility : 'PUBLIC'
}

const refreshSelectOptions = async () => {
    await loadCategoryTree()
    if (form.categoryId) {
        await loadTagsByCategory(form.categoryId)
    }
}

async function searchCategories(query) {
    if (!query) {
        categorySearchActive.value = false
        categorySearchResults.value = []
        return
    }
    categorySearchLoading.value = true
    categorySearchActive.value = true
    try {
        const res = await searchTaxonomyCategories({ key: query })
        if (res.success && res.data) {
            categorySearchResults.value = (res.data || []).map(c => ({
                id: c.id,
                name: c.name
            }))
        }
    } catch (e) {
        console.error('搜索分类失败', e)
    } finally {
        categorySearchLoading.value = false
    }
}

const showArticleUpdateEditorShow = async (row) => {
    const articleId = row?.id
    if (articleId == null) {
        showMessage('无法获取文章 ID', 'warning', 'message')
        return
    }
    articleEditLoading.value = true
    try {
        const e = await getArticleDetail(articleId)
        if (e && e.success === true && e.data) {
            await refreshSelectOptions()
            applyArticleDetailToForm(e.data)
            isArticleUpdateEditorShow.value = true
            await nextTick()
            articleEditKey.value += 1
            await nextTick()
        } else {
            showMessage((e && e.message) || '获取文章详情失败', 'warning', 'message')
        }
    } catch (err) {
        console.error(err)
        showMessage('获取文章详情失败，请稍后重试', 'error', 'message')
    } finally {
        articleEditLoading.value = false
    }
}

onMounted(() => {
})

const onUploadImg = async (files, callback) => {
    await Promise.all(
        files.map((file) => {
            let formData = new FormData()
            formData.append("file", file)
            return uploadFile(formData).then((res) => {
                callback([res.data.url])
            }).catch((err) => {
                console.error('文件上传失败:', err)
            })
        })
    )
}

const previewArticle = (row) => {
    if (row.visibility === 'PRIVATE') {
        showMessage('私有文章不会在前台公开展示，请先改为公开后再预览', 'warning', 'message')
        return
    }
    // 打开一个新页面
    let routeData = router.resolve({ path: '/article/detail', query: { articleId: row.id } });
    window.open(routeData.href, '_blank');
}

const publishArticleFormRef = ref(null)
const updateArticleFormRef = ref(null)
const rules = {
    title: [
        { required: true, message: '请输入文章标题', trigger: 'blur' },
        { min: 1, max: 40, message: '文章标题要求大于1个字符，小于40个字符', trigger: 'blur' },
    ],
    content: [{ required: true }],
    titleImage: [{ required: true }],
    categoryId: [{ required: true, message: '请选择文章分类', trigger: 'blur' }],
    tagIds: [{ required: true, message: '请选择文章标签', trigger: 'blur' }],
    description: [{ required: true, message: '请输入文章摘要', trigger: 'blur' }],
    visibility: [{ required: true, message: '请选择可见性', trigger: 'change' }],
}


// const handleMd = (md) => {
//     form.content = md
//     console.log('子组件回传过来的数据：' + form.content)
// }

const tableData = ref([])
// 当前页码
const current = ref(1)
const total = ref(0)
const size = ref(10)

// 获取分页数据
function getTableData() {
    console.log('获取分页数据')
    tableLoading.value = true
    getArticlePageList({ current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, searchTitle: searchTitle.value, categoryId: filterCategoryId.value })
        .then((res) => {
            if (res.success == true) {
                tableData.value = res.data.records
                current.value = res.data.current
                total.value = res.data.total
                size.value = res.data.size
            }
        }).finally(() => {
            tableLoading.value = false
        })
}
getTableData()

const handleSizeChange = (e) => {
    console.log('选择的页码' + e)
    size.value = e
    getTableData()
}


const publishLoading = ref(false)

const onSubmit = () => {
    publishArticleFormRef.value.validate((valid) => {
        if (!valid) {
            return false
        }
        publishLoading.value = true
        publishArticle(form).then((e) => {
        if (e.success == false) {
            var message = e.message
            showMessage(message, 'warning', 'message')
            return
        }

        showMessage('发布成功', 'success', 'message')
        isArticlePublishEditorShow.value = false
        getTableData()
    }).catch(() => {
        showMessage('发布失败，请重试', 'error', 'message')
    }).finally(() => {
        publishLoading.value = false
    })
    })
}

const buildUpdatePayload = () => ({
    id: form.id != null ? Number(form.id) : null,
    title: form.title,
    content: form.content,
    titleImage: form.titleImage,
    description: form.description,
    categoryId: form.categoryId != null ? Number(form.categoryId) : null,
    tagIds: Array.isArray(form.tagIds) ? form.tagIds.map((t) => Number(t)) : [],
    visibility: form.visibility,
})

const updateSubmit = async () => {
    const formEl = updateArticleFormRef.value
    if (!formEl) {
        showMessage('表单未就绪，请稍后重试', 'warning', 'message')
        return
    }
    try {
        await formEl.validate()
    } catch {
        showMessage('请检查必填项：标题、内容、封面、摘要、分类、标签、可见性', 'warning', 'message')
        return
    }
    if (form.id == null) {
        showMessage('文章 ID 缺失，请关闭后重新进入编辑', 'warning', 'message')
        return
    }
    try {
        const e = await updateArticle(buildUpdatePayload())
        if (e && e.success === false) {
            showMessage(e.message || '修改失败', 'warning', 'message')
            return
        }
        showMessage('修改成功', 'success', 'message')
        isArticleUpdateEditorShow.value = false
        location.reload()
    } catch (err) {
        console.error(err)
        showMessage('请求失败，请检查网络或后台日志', 'error', 'message')
    }
}

const deleteArticleSubmit = (row) => {
    console.log(row.id)
    ElMessageBox.confirm(
        '是否确认要删除该文章?',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            deleteArticle(row.id).then((e) => {
                if (e.success == true) {
                    showMessage('删除成功', 'success')
                    location.reload()
                } else {
                    let message = e.message
                    showMessage(message, 'warning')
                }
            })

        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '删除失败',
            })
        })
}

const categoryTree = ref([])
const flatCategories = ref([])
const tagOptions = ref([])
const tagSelectLoading = ref(false)
const categorySearchLoading = ref(false)
const categorySearchActive = ref(false)
const categorySearchResults = ref([])

async function loadCategoryTree() {
    try {
        const res = await getCategoryTree()
        if (res.success && res.data) {
            categoryTree.value = res.data
            flatCategories.value = flattenCategoryTree(res.data)
        }
    } catch (e) {
        console.error('加载分类树失败', e)
    }
}

function flattenCategoryTree(tree) {
    let result = []
    for (const node of tree) {
        if (node.children && node.children.length > 0) {
            for (const child of node.children) {
                result.push({
                    value: child.id,
                    label: child.name,
                    parentLabel: node.name,
                    group: node.name
                })
            }
        } else {
            result.push({
                value: node.id,
                label: node.name,
                parentLabel: null,
                group: null
            })
        }
    }
    return result
}

async function loadTagsByCategory(categoryId) {
    tagSelectLoading.value = true
    try {
        if (categoryId) {
            const res = await getTagsByCategory({ categoryId, current: 1, size: 200 })
            if (res.success && res.data) {
                tagOptions.value = (res.data.records || res.data || []).map(t => ({
                    value: t.id,
                    label: t.name,
                    categoryId: t.categoryId,
                    categoryName: t.categoryName
                }))
            }
        } else {
            const res = await searchTaxonomyTags({ key: '' })
            if (res.success && res.data) {
                tagOptions.value = (res.data || []).map(t => ({
                    value: t.id,
                    label: t.name,
                    categoryId: t.categoryId,
                    categoryName: t.categoryName
                }))
            }
        }
    } catch (e) {
        console.error('加载标签失败', e)
    } finally {
        tagSelectLoading.value = false
    }
}

async function searchTags(query) {
    tagSelectLoading.value = true
    try {
        const res = await searchTaxonomyTags({ key: query || '' })
        if (res.success && res.data) {
            tagOptions.value = (res.data || []).map(t => ({
                value: t.id,
                label: t.name,
                categoryId: t.categoryId,
                categoryName: t.categoryName
            }))
        }
    } catch (e) {
        console.error('搜索标签失败', e)
    } finally {
        tagSelectLoading.value = false
    }
}

const handleTagDropdownVisible = (visible) => {
    if (visible && tagOptions.value.length === 0) {
        loadTagsByCategory(form.categoryId || null)
    }
}

loadCategoryTree()

// ===== AI SEO 优化 =====
const seoDialogVisible = ref(false)
const seoLoading = ref(false)
const seoResult = ref(null)

function openSeoDialog() {
    if (!form.title.trim() || !form.content.trim()) {
        showMessage('请先输入标题和内容', 'warning')
        return
    }
    seoResult.value = null
    seoDialogVisible.value = true
    handleSeoOptimize()
}

async function handleSeoOptimize() {
    if (!form.title.trim() || !form.content.trim()) {
        showMessage('标题和内容不能为空', 'warning')
        return
    }
    seoLoading.value = true
    seoResult.value = null
    try {
        const res = await aiSeoOptimize({
            title: form.title.trim(),
            content: form.content.trim(),
            currentDescription: form.description.trim() || null
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
        form.title = seoResult.value.optimizedTitle
        showMessage('标题已应用', 'success')
    }
}

function applySeoDescription() {
    if (seoResult.value && seoResult.value.optimizedDescription) {
        form.description = seoResult.value.optimizedDescription
        showMessage('摘要已应用', 'success')
    }
}

function seoScoreColor(score) {
    if (score >= 80) return '#22c55e'
    if (score >= 60) return '#f59e0b'
    return '#ef4444'
}
</script>


<style scoped>
.avatar-uploader .avatar {
    width: 100%;
    max-width: 320px;
    display: block;
}

.message {
    z-index: 9999 !important;
}

.admin-article-table {
    width: 100%;
}

/* 标题列：限制宽度，避免占满整行留白 */
.admin-article-table :deep(.admin-col-title) {
    max-width: 300px;
}

.admin-article-table :deep(.admin-col-title .cell) {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

/* 行内操作：仅图标，紧凑 */
.admin-row-actions {
    display: inline-flex;
    flex-wrap: nowrap;
    align-items: center;
    justify-content: center;
    gap: 0.1rem;
    user-select: none;
}

.admin-row-actions :deep(.el-button) {
    margin: 0;
    padding: 0.25rem;
    min-width: 1.75rem;
    height: 1.75rem;
    font-size: 0;
    border-radius: 0.35rem;
    cursor: pointer;
}

.admin-row-actions :deep(.el-button .el-icon) {
    font-size: 1rem;
    margin-right: 0;
    margin-left: 0;
}

.admin-row-actions :deep(.el-button + .el-button) {
    margin-left: 0;
}

.admin-table-image {
    width: 56px;
    height: 40px;
    border-radius: 0.5rem;
    box-shadow: 0 1px 3px rgba(15, 23, 42, 0.08);
}

.admin-table-image :deep(.el-image__inner) {
    width: 56px;
    height: 40px;
    object-fit: cover;
    border-radius: 0.5rem;
    vertical-align: middle;
}

.vis-tag {
    font-weight: 600;
    letter-spacing: 0.02em;
    border: 0;
}

.admin-dialog-header {
    padding-bottom: 0.25rem;
}

.admin-wide-select {
    width: 100%;
    max-width: 640px;
}

.admin-title-row {
    display: flex;
    gap: 8px;
    width: 100%;
    max-width: 640px;
}

.admin-title-input {
    flex: 1;
}

.seo-loading {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 40px 0;
    color: #64748b;
    font-size: 14px;
}

.seo-result {
    display: flex;
    flex-direction: column;
    gap: 0;
}

.seo-section {
    border-bottom: 1px solid #e2e8f0;
}

.seo-section:last-child {
    border-bottom: none;
}

.seo-section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 0;
    font-size: 13px;
    font-weight: 600;
    color: #475569;
}

.seo-section-content {
    padding: 0 0 12px;
    font-size: 14px;
    line-height: 1.7;
    color: #334155;
}

.seo-suggestion {
    color: #64748b;
    font-style: italic;
}

.seo-keywords {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding-bottom: 12px;
}

.seo-keyword-tag {
    font-size: 13px;
}

.seo-score-row {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 0;
    font-size: 13px;
    color: #475569;
}

.seo-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px 0;
    color: #94a3b8;
    font-size: 14px;
}

.admin-cell-text {
    font-size: 13px;
    color: #334155;
}

.admin-cell-muted {
    font-size: 13px;
    color: #94a3b8;
}

.admin-tag-row {
    display: flex;
    flex-wrap: nowrap;
    gap: 4px;
    overflow: hidden;
    white-space: nowrap;
}

.admin-table-tag {
    font-size: 12px;
}
</style>

<style>
.avatar-uploader .el-upload {
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #2563eb;
    width: 178px;
    height: 178px;
    text-align: center;
}
</style>
