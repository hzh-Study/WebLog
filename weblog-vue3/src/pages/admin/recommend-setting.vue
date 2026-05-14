<template>
    <div class="admin-page">
        <section class="admin-page-header">
            <div>
                <span class="admin-kicker">Recommend</span>
                <h1 class="admin-page-title">推荐配置</h1>
                <p class="admin-page-desc">管理推荐系统的运行参数，监控推荐效果指标。</p>
            </div>
        </section>

        <div class="dashboard-grid" v-loading="dashboardLoading">
            <div class="dashboard-card">
                <div class="dashboard-card-icon bg-blue">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M2.036 12.322a1.012 1.012 0 0 1 0-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178Z" />
                        <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                    </svg>
                </div>
                <div class="dashboard-card-info">
                    <span class="dashboard-card-label">曝光数</span>
                    <span class="dashboard-card-value">{{ dashboard.exposureCount || 0 }}</span>
                </div>
            </div>
            <div class="dashboard-card">
                <div class="dashboard-card-icon bg-green">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M15.042 21.672 13.684 16.6m0 0-2.51 2.225.569-9.47 5.227 7.917-3.286-.672ZM12 2.25V4.5m5.834.166-1.591 1.591M20.25 10.5H18M7.757 14.743l-1.59 1.59M6 10.5H3.75m4.007-4.243-1.59-1.59" />
                    </svg>
                </div>
                <div class="dashboard-card-info">
                    <span class="dashboard-card-label">点击数</span>
                    <span class="dashboard-card-value">{{ dashboard.clickCount || 0 }}</span>
                </div>
            </div>
            <div class="dashboard-card">
                <div class="dashboard-card-icon bg-amber">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 0 1 3 19.875v-6.75ZM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 0 1-1.125-1.125V8.625ZM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 0 1-1.125-1.125V4.125Z" />
                    </svg>
                </div>
                <div class="dashboard-card-info">
                    <span class="dashboard-card-label">CTR</span>
                    <span class="dashboard-card-value">{{ dashboard.ctr || '0%' }}</span>
                </div>
            </div>
            <div class="dashboard-card">
                <div class="dashboard-card-icon bg-purple">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M7.5 8.25h9m-9 3H12m-9.75 1.51c0 1.6 1.123 2.994 2.707 3.227 1.087.16 2.185.283 3.293.369V21l4.076-4.076a1.526 1.526 0 0 1 1.037-.443 48.282 48.282 0 0 0 5.68-.494c1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0 0 12 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018Z" />
                    </svg>
                </div>
                <div class="dashboard-card-info">
                    <span class="dashboard-card-label">反馈数</span>
                    <span class="dashboard-card-value">{{ dashboard.feedbackCount || 0 }}</span>
                </div>
            </div>
            <div class="dashboard-card">
                <div class="dashboard-card-icon bg-rose">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M15 19.128a9.38 9.38 0 0 0 2.625.372 9.337 9.337 0 0 0 4.121-.952 4.125 4.125 0 0 0-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 0 1 8.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0 1 11.964-3.07M12 6.375a3.375 3.375 0 1 1-6.75 0 3.375 3.375 0 0 1 6.75 0Zm8.25 2.25a2.625 2.625 0 1 1-5.25 0 2.625 2.625 0 0 1 5.25 0Z" />
                    </svg>
                </div>
                <div class="dashboard-card-info">
                    <span class="dashboard-card-label">活跃用户</span>
                    <span class="dashboard-card-value">{{ dashboard.activeUserCount || 0 }}</span>
                </div>
            </div>
        </div>

        <el-card shadow="never" class="admin-table-card mt-5">
            <div class="admin-table-toolbar">
                <div>
                    <div class="admin-table-title">推荐配置项</div>
                    <div class="admin-table-desc">调整推荐算法参数，修改后即时生效。</div>
                </div>
                <el-button type="primary" @click="saveAll" :loading="saveLoading">保存修改</el-button>
            </div>

            <el-table :data="configList" stripe style="width: 100%" class="mt-4" v-loading="tableLoading">
                <el-table-column prop="configKey" label="配置键" width="220" />
                <el-table-column label="配置值" min-width="260">
                    <template #default="{ row }">
                        <el-input v-model="row.configValue" size="small" />
                    </template>
                </el-table-column>
                <el-table-column prop="description" label="说明" min-width="300" />
            </el-table>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getRecommendConfigList, updateRecommendConfig, getRecommendDashboard } from '@/api/admin/recommend'
import { showMessage } from '@/composables/util'

const dashboardLoading = ref(false)
const tableLoading = ref(false)
const saveLoading = ref(false)

const dashboard = reactive({
    exposureCount: 0,
    clickCount: 0,
    ctr: '0%',
    feedbackCount: 0,
    activeUserCount: 0
})

const configList = ref([])

const fetchDashboard = async () => {
    dashboardLoading.value = true
    try {
        const res = await getRecommendDashboard()
        if (res.success && res.data) {
            Object.assign(dashboard, res.data)
        }
    } catch (e) {
        console.error(e)
    } finally {
        dashboardLoading.value = false
    }
}

const fetchConfigList = async () => {
    tableLoading.value = true
    try {
        const res = await getRecommendConfigList()
        if (res.success) {
            configList.value = (res.data || []).map(item => ({ ...item }))
        }
    } catch (e) {
        console.error(e)
    } finally {
        tableLoading.value = false
    }
}

const saveAll = async () => {
    saveLoading.value = true
    try {
        for (const item of configList.value) {
            await updateRecommendConfig({
                id: item.id,
                configKey: item.configKey,
                configValue: item.configValue
            })
        }
        showMessage('配置保存成功', 'success')
    } catch (e) {
        showMessage('配置保存失败', 'error')
    } finally {
        saveLoading.value = false
    }
}

onMounted(() => {
    fetchDashboard()
    fetchConfigList()
})
</script>

<style scoped>
.dashboard-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1rem;
}

.dashboard-card {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1.25rem;
    border-radius: 0.9rem;
    border: 1px solid var(--admin-border);
    background: var(--admin-surface);
    box-shadow: var(--admin-shadow);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.dashboard-card:hover {
    transform: translateY(-2px);
    box-shadow: var(--admin-shadow-hover);
}

.dashboard-card-icon {
    width: 2.75rem;
    height: 2.75rem;
    border-radius: 0.7rem;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    color: #fff;
}

.dashboard-card-icon.bg-blue {
    background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.dashboard-card-icon.bg-green {
    background: linear-gradient(135deg, #10b981, #059669);
}

.dashboard-card-icon.bg-amber {
    background: linear-gradient(135deg, #f59e0b, #d97706);
}

.dashboard-card-icon.bg-purple {
    background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.dashboard-card-icon.bg-rose {
    background: linear-gradient(135deg, #f43f5e, #e11d48);
}

.dashboard-card-info {
    display: flex;
    flex-direction: column;
    gap: 0.15rem;
}

.dashboard-card-label {
    font-size: 0.78rem;
    color: #94a3b8;
    font-weight: 600;
}

.dashboard-card-value {
    font-size: 1.35rem;
    font-weight: 800;
    color: #172033;
    letter-spacing: -0.02em;
}

.admin-table-toolbar {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
}

.admin-table-title {
    font-size: 1rem;
    font-weight: 800;
    color: #172033;
}

.admin-table-desc {
    font-size: 0.8rem;
    color: #94a3b8;
    margin-top: 0.25rem;
}
</style>
