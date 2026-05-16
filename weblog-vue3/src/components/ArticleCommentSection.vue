<template>
  <section class="front-card front-card-solid comment-section">
    <div class="front-card-body">
      <div class="front-section-title">Comments</div>
      <h2 class="comment-title">评论</h2>
      <p class="comment-subtitle">欢迎交流，评论审核通过后展示</p>

      <div class="comment-form-wrapper">
        <div class="comment-form-fields">
          <div class="comment-form-row">
            <div class="comment-form-field">
              <input
                v-model="form.nickname"
                type="text"
                placeholder="请输入昵称"
                maxlength="64"
                class="comment-input"
              />
            </div>
            <div class="comment-form-field">
              <input
                v-model="form.email"
                type="text"
                placeholder="请输入邮箱（选填）"
                maxlength="128"
                class="comment-input"
              />
            </div>
            <div class="comment-form-field">
              <input
                v-model="form.website"
                type="text"
                placeholder="请输入个人网站（选填）"
                maxlength="255"
                class="comment-input"
              />
            </div>
          </div>
          <div class="comment-form-field">
            <textarea
              v-model="form.content"
              :placeholder="replyTarget ? `回复 @${replyTarget.nickname}` : '写下你的评论...'"
              maxlength="500"
              rows="4"
              class="comment-textarea"
            ></textarea>
          </div>
          <div class="comment-form-actions">
            <span v-if="replyTarget" class="comment-replying-hint">
              回复 @{{ replyTarget.nickname }}
              <button type="button" class="comment-cancel-reply" @click="cancelReply">取消回复</button>
            </span>
            <button
              type="button"
              class="comment-submit-btn"
              :disabled="submitLoading"
              @click="handleSubmit"
            >
              {{ submitLoading ? '提交中...' : (replyTarget ? '提交回复' : '发表评论') }}
            </button>
          </div>
        </div>
      </div>

      <div v-if="loading" class="comment-loading">
        <div class="comment-skeleton" v-for="n in 3" :key="n">
          <div class="skeleton-line skeleton-name"></div>
          <div class="skeleton-line skeleton-content"></div>
          <div class="skeleton-line skeleton-content short"></div>
        </div>
      </div>

      <div v-else-if="comments.length === 0" class="comment-empty">
        <p>暂无评论，欢迎留下第一条评论</p>
      </div>

      <div v-else class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-avatar-wrapper">
            <div class="comment-avatar">{{ (comment.nickname || '?')[0] }}</div>
          </div>
          <div class="comment-body">
            <div class="comment-header">
              <span class="comment-nickname">
                <a v-if="comment.website" :href="comment.website" target="_blank" rel="noopener noreferrer">{{ comment.nickname }}</a>
                <span v-else>{{ comment.nickname }}</span>
              </span>
              <span class="comment-time">{{ comment.createTime }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <button type="button" class="comment-reply-btn" @click="startReply(comment)">回复</button>
            </div>

            <div v-if="replyTarget && replyTarget.id === comment.id" class="comment-reply-form">
              <div class="comment-form-fields">
                <div class="comment-form-row">
                  <div class="comment-form-field">
                    <input
                      v-model="form.nickname"
                      type="text"
                      placeholder="请输入昵称"
                      maxlength="64"
                      class="comment-input"
                    />
                  </div>
                  <div class="comment-form-field">
                    <input
                      v-model="form.email"
                      type="text"
                      placeholder="请输入邮箱（选填）"
                      maxlength="128"
                      class="comment-input"
                    />
                  </div>
                  <div class="comment-form-field">
                    <input
                      v-model="form.website"
                      type="text"
                      placeholder="请输入个人网站（选填）"
                      maxlength="255"
                      class="comment-input"
                    />
                  </div>
                </div>
                <div class="comment-form-field">
                  <textarea
                    v-model="form.content"
                    :placeholder="`回复 @${replyTarget.nickname}`"
                    maxlength="500"
                    rows="3"
                    class="comment-textarea"
                  ></textarea>
                </div>
                <div class="comment-form-actions">
                  <button type="button" class="comment-cancel-submit" @click="cancelReply">取消</button>
                  <button
                    type="button"
                    class="comment-submit-btn"
                    :disabled="submitLoading"
                    @click="handleSubmit"
                  >
                    {{ submitLoading ? '提交中...' : '提交回复' }}
                  </button>
                </div>
              </div>
            </div>

            <div v-if="comment.children && comment.children.length" class="comment-replies">
              <div v-for="reply in comment.children" :key="reply.id" class="comment-reply-item">
                <div class="comment-avatar-wrapper">
                  <div class="comment-avatar comment-avatar-sm">{{ (reply.nickname || '?')[0] }}</div>
                </div>
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="comment-nickname">
                      <a v-if="reply.website" :href="reply.website" target="_blank" rel="noopener noreferrer">{{ reply.nickname }}</a>
                      <span v-else>{{ reply.nickname }}</span>
                    </span>
                    <span v-if="reply.replyToNickname" class="comment-reply-to">回复 @{{ reply.replyToNickname }}</span>
                    <span class="comment-time">{{ reply.createTime }}</span>
                  </div>
                  <div class="comment-content">{{ reply.content }}</div>
                  <div class="comment-actions">
                    <button type="button" class="comment-reply-btn" @click="startReply(reply)">回复</button>
                  </div>

                  <div v-if="replyTarget && replyTarget.id === reply.id" class="comment-reply-form">
                    <div class="comment-form-fields">
                      <div class="comment-form-row">
                        <div class="comment-form-field">
                          <input
                            v-model="form.nickname"
                            type="text"
                            placeholder="请输入昵称"
                            maxlength="64"
                            class="comment-input"
                          />
                        </div>
                        <div class="comment-form-field">
                          <input
                            v-model="form.email"
                            type="text"
                            placeholder="请输入邮箱（选填）"
                            maxlength="128"
                            class="comment-input"
                          />
                        </div>
                        <div class="comment-form-field">
                          <input
                            v-model="form.website"
                            type="text"
                            placeholder="请输入个人网站（选填）"
                            maxlength="255"
                            class="comment-input"
                          />
                        </div>
                      </div>
                      <div class="comment-form-field">
                        <textarea
                          v-model="form.content"
                          :placeholder="`回复 @${replyTarget.nickname}`"
                          maxlength="500"
                          rows="3"
                          class="comment-textarea"
                        ></textarea>
                      </div>
                      <div class="comment-form-actions">
                        <button type="button" class="comment-cancel-submit" @click="cancelReply">取消</button>
                        <button
                          type="button"
                          class="comment-submit-btn"
                          :disabled="submitLoading"
                          @click="handleSubmit"
                        >
                          {{ submitLoading ? '提交中...' : '提交回复' }}
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="total > size" class="comment-pagination">
        <div class="front-pagination">
          <button
            class="front-page-btn"
            :class="{ 'is-disabled': current <= 1 }"
            :disabled="current <= 1"
            @click="goToPage(current - 1)"
          >上一页</button>
          <span class="comment-page-info">{{ current }} / {{ pages }}</span>
          <button
            class="front-page-btn"
            :class="{ 'is-disabled': current >= pages }"
            :disabled="current >= pages"
            @click="goToPage(current + 1)"
          >下一页</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { getCommentList, publishComment } from '@/api/frontend/comment'
import { getToken } from '@/composables/auth'
import { showMessage } from '@/composables/util'

const props = defineProps({
  articleId: {
    type: Number,
    required: true
  }
})

const comments = ref([])
const loading = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const pages = ref(0)

const form = reactive({
  nickname: '',
  email: '',
  website: '',
  content: ''
})

const replyTarget = ref(null)
const submitLoading = ref(false)

function fetchComments(page) {
  if (!props.articleId) return
  loading.value = true
  getCommentList({
    articleId: props.articleId,
    current: page || current.value,
    size: size.value
  }).then((res) => {
    if (res && res.success) {
      comments.value = res.data || []
      total.value = res.total || 0
      current.value = res.current || 1
      pages.value = res.pages || 0
    } else {
      showMessage(res?.message || '评论加载失败，请刷新重试', 'error')
    }
  }).catch(() => {
    showMessage('评论加载失败，请刷新重试', 'error')
  }).finally(() => {
    loading.value = false
  })
}

function validateForm() {
  const nickname = (form.nickname || '').trim()
  if (!nickname) {
    showMessage('请输入昵称', 'warning')
    return false
  }
  const content = (form.content || '').trim()
  if (!content) {
    showMessage('请输入评论内容', 'warning')
    return false
  }
  if (content.length < 2) {
    showMessage('评论内容至少需要2个字符', 'warning')
    return false
  }
  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim())) {
    showMessage('邮箱格式不正确', 'warning')
    return false
  }
  return true
}

