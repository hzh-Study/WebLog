<template>
    <div class="taxonomy-category-selector">
        <div class="selector-header">
            <label class="selector-label">{{ label }}</label>
            <span class="selector-hint" v-if="hint">{{ hint }}</span>
        </div>

        <div class="selector-body">
            <div class="search-area">
                <el-input
                    v-model="searchKeyword"
                    :placeholder="placeholder || '输入关键字搜索...'"
                    clearable
                    @input="handleSearch"
                    @clear="handleClear"
                    class="search-input"
                >
                    <template #prefix>
                        <el-icon><Search /></el-icon>
                    </template>
                </el-input>
            </div>

            <div class="tree-area" v-if="!searchKeyword">
                <el-scrollbar height="280px">
                    <el-tree
                        :data="categoryTree"
                        :props="treeProps"
                        node-key="id"
                        :expand-on-click-node="false"
                        :default-expand-all="true"
                        @node-click="handleNodeClick"
                        class="category-tree"
                    >
                        <template #default="{ node, data }">
                            <span class="tree-node-content">
                                <span class="tree-node-label">{{ data.name }}</span>
                                <span v-if="data.children && data.children.length > 0" class="tree-node-count">
                                    {{ data.children.length }}个子分类
                                </span>
                            </span>
                        </template>
                    </el-tree>
                </el-scrollbar>
            </div>

            <div class="search-results" v-else>
                <el-scrollbar height="280px">
                    <div v-if="searchResults.length === 0" class="no-results">
                        <el-icon><Search /></el-icon>
                        <span>未找到匹配的分类</span>
                    </div>
                    <div v-else class="results-list">
                        <div
                            v-for="cat in searchResults"
                            :key="cat.id"
                            class="result-item"
                            :class="{ 'result-item-active': selectedId === cat.id }"
                            @click="selectCategory(cat)"
                        >
                            <div class="result-info">
                                <span class="result-name">{{ cat.name }}</span>
                                <span class="result-path" v-if="cat.parentName">
                                    {{ cat.parentName }}
                                </span>
                            </div>
                            <el-icon v-if="selectedId === cat.id" class="result-check"><Check /></el-icon>
                        </div>
                    </div>
                </el-scrollbar>
            </div>

            <div class="selected-display" v-if="selectedCategory">
                <div class="selected-label">已选择：</div>
                <el-tag type="primary" closable @close="clearSelection">
                    {{ selectedCategory.name }}
                </el-tag>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Search, Check } from '@element-plus/icons-vue'
import { getCategoryTree, searchCategories, getCategoryById } from '@/constants/taxonomy'

const props = defineProps({
    modelValue: {
        type: [Number, String],
        default: null
    },
    label: {
        type: String,
        default: '文章分类'
    },
    hint: {
        type: String,
        default: ''
    },
    placeholder: {
        type: String,
        default: ''
    }
})

const emit = defineEmits(['update:modelValue', 'change'])

const searchKeyword = ref('')
const searchResults = ref([])
const selectedId = computed(() => props.modelValue)
const selectedCategory = ref(null)
const categoryTree = getCategoryTree()

const treeProps = {
    children: 'children',
    label: 'name'
}

watch(() => props.modelValue, (newVal) => {
    if (newVal) {
        selectedCategory.value = getCategoryById(newVal)
    } else {
        selectedCategory.value = null
    }
}, { immediate: true })

function handleSearch() {
    if (!searchKeyword.value.trim()) {
        searchResults.value = []
        return
    }

    searchResults.value = searchCategories(searchKeyword.value.trim())
}

function handleClear() {
    searchKeyword.value = ''
    searchResults.value = []
}

function handleNodeClick(data) {
    if (!data.children || data.children.length === 0) {
        selectCategory(data)
    }
}

function selectCategory(cat) {
    emit('update:modelValue', cat.id)
    emit('change', cat)
    selectedCategory.value = cat
    searchKeyword.value = ''
    searchResults.value = []
}

function clearSelection() {
    emit('update:modelValue', null)
    emit('change', null)
    selectedCategory.value = null
}
</script>

<style scoped>
.taxonomy-category-selector {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.selector-header {
    display: flex;
    align-items: center;
    gap: 8px;
}

.selector-label {
    font-size: 14px;
    font-weight: 600;
    color: #334155;
}

.selector-hint {
    font-size: 12px;
    color: #94a3b8;
}

.selector-body {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.search-area {
    width: 100%;
}

.search-input {
    width: 100%;
}

.tree-area {
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    padding: 12px;
    background: #fafbfc;
}

.category-tree {
    background: transparent;
}

.category-tree :deep(.el-tree-node__content) {
    height: 36px;
    border-radius: 8px;
    margin-bottom: 4px;
}

.category-tree :deep(.el-tree-node__content:hover) {
    background: #f1f5f9;
}

.category-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
}

.tree-node-content {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
}

.tree-node-label {
    font-size: 14px;
    color: #334155;
}

.tree-node-count {
    font-size: 12px;
    color: #94a3b8;
    margin-left: auto;
}

.search-results {
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    padding: 12px;
    background: #fafbfc;
}

.no-results {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 40px 20px;
    color: #94a3b8;
    font-size: 14px;
}

.no-results .el-icon {
    font-size: 24px;
}

.results-list {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.result-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    border: 1.5px solid #e2e8f0;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.2s;
    background: #fff;
}

.result-item:hover {
    border-color: #c7d2fe;
    background: #f5f3ff;
}

.result-item-active {
    border-color: #6366f1;
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
}

.result-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.result-name {
    font-size: 14px;
    font-weight: 500;
    color: #334155;
}

.result-path {
    font-size: 12px;
    color: #94a3b8;
}

.result-check {
    color: #6366f1;
    font-size: 18px;
}

.selected-display {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px;
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
    border: 1.5px solid #c7d2fe;
    border-radius: 10px;
}

.selected-label {
    font-size: 13px;
    color: #64748b;
}
</style>
