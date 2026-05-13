<template>
    <Header></Header>

    <div class="front-container front-shell">
        <section class="front-page-header front-card">
            <div class="front-card-body front-page-header-inner">
                <span class="front-page-kicker">My Favorites</span>
                <h1 class="front-page-title">我的收藏</h1>
                <p class="front-page-description">你收藏的公开文章都会出现在这里。</p>
            </div>
        </section>

        <div class="front-grid front-grid--full-width-main">
            <main class="front-main-column">
                <div v-if="loading" class="front-card front-empty">
                    <p>加载中...</p>
                </div>

                <div v-else-if="list.length === 0" class="front-card front-empty">
                    <p>暂无收藏</p>
                </div>

                <div v-else class="favorite-grid">
                    <div v-for="item in list" :key="item.articleId" class="front-card front-card-solid front-card-hover favorite-item" @click="goDetail(item.articleId)">
                        <img v-if="item.titleImage" :src="item.titleImage" class="cover" />
                        <div class="info">
                            <h3>{{ item.title }}</h3>
                            <p>{{ item.description }}</p>
                            <div class="meta">
                                <span>阅读 {{ item.readNum }}</span>
                                <span>点赞 {{ item.likeNum }}</span>
                                <span>{{ item.createTime }}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <nav v-if="total > size" aria-label="收藏分页" class="front-pagination">
                    <el-pagination
                        background
                        layout="prev, pager, next"
                        :total="total"
                        :page-size="size"
                        v-model:current-page="current"
                        @current-change="handlePageChange"
                    />
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
import { ref, onMounted } from 'vue'
import { getFavoriteList } from '@/api/frontend/article'

const router = useRouter()

const list = ref([])
const loading = ref(true)
const current = ref(1)
const size = ref(10)
const total = ref(0)

const loadFavorites = async (pageNo) => {
    loading.value = true
    try {
        const res = await getFavoriteList({ current: pageNo, size: size.value })
        if (res.success) {
            const pageData = res.data || {}
            list.value = pageData.records || []
            current.value = pageData.current || pageNo
            total.value = pageData.total || 0
            size.value = pageData.size || 10
        }
    } catch (err) {
        console.error(err)
    } finally {
        loading.value = false
    }
}

const handlePageChange = (pageNo) => {
    loadFavorites(pageNo)
}

const goDetail = (articleId) => {
    router.push({ path: '/article/detail', query: { articleId } })
}

onMounted(() => {
    loadFavorites(1)
})
</script>

<style scoped>
.favorite-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 1.5rem;
}

.favorite-item {
    display: flex;
    flex-direction: column;
    overflow: hidden;
    cursor: pointer;
}

.favorite-item .cover {
    width: 100%;
    height: 12rem;
    object-fit: cover;
}

.favorite-item .info {
    display: flex;
    flex-direction: column;
    gap: 0.65rem;
    padding: 1.2rem 1.3rem;
}

.favorite-item .info h3 {
    font-size: 1.15rem;
    font-weight: 800;
    color: #172033;
    line-height: 1.4;
    display: -webkit-box;
    overflow: hidden;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.favorite-item .info p {
    color: #64748b;
    font-size: 0.9rem;
    line-height: 1.7;
    display: -webkit-box;
    overflow: hidden;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.favorite-item .meta {
    display: flex;
    flex-wrap: wrap;
    gap: 0.8rem;
    color: #94a3b8;
    font-size: 0.8rem;
    font-weight: 600;
}
</style>