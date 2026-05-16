<template>
    <Header></Header>

    <div class="front-container front-shell">
        <section class="front-page-header front-card">
            <div class="front-card-body front-page-header-inner">
                <span class="front-page-kicker">Search</span>
                <h1 class="front-page-title">{{ displayKeyword ? `搜索：${displayKeyword}` : '全部文章' }}</h1>
                <p class="front-page-description">
                    在标题、摘要与创作者中模糊匹配关键词。可直接在顶部搜索框输入后按回车或点击搜索图标。
                </p>
                <div class="front-badge-row">
                    <span class="front-badge"><strong>{{ total }}</strong> {{ displayKeyword ? '篇匹配' : '篇文章' }}</span>
                </div>
            </div>
        </section>

        <div class="front-grid front-grid--full-width-main">
            <main class="front-main-column">
                <section v-if="users.length" class="front-card front-card-solid user-search-card">
                    <div class="front-card-body">
                        <div class="front-section-title">Creators</div>
                        <div class="user-result-grid">
                            <a v-for="user in users" :key="user.id" @click="goUserHome(user.username)" class="user-result-item">
                                <img v-if="user.avatar" :src="user.avatar" class="user-result-avatar" alt="avatar">
                                <div v-else class="user-result-avatar user-result-avatar-fallback">{{ (user.nickname || user.username).slice(0, 1) }}</div>
                                <div>
                                    <div class="user-result-name">
                                        <template v-for="(part, pi) in highlightText(user.nickname || user.username)" :key="`${user.id}-name-${pi}`">
                                            <mark v-if="part.highlight" class="search-highlight">{{ part.text }}</mark>
                                            <span v-else>{{ part.text }}</span>
                                        </template>
                                    </div>
                                    <div class="user-result-handle">@{{ user.username }} · {{ user.publicArticleCount }} 篇公开内容</div>
                                </div>
                            </a>
                        </div>
                    </div>
                </section>

                <div v-if="isLoading" class="front-card front-card-solid search-loading-card">
                    <div class="front-card-body search-loading-body">
                        <span class="search-loading-dot"></span>
                        <span>{{ displayKeyword ? `正在搜索「${displayKeyword}」...` : '正在加载全部文章...' }}</span>
                    </div>
                </div>

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
                                <h2 class="article-title">
                                    <template v-for="(part, pi) in highlightText(article.title)" :key="`${article.id}-title-${pi}`">
                                        <mark v-if="part.highlight" class="search-highlight">{{ part.text }}</mark>
                                        <span v-else>{{ part.text }}</span>
                                    </template>
                                </h2>
                            </a>
                            <p class="article-desc">
                                <template v-for="(part, pi) in highlightText(article.description)" :key="`${article.id}-desc-${pi}`">
                                    <mark v-if="part.highlight" class="search-highlight">{{ part.text }}</mark>
                                    <span v-else>{{ part.text }}</span>
                                </template>
                            </p>
                            <p v-if="article.searchSnippet" class="article-search-snippet">
                                <span class="snippet-label">正文命中</span>
                                <template v-for="(part, pi) in highlightText(article.searchSnippet)" :key="`${article.id}-snippet-${pi}`">
                                    <mark v-if="part.highlight" class="search-highlight">{{ part.text }}</mark>
                                    <span v-else>{{ part.text }}</span>
                                </template>
                            </p>
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
                                <span
                                    v-for="(item, ti) in article.tags"
                                    :key="`${article.id}-${item.id}-${ti}`"
                                    @click="goTagArticleListPage(item.id, item.name)"
                                    class="front-chip front-chip-success cursor-pointer"
                                >
                                    {{ item.name }}
                                </span>
                            </div>
                        </div>
                    </article>
                </div>

                <div v-else-if="!isLoading" class="front-card front-empty">
                    <div class="empty-icon-wrap">
                        <svg class="icon" viewBox="0 0 1576 1024" xmlns="http://www.w3.org/2000/svg" width="200" height="200">
                            <path
                                d="M1260.533 834.866h-134.81l9.387-15.697c6.31-11.08 9.388-23.7 9.388-37.088V168.82c0-19.698-7.849-37.858-21.237-52.016-13.389-13.388-32.318-21.237-52.016-21.237H567.093c-19.698 0-37.857 7.849-52.015 21.237-14.158 14.158-21.237 32.318-21.237 52.016v32.317h-73.253c-19.698 0-37.858 7.849-52.016 21.238-14.158 14.158-22.006 32.317-22.006 52.015v612.338c0 12.62 3.078 25.238 9.387 37.088l9.388 15.697H251.92c-5.54 0-10.31 4.77-10.31 10.31 0 2.31 0.77 5.541 3.232 7.08 1.539 1.539 4.77 3.078 7.079 3.078h533.545l54.324 54.324c5.54 5.54 14.158 9.387 22.006 9.387 7.849 0 16.62-3.078 22.007-9.387 10.31-10.311 11.85-26.008 4.77-38.627l-9.387-16.62h129.27c5.54 0 10.31-4.771 10.31-10.311s-4.77-10.311-10.31-10.311h-30.01l9.388-15.697c6.31-11.08 9.388-23.7 9.388-37.088v-32.318h263.156c5.54 0 10.31-4.77 10.31-10.31 0.154-4.156-4.616-8.157-10.156-8.157z"
                                fill="#bfbfbf"
                            />
                        </svg>
                    </div>
                    <p v-if="displayKeyword">没有找到与「{{ displayKeyword }}」相关的文章。</p>
                    <p v-else>暂无文章数据。</p>
                </div>

                <nav v-if="total > 0 && pages > 1" aria-label="搜索结果分页" class="front-pagination">
                    <a @click="current > 1 ? getArticles(current - 1) : null" class="front-page-btn" :class="{ 'is-disabled': current <= 1 }">
                        <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 6 10">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 1 1 5l4 4" />
                        </svg>
                    </a>
                    <a v-for="page in pages" :key="page" @click="getArticles(page)" class="front-page-btn" :class="{ 'is-active': page === current }">
                        {{ page }}
                    </a>
                    <a @click="current < pages ? getArticles(current + 1) : null" class="front-page-btn" :class="{ 'is-disabled': current >= pages }">
                        <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 6 10">
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
import { useRouter, useRoute } from 'vue-router'
import { ref, watch, computed } from 'vue'
import { getIndexArticles } from '@/api/frontend/index'
import { searchPublicUsers } from '@/api/frontend/user'
import { normalizeArticleSummaryList } from '@/utils/article'

