<template>
    <div class="ai-quota-card">
        <div class="ai-quota-card-header">
            <span class="ai-quota-card-title">
                <el-icon><DataAnalysis /></el-icon>
                今日额度
            </span>
            <el-button link type="primary" size="small" @click="$emit('refresh')" :loading="loading">
                <el-icon><Refresh /></el-icon>
                刷新
            </el-button>
        </div>
        <div class="ai-quota-card-grid">
            <div class="ai-quota-card-item">
                <span class="ai-quota-card-label">文章生成</span>
                <span class="ai-quota-card-value">
                    <strong :class="{ 'text-red': quota.dailyArticleGenRemaining === 0 }">{{ quota.dailyArticleGenRemaining }}</strong>
                    / {{ quota.dailyArticleGenLimit }}
                </span>
                <span class="ai-quota-card-sub">(基础{{ quota.dailyArticleGenLimitBase }} + 弹性{{ quota.dailyArticleGen弹性额度 || 1 }})</span>
            </div>
            <div class="ai-quota-card-item">
                <span class="ai-quota-card-label">Token 余量</span>
                <span class="ai-quota-card-value">
                    <strong :class="{ 'text-red': quota.dailyTokenRemaining < 10000 }">{{ formatNumber(quota.dailyTokenRemaining) }}</strong>
                    / {{ formatNumber(quota.dailyTokenLimit) }}
                </span>
                <span class="ai-quota-card-sub">约 {{ quota.standard写作次数 || 0 }} 次标准写作</span>
            </div>
            <div class="ai-quota-card-item">
                <span class="ai-quota-card-label">冷却状态</span>
                <span class="ai-quota-card-value">
                    <strong :class="{ 'text-red': !quota.canInteractNow }">
                        {{ quota.canInteractNow ? '就绪' : remainingSeconds + 's' }}
                    </strong>
                </span>
            </div>
        </div>
        <div v-if="quota.token预警触发" class="ai-quota-card-warning">
            <el-icon><WarningFilled /></el-icon>
            <span>Token 余量低于 {{ quota.token预警阈值Percent || 20 }}%，请合理安排使用</span>
        </div>
        <div v-if="quota.need弹性分配" class="ai-quota-card-info">
            <el-icon><InfoFilled /></el-icon>
            <span>基础配额已用完，启用弹性分配模式</span>
        </div>
        <div v-if="!quota.canInteractNow && remainingSeconds > 0" class="ai-quota-card-warning">
            <el-icon><WarningFilled /></el-icon>
            <span>冷却中，请等待 {{ remainingSeconds }} 秒后再使用 AI 功能</span>
        </div>
        <div class="ai-quota-card-desc">
            <p>标准写作：约 {{ formatNumber(50000) }} tokens/次，可生成约 20000 字文章</p>
            <p>高复杂度内容：自动上浮至 {{ formatNumber(55000) }} tokens/次</p>
        </div>
    </div>
</template>

<script setup>
defineProps({
    quota: {
        type: Object,
        required: true
    },
    loading: {
        type: Boolean,
        default: false
    },
    remainingSeconds: {
        type: Number,
        default: 0
    }
})

defineEmits(['refresh'])

function formatNumber(num) {
    if (num == null) return '0'
    if (num >= 10000) return (num / 10000).toFixed(1) + '万'
    return num.toLocaleString()
}
</script>

<style scoped>
.ai-quota-card {
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border-radius: 12px;
    padding: 16px;
    border: 1px solid #bae6fd;
}

.ai-quota-card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
}

.ai-quota-card-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 700;
    color: #0369a1;
    font-size: 14px;
}

.ai-quota-card-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
}

.ai-quota-card-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding: 10px;
    background: rgba(255,255,255,0.7);
    border-radius: 8px;
}

.ai-quota-card-label {
    font-size: 11px;
    color: #64748b;
    font-weight: 600;
}

.ai-quota-card-value {
    font-size: 13px;
    color: #334155;
}

.ai-quota-card-value strong {
    font-size: 16px;
    font-weight: 800;
    color: #1e293b;
}

.ai-quota-card-sub {
    font-size: 10px;
    color: #94a3b8;
}

.ai-quota-card-warning {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 12px;
    padding: 10px 14px;
    background: #fef3c7;
    border-radius: 8px;
    color: #92400e;
    font-size: 13px;
    font-weight: 600;
}

.ai-quota-card-info {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 10px;
    padding: 10px 14px;
    background: #dbeafe;
    border-radius: 8px;
    color: #1e40af;
    font-size: 13px;
}

.ai-quota-card-desc {
    margin-top: 12px;
    padding: 10px 14px;
    background: rgba(255,255,255,0.5);
    border-radius: 8px;
    font-size: 11px;
    color: #64748b;
}

.ai-quota-card-desc p {
    margin: 2px 0;
}

.text-red {
    color: #ef4444 !important;
}
</style>