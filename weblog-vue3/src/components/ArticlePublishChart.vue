<template>
    <el-card shadow="never" class="admin-chart-card admin-filter-card">
        <template #header>
            <div class="flex justify-between">
                <div>
                    <div class="admin-table-title">文章发布热力图</div>
                    <div class="admin-table-desc">按最近 6 个月展示每日发布情况，布局与配色参考 GitHub Contribution Graph。</div>
                </div>
            </div>
        </template>

        <div ref="chartRef" class="dashboard-chart"></div>
    </el-card>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getDashboardPublishArticleStatisticsInfo } from '@/api/admin/dashboard'

const chartRef = ref(null)
let chartInstance = null

const dayNames = ['日', '一', '二', '三', '四', '五', '六']
const monthNames = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']

const formatDate = (date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
}

const addDays = (date, days) => {
    const next = new Date(date)
    next.setDate(next.getDate() + days)
    return next
}

const getRange = () => {
    const end = new Date()
    const start = new Date(end.getFullYear(), end.getMonth() - 5, 1)

    // GitHub 风格：按完整周对齐，让首尾边框整齐
    const alignedStart = addDays(start, -start.getDay())
    const alignedEnd = addDays(end, 6 - end.getDay())

    return {
        start,
        end,
        alignedStart,
        alignedEnd,
    }
}

const buildVisualPieces = (maxValue) => {
    if (maxValue <= 0) {
        return [{ value: 0, label: '0', color: '#ebedf0' }]
    }

    const step = Math.max(1, Math.ceil(maxValue / 4))
    return [
        { min: 0, max: 0, label: '0', color: '#ebedf0' },
        { min: 1, max: step, label: `1-${step}`, color: '#c6e6c6' },
        { min: step + 1, max: step * 2, label: `${step + 1}-${step * 2}`, color: '#7bc96f' },
        { min: step * 2 + 1, max: step * 3, label: `${step * 2 + 1}-${step * 3}`, color: '#239a3b' },
        { min: step * 3 + 1, max: maxValue, label: `>${step * 3}`, color: '#196127' },
    ]
}

const renderChart = async () => {
    const response = await getDashboardPublishArticleStatisticsInfo()
    const rawMap = response?.success && response.data ? response.data : {}
    const data = Object.entries(rawMap).map(([date, count]) => [date, Number(count || 0)])
    const maxValue = data.reduce((max, item) => Math.max(max, item[1]), 0)
    const { start, end, alignedStart, alignedEnd } = getRange()
    const yearLabelText = start.getFullYear() === end.getFullYear()
        ? `${end.getFullYear()}`
        : `${start.getFullYear()}-${end.getFullYear()}`

    await nextTick()
    if (!chartRef.value) return

    if (!chartInstance) {
        chartInstance = echarts.init(chartRef.value)
    }

    chartInstance.setOption({
        animation: true,
        tooltip: {
            confine: true,
            backgroundColor: 'rgba(15, 23, 42, 0.92)',
            borderWidth: 0,
            textStyle: {
                color: '#fff',
            },
            formatter: (params) => {
                const [date, value] = params.value
                return `${date}<br/>发布 ${value} 篇`
            },
        },
        visualMap: {
            type: 'piecewise',
            orient: 'horizontal',
            left: 'center',
            bottom: 0,
            itemWidth: 14,
            itemHeight: 14,
            itemGap: 10,
            textGap: 8,
            textStyle: {
                color: '#64748b',
                fontSize: 12,
                fontWeight: 600,
            },
            pieces: buildVisualPieces(maxValue),
        },
        calendar: {
            top: 20,
            left: 42,
            right: 24,
            bottom: 60,
            range: [formatDate(alignedStart), formatDate(alignedEnd)],
            orient: 'horizontal',
            cellSize: ['auto', 18],
            splitLine: {
                show: true,
                lineStyle: {
                    color: '#d1d5db',
                    width: 1,
                },
            },
            itemStyle: {
                color: '#ebedf0',
                borderWidth: 0,
            },
            yearLabel: {
                show: true,
                position: 'left',
                margin: 28,
                color: '#cbd5e1',
                fontSize: 18,
                fontWeight: 700,
                formatter: () => yearLabelText,
            },
            monthLabel: {
                show: true,
                margin: 12,
                color: '#0f172a',
                fontSize: 13,
                fontWeight: 600,
                align: 'left',
                formatter: (params) => monthNames[new Date(params).getMonth()],
            },
            dayLabel: {
                firstDay: 0,
                nameMap: dayNames,
                margin: 10,
                color: '#0f172a',
                fontSize: 12,
                fontWeight: 500,
            },
        },
        series: [
            {
                type: 'heatmap',
                coordinateSystem: 'calendar',
                data,
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

