<template>
    <Header></Header>

    <div class="front-container front-shell">
        <section class="front-page-header front-card">
            <div class="front-card-body front-page-header-inner">
                <span class="front-page-kicker">Categories</span>
                <h1 class="front-page-title">{{ isHotScope ? '热门分类 TOP10' : '按分类浏览全部内容' }}</h1>
                <p class="front-page-description">{{ isHotScope ? '按公开文章数量展示前 10 个热门分类，从高频主题快速进入相关文章。' : '更清晰地找到你关心的主题方向，从分类入口快速进入相关文章列表。' }}</p>
                <div class="front-badge-row">
                    <span class="front-badge"><strong>{{ categories.length }}</strong> 个分类</span>
                </div>
            </div>
        </section>

        <div class="front-grid front-grid--full-width-main">
            <main class="front-main-column">
                <section class="front-card front-card-solid">
                    <div class="front-card-body">
                        <div class="front-section-title">Category Index</div>
                        <h2 class="section-title">分类导航</h2>
                        <div class="category-cloud">
                            <span @click="goCatagoryArticleListPage(item.id, item.name)" v-for="item in categories" :key="item.id" class="category-cloud-item">
                                {{ item.name }}<template v-if="item.articleCount != null"> <span class="cat-stat">（{{ item.articleCount }} 篇）</span></template>
                            </span>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    </div>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/components/Header.vue'
import Footer from '@/layouts/components/Footer.vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories, getCategoriesAll } from '@/api/frontend/category'
import { computed, ref, watch } from 'vue'

const router = useRouter()
const route = useRoute()
const isHotScope = computed(() => route.query.scope === 'hot')

const goCatagoryArticleListPage = (id, name) => {
    router.push({ path: '/category/list', query: { id: id, name: name } })
}

const categories = ref([])

const loadCategories = () => {
    const request = isHotScope.value ? getCategories() : getCategoriesAll()
    request.then((e) => {
        if (e.success) {
            categories.value = e.data || []
        }
    })
}

watch(
    () => route.query.scope,
    loadCategories,
    { immediate: true }
)


</script>

<style scoped>
.section-title {
    font-size: 1.35rem;
    font-weight: 800;
    color: #172033;
    margin-bottom: 1rem;
}

.category-cloud {
    display: flex;
    flex-wrap: wrap;
    gap: 0.8rem;
}

.category-cloud-item {
    display: inline-flex;
    align-items: center;
    padding: 0.85rem 1rem;
    border-radius: 999px;
    background: rgba(16, 185, 129, 0.12);
    color: #047857;
    font-size: 0.95rem;
    font-weight: 700;
    cursor: pointer;
}

.category-cloud-item:hover {
    transform: translateY(-2px);
    background: rgba(16, 185, 129, 0.18);
}

.cat-stat {
    color: #065f46;
    font-size: 0.85em;
    font-weight: 600;
    opacity: 0.9;
}
</style>