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
                <div class="admin-table-desc">发文时只能选择本人创建的分类与标签，与他人数据互不影响。</div>
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
                <el-input v-model="form.title" autocomplete="off" size="large" maxlength="40" show-word-limit clearable />
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
                <el-select v-model="form.categoryId" clearable placeholder="---请选择---" size="large" class="admin-wide-select">
                    <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="标签" prop="tags">
                <!-- 标签选择 -->
                <el-select v-model="form.tags" multiple filterable remote reserve-keyword placeholder="---请输入---" class="admin-wide-select"
                    remote-show-suffix :remote-method="remoteMethod" allow-create default-first-option
                    :loading="tagSelectLoading" size="large">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
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
                <el-input v-model="form.title" autocomplete="off" size="large" maxlength="40" show-word-limit clearable />
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
                <el-select v-model="form.categoryId" clearable placeholder="---请选择---" size="large" class="admin-wide-select">
                    <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="标签" prop="tags">
                <!-- 标签选择 -->
                <el-select v-model="form.tags" multiple filterable remote reserve-keyword placeholder="---请输入---" class="admin-wide-select"
                    remote-show-suffix :remote-method="remoteMethod" allow-create default-first-option
                    :loading="tagSelectLoading" size="large">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
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
</template>

<script setup>
// import { ElMessage, ElMessageBox } from 'element-plus'
import { ref, reactive, nextTick, onMounted, onUnmounted } from 'vue'
// import MDEditor from '@/components/MDEditor.vue'
import { publishArticle, getArticlePageList, deleteArticle, getArticleDetail, updateArticle } from '@/api/admin/article'
import { uploadFile } from '@/api/admin/file'
import MdEditor from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { showMessage } from '@/composables/util'
import { useRouter, useRoute } from 'vue-router'
import { getCategorySelect } from '@/api/admin/category'
import { selectTags, getTagSelect } from '@/api/admin/tag'
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
    content: '请输入内容',
    titleImage: '',
    categoryId: null,
    tags: [],
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
const startDate = reactive({})
const endDate = reactive({})

const reset = () => {
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    searchTitle.value = ''
    current.value = 1
    getTableData()
}

const datepickerChange = (e) => {
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
    form.tags = []
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

/**
 * 与 el-option 的 :value 类型一致，否则不显示 label 只显示数字
 */
const coerceToOptionValue = (val, list) => {
    if (val == null || val === '') return null
    if (!list || !list.length) {
        return typeof val === 'string' && /^\d+$/.test(val) ? Number(val) : val
    }
    const t = typeof list[0].value
    if (t === 'number') return Number(val)
    if (t === 'string') return String(val)
    return val
}

const mergeMissingTagOptions = (ids) => {
    if (!Array.isArray(ids) || !ids.length) return
    const list = options.value
    if (!list || !list.length) return
    const valueType = typeof list[0].value
    for (const raw of ids) {
        if (raw == null || raw === '') continue
        if (list.some((o) => o.value == raw)) continue
        const n = Number(raw)
        const value = valueType === 'string' ? String(raw) : Number.isNaN(n) ? raw : n
        list.push({ value, label: `未在下拉中(ID:${raw})` })
    }
}

const applyArticleDetailToForm = (d) => {
    if (!d) return
    const cats = categories.value
    const tags = options.value
    form.id = d.id
    form.title = d.title ?? ''
    form.content = d.content != null ? String(d.content) : ''
    form.titleImage = d.titleImage ?? ''
    const cid = d.categoryId
    form.categoryId = cid == null || cid === '' ? null : coerceToOptionValue(cid, cats)
    const raw = d.tagIds
    if (Array.isArray(raw) && raw.length) {
        form.tags = raw.map((t) => coerceToOptionValue(t, tags))
    } else {
        form.tags = []
    }
    mergeMissingTagOptions(form.tags)
    form.description = d.description ?? ''
    form.visibility = d.visibility != null && d.visibility !== '' ? d.visibility : 'PUBLIC'
}

const refreshSelectOptions = async () => {
    const [c, t] = await Promise.all([getCategorySelect(), getTagSelect()])
    if (c && c.success && Array.isArray(c.data)) {
        categories.value = c.data
    }
    if (t && t.success && Array.isArray(t.data)) {
        options.value = t.data
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
    const res = await Promise.all(
        files.map((file) => {
            return new Promise((rev, rej) => {
                console.log('==> 开始上传文件...')
                let formData = new FormData()
                formData.append("file", file);
                uploadFile(formData).then((res) => {
                    console.log(res)
                    console.log('访问路径：' + res.data.url)
                    callback([res.data.url]);
                })
            });
        })
    );
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
    tags: [{ required: true, message: '请选择文章标签', trigger: 'blur' }],
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
    getArticlePageList({ current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, searchTitle: searchTitle.value })
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


const onSubmit = () => {
    console.log('提交内容' + form.content)
    publishArticleFormRef.value.validate((valid) => {
        if (!valid) {
            return false
        }
        publishArticle(form).then((e) => {
        console.log(e)
        if (e.success == false) {
            var message = e.message
            showMessage(message, 'warning', 'message')
            return
        }

        showMessage('发布成功', 'success', 'message')
        isArticlePublishEditorShow.value = false
        location.reload()
    })
    })
}

/** 后端 UpdateArticleReqVO.tags 为 List<String>，且业务里用标签 ID 的字符串与名称区分 */
const buildUpdatePayload = () => ({
    id: form.id != null ? Number(form.id) : null,
    title: form.title,
    content: form.content,
    titleImage: form.titleImage,
    description: form.description,
    categoryId: form.categoryId != null ? Number(form.categoryId) : null,
    tags: Array.isArray(form.tags) ? form.tags.map((t) => String(t)) : [],
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

// 文章分类
const categories = ref([])
getCategorySelect().then((e) => {
    console.log('获取分类数据')
    console.log(e)
    categories.value = e.data
})

// 文章标签
const tagSelectLoading = ref(false)
const options = ref([])
getTagSelect().then((e) => {
    console.log('获取标签数据')
    console.log(e)
    options.value = e.data
})

const remoteMethod = (query) => {
    console.log('远程搜索')
    console.log(options.value)
    if (query) {
        tagSelectLoading.value = true
        setTimeout(() => {
            tagSelectLoading.value = false
            selectTags(query).then((e) => {
                if (e.success) {
                    options.value = e.data
                }
            })
            //   options.value = list.value.filter((item) => {
            //     return item.label.toLowerCase().includes(query.toLowerCase())
            //   })
        }, 200)
    }
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
