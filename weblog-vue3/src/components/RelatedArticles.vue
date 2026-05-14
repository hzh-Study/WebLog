<template>
  <div class="related-articles">
    <div class="related-header">
      <h3 class="related-title">相关推荐</h3>
      <button class="refresh-btn" @click="fetchRelated">换一批</button>
    </div>

    <div v-if="loading" class="related-skeleton">
      <div v-for="i in 3" :key="i" class="skeleton-card">
        <div class="skeleton-img"></div>
        <div class="skeleton-text"></div>
        <div class="skeleton-text short"></div>
      </div>
    </div>

    <div v-else-if="list && list.length > 0" class="related-list">
      <article v-for="article in list" :key="article.id" class="related-card" @click="goArticleDetail(article.id)">
        <img v-if="article.titleImage" class="related-cover" :src="article.titleImage" :alt="article.title">
        <div v-else class="related-cover related-cover-fallback"></div>
        <div class="related-info">
          <h4 class="related-article-title">{{ article.title }}</h4>
          <div class="related-meta">
            <span v-if="article.author">{{ article.author.nickname || article.author.username }}</span>
            <span v-if="article.createTime">{{ article.createTime }}</span>
          </div>
        </div>
      </article>
    </div>

    <div v-else class="related-empty">
      <p>暂无相关推荐</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRelatedArticles } from '@/api/frontend/recommend'

const props = defineProps({
  articleId: {
    type: Number,
    required: true
  }
})

const router = useRouter()
const list = ref([])
const loading = ref(false)

const fetchRelated = async () => {
  loading.value = true
  try {
    const res = await getRelatedArticles({ articleId: props.articleId })
    if (res.success) {
      list.value = res.data || []
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const goArticleDetail = (articleId) => {
  router.push({ path: '/article/detail', query: { articleId } })
}

onMounted(() => {
  fetchRelated()
})
</script>

<style scoped>
.related-articles {
  width: 100%;
}

.related-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.related-title {
  font-size: 1.25rem;
  font-weight: 800;
  color: #172033;
}

.refresh-btn {
  padding: 0.35rem 1rem;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 999px;
  background: rgba(248, 250, 252, 0.92);
  color: #64748b;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}

.refresh-btn:hover {
  color: #172033;
  border-color: rgba(148, 163, 184, 0.4);
  background: #fff;
}

.related-list {
  display: flex;
  gap: 1rem;
  overflow-x: auto;
  padding-bottom: 0.5rem;
}

.related-card {
  flex: 0 0 220px;
  border-radius: 0.9rem;
  border: 1px solid rgba(148, 163, 184, 0.15);
  background: #fff;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s ease;
}

.related-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
}

.related-cover {
  width: 100%;
  height: 8rem;
  object-fit: cover;
}

.related-cover-fallback {
  background: linear-gradient(135deg, #2563eb, #4f46e5);
}

.related-info {
  padding: 0.75rem;
}

.related-article-title {
  font-size: 0.9rem;
  font-weight: 700;
  color: #172033;
  line-height: 1.4;
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.related-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  color: #94a3b8;
  font-size: 0.75rem;
}

.related-empty {
  text-align: center;
  padding: 2rem 0;
  color: #94a3b8;
}

.related-skeleton {
  display: flex;
  gap: 1rem;
}

.skeleton-card {
  flex: 0 0 220px;
  border-radius: 0.9rem;
  overflow: hidden;
  background: #f8fafc;
}

.skeleton-img {
  width: 100%;
  height: 8rem;
  background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-text {
  height: 0.85rem;
  margin: 0.75rem;
  border-radius: 0.25rem;
  background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-text.short {
  width: 60%;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