const router = useRouter()
const route = useRoute()

const displayKeyword = computed(() => {
    const q = route.query.q
    return q != null && String(q).trim() !== '' ? String(q).trim() : ''
})

const goArticleDetail = (articleId) => {
    router.push({ path: '/article/detail', query: { articleId: articleId } })
}

const articles = ref([])
const users = ref([])
const current = ref(1)
const total = ref(0)
const size = ref(10)
const pages = ref(0)
const articlesLoading = ref(false)
const usersLoading = ref(false)
const isLoading = computed(() => articlesLoading.value || usersLoading.value)

const escapeRegExp = (text) => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')

const highlightText = (text) => {
    const source = text == null ? '' : String(text)
    const keyword = displayKeyword.value
    if (!keyword) {
        return [{ text: source, highlight: false }]
    }

    const regexp = new RegExp(`(${escapeRegExp(keyword)})`, 'ig')
    return source
        .split(regexp)
        .filter((part) => part !== '')
        .map((part) => ({
            text: part,
            highlight: part.toLowerCase() === keyword.toLowerCase(),
        }))
}

function getArticles(currentNo) {
    const kw = displayKeyword.value
    articlesLoading.value = true
    getIndexArticles({ current: currentNo, size: size.value, keyword: kw || undefined })
        .then((res) => {
            if (res.success === true) {
                articles.value = normalizeArticleSummaryList(res.data || [])
                current.value = res.current
                total.value = res.total
                size.value = res.size
                pages.value = res.pages
            }
        })
        .finally(() => {
            articlesLoading.value = false
        })
}

function getUsers() {
    const kw = displayKeyword.value
    if (!kw) {
        users.value = []
        return
    }
    usersLoading.value = true
    searchPublicUsers(kw).then((res) => {
        if (res.success) {
            users.value = res.data || []
        }
    })
        .finally(() => {
            usersLoading.value = false
        })
}

watch(
    () => [route.path, route.query.q],
    () => {
        if (route.path !== '/search') return
        current.value = 1
        getArticles(1)
        getUsers()
    },
    { immediate: true }
)

const goTagArticleListPage = (id, name) => {
    router.push({ path: '/tag/list', query: { id: id, name: name } })
}

const goUserHome = (username) => {
    router.push(`/u/${username}`)
}
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

.article-search-snippet {
    color: #64748b;
    line-height: 1.75;
    padding: 0.75rem 0.85rem;
    border-radius: 0.9rem;
    background: rgba(16, 185, 129, 0.08);
    border: 1px solid rgba(16, 185, 129, 0.14);
}

.snippet-label {
    display: inline-flex;
    margin-right: 0.45rem;
    color: #047857;
    font-size: 0.75rem;
    font-weight: 800;
}

.article-author-line {
    color: #64748b;
    font-size: 0.95rem;
}

.user-search-card {
    margin-bottom: 1.5rem;
}

.search-loading-card {
    margin-bottom: 1.5rem;
}

.search-loading-body {
    display: flex;
    align-items: center;
    gap: 0.65rem;
    color: #64748b;
    font-weight: 700;
}

.search-loading-dot {
    width: 0.65rem;
    height: 0.65rem;
    border-radius: 999px;
    background: #3b82f6;
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.35);
    animation: search-pulse 1.2s ease-out infinite;
}

.search-highlight {
    padding: 0 0.18rem;
    border-radius: 0.35rem;
    background: rgba(16, 185, 129, 0.16);
    color: #047857;
}

@keyframes search-pulse {
    0% {
        box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.35);
    }

    100% {
        box-shadow: 0 0 0 0.45rem rgba(59, 130, 246, 0);
    }
}

.user-result-grid {
    display: grid;
    gap: 0.9rem;
}

.user-result-item {
    display: flex;
    align-items: center;
    gap: 0.9rem;
    padding: 0.95rem 1rem;
    border-radius: 18px;
    background: rgba(248, 250, 252, 0.9);
}

.user-result-avatar {
    width: 3rem;
    height: 3rem;
    border-radius: 999px;
    object-fit: cover;
}

.user-result-avatar-fallback {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #dbeafe, #bfdbfe);
    color: #1d4ed8;
    font-weight: 800;
}

.user-result-name {
    color: #172033;
    font-weight: 800;
}

.user-result-handle {
    color: #64748b;
    font-size: 0.88rem;
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
