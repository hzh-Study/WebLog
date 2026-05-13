<template>
    <el-card shadow="never" class="admin-chart-card admin-filter-card">
        <template #header>
            <div class="flex justify-between">
                <div>
                    <div class="admin-table-title">PV / UV 趋势</div>
                    <div class="admin-table-desc">页面浏览量（PV）与独立访客数（UV）随时间变化趋势</div>
                </div>
                <el-radio-group v-model="period" size="small" @change="handlePeriodChange">
                    <el-radio-button label="day">近7天</el-radio-button>
                    <el-radio-button label="week">近30天</el-radio-button>
                    <el-radio-button label="month">近90天</el-radio-button>
                </el-radio-group>
            </div>
        </template>

        <div ref="chartRef" class="dashboard-chart"></div>
    </el-card>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getPvUvTrend } from '@/api/admin/dashboard'

const chartRef = ref(null)
const period = ref('day')
let chartInstance = null

const fetchData = async () => {
    const response = await getPvUvTrend({ period: period.value })
    if (!response?.success) return null
    return response.data
}

const renderChart = async () => {
    const data = await fetchData()
    if (!data) return

    const dates = data.pvDates || data.dates || []
    const pvCounts = data.pvCounts || []
    const uvCounts = data.uvCounts || []

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
        },
        legend: {
            data: ['PV', 'UV'],
            bottom: 0,
            textStyle: {
                color: '#64748b',
                fontSize: 12,
            },
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: 36,
            top: 16,
            containLabel: true,
        },
        xAxis: {
            type: 'category',
            data: dates,
            axisLine: {
                lineStyle: {
                    color: '#e2e8f0',
                },
            },
            axisTick: {
                show: false,
            },
            axisLabel: {
                color: '#64748b',
                fontSize: 11,
            },
        },
        yAxis: {
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
        series: [
            {
                name: 'PV',
                type: 'line',
                data: pvCounts,
                smooth: true,
                symbol: 'circle',
                symbolSize: 6,
                lineStyle: {
                    width: 2,
                    color: '#3b82f6',
                },
                itemStyle: {
                    color: '#3b82f6',
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(59, 130, 246, 0.25)' },
                        { offset: 1, color: 'rgba(59, 130, 246, 0.02)' },
                    ]),
                },
            },
            {
                name: 'UV',
                type: 'line',
                data: uvCounts,
                smooth: true,
                symbol: 'circle',
                symbolSize: 6,
                lineStyle: {
                    width: 2,
                    color: '#22c55e',
                },
                itemStyle: {
                    color: '#22c55e',
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(34, 197, 94, 0.25)' },
                        { offset: 1, color: 'rgba(34, 197, 94, 0.02)' },
                    ]),
                },
            },
        ],
    }, true)
}

const handlePeriodChange = () => {
    if (chartInstance) {
        chartInstance.dispose()
        chartInstance = null
    }
    renderChart()
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