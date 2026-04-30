<template>
    <div class="front-card author-card">
        <div class="author-top">
            <div class="author-cover"></div>
            <div class="author-avatar-wrap">
                <img v-if="avatar" class="author-avatar" :src="avatar" alt="作者头像">
                <div v-else class="author-avatar author-avatar-fallback">{{ authorInitial }}</div>
            </div>
        </div>

        <div class="author-body">
            <div class="front-section-title">About</div>
            <h3 class="author-name">{{ displayName }}</h3>
            <p v-if="username && username !== displayName" class="author-handle">@{{ username }}</p>
            <p class="author-intro">{{ introduction }}</p>

            <div class="author-stats">
                <div class="author-stat">
                    <span class="author-stat-label">身份</span>
                    <strong>{{ username ? '创作者' : '平台' }}</strong>
                </div>
                <div class="author-stat">
                    <span class="author-stat-label">公开内容</span>
                    <strong>{{ publicArticleCountText }}</strong>
                </div>
            </div>

            <div v-if="socialLinks.length" class="author-socials">
                <a v-for="item in socialLinks" :key="item.label" :href="item.href" target="_blank" class="author-social-link" :title="item.label">
                    <span>{{ item.short }}</span>
                </a>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'

const props = defineProps({
    profile: {
        type: Object,
        default: null,
    },
})

const store = useStore()

const currentProfile = computed(() => {
    if (props.profile) return props.profile
    const setting = store.state.setting || {}
    return {
        nickname: setting.author || 'WeBlog',
        avatar: setting.avatar || '',
        bio: setting.introduction || '欢迎来到多账号内容平台。',
        githubHome: setting.githubHome,
        csdnHome: setting.csdnHome,
        giteeHome: setting.giteeHome,
        zhihuHome: setting.zhihuHome,
    }
})

const avatar = computed(() => currentProfile.value.avatar || '')
const displayName = computed(() => currentProfile.value.nickname || currentProfile.value.author || currentProfile.value.username || 'WeBlog')
const username = computed(() => currentProfile.value.username || '')
const authorInitial = computed(() => displayName.value.slice(0, 1).toUpperCase())
const introduction = computed(() => currentProfile.value.bio || currentProfile.value.introduction || '这个用户还没有填写个人简介。')
const publicArticleCountText = computed(() => {
    const count = currentProfile.value.publicArticleCount
    return count == null ? '--' : `${count} 篇`
})

const socialLinks = computed(() => {
    const setting = currentProfile.value || {}
    return [
        { label: '网站', short: 'WB', href: setting.website },
        { label: 'GitHub', short: 'GH', href: setting.githubHome },
        { label: 'CSDN', short: 'CS', href: setting.csdnHome },
        { label: 'Gitee', short: 'GT', href: setting.giteeHome },
        { label: '知乎', short: 'ZH', href: setting.zhihuHome },
    ].filter(item => item.href)
})
</script>

<style scoped>
.author-card {
    overflow: hidden;
}

.author-top {
    position: relative;
    min-height: 8.5rem;
}

.author-cover {
    height: 7.25rem;
    background:
        radial-gradient(circle at top left, rgba(255, 255, 255, 0.45), transparent 40%),
        linear-gradient(135deg, #2563eb 0%, #4f46e5 52%, #0f172a 100%);
}

.author-avatar-wrap {
    position: absolute;
    left: 1.5rem;
    bottom: -1.8rem;
    display: inline-flex;
    padding: 0.28rem;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.92);
    box-shadow: 0 18px 28px rgba(15, 23, 42, 0.16);
}

.author-avatar {
    width: 4.85rem;
    height: 4.85rem;
    border-radius: 999px;
    object-fit: cover;
}

.author-avatar-fallback {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #dbeafe, #bfdbfe);
    color: #1d4ed8;
    font-size: 1.45rem;
    font-weight: 800;
}

.author-body {
    padding: 2.75rem 1.5rem 1.5rem;
}

.author-name {
    font-size: 1.45rem;
    font-weight: 800;
    color: #172033;
    letter-spacing: -0.03em;
}

.author-handle {
    margin-top: 0.25rem;
    color: #94a3b8;
    font-size: 0.9rem;
    font-weight: 700;
}

.author-intro {
    margin-top: 0.7rem;
    color: #64748b;
    line-height: 1.8;
    font-size: 0.95rem;
}

.author-stats {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0.9rem;
    margin-top: 1.25rem;
}

.author-stat {
    padding: 0.95rem 1rem;
    border-radius: 18px;
    background: rgba(248, 250, 252, 0.95);
    border: 1px solid rgba(148, 163, 184, 0.14);
}

.author-stat-label {
    display: block;
    color: #94a3b8;
    font-size: 0.74rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.12em;
}

.author-stat strong {
    display: block;
    margin-top: 0.35rem;
    color: #172033;
    font-size: 1rem;
    font-weight: 800;
}

.author-socials {
    display: flex;
    flex-wrap: wrap;
    gap: 0.7rem;
    margin-top: 1.25rem;
}

.author-social-link {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 3rem;
    padding: 0.8rem 0.95rem;
    border-radius: 999px;
    background: rgba(59, 130, 246, 0.08);
    color: #1d4ed8;
    font-weight: 800;
    letter-spacing: 0.04em;
}

.author-social-link:hover {
    background: rgba(59, 130, 246, 0.14);
    transform: translateY(-2px);
}
</style>