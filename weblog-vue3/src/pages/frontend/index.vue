<template>
    <Header></Header>

    <div class="front-container front-shell">
        <section class="front-page-header front-card">
            <div class="front-card-body front-page-header-inner">
                <span class="front-page-kicker">WebLog Frontend</span>
                <h1 class="front-page-title">{{ heroTitle }}</h1>
                <p class="front-page-description">{{ heroDescription }}</p>
                <div class="front-badge-row">
                    <span class="front-badge"><strong>{{ total }}</strong> 篇公开文章</span>
                    <router-link :to="{ path: '/category', query: { scope: 'hot' } }" class="front-badge front-hero-stat-link" title="查看热门分类">
                        <strong>{{ hotCategoryCount }}</strong> 条热门分类
                    </router-link>
                    <router-link :to="{ path: '/tag', query: { scope: 'hot' } }" class="front-badge front-hero-stat-link" title="查看热门标签">
                        <strong>{{ hotTagCount }}</strong> 条热门标签
                    </router-link>
                </div>
            </div>
        </section>

        <div class="front-grid front-grid--full-width-main">
            <main class="front-main-column">
                <section class="front-card front-card-solid recommend-section">
                    <div class="front-card-body">
                        <div class="recommend-section-header">
                            <div class="front-section-title">Recommended</div>
                            <h2 class="sidebar-title">为你推荐</h2>
                        </div>
                        <RecommendFeed :articles="recommendArticles" @refresh="loadRecommendFeed" />
                    </div>
                </section>

                <div v-if="articles.length" class="article-grid">
                    <article v-for="article in articles" :key="article.id" class="front-card front-card-solid front-card-hover article-card">
                        <a @click="goArticleDetail(article.id)" class="article-cover-link">
                            <img v-if="article.titleImage" class="article-cover" :src="article.titleImage" :alt="article.title">
                            <div v-else class="article-cover article-cover-fallback">
                                <span>{{ article.title }}</span>
                            </div>
                        </a>

                        <div class="front-card-body article-card-body">
                            <div class="article-tag-row" v-if="article.tags && article.tags.length">
                                <span v-for="item in article.tags" :key="item.id" @click="goTagArticleListPage(item.id, item.name)" class="front-chip front-chip-success cursor-pointer">
                                    {{ item.name }}
                                </span>
                            </div>

                            <a @click="goArticleDetail(article.id)" class="article-title-link">
                                <h2 class="article-title">{{ article.title }}</h2>
                            </a>

                            <p class="article-desc">{{ article.description }}</p>

                            <div v-if="article.author" class="article-author-line">
                                <a @click="goUserHome(article.author.username)" class="front-link">
                                    {{ article.author.nickname || article.author.username }}
                                </a>
                            </div>

                            <div class="front-meta">
                                <span class="front-meta-item">
                                    <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M5 1v3m5-3v3m5-3v3M1 7h18M5 11h10M2 3h16a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z" />
                                    </svg>
                                    {{ article.createTime }}
                                </span>
                                <span class="front-meta-item">
                                    <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 18 18">
                                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M1 5v11a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V6a1 1 0 0 0-1-1H1Zm0 0V2a1 1 0 0 1 1-1h5.443a1 1 0 0 1 .8.4l2.7 3.6H1Z" />
                                    </svg>
                                    <a v-if="article.category" @click="goCatagoryArticleListPage(article.category.id, article.category.name)" class="front-link">
                                        {{ article.category.name }}
                                    </a>
                                    <span v-else>未分类</span>
                                </span>
                            </div>
                        </div>
                    </article>
                </div>

                <div v-else class="front-card front-empty">
                    <svg class="w-20 h-20" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.4"
                            d="M7 8h10M7 12h6m-8 8h14a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2Z" />
                    </svg>
                    <p>还没有可展示的文章内容。</p>
                </div>

                <nav v-if="total > 0 && pages > 1" aria-label="首页分页" class="front-pagination">
                    <a @click="current > 1 ? getArticles(current - 1) : null" class="front-page-btn" :class="{ 'is-disabled': current <= 1 }">
                        <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 6 10">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 1 1 5l4 4" />
                        </svg>
                    </a>
                    <a v-for="page in pages" :key="page" @click="getArticles(page)" class="front-page-btn" :class="{ 'is-active': page === current }">
                        {{ page }}
                    </a>
                    <a @click="current < pages ? getArticles(current + 1) : null" class="front-page-btn" :class="{ 'is-disabled': current >= pages }">
                        <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 6 10">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 9 4-4-4-4" />
                        </svg>
                    </a>
                </nav>
            </main>
        </div>
    </div>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/components/Header.vue'
