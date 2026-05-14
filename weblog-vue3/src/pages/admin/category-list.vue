<template>
    <div class="admin-page">
    <section class="admin-page-header">
        <div>
            <span class="admin-kicker">Category Manager</span>
            <h1 class="admin-page-title">分类管理</h1>
            <p class="admin-page-desc">管理您已添加的分类，点击「新增分类」从系统分类库中选择。</p>
        </div>
        <div class="admin-page-actions">
            <span class="admin-summary-chip"><strong>{{ total }}</strong> 个分类</span>
        </div>
    </section>

    <el-card shadow="never" class="admin-filter-card mb-5">
        <div class="admin-filter-grid">
            <div class="admin-field">
                <span class="admin-field-label">分类名称</span>
                <el-input v-model="searchCategoryName" placeholder="请输入（模糊查询）" />
            </div>
            <div class="admin-field">
                <span class="admin-field-label">创建日期</span>
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
                <div class="admin-table-title">我的分类</div>
                <div class="admin-table-desc">您已添加的分类列表。</div>
            </div>
            <el-button type="primary" :icon="Plus" @click="openAddDialog">新增分类</el-button>
        </div>

        <el-table :data="tableData" stripe style="width: 100%" class="mt-4" v-loading="tableLoading">
            <el-table-column prop="name" label="分类名称" width="180" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="100" align="center">
                <template #default="scope">
                    <el-button type="danger" size="small" link @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class="admin-pagination-wrap">
            <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]"
                :small="small" :disabled="disabled" background="true" layout="total, sizes, prev, pager, next, jumper"
                :total="total" @size-change="handleSizeChange" @current-change="getTableData" />
        </div>
    </el-card>

    <el-dialog v-model="showAddDialog" title="从分类库中选择" width="640px" top="5vh" destroy-on-close @open="initAddDialog">
        <div style="margin-bottom: 12px; display: flex; gap: 8px;">
            <el-input v-model="addSearchKeyword" placeholder="搜索分类名称" clearable @keyup.enter="searchAddList" @clear="searchAddList" />
            <el-button type="primary" :icon="Search" @click="searchAddList">搜索</el-button>
        </div>
        <el-table :data="addTableData" v-loading="addLoading" max-height="300" highlight-current-row
            @current-change="handleAddCurrentChange" ref="addTableRef">
            <el-table-column prop="name" label="分类名称" />
            <el-table-column prop="createTime" label="创建时间" width="180" show-overflow-tooltip />
        </el-table>
        <div style="margin-top: 12px; display: flex; justify-content: center;">
            <el-pagination v-model:current-page="addCurrent" v-model:page-size="addSize" :page-sizes="[10, 20, 50, 100]"
                :total="addTotal" layout="total, sizes, prev, pager, next" @current-change="getAddList" @size-change="handleAddSizeChange" small />
        </div>
        <template #footer>
            <el-button @click="showAddDialog = false">取消</el-button>
            <el-button type="primary" :disabled="!addSelectedItem" :loading="addSubmitting" @click="confirmAdd">确认添加</el-button>
        </template>
    </el-dialog>
</div>

</template>

<script setup>
import { ref, reactive } from 'vue'
import { getCategoryPageList, addCategory, deleteCategory } from '@/api/admin/category'
import { showMessage } from '@/composables/util'
import { ElMessageBox } from 'element-plus'
import moment from 'moment';
import { Search, RefreshRight, Plus } from '@element-plus/icons-vue'

const searchCategoryName = ref('')
const pickDate = ref('')
const startDate = ref(null)
const endDate = ref(null)
const small = ref(false)
const disabled = ref(false)

const reset = () => {
    pickDate.value = ''
    startDate.value = null
    endDate.value = null
    searchCategoryName.value = ''
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

const tableLoading = ref(false)
const tableData = ref([])
const current = ref(1)
const total = ref(0)
const size = ref(10)

function getTableData() {
    tableLoading.value = true
    getCategoryPageList({ current: current.value, size: size.value, startDate: startDate.value, endDate: endDate.value, categoryName: searchCategoryName.value })
        .then((res) => {
            if (res.success == true) {
                tableData.value = res.data
                current.value = res.current
                total.value = res.total
                size.value = res.size
            }
        }).finally(() => {
            tableLoading.value = false
        })
}
getTableData()

const handleSizeChange = (e) => {
    size.value = e
    getTableData()
}

const handleDelete = (row) => {
    ElMessageBox.confirm(`确定要删除分类「${row.name}」吗？`, '删除确认', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        deleteCategory(row.id).then((res) => {
            if (res.success) {
                showMessage('删除成功', 'success')
                getTableData()
            } else {
                showMessage(res.message || '删除失败', 'warning')
            }
        })
    }).catch(() => {})
}

const showAddDialog = ref(false)
const addLoading = ref(false)
const addSubmitting = ref(false)
const addTableRef = ref(null)
const addSearchKeyword = ref('')
const addTableData = ref([])
const addCurrent = ref(1)
const addTotal = ref(0)
const addSize = ref(10)
const addSelectedItem = ref(null)

const initAddDialog = () => {
    addSearchKeyword.value = ''
    addCurrent.value = 1
    addSelectedItem.value = null
    getAddList()
}

const searchAddList = () => {
    addCurrent.value = 1
    getAddList()
}

const getAddList = () => {
    addLoading.value = true
    getCategoryPageList({
        current: addCurrent.value,
        size: addSize.value,
        categoryName: addSearchKeyword.value || undefined,
        library: true,
    }).then((res) => {
        console.log('Category library response:', JSON.stringify(res).substring(0, 500))
        if (res.success) {
            addTableData.value = res.data
            addCurrent.value = res.current
            addTotal.value = res.total
            addSize.value = res.size
        }
    }).finally(() => {
        addLoading.value = false
    })
}

const handleAddCurrentChange = (row) => {
    addSelectedItem.value = row
}

const handleAddSizeChange = (e) => {
    addSize.value = e
    addCurrent.value = 1
    getAddList()
}

const openAddDialog = () => {
    showAddDialog.value = true
}

const confirmAdd = () => {
    if (!addSelectedItem.value) return
    addSubmitting.value = true
    addCategory({ name: addSelectedItem.value.name }).then((res) => {
        if (res.success) {
            showMessage('添加分类成功', 'success')
            showAddDialog.value = false
            getTableData()
        } else {
            showMessage(res.message || '添加分类失败', 'warning')
        }
    }).catch(() => {
        showMessage('添加分类失败', 'error')
    }).finally(() => {
        addSubmitting.value = false
    })
}

</script>

<style>
</style>
