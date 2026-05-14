<template>
    <Header></Header>

    <div class="front-container front-shell">
        <div class="front-grid">
            <main class="front-main-column">
                <section class="front-card front-card-solid detail-hero">
                    <div class="front-card-body detail-hero-body">
                        <nav class="detail-breadcrumb" aria-label="Breadcrumb">
                            <a @click="router.push('/')">首页</a>
                            <span>/</span>
                            <span>正文</span>
                        </nav>

                        <span class="front-page-kicker">Article Detail</span>
                        <div class="front-title-divider"></div>
                        <h1 class="detail-title">{{ article.title }}</h1>

                        <div class="front-meta detail-meta">
                            <span class="front-meta-item">
                                <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M5 1v3m5-3v3m5-3v3M1 7h18M5 11h10M2 3h16a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z" />
                                </svg>
                                发表于 {{ article.updateTime }}
                            </span>
                            <span class="front-meta-item">
                                <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 18 18">
                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M1 5v11a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V6a1 1 0 0 0-1-1H1Zm0 0V2a1 1 0 0 1 1-1h5.443a1 1 0 0 1 .8.4l2.7 3.6H1Z" />
                                </svg>
                                <a @click="goCatagoryArticleListPage(article.categoryId, article.categoryName)" class="front-link">
                                    {{ article.categoryName }}
                                </a>
                            </span>
                            <span v-if="article.author" class="front-meta-item">
                                <a @click="goUserHome(article.author.username)" class="front-link">
                                    {{ article.author.nickname || article.author.username }}
                                </a>
                            </span>
                            <span class="front-meta-item">
                                <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 14">
                                    <g stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
                                        <path d="M10 10a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z" />
                                        <path d="M10 13c4.97 0 9-2.686 9-6s-4.03-6-9-6-9 2.686-9 6 4.03 6 9 6Z" />
                                    </g>
                                </svg>
                                阅读量 {{ article.readNum }}
                            </span>
                        </div>

                        <div class="detail-tag-row" v-if="article.tags && article.tags.length">
                            <span v-for="item in article.tags" :key="item.id" @click="goTagArticleListPage(item.id, item.name)" class="front-chip front-chip-success cursor-pointer">
                                # {{ item.name }}
                            </span>
                        </div>
                    </div>
                </section>

                <section class="front-card front-card-solid detail-content-card">
                    <div class="front-card-body detail-content-body">
                        <div class="article-content" v-viewer v-html="article.content" v-highlight></div>
                    </div>
                </section>

                <ArticleInteraction 
                  :articleId="article.id" 
                  :likeNum="article.likeNum || 0" 
                  :favoriteNum="article.favoriteNum || 0"
                  :liked="article.liked || false"
                  :favorited="article.favorited || false"
                />

                <ArticleAiReader v-if="article.id" :article-id="article.id" />

                <RelatedArticles v-if="article.id" :article-id="article.id" />

                <section class="front-card front-card-solid detail-nav-card">
                    <div class="front-card-body detail-nav-grid">
                        <a v-if="article.preArticleId" @click="goArticleDetail(article.preArticleId)" class="detail-nav-item">
                            <span class="detail-nav-label">上一篇</span>
                            <span class="detail-nav-title">{{ article.preArticleTitle }}</span>
                        </a>
                        <div v-else class="detail-nav-item is-empty">
                            <span class="detail-nav-label">上一篇</span>
                            <span class="detail-nav-title">已经是第一篇了</span>
                        </div>

                        <a v-if="article.nextArticleId" @click="goArticleDetail(article.nextArticleId)" class="detail-nav-item detail-nav-item-right">
                            <span class="detail-nav-label">下一篇</span>
                            <span class="detail-nav-title">{{ article.nextArticleTitle }}</span>
                        </a>
                        <div v-else class="detail-nav-item detail-nav-item-right is-empty">
                            <span class="detail-nav-label">下一篇</span>
                            <span class="detail-nav-title">已经是最后一篇了</span>
                        </div>
                    </div>
                </section>
            </main>

            <aside class="front-side-column">
                <div class="front-sidebar-stack sticky top-24">
                    <section class="front-card front-card-solid">
                        <div class="front-card-body">
                            <div class="front-section-title">Categories</div>
                            <h2 class="sidebar-title">文章分类</h2>
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

                    <section class="front-card front-card-solid">
                        <div class="front-card-body">
                            <div class="front-section-title">Tags</div>
                            <h2 class="sidebar-title">文章标签</h2>
                            <div class="sidebar-chip-row">
                                <span @click="goTagArticleListPage(item.id, item.name)" v-for="item in tags" :key="item.id" class="front-chip front-chip-success cursor-pointer">
                                    {{ item.name }}
                                </span>
                            </div>
                        </div>
                    </section>

                    <HotSidebar /></div>
            </aside>
        </div>
    </div>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/components/Header.vue'
