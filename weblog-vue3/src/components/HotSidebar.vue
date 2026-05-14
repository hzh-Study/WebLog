<template>
  <div class="hot-sidebar">
    <h3 class="hot-title">热门推荐</h3>
    <ul v-if="list && list.length > 0" class="hot-list">
      <li v-for="(article, index) in list" :key="article.id" class="hot-item" @click="goArticleDetail(article.id)">
        <span class="hot-rank" :class="rankClass(index)">{{ index + 1 }}</span>
        <div class="hot-info">
          <span class="hot-article-title">{{ article.title }}</span>
          <span v-if="article.readNum != null" class="hot-read">{{ article.readNum }} 阅读</span>
        </div>
      </li>
    </ul>
    <div v-else class="hot-empty">
      <p>暂无热门文章</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotArticles } from '@/api/frontend/recommend'

const router = useRouter()
const list = ref([])

const fetchHot = async () => {
  try {
    const res = await getHotArticles({ limit: 10 })
    if (res.success) {
      list.value = res.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

const goArticleDetail = (articleId) => {
  router.push({ path: '/article/detail', query: { articleId } })
}

const rankClass = (index) => {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return ''
}

onMounted(() => {
  fetchHot()
})
</script>

<style scoped>
.hot-sidebar {
  width: 100%;
}

.hot-title {
  font-size: 1.1rem;
  font-weight: 800;
  color: #172033;
  margin-bottom: 1rem;
}

.hot-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.hot-item {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 0.6rem 0.5rem;
  border-radius: 0.6rem;
  cursor: pointer;
  transition: background 0.2s ease;
}

.hot-item:hover {
  background: rgba(59, 130, 246, 0.06);
}

.hot-rank {
  flex-shrink: 0;
  width: 1.5rem;
  height: 1.5rem;
  border-radius: 0.35rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 800;
  color: #94a3b8;
  background: #f1f5f9;
}

.rank-gold {
  color: #fff;
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.rank-silver {
  color: #fff;
  background: linear-gradient(135deg, #94a3b8, #64748b);
}

.rank-bronze {
  color: #fff;
  background: linear-gradient(135deg, #c2855a, #a16207);
}

.hot-info {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  min-width: 0;
}

.hot-article-title {
  font-size: 0.88rem;
  font-weight: 600;
  color: #172033;
  line-height: 1.4;
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.hot-item:hover .hot-article-title {
  color: #1d4ed8;
}

.hot-read {
  font-size: 0.75rem;
  color: #94a3b8;
}

.hot-empty {
  text-align: center;
  padding: 1.5rem 0;
  color: #94a3b8;
}
</style>
