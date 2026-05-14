<template>
    <div class="taxonomy-tag-selector">
        <div class="selector-header">
            <label class="selector-label">{{ label }}</label>
            <span class="selector-hint" v-if="hint">{{ hint }}</span>
        </div>

        <div class="selector-body">
            <div class="search-area">
                <el-input
                    v-model="searchKeyword"
                    :placeholder="placeholder || '输入关键字搜索标签...'"
                    clearable
                    @input="handleSearch"
                    @clear="handleClear"
                    class="search-input"
                    :disabled="selectedTags.length >= maxTags"
                >
                    <template #prefix>
                        <el-icon><Search /></el-icon>
                    </template>
                </el-input>
            </div>

            <div class="search-results" v-if="searchKeyword && searchResults.length > 0">
                <el-scrollbar height="200px">
                    <div class="results-list">
                        <div
                            v-for="tag in searchResults"
                            :key="tag.id"
                            class="result-item"
                            :class="{
                                'result-item-selected': isSelected(tag.id),
                                'result-item-disabled': selectedTags.length >= maxTags && !isSelected(tag.id)
                            }"
                            @click="selectTag(tag)"
                        >
                            <div class="result-info">
                                <span class="result-name">{{ tag.name }}</span>
                                <span class="result-category" v-if="tag.categoryName">
                                    {{ tag.categoryName }}
                                </span>
                            </div>
                            <el-icon v-if="isSelected(tag.id)" class="result-check"><Check /></el-icon>
                        </div>
                    </div>
                </el-scrollbar>
            </div>

            <div class="category-filter" v-if="categoryOptions.length > 0 && !searchKeyword">
                <div class="filter-label">按分类筛选：</div>
                <div class="filter-chips">
                    <el-tag
                        v-for="cat in categoryOptions"
                        :key="cat.id"
                        :type="selectedCategoryId === cat.id ? 'primary' : 'info'"
                        class="filter-chip"
                        @click="filterByCategory(cat.id)"
                    >
                        {{ cat.name }}
                    </el-tag>
                    <el-tag
                        v-if="selectedCategoryId"
                        type="info"
                        class="filter-chip filter-chip-clear"
                        @click="clearCategoryFilter"
                    >
                        清除筛选
                    </el-tag>
                </div>
            </div>

            <div class="tags-display" v-if="filteredTags.length > 0 && !searchKeyword">
                <el-scrollbar height="220px">
                    <div class="tags-grid">
                        <div
                            v-for="tag in filteredTags"
                            :key="tag.id"
                            class="tag-item"
                            :class="{
                                'tag-item-selected': isSelected(tag.id),
                                'tag-item-disabled': selectedTags.length >= maxTags && !isSelected(tag.id)
                            }"
                            @click="selectTag(tag)"
                        >
                            <div class="tag-content">
                                <span class="tag-name">{{ tag.name }}</span>
                                <span class="tag-category" v-if="tag.categoryName">
                                    {{ tag.categoryName }}
                                </span>
                            </div>
                            <el-icon v-if="isSelected(tag.id)" class="tag-check"><Check /></el-icon>
                        </div>
                    </div>
                </el-scrollbar>
            </div>

            <div class="selected-tags" v-if="selectedTags.length > 0">
                <div class="selected-label">
                    已选择 ({{ selectedTags.length }}/{{ maxTags }})：
                </div>
                <div class="selected-chips">
                    <TransitionGroup name="tag">
                        <el-tag
                            v-for="tag in selectedTags"
                            :key="tag.id"
                            type="primary"
                            closable
                            @close="removeTag(tag.id)"
                            class="selected-chip"
                        >
                            {{ tag.name }}
                        </el-tag>
                    </TransitionGroup>
                </div>
            </div>

            <div class="empty-state" v-if="filteredTags.length === 0 && !searchKeyword && !selectedCategoryId">
                <el-icon><Search /></el-icon>
                <span>请先选择分类以查看可用标签</span>
            </div>

            <div class="empty-state" v-if="searchResults.length === 0 && searchKeyword && !selectedCategoryId">
                <el-icon><Search /></el-icon>
                <span>未找到匹配的标签</span>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Search, Check } from '@element-plus/icons-vue'
import { getTagsByCategoryId, searchTags, getCategoryById } from '@/constants/taxonomy'

const props = defineProps({
    modelValue: {
        type: Array,
        default: () => []
    },
    label: {
        type: String,
        default: '文章标签'
    },
    hint: {
        type: String,
        default: ''
    },
    placeholder: {
        type: String,
        default: ''
    },
    maxTags: {
        type: Number,
        default: 5
    },
    minTags: {
        type: Number,
        default: 3
    },
    categoryId: {
        type: [Number, String],
        default: null
    }
})

const emit = defineEmits(['update:modelValue', 'change'])

const searchKeyword = ref('')
const searchResults = ref([])
const selectedCategoryId = ref(props.categoryId)
const allTags = ref([])

const selectedTagIds = computed(() => props.modelValue.map(t => t.id))

const selectedTags = computed(() => props.modelValue)