import Footer from '@/layouts/components/Footer.vue'
import ArticleInteraction from '@/components/ArticleInteraction.vue'
import ArticleAiReader from '@/components/ArticleAiReader.vue'
import RelatedArticles from '@/components/RelatedArticles.vue'
import HotSidebar from '@/components/HotSidebar.vue'
import { useRoute, useRouter } from 'vue-router';
import { getArticleDetail } from '@/api/frontend/article';
import { ref, reactive, watch } from 'vue'
import { getCategories } from '@/api/frontend/category'
import { getTags } from '@/api/frontend/tag'
import { showMessage } from '@/composables/util'
import { useTracker } from '@/composables/useTracker'

const router = useRouter()
const route = useRoute()
const { trackArticleView } = useTracker()
const article = reactive({
    id: null,
    title: '',
    content: '',
    updateTime: '',
    readNum: 0,
    categoryId: null,
    categoryName: '',
    preArticleId: null,
    preArticleTitle: '',
    nextArticleId: null,
    nextArticleTitle: '',
    tags: [],
    author: null,
    likeNum: 0,
    favoriteNum: 0,
    liked: false,
    favorited: false,
})

function queryArticleDetail(articleId) {
    const raw = Array.isArray(articleId) ? articleId[0] : articleId
    if (raw == null || raw === '') {
        showMessage('链接中缺少文章 ID，请从首页或列表进入', 'warning')
        return
    }
    const id = typeof raw === 'string' ? Number(raw) : raw
    if (Number.isNaN(id)) {
        showMessage('无效的文章 ID', 'warning')
        return
    }
    getArticleDetail(id)
        .then((e) => {
            if (!e || !e.success || !e.data) {
                showMessage(e?.message || '文章加载失败', 'error')
                return
            }
            const d = e.data
            article.id = d.id
            article.title = d.title
            article.content = d.content
            article.updateTime = d.updateTime
            article.categoryId = d.categoryId
            article.categoryName = d.categoryName
            article.readNum = d.readNum
            article.likeNum = d.likeNum || 0
            article.favoriteNum = d.favoriteNum || 0
            article.liked = d.liked || false
            article.favorited = d.favorited || false
            article.tags = d.tags || []
            article.author = d.author
            if (d.preArticle) {
                article.preArticleId = d.preArticle.id
                article.preArticleTitle = d.preArticle.title
            } else {
                article.preArticleId = null
            }

            if (d.nextArticle) {
                article.nextArticleId = d.nextArticle.id
                article.nextArticleTitle = d.nextArticle.title
            } else {
                article.nextArticleId = null
            }

            trackArticleView(d.id, 0)
        })
        .catch((err) => {
            console.error(err)
        })
}

watch(
    () => route.query.articleId,
    (aid) => queryArticleDetail(aid),
    { immediate: true }
)

const goArticleDetail = (articleId) => {
    router.push({ path: '/article/detail', query: { articleId: articleId } })
}


// 获取分类
const categories = ref([])
getCategories().then((e) => {
    console.log('获取分类数据')
    console.log(e)
    categories.value = e.data
})


// 获取标签
const tags = ref([])
getTags().then((e) => {
    console.log('获取标签数据')
    console.log(e)
    tags.value = e.data
})

const goCatagoryArticleListPage = (id, name) => {
    router.push({ path: '/category/list', query: { id: id, name: name } })
}

const goTagArticleListPage = (id, name) => {
    router.push({ path: '/tag/list', query: { id: id, name: name } })
}

const goUserHome = (username) => {
    router.push(`/u/${username}`)
}

</script>

<style scoped>
.detail-hero-body {
    display: flex;
    flex-direction: column;
    gap: 1.15rem;
}

.detail-breadcrumb {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    color: #94a3b8;
    font-size: 0.9rem;
    font-weight: 600;
}

.detail-breadcrumb a {
    color: #64748b;
}

.detail-breadcrumb a:hover {
    color: #1d4ed8;
}

.detail-title {
    font-size: clamp(2rem, 4vw, 3.2rem);
    line-height: 1.12;
    color: #172033;
    letter-spacing: -0.04em;
    font-weight: 800;
}

.detail-meta {
    margin-top: 0.2rem;
}

.detail-tag-row {
    display: flex;
    flex-wrap: wrap;
    gap: 0.75rem;
}

.detail-content-card {
    margin-top: 1.5rem;
}

.detail-content-body {
    padding: 2rem;
}

.article-content {
    color: #334155;
    font-size: 1.03rem;
}

:deep(.article-content p) {
    margin: 0 0 1.45rem;
    line-height: 1.95;
    color: #334155;
    letter-spacing: 0.01em;
}

:deep(.article-content h1),
:deep(.article-content h2),
:deep(.article-content h3),
:deep(.article-content h4),
:deep(.article-content h5),
:deep(.article-content h6) {
    color: #172033;
    line-height: 1.38;
    font-weight: 800;
    letter-spacing: -0.03em;
}

