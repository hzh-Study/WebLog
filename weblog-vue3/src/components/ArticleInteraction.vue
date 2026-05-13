<template>
  <div class="article-interaction-bar">
    <button class="interaction-btn" :class="{ active: localLiked }" @click="handleLike">
      <svg class="icon" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 0 0 0 6.364L12 20.364l7.682-7.682a4.5 4.5 0 0 0-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 0 0-6.364 0Z" />
      </svg>
      <span>点赞 {{ localLikeNum }}</span>
    </button>

    <button class="interaction-btn" :class="{ active: localFavorited }" @click="handleFavorite">
      <svg class="icon" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M11.48 3.499a.562.562 0 0 1 1.04 0l2.125 5.111a.563.563 0 0 0 .475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 0 0-.182.557l1.285 5.385a.562.562 0 0 1-.84.61l-4.725-2.885a.562.562 0 0 0-.586 0L6.982 20.54a.562.562 0 0 1-.84-.61l1.285-5.386a.562.562 0 0 0-.182-.557l-4.204-3.602a.562.562 0 0 1 .321-.988l5.518-.442a.563.563 0 0 0 .475-.345L11.48 3.5Z" />
      </svg>
      <span>收藏 {{ localFavoriteNum }}</span>
    </button>

    <el-popover trigger="click" placement="top" :width="200">
      <div class="share-options">
        <el-button link @click="copyLink">复制链接</el-button>
        <el-button link @click="shareWeibo">分享到微博</el-button>
      </div>
      <template #reference>
        <button class="interaction-btn">
          <svg class="icon" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M7.217 10.907a2.25 2.25 0 1 0 0 2.186m0-2.186c.18.324.283.696.283 1.093s-.103.77-.283 1.093m0-2.186l9.566-5.314m-9.566 7.5 9.566 5.314m0 0a2.25 2.25 0 1 0 3.935 2.186 2.25 2.25 0 0 0-3.935-2.186Zm0-12.814a2.25 2.25 0 1 0 3.933-2.185 2.25 2.25 0 0 0-3.933 2.185Z" />
          </svg>
          <span>分享</span>
        </button>
      </template>
    </el-popover>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { likeArticle, favoriteArticle } from '@/api/frontend/article'
import { ElMessage } from 'element-plus'

const props = defineProps({
  articleId: {
    type: Number,
    required: true,
  },
  likeNum: {
    type: Number,
    default: 0,
  },
  favoriteNum: {
    type: Number,
    default: 0,
  },
  liked: {
    type: Boolean,
    default: false,
  },
  favorited: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['like-toggled', 'favorite-toggled'])

const localLiked = ref(props.liked)
const localLikeNum = ref(props.likeNum)
const localFavorited = ref(props.favorited)
const localFavoriteNum = ref(props.favoriteNum)

const handleLike = async () => {
  try {
    const res = await likeArticle(props.articleId)
    if (res.success) {
      localLiked.value = !localLiked.value
      localLikeNum.value += localLiked.value ? 1 : -1
      emit('like-toggled', localLiked.value)
    }
  } catch (err) {
    console.error(err)
  }
}

const handleFavorite = async () => {
  try {
    const res = await favoriteArticle(props.articleId)
    if (res.success) {
      localFavorited.value = !localFavorited.value
      localFavoriteNum.value += localFavorited.value ? 1 : -1
      emit('favorite-toggled', localFavorited.value)
      ElMessage.success(localFavorited.value ? '已收藏' : '已取消收藏')
    }
  } catch (err) {
    console.error(err)
  }
}

const copyLink = () => {
  navigator.clipboard.writeText(window.location.href)
  ElMessage.success('链接已复制')
}

const shareWeibo = () => {
  window.open(`https://service.weibo.com/share/share.php?url=${encodeURIComponent(window.location.href)}&title=${encodeURIComponent(document.title)}`)
}
</script>

<style scoped>
.article-interaction-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  padding: 1rem 0;
}

.interaction-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.55rem 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 999px;
  background: rgba(248, 250, 252, 0.92);
  color: #64748b;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}

.interaction-btn:hover {
  color: #172033;
  border-color: rgba(148, 163, 184, 0.4);
  background: #fff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
}

.interaction-btn.active {
  color: #2563eb;
  border-color: rgba(37, 99, 235, 0.3);
  background: rgba(59, 130, 246, 0.08);
}

.interaction-btn .icon {
  width: 1.15rem;
  height: 1.15rem;
  flex-shrink: 0;
}

.share-options {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}
</style>