<template>
    <Header></Header>

    <div class="front-container front-shell">
        <section class="front-page-header front-card">
            <div class="front-card-body front-page-header-inner">
                <span class="front-page-kicker">Tags</span>
                <h1 class="front-page-title">{{ isHotScope ? '热门标签 TOP10' : '按标签探索内容主题' }}</h1>
                <p class="front-page-description">{{ isHotScope ? '按公开文章数量展示前 10 个热门标签，快速进入高频话题。' : '通过更细粒度的标签快速定位文章话题，适合查找具体技术点与记录片段。' }}</p>
                <div class="front-badge-row">
                    <span class="front-badge"><strong>{{ tags.length }}</strong> 个标签</span>
                </div>
            </div>
        </section>

        <div class="front-grid front-grid--full-width-main">
            <main class="front-main-column">
                <section class="front-card front-card-solid">
                    <div class="front-card-body">
                        <div class="front-section-title">Tag Cloud</div>
                        <h2 class="section-title">标签云</h2>
                        <div class="tag-cloud">
                            <span @click="goTagArticleListPage(item.id, item.name)" v-for="item in tags" :key="item.id" class="tag-cloud-item">
                                {{ item.name }}<template v-if="item.articleCount != null"> <span class="tag-stat">（{{ item.articleCount }} 篇）</span></template>
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
import { getTags, getTagsAll } from '@/api/frontend/tag'
import { computed, ref, watch } from 'vue'

const router = useRouter()
const route = useRoute()
const isHotScope = computed(() => route.query.scope === 'hot')

const goTagArticleListPage = (id, name) => {
    router.push({path: '/tag/list', query: {id: id, name: name}})
}

const tags = ref([])

const loadTags = () => {
    const request = isHotScope.value ? getTags() : getTagsAll()
    request.then((e) => {
        if (e.success) {
            tags.value = e.data || []
        }
    })
}

watch(
    () => route.query.scope,
    loadTags,
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

.tag-cloud {
    display: flex;
    flex-wrap: wrap;
    gap: 0.8rem;
}

.tag-cloud-item {
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

.tag-cloud-item:hover {
    transform: translateY(-2px);
    background: rgba(16, 185, 129, 0.18);
}

.tag-stat {
    color: #065f46;
    font-size: 0.85em;
    font-weight: 600;
    opacity: 0.9;
}
</style>