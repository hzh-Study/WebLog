<template>
  <div class="comment-list-page">
    <div class="comment-page-header">
      <div>
        <h2 class="comment-page-title">评论管理</h2>
        <p class="comment-page-desc">审核、拒绝或删除文章评论，确保前台评论内容可控</p>
      </div>
    </div>

    <div class="comment-filter-bar">
      <div class="comment-filter-row">
        <div class="comment-filter-item">
          <label class="comment-filter-label">评论状态</label>
          <el-select v-model="filters.status" placeholder="全部" clearable>
            <el-option label="全部" :value="null" />
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </div>
        <div class="comment-filter-item">
          <label class="comment-filter-label">评论人昵称</label>
          <el-input v-model="filters.nickname" placeholder="输入昵称搜索" clearable />
        </div>
        <div class="comment-filter-item">
          <label class="comment-filter-label">文章标题</label>
          <el-input v-model="filters.articleTitle" placeholder="输入文章标题搜索" clearable />
        </div>
        <div class="comment-filter-actions">
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>

    <div class="comment-table-wrapper">
      <el-table
        :data="tableData"
        v-loading="loading"
        element-loading-text="加载中..."
        border
        stripe
        class="comment-table"
      >
        <el-table-column prop="id" label="评论ID" width="90" />
        <el-table-column prop="articleTitle" label="文章标题" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.articleTitle">{{ row.articleTitle }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="评论人昵称" width="130" />
        <el-table-column prop="content" label="评论内容" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tooltip :content="row.content" placement="top" :show-after="500">
              <span class="comment-content-cell">{{ row.content }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="parentId" label="父评论ID" width="100">
          <template #default="{ row }">
            <span v-if="row.parentId">{{ row.parentId }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger" size="small">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="comment-actions">
              <el-button
                v-if="row.status !== 1"
                type="success"
                size="small"
                :loading="row._approving"
                @click="handleReview(row, 1)"
              >通过</el-button>
              <el-button
                v-if="row.status !== 2"
                type="warning"
                size="small"
                :loading="row._rejecting"
                @click="handleReview(row, 2)"
              >拒绝</el-button>
              <el-button
                type="danger"
                size="small"
                :loading="row._deleting"
                @click="handleDelete(row)"
              >删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="comment-pagination-bar">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleQuery"
        @current-change="handleQuery"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCommentPageList, reviewComment, deleteComment } from '@/api/admin/comment'
import { showMessage, showModel } from '@/composables/util'

const loading = ref(false)
const tableData = ref([])

const filters = reactive({
  status: null,
  nickname: '',
  articleTitle: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

function handleQuery() {
  loading.value = true
  getCommentPageList({
    current: pagination.current,
    size: pagination.size,
    status: filters.status,
    nickname: filters.nickname || undefined,
    articleTitle: filters.articleTitle || undefined
  }).then((res) => {
    if (res && res.success) {
      tableData.value = (res.data || []).map(row => ({
        ...row,
        _approving: false,
        _rejecting: false,
        _deleting: false
      }))
      pagination.total = res.total || 0
    } else {
      showMessage(res?.message || '获取评论列表失败', 'error')
    }
  }).catch(() => {
    showMessage('获取评论列表失败', 'error')
  }).finally(() => {
    loading.value = false
  })
}

function handleReset() {
  filters.status = null
  filters.nickname = ''
  filters.articleTitle = ''
  pagination.current = 1
  handleQuery()
}

function handleReview(row, status) {
  const isApproved = status === 1
  const confirmMsg = isApproved ? '确认通过该评论吗？' : '确认拒绝该评论吗？'
  const loadingKey = isApproved ? '_approving' : '_rejecting'

  showModel(confirmMsg, 'warning', '提示').then(() => {
    row[loadingKey] = true
    return reviewComment({ commentId: row.id, status })
  }).then((res) => {
    if (res && res.success) {
      showMessage(isApproved ? '审核通过' : '已拒绝评论')
      row.status = status
    } else {
      showMessage(res?.message || '操作失败', 'error')
    }
  }).catch((err) => {
    if (err !== 'cancel') {
      showMessage('操作失败', 'error')
    }
  }).finally(() => {
    row[loadingKey] = false
  })
}

function handleDelete(row) {
  showModel('确认删除该评论吗？删除后前台将不可见。', 'warning', '提示').then(() => {
    row._deleting = true
    return deleteComment({ commentId: row.id })
  }).then((res) => {
    if (res && res.success) {
      showMessage('删除成功')
      handleQuery()
    } else {
      showMessage(res?.message || '删除失败', 'error')
    }
  }).catch((err) => {
    if (err !== 'cancel') {
      showMessage('删除失败', 'error')
    }
  }).finally(() => {
    row._deleting = false
  })
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.comment-list-page {
  padding: 0;
}

.comment-page-header {
  margin-bottom: 1.25rem;
}

.comment-page-title {
  font-size: 1.35rem;
  font-weight: 700;
  color: #172033;
  margin: 0 0 0.35rem;
}

.comment-page-desc {
  color: #94a3b8;
  font-size: 0.88rem;
  margin: 0;
}

.comment-filter-bar {
  background: #fff;
  border-radius: 12px;
  padding: 1.15rem 1.25rem;
  margin-bottom: 1rem;
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.comment-filter-row {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 1rem;
}

.comment-filter-item {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.comment-filter-label {
  font-size: 0.78rem;
  color: #64748b;
  font-weight: 600;
}

.comment-filter-item :deep(.el-select),
.comment-filter-item :deep(.el-input) {
  width: 180px;
}

.comment-filter-actions {
  display: flex;
  gap: 0.5rem;
  align-items: flex-end;
  padding-bottom: 1px;
}

.comment-table-wrapper {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.comment-table {
  width: 100%;
}

.comment-content-cell {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.text-muted {
  color: #cbd5e1;
}

.comment-actions {
  display: flex;
  gap: 0.35rem;
  flex-wrap: wrap;
}

.comment-pagination-bar {
  margin-top: 1rem;
  display: flex;
  justify-content: flex-end;
}
</style>