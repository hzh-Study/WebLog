<template>
    <div class="ai-recommend-panel">
        <el-form label-position="top">
            <el-form-item label="文章正文">
                <el-input
                    :model-value="content"
                    @update:model-value="$emit('update:content', $event)"
                    type="textarea"
                    :rows="5"
                    placeholder="请输入文章正文，AI 将据此推荐分类和标签..."
                    maxlength="10000"
                    show-word-limit
                />
            </el-form-item>
            <el-form-item label="推荐要求（可选）">
                <el-input
                    :model-value="requirement"
                    @update:model-value="$emit('update:requirement', $event)"
                    type="textarea"
                    :rows="2"
                    placeholder="描述您想要的风格或方向..."
                    maxlength="500"
                    show-word-limit
                />
            </el-form-item>
        </el-form>

        <div class="ai-recommend-btns">
            <el-button
                type="primary"
                :loading="categoryLoading"
                :disabled="disabled"
                @click="$emit('recommendCategories')"
            >
                <el-icon><MagicStick /></el-icon>
                推荐分类
            </el-button>
            <el-button
                type="success"
                :loading="tagLoading"
                :disabled="disabled"
                @click="$emit('recommendTags')"
            >
                <el-icon><MagicStick /></el-icon>
                推荐标签
            </el-button>
        </div>

        <div v-if="categories.length > 0" class="ai-result-area">
            <div class="ai-result-header">
                <span>推荐分类</span>
                <el-tag size="small" type="info">消耗 {{ categoryTokens }} tokens</el-tag>
            </div>
            <div class="ai-recommend-list">
                <div v-for="cat in categories" :key="cat" class="ai-recommend-item">
                    <span>{{ cat }}</span>
                    <el-button
                        v-if="!addedCategories.includes(cat)"
                        link type="primary" size="small"
                        @click="$emit('addCategory', cat)"
                    >添加</el-button>
                    <span v-else class="added-check">
                        <el-icon><Check /></el-icon> 已添加
                    </span>
                </div>
            </div>
        </div>

        <div v-if="tags.length > 0" class="ai-result-area">
            <div class="ai-result-header">
                <span>推荐标签</span>
                <el-tag size="small" type="info">消耗 {{ tagTokens }} tokens</el-tag>
            </div>
            <div class="ai-recommend-list ai-tag-list">
                <el-tag
                    v-for="tag in tags" :key="tag"
                    class="ai-tag-item"
                    :type="addedTags.includes(tag) ? 'success' : ''"
                >
                    {{ tag }}
                    <el-button v-if="!addedTags.includes(tag)" link type="primary" size="small" @click="$emit('addTag', tag)" class="ml-1">添加</el-button>
                    <span v-else class="ml-1 added-check"><el-icon><Check /></el-icon></span>
                </el-tag>
            </div>
        </div>
    </div>
</template>

<script setup>
defineProps({
    content: { type: String, default: '' },
    requirement: { type: String, default: '' },
    categoryLoading: { type: Boolean, default: false },
    tagLoading: { type: Boolean, default: false },
    disabled: { type: Boolean, default: false },
    categories: { type: Array, default: () => [] },
    tags: { type: Array, default: () => [] },
    addedCategories: { type: Array, default: () => [] },
    addedTags: { type: Array, default: () => [] },
    categoryTokens: { type: Number, default: 0 },
    tagTokens: { type: Number, default: 0 }
})

defineEmits(['update:content', 'update:requirement', 'recommendCategories', 'recommendTags', 'addCategory', 'addTag'])
</script>

<style scoped>
.ai-recommend-panel {
    display: flex;
    flex-direction: column;
}

.ai-recommend-btns {
    display: flex;
    gap: 12px;
}

.ai-result-area {
    margin-top: 20px;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    overflow: hidden;
}

.ai-result-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 14px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    font-size: 13px;
    font-weight: 600;
    color: #475569;
}

.ai-recommend-list {
    padding: 14px;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.ai-recommend-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 12px;
    background: #f8fafc;
    border-radius: 8px;
    font-size: 14px;
    color: #334155;
}

.ai-tag-list {
    flex-direction: row;
    flex-wrap: wrap;
}

.ai-tag-item {
    font-size: 13px;
}

.added-check {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    color: #16a34a;
    font-size: 12px;
    font-weight: 600;
}
</style>