function handleSubmit() {
  if (!validateForm()) return

  const data = {
    articleId: props.articleId,
    nickname: form.nickname.trim(),
    email: form.email.trim() || undefined,
    website: form.website.trim() || undefined,
    content: form.content.trim()
  }

  if (replyTarget.value) {
    const target = replyTarget.value
    data.parentId = target.parentId ? target.rootId : target.id
    data.replyToCommentId = target.id
  }

  submitLoading.value = true
  publishComment(data).then((res) => {
    if (res && res.success) {
      showMessage(res.data?.message || '评论已提交，审核通过后展示')
      form.content = ''
      replyTarget.value = null
    } else {
      showMessage(res?.message || '评论提交失败，请稍后重试', 'error')
    }
  }).catch(() => {
    showMessage('评论提交失败，请稍后重试', 'error')
  }).finally(() => {
    submitLoading.value = false
  })
}

function startReply(comment) {
  replyTarget.value = comment
  form.content = ''
}

function cancelReply() {
  replyTarget.value = null
  form.content = ''
}

function goToPage(page) {
  current.value = page
  fetchComments(page)
  const section = document.querySelector('.comment-section')
  if (section) {
    section.scrollIntoView({ behavior: 'smooth' })
  }
}

watch(() => props.articleId, (newVal) => {
  if (newVal) {
    current.value = 1
    fetchComments(1)
  }
}, { immediate: false })

