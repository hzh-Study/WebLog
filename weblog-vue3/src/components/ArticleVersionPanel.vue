<template>
    <el-dialog v-model="visible" title="版本历史" width="65%" destroy-on-close @open="loadVersions">
        <div v-loading="loading">
            <div v-if="versions.length === 0 && !loading" class="version-empty">
                暂无版本记录
            </div>

            <div v-else class="version-list">
                <div v-for="item in versions" :key="item.id" class="version-item">
                    <div class="version-item-left">
                        <el-checkbox
                            :model-value="isSelected(item.id)"
                            :disabled="!isSelected(item.id) && selectedIds.length >= 2"
                            @change="toggleSelect(item.id)"
                        />
                        <span class="version-dot"></span>
                    </div>
                    <div class="version-item-content">
                        <div class="version-item-head">
                            <span class="version-number">v{{ item.version }}</span>
                            <span class="version-title">{{ item.title }}</span>
                            <span class="version-time">{{ item.createTime }}</span>
                        </div>
                        <p class="version-summary">{{ item.summary || '无变更摘要' }}</p>
                        <div class="version-item-actions">
                            <el-button link type="primary" @click="viewDetail(item)">查看详情</el-button>
                            <el-button link type="warning" @click="rollbackVersionSubmit(item)">回滚到此版本</el-button>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="selectedIds.length > 0" class="version-compare-bar">
                <span v-if="selectedIds.length === 1" class="compare-hint">请再选择一个版本进行对比</span>
                <el-button v-if="selectedIds.length === 2" type="primary" @click="compareVersions">对比</el-button>
            </div>

            <div v-if="diffResult" class="diff-area">
                <div class="diff-panel">
                    <div class="diff-label">旧版内容</div>
                    <el-input type="textarea" :rows="12" :model-value="diffResult.oldContent" readonly />
                </div>
                <div class="diff-panel">
                    <div class="diff-label">新版内容</div>
                    <el-input type="textarea" :rows="12" :model-value="diffResult.newContent" readonly />
                </div>
            </div>
        </div>

        <el-dialog v-model="detailVisible" title="版本详情" width="40%" append-to-body destroy-on-close>
            <div v-if="detail" class="version-detail">
                <p><strong>版本号：</strong>v{{ detail.version }}</p>
                <p><strong>标题：</strong>{{ detail.title }}</p>
                <p><strong>变更摘要：</strong>{{ detail.summary || '无' }}</p>
                <p><strong>创建时间：</strong>{{ detail.createTime }}</p>
                <div class="mt-3">
                    <strong>内容：</strong>
                    <el-input type="textarea" :rows="10" :model-value="detail.content" readonly class="mt-2" />
                </div>
            </div>
        </el-dialog>

        <template #footer>
            <el-button @click="visible = false">关闭</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getVersionList, getVersionDetail, getVersionDiff, rollbackVersion } from '@/api/admin/article'
import { showMessage } from '@/composables/util'

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false
    },
    articleId: {
        type: Number,
        default: null
    }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
})

const loading = ref(false)
const versions = ref([])
const selectedIds = ref([])
const diffResult = ref(null)
const detailVisible = ref(false)
const detail = ref(null)

function loadVersions() {
    if (!props.articleId) return
    loading.value = true
    selectedIds.value = []
    diffResult.value = null
    getVersionList(props.articleId)
        .then((res) => {
            if (res.success && res.data) {
                versions.value = Array.isArray(res.data) ? res.data : (res.data.records || [])
            }
        })
        .finally(() => {
            loading.value = false
        })
}

function isSelected(id) {
    return selectedIds.value.includes(id)
}

function toggleSelect(id) {
    const idx = selectedIds.value.indexOf(id)
    if (idx >= 0) {
        selectedIds.value.splice(idx, 1)
        diffResult.value = null
    } else {
        if (selectedIds.value.length >= 2) return
        selectedIds.value.push(id)
    }
}

function compareVersions() {
    if (selectedIds.value.length !== 2) return
    const [id1, id2] = selectedIds.value
    getVersionDiff({ articleId: props.articleId, versionId1: id1, versionId2: id2 })
        .then((res) => {
            if (res.success && res.data) {
                diffResult.value = res.data
            } else {
                showMessage((res && res.message) || '获取版本对比失败', 'warning')
            }
        })
        .catch(() => {
            showMessage('获取版本对比失败，请稍后重试', 'error')
        })
}

function viewDetail(item) {
    getVersionDetail(item.id)
        .then((res) => {
            if (res.success && res.data) {
                detail.value = res.data
                detailVisible.value = true
            } else {
                showMessage((res && res.message) || '获取版本详情失败', 'warning')
            }
        })
        .catch(() => {
            showMessage('获取版本详情失败，请稍后重试', 'error')
        })
}

function rollbackVersionSubmit(item) {
    ElMessageBox.confirm(
        '回滚后将覆盖当前文章内容，是否确认要回滚到版本 v' + item.version + '?',
        '提示',
        {
            confirmButtonText: '确认回滚',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            rollbackVersion({ articleId: props.articleId, versionId: item.id })
                .then((res) => {
                    if (res.success) {
                        showMessage('回滚成功', 'success')
                        visible.value = false
                    } else {
                        showMessage((res && res.message) || '回滚失败', 'warning')
                    }
                })
                .catch(() => {
                    showMessage('回滚失败，请稍后重试', 'error')
                })
        })
        .catch(() => {
            ElMessage({ type: 'info', message: '取消回滚' })
        })
}
</script>

<style scoped>
.version-empty {
    text-align: center;
    padding: 2rem 0;
    color: #94a3b8;
    font-size: 0.875rem;
}

.version-list {
    position: relative;
}

.version-item {
    display: flex;
    gap: 0.75rem;
    padding: 0.75rem 0;
    border-bottom: 1px solid #f1f5f9;
}

.version-item-left {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 0.25rem;
    flex-shrink: 0;
}

.version-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: #6366f1;
    margin-top: 0.5rem;
}

.version-item-content {
    flex: 1;
    min-width: 0;
}

.version-item-head {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    flex-wrap: wrap;
}

.version-number {
    font-weight: 700;
    color: #6366f1;
    font-size: 0.875rem;
}

.version-title {
    font-weight: 500;
    color: #1e293b;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.version-time {
    margin-left: auto;
    font-size: 0.8rem;
    color: #94a3b8;
    white-space: nowrap;
}

.version-summary {
    margin: 0.25rem 0 0.5rem;
    font-size: 0.8rem;
    color: #64748b;
}

.version-item-actions {
    display: flex;
    gap: 0.5rem;
}

.version-compare-bar {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 0;
}

.compare-hint {
    font-size: 0.8rem;
    color: #94a3b8;
}

.diff-area {
    display: flex;
    gap: 1rem;
    margin-top: 0.5rem;
}

.diff-panel {
    flex: 1;
    min-width: 0;
}

.diff-label {
    font-weight: 600;
    color: #334155;
    margin-bottom: 0.35rem;
    font-size: 0.85rem;
}

.version-detail p {
    margin: 0.35rem 0;
    font-size: 0.85rem;
    color: #334155;
}
</style>