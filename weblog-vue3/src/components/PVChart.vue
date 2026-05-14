<template>
    <div>
        <el-card shadow="never" class="admin-chart-card admin-filter-card">
            <template #header>
                <div class="flex justify-between">
                    <div>
                        <div class="admin-table-title">PV 访问量统计</div>
                        <div class="admin-table-desc">访问趋势与内容曝光变化</div>
                    </div>
                </div>
            </template>
            <div ref="chartRef" class="dashboard-chart"></div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { getDashboardPVStatisticsInfo } from '@/api/admin/dashboard'

const chartRef = ref(null)
let chartInstance = null

const handleResize = () => {
    if (chartInstance) {
        chartInstance.resize()
    }
}

onMounted(async () => {
    try {
        const e = await getDashboardPVStatisticsInfo()
        if (e.success && chartRef.value) {
            chartInstance = echarts.init(chartRef.value)

            const date = e.data.pvDates || []
            const data = (e.data.pvCounts && e.data.pvCounts.length) ? e.data.pvCounts : new Array(date.length).fill(0)

            const option = {
                xAxis: {
                    type: 'category',
                    data: date
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        data: data,
                        type: 'line'
                    }
                ]
            }

            chartInstance.setOption(option)
            window.addEventListener('resize', handleResize)
        }
    } catch (err) {
        console.error('PV统计数据加载失败:', err)
    }
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    if (chartInstance) {
        chartInstance.dispose()
        chartInstance = null
    }
})
</script>
