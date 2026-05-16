<template>
    <Header></Header>

    <div class="front-container front-shell">
        <section class="front-page-header front-card">
            <div class="front-card-body front-page-header-inner">
                <span class="front-page-kicker">Category Archive</span>
                <h1 class="front-page-title">分类：{{ categoryName }}</h1>
                <p class="front-page-description">当前分类下的相关文章会按卡片形式展示，方便继续深入浏览同主题内容。</p>
                <div class="front-badge-row">
                    <span class="front-badge"><strong>{{ total }}</strong> 篇文章</span>
                </div>
            </div>
        </section>

        <div class="front-grid">
            <main class="front-main-column">
                <div v-if="articles && articles.length > 0" class="article-grid">
                    <article v-for="(article, index) in articles" :key="index" class="front-card front-card-solid front-card-hover">
                        <a @click="goArticleDetail(article.id)" class="article-cover-link">
                            <img v-if="article.titleImage" class="article-cover" :src="article.titleImage" :alt="article.title">
                            <div v-else class="article-cover article-cover-fallback">
                                <span>{{ article.title }}</span>
                            </div>
                        </a>
                        <div class="front-card-body article-card-body">
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
                                <span class="front-meta-item">{{ article.createTime }}</span>
                                <span class="front-meta-item">点赞 {{ article.likeNum || 0 }}</span>
                                <span class="front-meta-item">收藏 {{ article.favoriteNum || 0 }}</span>
                            </div>
                            <div class="sidebar-chip-row">
                                <span @click="goTagArticleListPage(item.id, item.name)" v-for="item in article.tags" :key="item.id" class="front-chip front-chip-success cursor-pointer">
                                    {{ item.name }}
                                </span>
                            </div>
                        </div>
                    </article>
                </div>

                <div v-else class="front-card front-empty">
                    <div class="empty-icon-wrap">
                        <svg t="1687255143784" class="icon" viewBox="0 0 1576 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="1447" width="200" height="200">
                            <path
                                d="M1260.533 834.866h-134.81l9.387-15.697c6.31-11.08 9.388-23.7 9.388-37.088V168.82c0-19.698-7.849-37.858-21.237-52.016-13.389-13.388-32.318-21.237-52.016-21.237H567.093c-19.698 0-37.857 7.849-52.015 21.237-14.158 14.158-21.237 32.318-21.237 52.016v32.317h-73.253c-19.698 0-37.858 7.849-52.016 21.238-14.158 14.158-22.006 32.317-22.006 52.015v612.338c0 12.62 3.078 25.238 9.387 37.088l9.388 15.697H251.92c-5.54 0-10.31 4.77-10.31 10.31 0 2.31 0.77 5.541 3.232 7.08 1.539 1.539 4.77 3.078 7.079 3.078h533.545l54.324 54.324c5.54 5.54 14.158 9.387 22.006 9.387 7.849 0 16.62-3.078 22.007-9.387 10.31-10.311 11.85-26.008 4.77-38.627l-9.387-16.62h129.27c5.54 0 10.31-4.771 10.31-10.311s-4.77-10.311-10.31-10.311h-30.01l9.388-15.697c6.31-11.08 9.388-23.7 9.388-37.088v-32.318h263.156c5.54 0 10.31-4.77 10.31-10.31 0.154-4.156-4.616-8.157-10.156-8.157z"
                                fill="#bfbfbf" p-id="1448"></path>
                        </svg>
                    </div>
                    <p>此分类下还未发布博客。</p>
                </div>

                <nav v-if="total > 0 && pages > 1" aria-label="分类文章分页" class="front-pagination">
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

            <aside class="front-side-column">
                <div class="front-sidebar-stack sticky top-24">
                    <section class="front-card front-card-solid">
                        <div class="front-card-body">
                            <div class="front-section-title">Tags</div>
                            <h4 class="section-title">相关标签</h4>
                            <div class="sidebar-chip-row">
                                <span @click="goTagArticleListPage(item.id, item.name)" v-for="item in tags" :key="item.id" class="front-chip front-chip-success cursor-pointer">
                                    {{ item.name }}
                                </span>
                            </div>
                        </div>
                    </section>
                </div>
            </aside>
        </div>
    </div>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/components/Header.vue'
import Footer from '@/layouts/components/Footer.vue'
import { useRouter, useRoute } from 'vue-router'
import { ref } from 'vue'
import { getCategoryArticles } from '@/api/frontend/category'
import { getTags } from '@/api/frontend/tag'
import { normalizeArticleSummaryList } from '@/utils/article'


const router = useRouter()
const route = useRoute()

const categoryName = ref(route.query.name)

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
const size = ref(10)
const pages = ref(0)

// 获取分页数据
function getArticles(currentNo) {
    console.log('获取分页数据')
    let categoryId = route.query.id
    getCategoryArticles({ current: currentNo, size: size.value, categoryId: categoryId })
        .then((res) => {
            console.log(res)
            if (res.success == true) {
                articles.value = normalizeArticleSummaryList(res.data || [])
                current.value = res.current
                total.value = res.total
                size.value = res.size
                pages.value = res.pages
            }
        })
}
getArticles(current.value)

const goTagArticleListPage = (id, name) => {
    router.push({ path: '/tag/list', query: { id: id, name: name } })
}

// 获取标签
const tags = ref([])
getTags().then((e) => {
    console.log('获取标签数据')
    console.log(e)
    tags.value = e.data
})

</script>

<style scoped>
.article-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
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
    background: linear-gradient(135deg, #2563eb, #4f46e5);
    color: #fff;
    font-size: 1.2rem;
    font-weight: 700;
}

.article-card-body {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.article-title {
    font-size: 1.4rem;
    line-height: 1.35;
    color: #172033;
    font-weight: 800;
    letter-spacing: -0.03em;
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

.empty-icon-wrap {
    margin-bottom: 0.75rem;
}

.section-title {
    font-size: 1.25rem;
    font-weight: 800;
    color: #172033;
    margin-bottom: 1rem;
}

.sidebar-chip-row {
    display: flex;
    flex-wrap: wrap;
    gap: 0.7rem;
}
</style>
