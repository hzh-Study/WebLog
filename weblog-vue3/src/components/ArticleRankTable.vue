<template>
    <el-card shadow="never" class="admin-chart-card admin-filter-card">
        <template #header>
            <div>
                <div class="admin-table-title">热门文章排行</div>
                <div class="admin-table-desc">浏览量最高的 10 篇文章</div>
            </div>
        </template>

        <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
            <el-table-column prop="rank" label="排名" width="70" align="center">
                <template #default="{ row }">
                    <span class="rank-badge" :class="getRankClass(row.rank)">{{ row.rank }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="title" label="文章标题" min-width="200" show-overflow-tooltip />
            <el-table-column prop="pv" label="浏览量" width="120" align="center" />
            <el-table-column prop="percentage" label="占比" width="140" align="center">
                <template #default="{ row }">
                    <div class="percentage-cell">
                        <el-progress
                            :percentage="Number(row.percentage) || 0"
                            :stroke-width="6"
                            :show-text="false"
                            color="#3b82f6"
                        />
                        <span class="percentage-text">{{ row.percentage || '0' }}%</span>
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getArticleRank } from '@/api/admin/dashboard'

const tableData = ref([])
const loading = ref(false)

const getRankClass = (rank) => {
    if (rank === 1) return 'rank-1'
    if (rank === 2) return 'rank-2'
    if (rank === 3) return 'rank-3'
    return ''
}

const fetchData = async () => {
    loading.value = true
    try {
        const response = await getArticleRank()
        if (response?.success && response.data) {
            tableData.value = (response.data || []).map((item, index) => ({
                ...item,
                rank: index + 1,
            }))
        }
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.rank-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    font-size: 12px;
    font-weight: 700;
    color: #64748b;
    background: #f1f5f9;
}

.rank-badge.rank-1 {
    color: #fff;
    background: linear-gradient(135deg, #f59e0b, #d97706);
}

.rank-badge.rank-2 {
    color: #fff;
    background: linear-gradient(135deg, #94a3b8, #64748b);
}

.rank-badge.rank-3 {
    color: #fff;
    background: linear-gradient(135deg, #d97706, #92400e);
}

.percentage-cell {
    display: flex;
    align-items: center;
    gap: 8px;
}

.percentage-text {
    font-size: 12px;
    color: #64748b;
    white-space: nowrap;
}
</style>