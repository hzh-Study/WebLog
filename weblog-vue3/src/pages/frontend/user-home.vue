<template>
    <Header />

    <div class="front-container front-shell">
        <section class="front-page-header front-card">
            <div class="front-card-body front-page-header-inner">
                <span class="front-page-kicker">Creator Page</span>
                <h1 class="front-page-title">{{ profile.nickname || profile.username }}</h1>
                <p class="front-page-description">{{ profile.bio || '这个用户还没有填写个人简介。' }}</p>
                <div class="front-badge-row">
                    <span class="front-badge"><strong>{{ total }}</strong> 篇公开内容</span>
                    <span class="front-badge"><strong>@{{ profile.username }}</strong> 主页</span>
                </div>
            </div>
        </section>

        <div class="front-grid" :class="{ 'front-grid--full-width-main': !showVisitorAboutCard }">
            <main class="front-main-column">
                <div v-if="articles.length" class="article-grid">
                    <article v-for="article in articles" :key="article.id" class="front-card front-card-solid front-card-hover article-card">
                        <a @click="goArticleDetail(article.id)" class="article-cover-link">
                            <img v-if="article.titleImage" class="article-cover" :src="article.titleImage" :alt="article.title">
                            <div v-else class="article-cover article-cover-fallback">
                                <span>{{ article.title }}</span>
                            </div>
                        </a>
                        <div class="front-card-body article-card-body">
                            <div class="article-tag-row" v-if="article.tags?.length">
                                <span v-for="item in article.tags" :key="item.id" @click="goTagArticleListPage(item.id, item.name)" class="front-chip front-chip-success cursor-pointer">
                                    {{ item.name }}
                                </span>
                            </div>
                            <a @click="goArticleDetail(article.id)" class="article-title-link">
                                <h2 class="article-title">{{ article.title }}</h2>
                            </a>
                            <p class="article-desc">{{ article.description }}</p>
                            <div class="front-meta">
                                <span class="front-meta-item">{{ article.createTime }}</span>
                                <a v-if="article.category" @click="goCatagoryArticleListPage(article.category.id, article.category.name)" class="front-link">
                                    {{ article.category.name }}
                                </a>
                            </div>
                        </div>
                    </article>
                </div>

                <div v-else class="front-card front-empty">
                    <p>这个用户暂时还没有公开内容。</p>
                </div>

                <nav v-if="total > 0 && pages > 1" aria-label="用户主页分页" class="front-pagination">
                    <a @click="current > 1 ? getArticles(current - 1) : null" class="front-page-btn" :class="{ 'is-disabled': current <= 1 }">上一页</a>
                    <a v-for="page in pages" :key="page" @click="getArticles(page)" class="front-page-btn" :class="{ 'is-active': page === current }">
                        {{ page }}
                    </a>
                    <a @click="current < pages ? getArticles(current + 1) : null" class="front-page-btn" :class="{ 'is-disabled': current >= pages }">下一页</a>
                </nav>
            </main>

            <aside v-if="showVisitorAboutCard" class="front-side-column">
                <div class="front-sidebar-stack sticky top-24">
                    <UserInfoCard :profile="profile" />
                </div>
            </aside>
        </div>
    </div>

    <Footer />
</template>

<script setup>
import Header from '@/layouts/components/Header.vue'
import Footer from '@/layouts/components/Footer.vue'
import UserInfoCard from '@/components/UserInfoCard.vue'
import { useRoute, useRouter } from 'vue-router'
import { computed, reactive, ref, watch } from 'vue'
import { getIndexArticles } from '@/api/frontend/index'
import { getPublicUserDetail } from '@/api/frontend/user'
import store from '@/store'

const route = useRoute()
const router = useRouter()

/** 仅在看「他人」主页时展示侧栏 ABOUT；看自己的主页或顶栏无「关于」入口，避免与上方大标题重复 */
const showVisitorAboutCard = computed(() => {
    const pageUser = String(route.params.username || '')
    if (!pageUser) return false
    const me = store.state.user?.username ? String(store.state.user.username) : ''
    if (!me) return true
    return me !== pageUser
})

const profile = reactive({
    username: '',
    nickname: '',
    avatar: '',
    bio: '',
    website: '',
    githubHome: '',
    giteeHome: '',
    csdnHome: '',
    zhihuHome: '',
    publicArticleCount: 0,
})

const articles = ref([])
const current = ref(1)
const total = ref(0)
const size = ref(10)
const pages = ref(0)

const syncProfile = (data = {}) => {
    profile.username = data.username || ''
    profile.nickname = data.nickname || data.username || ''
    profile.avatar = data.avatar || ''
    profile.bio = data.bio || ''
    profile.website = data.website || ''
    profile.githubHome = data.githubHome || ''
    profile.giteeHome = data.giteeHome || ''
    profile.csdnHome = data.csdnHome || ''
    profile.zhihuHome = data.zhihuHome || ''
    profile.publicArticleCount = data.publicArticleCount || 0
}

const getProfile = async () => {
    const username = route.params.username
    const res = await getPublicUserDetail(username)
    if (res.success) syncProfile(res.data)
}

const getArticles = async (pageNo) => {
    const res = await getIndexArticles({
        current: pageNo,
        size: size.value,
        username: route.params.username,
    })
    if (res.success) {
        articles.value = res.data || []
        current.value = res.current
        total.value = res.total
        size.value = res.size
        pages.value = res.pages
    }
}

const goArticleDetail = (articleId) => router.push({ path: '/article/detail', query: { articleId } })
const goCatagoryArticleListPage = (id, name) => router.push({ path: '/category/list', query: { id, name } })
const goTagArticleListPage = (id, name) => router.push({ path: '/tag/list', query: { id, name } })

watch(
    () => route.params.username,
    async () => {
        current.value = 1
        await getProfile()
        await getArticles(1)
    },
    { immediate: true }
)
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

.article-tag-row {
    display: flex;
    flex-wrap: wrap;
    gap: 0.65rem;
}

.article-title {
    font-size: 1.45rem;
    line-height: 1.35;
    letter-spacing: -0.03em;
    color: #172033;
    font-weight: 800;
}

.article-desc {
    color: #64748b;
    line-height: 1.85;
    display: -webkit-box;
    overflow: hidden;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
}
</style>