const categoryOptions = computed(() => {
    if (!selectedCategoryId.value) {
        const parent = getCategoryById(selectedCategoryId.value)
        if (parent) {
            return [{ id: parent.id, name: parent.name }]
        }
    }
    const allCategories = []
    for (const cat of allTags.value) {
        if (cat.categoryId && cat.categoryName && !allCategories.find(c => c.id === cat.categoryId)) {
            allCategories.push({ id: cat.categoryId, name: cat.categoryName })
        }
    }
    return allCategories
})

const filteredTags = computed(() => {
    return allTags.value
})

watch(() => props.categoryId, (newVal) => {
    selectedCategoryId.value = newVal
    if (newVal) {
        loadTagsByCategory(newVal)
    } else {
        allTags.value = []
    }
}, { immediate: true })

function isSelected(tagId) {
    return selectedTagIds.value.includes(tagId)
}

function loadTagsByCategory(categoryId) {
    if (!categoryId) {
        allTags.value = []
        return
    }

    const tags = getTagsByCategoryId(categoryId) || []
    const category = getCategoryById(categoryId)
    
    allTags.value = tags.map(tag => ({
        ...tag,
        categoryName: category ? category.name : ''
    }))
}

function handleSearch() {
    if (!searchKeyword.value.trim()) {
        searchResults.value = []
        return
    }

    const results = searchTags(searchKeyword.value.trim())
    
    searchResults.value = results.map(tag => {
        const category = getCategoryById(tag.categoryId)
        return {
            ...tag,
            categoryName: category ? category.name : ''
        }
    })
}

function handleClear() {
    searchKeyword.value = ''
    searchResults.value = []
}

function filterByCategory(categoryId) {
    selectedCategoryId.value = selectedCategoryId.value === categoryId ? null : categoryId
    loadTagsByCategory(selectedCategoryId.value)
}

function clearCategoryFilter() {
    selectedCategoryId.value = null
    allTags.value = []
}

function selectTag(tag) {
    if (isSelected(tag.id)) {
        removeTag(tag.id)
        return
    }

    if (selectedTags.value.length >= props.maxTags) {
        return
    }

    const newSelected = [...props.modelValue, tag]
    emit('update:modelValue', newSelected)
    emit('change', newSelected)
}

function removeTag(tagId) {
    const newSelected = props.modelValue.filter(t => t.id !== tagId)
    emit('update:modelValue', newSelected)
    emit('change', newSelected)
}
</script>

<style scoped>
.taxonomy-tag-selector {
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

.search-results {
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    padding: 12px;
    background: #fafbfc;
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
    padding: 10px 14px;
    border: 1.5px solid #e2e8f0;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.2s;
    background: #fff;
}

.result-item:hover:not(.result-item-disabled) {
    border-color: #c7d2fe;
    background: #f5f3ff;
}

.result-item-selected {
    border-color: #6366f1;
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
}

.result-item-disabled {
    opacity: 0.5;
    cursor: not-allowed;
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

.result-category {
    font-size: 12px;
    color: #94a3b8;
}

.result-check {
    color: #6366f1;
    font-size: 18px;
}

.category-filter {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.filter-label {
    font-size: 13px;
    color: #64748b;
    font-weight: 500;
}

.filter-chips {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
}

.filter-chip {
    cursor: pointer;
    transition: all 0.2s;
}

.filter-chip:not(.filter-chip-clear):hover {
    transform: translateY(-1px);
}

.filter-chip-clear {
    cursor: pointer;
    opacity: 0.7;
}

.filter-chip-clear:hover {
    opacity: 1;
}

.tags-display {
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    padding: 12px;
    background: #fafbfc;
}

.tags-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 8px;
}

.tag-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 12px;
    border: 1.5px solid #e2e8f0;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.2s;
    background: #fff;
}

.tag-item:hover:not(.tag-item-disabled) {
    border-color: #c7d2fe;
    background: #f5f3ff;
    transform: translateY(-1px);
}

.tag-item-selected {
    border-color: #6366f1;
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
}

.tag-item-disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.tag-content {
    display: flex;
    flex-direction: column;
    gap: 2px;
    flex: 1;
    min-width: 0;
}

.tag-name {
    font-size: 13px;
    font-weight: 500;
    color: #334155;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.tag-category {
    font-size: 11px;
    color: #94a3b8;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.tag-check {
    color: #6366f1;
    font-size: 16px;
    flex-shrink: 0;
}

.selected-tags {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 12px;
    background: linear-gradient(135deg, #eef2ff 0%, #f3e8ff 100%);
    border: 1.5px solid #c7d2fe;
    border-radius: 10px;
}

.selected-label {
    font-size: 13px;
    color: #64748b;
    font-weight: 500;
}

.selected-chips {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
}

.selected-chip {
    transition: all 0.2s;
}

.selected-chip:hover {
    transform: scale(1.05);
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 40px 20px;
    color: #94a3b8;
    font-size: 14px;
    border: 2px dashed #e2e8f0;
    border-radius: 12px;
    background: #fafbfc;
}

.empty-state .el-icon {
    font-size: 32px;
}

.tag-enter-active,
.tag-leave-active {
    transition: all 0.3s ease;
}

.tag-enter-from,
.tag-leave-to {
    opacity: 0;
    transform: scale(0.8);
}

.tag-move {
    transition: transform 0.3s ease;
}
</style>