:deep(.article-content h2) {
    margin: 3rem 0 1.15rem;
    padding-bottom: 0.85rem;
    font-size: 1.9rem;
    border-bottom: 1px solid rgba(148, 163, 184, 0.18);
}

:deep(.article-content h3) {
    margin: 2.25rem 0 1rem;
    font-size: 1.45rem;
}

:deep(.article-content h4) {
    margin: 1.8rem 0 0.8rem;
    font-size: 1.18rem;
}

:deep(.article-content ul),
:deep(.article-content ol) {
    margin: 0 0 1.5rem;
    padding-left: 1.5rem;
    color: #334155;
}

:deep(.article-content li) {
    margin: 0.45rem 0;
    line-height: 1.9;
}

:deep(blockquote) {
    margin: 2rem 0;
    padding: 1.2rem 1.3rem;
    border-left: 4px solid #3b82f6;
    border-radius: 0 18px 18px 0;
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(99, 102, 241, 0.06));
    color: #52607a;
}

:deep(blockquote p:last-child) {
    margin-bottom: 0;
}

:deep(code:not(pre code)) {
    padding: 0.18rem 0.45rem;
    margin: 0 0.1rem;
    border-radius: 8px;
    color: #1d4ed8;
    background: rgba(59, 130, 246, 0.08);
    font-size: 0.94em !important;
    font-family: Consolas, Monaco, Menlo, monospace;
}

:deep(pre) {
    position: relative;
    margin: 1.8rem 0;
    padding: 1.1rem 1.1rem 1rem 1.1rem;
    overflow: hidden;
    border-radius: 20px;
    background: linear-gradient(180deg, #0f172a 0%, #111827 100%);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

:deep(pre::before) {
    content: '';
    position: absolute;
    top: 1rem;
    left: 1rem;
    width: 0.65rem;
    height: 0.65rem;
    border-radius: 999px;
    background: #fb7185;
    box-shadow: 1.1rem 0 #fbbf24, 2.2rem 0 #34d399;
}

:deep(pre code.hljs) {
    display: block;
    overflow-x: auto;
    padding: 2rem 0.2rem 0.2rem !important;
    background: transparent !important;
}

:deep(.article-content a) {
    color: #2563eb;
    text-decoration: none;
}

:deep(.article-content a:hover) {
    color: #1d4ed8;
    text-decoration: underline;
}

:deep(.article-content img) {
    display: block;
    max-width: 100%;
    margin: 1.5rem auto;
    border-radius: 18px;
    box-shadow: 0 18px 36px rgba(15, 23, 42, 0.12);
    cursor: zoom-in;
}

:deep(.image-caption) {
    display: block;
    width: fit-content;
    max-width: 85%;
    margin: 0.2rem auto 1.5rem;
    padding: 0.35rem 0.9rem;
    border-radius: 999px;
    background: rgba(248, 250, 252, 0.96);
    color: #64748b;
    font-size: 0.82rem;
    text-align: center;
}

:deep(table) {
    width: 100%;
    margin: 1.8rem 0;
    border-collapse: separate;
    border-spacing: 0;
    overflow: hidden;
    border-radius: 18px;
    border: 1px solid rgba(148, 163, 184, 0.16);
}

:deep(table th) {
    background: rgba(248, 250, 252, 0.95);
    color: #172033;
    font-weight: 700;
}

:deep(table th),
:deep(table td) {
    padding: 0.9rem 1rem;
    border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}

:deep(table tr:nth-child(2n)) {
    background: rgba(248, 250, 252, 0.55);
}

:deep(table tr:last-child td) {
    border-bottom: none;
}

:deep(strong) {
    color: #172033;
}

.detail-nav-card {
    margin-top: 1.5rem;
}

.detail-nav-grid {
    display: grid;
    gap: 1rem;
    grid-template-columns: 1fr;
}

.detail-nav-item {
    display: flex;
    flex-direction: column;
    gap: 0.55rem;
    padding: 1.2rem 1.3rem;
    border-radius: 20px;
    background: rgba(248, 250, 252, 0.92);
    border: 1px solid rgba(148, 163, 184, 0.12);
}

.detail-nav-item:hover {
    background: rgba(59, 130, 246, 0.08);
    border-color: rgba(96, 165, 250, 0.24);
}

.detail-nav-item-right {
    text-align: right;
}

.detail-nav-item.is-empty {
    opacity: 0.72;
}

.detail-nav-label {
    color: #94a3b8;
    font-size: 0.74rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.12em;
}

.detail-nav-title {
    color: #172033;
    font-size: 1rem;
    font-weight: 700;
    line-height: 1.6;
}

.sidebar-title {
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

@media (min-width: 768px) {
    .detail-nav-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .detail-content-body {
        padding: 2.35rem;
    }
}
</style>
