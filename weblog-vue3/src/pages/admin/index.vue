<template>
    <div class="admin-page">
        <section class="admin-page-header">
            <div>
                <span class="admin-kicker">Dashboard</span>
                <h1 class="admin-page-title">后台概览</h1>
                <p class="admin-page-desc">用更清晰的统计卡片和图表容器查看当前博客内容与访问趋势。</p>
            </div>
        </section>

        <div class="admin-stat-grid">
            <el-card shadow="never" class="admin-stat-card">
                <div class="flex items-center">
                    <div class="mr-4"><el-icon class="admin-stat-icon"><Document /></el-icon></div>
                    <div>
                        <div class="admin-stat-label">文章总数</div>
                        <CountTo :value="articleTotalCount"></CountTo>
                    </div>
                </div>
            </el-card>
            <el-card shadow="never" class="admin-stat-card">
                <div class="flex items-center">
                    <div class="mr-4"><el-icon class="admin-stat-icon"><Folder /></el-icon></div>
                    <div>
                        <div class="admin-stat-label">分类总数</div>
                        <CountTo :value="categoryTotalCount"></CountTo>
                    </div>
                </div>
            </el-card>
            <el-card shadow="never" class="admin-stat-card">
                <div class="flex items-center">
                    <div class="mr-4"><el-icon class="admin-stat-icon"><PriceTag /></el-icon></div>
                    <div>
                        <div class="admin-stat-label">标签总数</div>
                        <CountTo :value="tagTotalCount"></CountTo>
                    </div>
                </div>
            </el-card>
            <el-card shadow="never" class="admin-stat-card">
                <div class="flex items-center">
                    <div class="mr-4"><el-icon class="admin-stat-icon"><View /></el-icon></div>
                    <div>
                        <div class="admin-stat-label">总浏览量</div>
                        <CountTo :value="pvTotalCount"></CountTo>
                    </div>
                </div>
            </el-card>
        </div>

        <el-row :gutter="20" class="mt-5">
            <el-col :xs="24" :lg="12">
                <ArticlePublishChart></ArticlePublishChart>
            </el-col>
            <el-col :xs="24" :lg="12" class="mt-5 lg:mt-0">
                <PVChart></PVChart>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import CountTo from '@/components/CountTo.vue'
import ArticlePublishChart from '@/components/ArticlePublishChart.vue'
import PVChart from '@/components/PVChart.vue'
import { ref } from 'vue'
import { getDashboardArticleStatisticsInfo } from '@/api/admin/dashboard'


const articleTotalCount = ref(0)
const categoryTotalCount = ref(0)
const tagTotalCount = ref(0)
const pvTotalCount = ref(0)

getDashboardArticleStatisticsInfo()
    .then((e) => {
        if (e && e.success && e.data) {
            articleTotalCount.value = Number(e.data.articleTotalCount) || 0
            categoryTotalCount.value = Number(e.data.categoryTotalCount) || 0
            tagTotalCount.value = Number(e.data.tagTotalCount) || 0
            pvTotalCount.value = Number(e.data.pvTotalCount) || 0
        }
    })
    .catch((err) => {
        console.error('[Dashboard] 顶栏统计接口失败，数字将保持 0。若文章列表有数据，请检查网络或 /admin/dashboard/article/statistics 是否超时。', err)
    })


</script>

<style scoped>
:deep(.count-to) {
    color: #172033;
    font-size: 1.8rem;
    font-weight: 800;
}
</style>