import Footer from '@/layouts/components/Footer.vue'
import RecommendFeed from '@/components/RecommendFeed.vue'
import { useRouter } from 'vue-router'
import { computed, ref, onMounted } from 'vue'
import { getIndexArticles } from '@/api/frontend/index'
import { getCategories } from '@/api/frontend/category'
import { getTags } from '@/api/frontend/tag'
import { getRecommendFeed } from '@/api/frontend/recommend'
import { useTracker } from '@/composables/useTracker'
import store from '@/store'

const router = useRouter()
const { trackPageView } = useTracker()

const recommendArticles = ref([])
const recommendPage = ref(1)

function loadRecommendFeed() {
    getRecommendFeed({ current: recommendPage.value, size: 6 }).then(res => {
        if (res.success) {
            recommendArticles.value = res.data || []
            recommendPage.value = res.current || 1
        }
    }).catch(() => {
        recommendArticles.value = []
    })
}

onMounted(() => {
    loadRecommendFeed()
    trackPageView('/', document.referrer)
})

/** 与旧数据兼容：曾用名 hzh / hzh博客 的站点名在页面上显示为 WebLog */
const displaySiteName = computed(() => {
    const n = (store.state.setting.blogName || '').trim()
    if (!n || /^hzh/i.test(n)) return 'WebLog'
    return n
})
const heroTitle = computed(() => `${displaySiteName.value}，用更舒服的方式记录内容`)
const heroDescription = computed(() => {
    const intro = (store.state.setting.introduction || '').trim()
    if (!intro) {
        return '这里汇集了近期文章、分类导航与精选主题，让阅读和浏览都更轻盈。'
    }
    return intro.replace(/hzh/gi, 'WebLog')
})
const hotCategoryCount = computed(() => Math.min(categories.value.length, 10))
const hotTagCount = computed(() => Math.min(tags.value.length, 10))

const goArticleDetail = (articleId) => {
    console.log('跳转详情页' + articleId)
    router.push({ path: '/article/detail', query: { articleId: articleId } })
}

const goUserHome = (username) => {
    router.push(`/u/${username}`)
}

const articles = ref([])
// 当前页码
const current = ref(1)
const total = ref(0)
const size = ref(12)
const pages = ref(0)

// 获取分页数据
function getArticles(currentNo) {
    console.log('获取分页数据')
    getIndexArticles({ current: currentNo, size: size.value })
        .then((res) => {
            console.log(res)
            if (res.success == true) {
                articles.value = res.data || []
                current.value = res.current
                total.value = res.total
                size.value = res.size
                pages.value = res.pages
            }
        })
}
getArticles(current.value)

// 获取分类
const categories = ref([])
getCategories().then((e) => {
    console.log('获取分类数据')
    console.log(e)
    categories.value = e.data || []
})


// 获取标签
const tags = ref([])
getTags().then((e) => {
    console.log('获取标签数据')
    console.log(e)
    tags.value = e.data || []
})


const goCatagoryArticleListPage = (id, name) => {
    router.push({ path: '/category/list', query: { id: id, name: name } })
}

const goTagArticleListPage = (id, name) => {
    console.log('跳转 id' + id)
    router.push({ path: '/tag/list', query: { id: id, name: name } })
}

</script>

<style scoped>
a.front-hero-stat-link {
    text-decoration: none;
    color: inherit;
    cursor: pointer;
    transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

a.front-hero-stat-link:hover {
    border-color: rgba(59, 130, 246, 0.35);
    box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.12);
}

.article-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
}

.article-card {
    height: 100%;
}

.article-cover-link {
    display: block;
}

.article-cover {
    width: 100%;
    height: 14rem;
    object-fit: cover;
}

.article-cover-fallback {
    display: flex;
    align-items: flex-end;
    padding: 1.5rem;
    background:
        linear-gradient(180deg, rgba(15, 23, 42, 0.1), rgba(15, 23, 42, 0.72)),
        linear-gradient(135deg, #2563eb, #4f46e5);
    color: #fff;
    font-size: 1.2rem;
    font-weight: 700;
}

.article-card-body {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.article-tag-row {
    display: flex;
    flex-wrap: wrap;
    gap: 0.65rem;
}

.article-title-link {
    display: block;
}

.article-title {
    font-size: 1.45rem;
    line-height: 1.35;
    letter-spacing: -0.03em;
    color: #172033;
    font-weight: 800;
}

.article-title-link:hover .article-title {
    color: #1d4ed8;
}

.article-desc {
    color: #64748b;
    line-height: 1.85;
    display: -webkit-box;
    overflow: hidden;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
}

.article-author-line {
    color: #64748b;
    font-size: 0.95rem;
}

.recommend-section {
    margin-bottom: 1.5rem;
}

.recommend-section-header {
    margin-bottom: 1rem;
}
</style>