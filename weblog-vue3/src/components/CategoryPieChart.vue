<template>
    <el-card shadow="never" class="admin-chart-card admin-filter-card">
        <template #header>
            <div>
                <div class="admin-table-title">分类占比</div>
                <div class="admin-table-desc">各分类下的文章数量分布</div>
            </div>
        </template>

        <div ref="chartRef" class="dashboard-chart"></div>
    </el-card>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getCategoryRatio } from '@/api/admin/dashboard'

const chartRef = ref(null)
let chartInstance = null

const renderChart = async () => {
    const response = await getCategoryRatio()
    if (!response?.success || !response.data) return

    const rawData = response.data
    const names = rawData.names || rawData.categories || []
    const values = (rawData.counts || rawData.values || []).map((v) => Number(v))

    if (names.length === 0) return

    const seriesData = names.map((name, i) => ({
        name,
        value: values[i] || 0,
    }))

    await nextTick()
    if (!chartRef.value) return

    if (!chartInstance) {
        chartInstance = echarts.init(chartRef.value)
    }

    chartInstance.setOption({
        tooltip: {
            trigger: 'item',
            confine: true,
            backgroundColor: 'rgba(15, 23, 42, 0.92)',
            borderWidth: 0,
            textStyle: {
                color: '#fff',
            },
            formatter: (params) => {
                return `${params.name}<br/>文章数：${params.value}（${params.percent}%）`
            },
        },
        legend: {
            orient: 'vertical',
            left: 0,
            top: 'center',
            textStyle: {
                color: '#64748b',
                fontSize: 12,
            },
            itemWidth: 10,
            itemHeight: 10,
            itemGap: 12,
        },
        series: [
            {
                type: 'pie',
                radius: ['40%', '70%'],
                center: ['55%', '50%'],
                roseType: 'radius',
                itemStyle: {
                    borderRadius: 4,
                    borderColor: '#fff',
                    borderWidth: 2,
                },
                label: {
                    color: '#64748b',
                    fontSize: 12,
                    formatter: '{b}\n{d}%',
                },
                emphasis: {
                    label: {
                        fontSize: 16,
                        fontWeight: 'bold',
                    },
                },
                data: seriesData,
            },
        ],
    }, true)
}

const handleResize = () => {
    chartInstance?.resize()
}

onMounted(async () => {
    await renderChart()
    window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    chartInstance?.dispose()
    chartInstance = null
})
</script>