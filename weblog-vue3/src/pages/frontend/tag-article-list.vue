<template>
    <Header></Header>

    <div class="front-container front-shell">
        <section class="front-page-header front-card">
            <div class="front-card-body front-page-header-inner">
                <span class="front-page-kicker">Tag Archive</span>
                <h1 class="front-page-title">标签：{{ tagName }}</h1>
                <p class="front-page-description">通过标签聚合同类内容，更快回到你关心的技术点和主题记录。</p>
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
                                d="M1260.533 834.866h-134.81l9.387-15.697c6.31-11.08 9.388-23.7 9.388-37.088V168.82c0-19.698-7.849-37.858-21.237-52.016-13.389-13.388-32.318-21.237-52.016-21.237H567.093c-19.698 0-37.857 7.849-52.015 21.237-14.158 14.158-21.237 32.318-21.237 52.016v32.317h-73.253c-19.698 0-37.858 7.849-52.016 21.238-14.158 14.158-22.006 32.317-22.006 52.015v612.338c0 12.62 3.078 25.238 9.387 37.088l9.388 15.697H251.92c-5.54 0-10.31 4.77-10.31 10.31 0 2.31 0.77 5.541 3.232 7.08 1.539 1.539 4.77 3.078 7.079 3.078h533.545l54.324 54.324c5.54 5.54 14.158 9.387 22.006 9.387 7.849 0 16.62-3.078 22.007-9.387 10.31-10.311 11.85-26.008 4.77-38.627l-9.387-16.62h129.27c5.54 0 10.31-4.771 10.31-10.311s-4.77-10.311-10.31-10.311h-30.01l9.388-15.697c6.31-11.08 9.388-23.7 9.388-37.088v-32.318h263.156c5.54 0 10.31-4.77 10.31-10.31 0.154-4.156-4.616-8.157-10.156-8.157z m-283.624 52.016c0 29.086-23.7 52.785-52.785 52.785h-70.945l-73.252-74.022c-7.849-7.849-19.699-11.08-30.01-7.849l-6.309 1.54-30.778-30.78 5.54-7.078c42.628-57.556 33.856-138.657-19.699-186.056-53.554-47.245-135.579-44.167-186.055 7.079-50.477 50.476-52.786 132.347-6.31 186.825 46.475 53.555 127.73 63.096 184.363 20.468l7.08-5.54 30.778 30.778-1.54 6.31c-3.077 11.08 0 22.93 7.85 30.778l29.085 29.086H419.665c-29.086 0-52.786-23.7-52.786-52.785V273.775c0-28.317 22.93-52.016 51.247-52.016h507.537c28.316 0 51.246 23.7 51.246 52.016v613.107z m-286.086-65.404c-22.93 22.93-52.016 33.856-82.025 33.856s-59.094-11.08-82.024-33.856c-21.853-21.7-34.01-51.247-33.857-82.025 0-30.779 11.85-60.634 33.857-82.025 22.006-22.006 51.246-33.856 82.024-33.856s59.864 11.85 82.025 33.856c22.007 22.007 33.856 51.246 33.856 82.025s-11.85 60.018-33.856 82.025z m433.36-40.166c0.001 29.085-23.698 52.785-52.784 52.785h-74.022V274.544c0-19.698-7.849-37.857-21.238-52.016-13.388-13.388-32.317-21.237-52.015-21.237H514.308v-31.548c0-29.085 23.7-52.785 52.785-52.785h504.46c29.085 0 52.785 23.7 52.785 52.785v611.569zM167.436 940.436H41.397c-5.54 0-10.31 4.771-10.31 10.311 0 3.078 0.769 5.54 3.077 7.08 1.539 1.538 4.77 3.077 7.08 3.077H167.28c5.54 0 10.31-4.77 10.31-10.31s-3.846-10.158-10.156-10.158z m0 0"
                                fill="#bfbfbf" p-id="1448"></path>
                            <path
                                d="M482.76 327.33h230.993c5.54 0 10.31-4.772 10.31-10.312s-4.77-10.31-10.31-10.31H482.76c-5.54 0-10.31 4.77-10.31 10.31 0 2.309 0.77 5.54 3.078 7.08 1.692 2.462 4.77 3.231 7.232 3.231z m336.563 85.102H482.76c-5.54 0-10.31 4.77-10.31 10.31 0 3.078 0.77 5.54 3.078 7.08 1.538 1.538 4.77 3.077 7.079 3.077h336.562c5.54 0 10.311-4.77 10.311-10.31 0-5.387-4.77-10.157-10.157-10.157z m-189.288 105.57H482.607c-5.54 0-10.311 4.77-10.311 10.31 0 3.078 0.77 5.54 3.078 7.08 1.539 1.539 4.77 3.077 7.079 3.077h147.429c5.54 0 10.31-4.77 10.31-10.31s-4.616-10.157-10.157-10.157zM157.278 707.905h21.237c5.54 0 10.311 4.77 10.311 10.31s-4.77 10.312-10.31 10.312h-21.238v21.237c0 5.54-4.77 10.31-10.31 10.31-3.079 0-5.54-0.769-7.08-3.077-2.308-1.54-3.078-4.771-3.078-7.08V728.68h-21.39c-5.54 0-10.311-4.77-10.311-10.31s4.77-10.311 10.31-10.311h21.237v-21.237c0-5.54 4.771-10.311 10.311-10.311s10.311 4.77 10.311 10.31v21.084z m1387.032-85.102v-21.238c0-3.231-0.77-5.54-3.078-7.079-2.308-1.539-4.77-3.078-7.079-3.078-5.54 0-10.31 4.771-10.31 10.311v21.237h-21.238c-3.077 0-5.54 0.77-7.079 3.232-1.539 2.309-3.231 4.77-3.231 7.08 0 5.54 4.77 10.31 10.31 10.31h21.238v21.237c0 5.54 4.77 10.31 10.31 10.31s10.311-4.77 10.311-10.31V643.27h21.237c5.54 0 10.311-4.77 10.311-10.31s-4.77-10.311-10.31-10.311h-21.392zM267.62 47.553h31.548c8.618 0 15.697 7.079 15.697 15.697s-7.08 15.697-15.697 15.697h-31.548v31.548c0 8.618-7.08 15.697-15.697 15.697-4.001 0-7.849-1.54-11.08-4.77-3.078-2.31-4.771-6.31-4.771-11.081V79.1h-31.548c-4.001 0-7.849-1.54-11.08-4.771-3.078-2.308-4.77-6.31-4.77-11.08 0-8.618 7.078-15.697 15.696-15.697h31.548V16.005c0-8.618 7.08-15.697 15.697-15.697s15.697 7.079 15.697 15.697v31.548zM62.634 274.544c-22.93 0-43.397 11.85-54.324 31.548-11.08 19.698-11.08 44.167 0 63.096 11.08 19.698 32.318 31.548 54.324 31.548 34.626 0 63.096-28.316 63.096-63.096s-28.47-63.096-63.096-63.096z m27.547 79.562c-5.54 9.388-15.697 15.697-27.547 15.697-17.39 0-31.548-14.158-31.548-31.548s14.158-31.547 31.548-31.547c11.08 0 21.237 6.31 27.547 15.697 5.54 9.695 5.54 21.39 0 31.701z m1275.306-205.754c-22.93 0-43.397 11.85-54.324 31.548-11.08 19.698-11.08 44.167 0 63.096 11.08 19.698 32.318 31.548 54.324 31.548 34.626 0 63.096-28.316 63.096-63.096-0.154-34.626-27.7-63.096-63.096-63.096z m27.547 78.793c-5.54 9.388-15.697 15.697-27.547 15.697-17.39 0-31.548-14.158-31.548-31.548s14.158-31.548 31.548-31.548c11.08 0 22.007 6.31 27.547 15.697 5.54 9.696 5.54 22.315 0 31.702z m0 0"
                                fill="#bfbfbf" p-id="1449"></path>
                        </svg>
                    </div>
                    <p>此标签下还未发布博客。</p>
                </div>

                <nav v-if="total > 0 && pages > 1" aria-label="标签文章分页" class="front-pagination">
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
                            <div class="front-section-title">Categories</div>
                            <h4 class="section-title">相关分类</h4>
                            <div class="sidebar-links">
                                <a @click="goCatagoryArticleListPage(item.id, item.name)" v-for="item in categories" :key="item.id" class="front-list-link">
                                    <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 21 18">
                                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.1"
                                            d="M2.539 17h12.476l4-9H5m-2.461 9a1 1 0 0 1-.914-1.406L5 8m-2.461 9H2a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h5.443a1 1 0 0 1 .8.4l2.7 3.6H16a1 1 0 0 1 1 1v2H5" />
                                    </svg>
                                    <span>{{ item.name }}</span>
                                </a>
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
import { getTagArticles } from '@/api/frontend/tag'
import { getCategories } from '@/api/frontend/category'

const router = useRouter()
const route = useRoute()

const tagName = ref(route.query.name)

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
    console.log('获取标签分页数据' + route.query.id)
    getTagArticles({ current: currentNo, size: size.value, tagId: route.query.id })
        .then((res) => {
            console.log(res)
            if (res.success == true) {
                articles.value = res.data
                current.value = res.current
                total.value = res.total
                size.value = res.size
                pages.value = res.pages
            }
        })
}
getArticles(current.value)

const goCatagoryArticleListPage = (id, name) => {
    router.push({ path: '/category/list', query: { id: id, name: name } })
}

// 获取分类
const categories = ref([])
getCategories().then((e) => {
    console.log('获取分类数据')
    console.log(e)
    categories.value = e.data
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

.sidebar-links {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.sidebar-chip-row {
    display: flex;
    flex-wrap: wrap;
    gap: 0.7rem;
}
</style>