onMounted(() => {
  if (props.articleId) {
    fetchComments(1)
  }
})
</script>

<style scoped>
.comment-section {
  margin-top: 1.5rem;
}

.comment-title {
  font-size: 1.5rem;
  font-weight: 800;
  color: #172033;
  margin-bottom: 0.35rem;
}

.comment-subtitle {
  color: #94a3b8;
  font-size: 0.88rem;
  margin-bottom: 1.5rem;
}

.comment-form-wrapper {
  margin-bottom: 1.5rem;
  padding: 1.25rem;
  border-radius: 16px;
  background: rgba(248, 250, 252, 0.7);
  border: 1px solid rgba(148, 163, 184, 0.14);
}

.comment-form-fields {
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.comment-form-row {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0.75rem;
}

@media (min-width: 640px) {
  .comment-form-row {
    grid-template-columns: repeat(3, 1fr);
  }
}

.comment-input {
  width: 100%;
  padding: 0.65rem 0.85rem;
  border: 1px solid rgba(148, 163, 184, 0.25);
  border-radius: 10px;
  font-size: 0.9rem;
  color: #334155;
  background: #fff;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.comment-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.comment-input::placeholder {
  color: #cbd5e1;
}

.comment-textarea {
  width: 100%;
  padding: 0.7rem 0.85rem;
  border: 1px solid rgba(148, 163, 184, 0.25);
  border-radius: 10px;
  font-size: 0.9rem;
  color: #334155;
  background: #fff;
  resize: vertical;
  outline: none;
  font-family: inherit;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.comment-textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.comment-textarea::placeholder {
  color: #cbd5e1;
}

.comment-form-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.75rem;
}

.comment-replying-hint {
  font-size: 0.85rem;
  color: #64748b;
}

.comment-cancel-reply {
  border: none;
  background: none;
  color: #ef4444;
  cursor: pointer;
  font-size: 0.82rem;
  margin-left: 0.5rem;
}

.comment-cancel-submit {
  border: 1px solid rgba(148, 163, 184, 0.25);
  background: #fff;
  color: #64748b;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.85rem;
  transition: background 0.2s;
}

.comment-cancel-submit:hover {
  background: rgba(248, 250, 252, 0.9);
}

.comment-loading {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.comment-skeleton {
  padding: 1rem;
  border-radius: 12px;
  background: rgba(248, 250, 252, 0.55);
}

.skeleton-line {
  height: 0.75rem;
  border-radius: 6px;
  background: linear-gradient(90deg, rgba(148, 163, 184, 0.12), rgba(148, 163, 184, 0.24), rgba(148, 163, 184, 0.12));
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  margin-bottom: 0.55rem;
}

.skeleton-name {
  width: 28%;
}

.skeleton-content {
  width: 100%;
}

.skeleton-content.short {
  width: 65%;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.comment-empty {
  text-align: center;
  padding: 2.5rem 1rem;
  color: #94a3b8;
  font-size: 0.95rem;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.comment-item {
  display: flex;
  gap: 0.85rem;
  padding: 1.15rem;
  border-radius: 14px;
  background: rgba(248, 250, 252, 0.55);
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.comment-reply-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.9rem;
  margin-top: 0.65rem;
  border-radius: 12px;
  background: rgba(248, 250, 252, 0.45);
  border: 1px solid rgba(148, 163, 184, 0.07);
}

.comment-avatar-wrapper {
  flex-shrink: 0;
}

.comment-avatar {
  width: 2.4rem;
  height: 2.4rem;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.95rem;
}

.comment-avatar-sm {
  width: 2rem;
  height: 2rem;
  font-size: 0.82rem;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 0.55rem;
  flex-wrap: wrap;
  margin-bottom: 0.4rem;
}

.comment-nickname {
  font-weight: 700;
  color: #172033;
  font-size: 0.92rem;
}

.comment-nickname a {
  color: #2563eb;
  text-decoration: none;
}

.comment-nickname a:hover {
  text-decoration: underline;
}

.comment-reply-to {
  color: #64748b;
  font-size: 0.8rem;
}

.comment-time {
  color: #94a3b8;
  font-size: 0.78rem;
}

.comment-content {
  color: #475569;
  font-size: 0.93rem;
  line-height: 1.7;
  word-break: break-word;
}

.comment-actions {
  margin-top: 0.5rem;
}

.comment-reply-btn {
  border: none;
  background: none;
  color: #64748b;
  font-size: 0.82rem;
  cursor: pointer;
  padding: 0;
  transition: color 0.2s;
}

.comment-reply-btn:hover {
  color: #3b82f6;
}

.comment-replies {
  margin-top: 0.5rem;
}

.comment-reply-form {
  margin-top: 0.75rem;
  padding: 1rem;
  border-radius: 12px;
  background: rgba(248, 250, 252, 0.75);
  border: 1px solid rgba(148, 163, 184, 0.14);
}

.comment-pagination {
  margin-top: 1.5rem;
  display: flex;
  justify-content: center;
}

.comment-submit-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1.25rem;
  border: 0;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
  cursor: pointer;
  transition: opacity 0.2s, transform 0.2s;
}

.comment-submit-btn:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
}

.comment-submit-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.comment-page-info {
  display: inline-flex;
  align-items: center;
  padding: 0 0.75rem;
  color: #64748b;
  font-size: 0.88rem;
  font-weight: 600;
}
</style>