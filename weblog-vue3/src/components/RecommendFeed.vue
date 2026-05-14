<template>
  <div class="recommend-feed">
    <div v-if="articles && articles.length > 0" class="article-grid">
      <article v-for="article in articles" :key="article.id" class="front-card front-card-solid front-card-hover recommend-card">
        <span class="recommend-badge">推荐</span>
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
            {{ article.author.nickname || article.author.username }}
          </div>
          <div class="article-meta-row">
            <span v-if="article.createTime" class="article-date">{{ article.createTime }}</span>
            <span v-if="article.category && article.category.name" class="front-chip front-chip-primary">
              {{ article.category.name }}
            </span>
          </div>
          <div v-if="article.tags && article.tags.length" class="sidebar-chip-row">
            <span v-for="tag in article.tags" :key="tag.id" class="front-chip front-chip-success">
              {{ tag.name }}
            </span>
          </div>
        </div>
      </article>
    </div>
    <div class="recommend-refresh">
      <button class="refresh-btn" @click="emit('refresh')">换一批</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  articles: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['refresh'])

const router = useRouter()

const goArticleDetail = (articleId) => {
  router.push({ path: '/article/detail', query: { articleId } })
}
</script>

<style scoped>
.recommend-feed {
  width: 100%;
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}

.recommend-card {
  position: relative;
}

.recommend-badge {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  z-index: 2;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: #fff;
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.05em;
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

.article-meta-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.article-date {
  color: #94a3b8;
  font-size: 0.85rem;
}

.sidebar-chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.7rem;
}

.recommend-refresh {
  display: flex;
  justify-content: center;
  margin-top: 1.5rem;
}

.refresh-btn {
  padding: 0.55rem 1.5rem;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 999px;
  background: rgba(248, 250, 252, 0.92);
  color: #64748b;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}

.refresh-btn:hover {
  color: #172033;
  border-color: rgba(148, 163, 184, 0.4);
  background: #fff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
}
</style>
