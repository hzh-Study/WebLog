<template>
    <el-card shadow="never" class="admin-chart-card admin-filter-card">
        <template #header>
            <div>
                <div class="admin-table-title">访客地域分布</div>
                <div class="admin-table-desc">按地区统计访客数量</div>
            </div>
        </template>

        <div ref="chartRef" class="dashboard-chart"></div>
    </el-card>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getVisitorRegion } from '@/api/admin/dashboard'

const chartRef = ref(null)
let chartInstance = null

const renderChart = async () => {
    const response = await getVisitorRegion()
    if (!response?.success || !response.data) return

    const rawData = response.data
    const regions = rawData.regions || rawData.names || []
    const values = rawData.counts || rawData.values || []

    const data = regions.map((name, i) => ({
        name,
        value: values[i] || 0,
    })).sort((a, b) => b.value - a.value)

    const names = data.map((item) => item.name).reverse()
    const counts = data.map((item) => item.value).reverse()

    await nextTick()
    if (!chartRef.value) return

    if (!chartInstance) {
        chartInstance = echarts.init(chartRef.value)
    }

    chartInstance.setOption({
        tooltip: {
            trigger: 'axis',
            confine: true,
            backgroundColor: 'rgba(15, 23, 42, 0.92)',
            borderWidth: 0,
            textStyle: {
                color: '#fff',
            },
            formatter: (params) => {
                const item = params[0]
                return `${item.name}<br/>访客数：${item.value}`
            },
        },
        grid: {
            left: '3%',
            right: '8%',
            bottom: '3%',
            top: '3%',
            containLabel: true,
        },
        xAxis: {
            type: 'value',
            splitLine: {
                lineStyle: {
                    color: '#f1f5f9',
                },
            },
            axisLabel: {
                color: '#64748b',
                fontSize: 11,
            },
        },
        yAxis: {
            type: 'category',
            data: names,
            axisLine: {
                lineStyle: {
                    color: '#e2e8f0',
                },
            },
            axisTick: {
                show: false,
            },
            axisLabel: {
                color: '#334155',
                fontSize: 12,
                fontWeight: 500,
            },
        },
        series: [
            {
                type: 'bar',
                data: counts,
                barWidth: 16,
                itemStyle: {
                    borderRadius: [0, 4, 4, 0],
                    color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                        { offset: 0, color: '#3b82f6' },
                        { offset: 1, color: '#8b5cf6' },
                    ]),
                },
                label: {
                    show: true,
                    position: 'right',
                    color: '#64748b',
                    fontSize: 11,
                },
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