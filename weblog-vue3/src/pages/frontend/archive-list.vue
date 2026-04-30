<template>
    <Header></Header>

    <div class="front-container front-shell">
        <section class="front-page-header front-card">
            <div class="front-card-body front-page-header-inner">
                <span class="front-page-kicker">Archive</span>
                <h1 class="front-page-title">按时间线回看过往内容</h1>
                <p class="front-page-description">通过归档梳理文章发布时间，快速浏览不同阶段的创作与记录。</p>
                <div class="front-badge-row">
                    <span class="front-badge"><strong>{{ total }}</strong> 条归档记录</span>
                </div>
            </div>
        </section>

        <div class="front-grid front-grid--archive">
            <main class="front-main-column">
                <div class="archive-stack">
                    <section v-for="(item, index) in archives" :key="index" class="front-card front-card-solid archive-group">
                        <div class="front-card-body">
                            <div class="archive-month">{{ item.month }}</div>
                            <ol class="archive-list">
                                <li v-for="(item2, index2) in item.articles" :key="index2">
                                    <a @click="goArticleDetail(item2.id)" class="archive-item">
                                        <img v-if="item2.titleImage" class="archive-thumb" :src="item2.titleImage" :alt="item2.title">
                                        <div v-else class="archive-thumb archive-thumb-fallback">WebLog</div>
                                        <div class="archive-copy">
                                            <div class="archive-title">{{ item2.title }}</div>
                                            <div v-if="item2.author" class="archive-author">
                                                <a @click.stop="goUserHome(item2.author.username)" class="front-link">
                                                    {{ item2.author.nickname || item2.author.username }}
                                                </a>
                                            </div>
                                            <span class="front-meta-item archive-time">
                                                <svg class="w-3.5 h-3.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                        d="M5 1v3m5-3v3m5-3v3M1 7h18M5 11h10M2 3h16a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z" />
                                                </svg>
                                                {{ item2.createTime }}
                                            </span>
                                        </div>
                                    </a>
                                </li>
                            </ol>
                        </div>
                    </section>
                </div>

                <nav v-if="total > 0 && pages > 1" aria-label="归档分页" class="front-pagination">
                    <a @click="current > 1 ? getArchiveList(current - 1) : null" class="front-page-btn" :class="{ 'is-disabled': current <= 1 }">
                        <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 6 10">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 1 1 5l4 4" />
                        </svg>
                    </a>
                    <a v-for="page in pages" :key="page" @click="getArchiveList(page)" class="front-page-btn" :class="{ 'is-active': page === current }">
                        {{ page }}
                    </a>
                    <a @click="current < pages ? getArchiveList(current + 1) : null" class="front-page-btn" :class="{ 'is-disabled': current >= pages }">
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
import { useRouter } from 'vue-router'
import { getArchives } from '@/api/frontend/archive'
import { ref } from 'vue'

const router = useRouter()

const goArticleDetail = (articleId) => {
    console.log('跳转详情页' + articleId)
    router.push({ path: '/article/detail', query: { articleId: articleId } })
}

const goUserHome = (username) => {
    router.push(`/u/${username}`)
}

const archives = ref([])
// 当前页码
const current = ref(1)
const total = ref(0)
const size = ref(10)
const pages = ref(0)

// 获取分页数据
function getArchiveList(currentPage) {
    console.log('获取分页数据')
    getArchives({ current: currentPage, size: size.value })
        .then((res) => {
            console.log(res)
            if (res.success == true) {
                archives.value = res.data
                current.value = res.current
                total.value = res.total
                size.value = res.size
                pages.value = res.pages
            }
        })
}
getArchiveList(current.value)

</script>

<style scoped>
/* 去掉侧栏后，宽屏仍用单列全宽，避免主栏只占 2.1/3 格 */
@media (min-width: 960px) {
    .front-grid.front-grid--archive {
        grid-template-columns: minmax(0, 1fr);
    }
}

.archive-stack {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
}

.archive-month {
    font-size: 1.2rem;
    font-weight: 800;
    color: #172033;
    margin-bottom: 1rem;
}

.archive-list {
    display: flex;
    flex-direction: column;
    gap: 0.85rem;
}

.archive-item {
    display: flex;
    gap: 1rem;
    align-items: center;
    padding: 0.9rem;
    border-radius: 18px;
}

.archive-item:hover {
    background: rgba(59, 130, 246, 0.08);
}

.archive-thumb {
    width: 5.25rem;
    height: 3.4rem;
    border-radius: 14px;
    object-fit: cover;
    flex-shrink: 0;
}

.archive-thumb-fallback {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #2563eb, #4f46e5);
    color: #fff;
    font-weight: 800;
}

.archive-copy {
    min-width: 0;
}

.archive-title {
    color: #172033;
    font-weight: 700;
    line-height: 1.6;
}

.archive-time {
    margin-top: 0.45rem;
    color: #94a3b8;
    font-size: 0.83rem;
}

.archive-author {
    margin-top: 0.35rem;
}
</style>