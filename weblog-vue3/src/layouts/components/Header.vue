<template>
    <header class="sticky top-0 z-100 pt-1.5 sm:pt-2">
        <div class="front-container">
            <nav class="front-nav front-card">
                <div class="front-nav-bar">
                    <button type="button" class="front-brand" @click="goTo('/')">
                        <span class="front-brand-icon">N</span>
                        <span class="front-brand-copy">
                            <span class="front-brand-name">NOTICE</span>
                        </span>
                    </button>

                    <ul class="front-nav-menu">
                        <li v-for="item in navItems" :key="item.path">
                            <button type="button" @click="goTo(item.path)" class="front-nav-link" :class="{ 'is-active': isCurrent(item.path) }">
                                {{ item.label }}
                            </button>
                        </li>
                    </ul>

                    <div class="front-nav-actions">
                        <div class="front-search hidden md:flex">
                            <button type="button" class="front-search-icon-btn" aria-label="执行搜索" @click="runSearch">
                                <svg class="w-3.5 h-3.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z" />
                                </svg>
                            </button>
                            <input
                                v-model.trim="searchQuery"
                                type="search"
                                enterkeyhint="search"
                                placeholder="搜索公开文章或创作者…"
                                autocomplete="off"
                                @keyup.enter="runSearch"
                            >
                        </div>

                        <button v-if="!showAvatarEntry" type="button" class="front-login-entry" @click="goTo('/login')">
                            <svg class="w-3.5 h-3.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M10 10a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm-7 8a7 7 0 1 1 14 0H3Z" />
                            </svg>
                            <span>登录</span>
                        </button>

                        <button v-else type="button" class="front-user-trigger" @click="goTo('/admin')" aria-label="进入管理页面">
                            <img v-if="avatar" class="front-user-avatar" :src="avatar" alt="用户头像">
                            <span v-else class="front-user-avatar front-user-avatar-fallback">{{ userInitial }}</span>
                        </button>
                    </div>
                </div>
            </nav>
        </div>

    </header>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import { getToken } from '@/composables/auth'

const store = useStore()
const route = useRoute()
const router = useRouter()

const searchQuery = ref('')
let searchTimer = null
let syncingSearchFromRoute = false

watch(
    () => [route.path, route.query.q],
    () => {
        syncingSearchFromRoute = true
        if (route.path === '/search' && route.query.q != null && String(route.query.q) !== '') {
            searchQuery.value = String(route.query.q)
        } else if (route.path !== '/search') {
            searchQuery.value = ''
        }
        requestAnimationFrame(() => {
            syncingSearchFromRoute = false
        })
    },
    { immediate: true }
)

const runSearch = () => {
    if (searchTimer) {
        clearTimeout(searchTimer)
        searchTimer = null
    }
    const q = searchQuery.value.trim()
    router.push({ path: '/search', query: q ? { q } : {} })
}

watch(searchQuery, () => {
    if (syncingSearchFromRoute) return
    if (searchTimer) clearTimeout(searchTimer)

    searchTimer = setTimeout(() => {
        const q = searchQuery.value.trim()
        if (!q && route.path !== '/search') return
        router.push({ path: '/search', query: q ? { q } : {} })
    }, 400)
})

const navItems = [
    { path: '/', label: '首页' },
    { path: '/category', label: '分类' },
    { path: '/tag', label: '标签' },
    { path: '/archive', label: '归档' },
]

const avatar = computed(() => store.state.user.avatar || '')
const userInitial = computed(() => {
    const displayName = store.state.user.nickname || store.state.user.username || 'W'
    return displayName.slice(0, 1).toUpperCase()
})
const isLogin = computed(() => Object.keys(store.state.user || {}).length > 0)
const hasToken = computed(() => Boolean(getToken()))
const showAvatarEntry = computed(() => hasToken.value || isLogin.value)

const isCurrent = (path) => route.path === path
const goTo = (path) => router.push(path)

onMounted(async () => {
    if (hasToken.value && !isLogin.value) {
        try {
            await store.dispatch('getAdminInfo')
        } catch (error) {
            console.log('头部获取用户信息失败', error)
        }
    }
})

onBeforeUnmount(() => {
    if (searchTimer) clearTimeout(searchTimer)
})
</script>

<style scoped>
.front-nav {
    padding: 0.6rem 1rem;
    border-radius: var(--front-radius-lg);
}

.front-nav-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 0.75rem;
    min-width: 0;
}

.front-brand {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    min-width: 0;
    background: transparent;
    border: 0;
    padding: 0;
    cursor: pointer;
}

.front-brand-icon {
    width: 2.2rem;
    height: 2.2rem;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 0.75rem;
    color: #fff;
    font-size: 0.95rem;
    font-weight: 800;
    background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
    box-shadow: 0 6px 14px rgba(59, 130, 246, 0.22);
}

.front-brand-copy {
    display: flex;
    flex-direction: column;
    min-width: 0;
}

.front-brand-name {
    font-size: 1.15rem;
    font-weight: 800;
    letter-spacing: -0.03em;
    color: #172033;
}

.front-nav-actions {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    min-width: 0;
    margin-left: auto;
    flex: 0 1 auto;
}

.front-search,
.front-search-mobile {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    width: min(17rem, 32vw);
    min-width: 0;
    max-width: 100%;
    flex: 1 1 auto;
    padding: 0.45rem 0.7rem;
    border-radius: 999px;
    background: rgba(248, 250, 252, 0.9);
    border: 1px solid rgba(148, 163, 184, 0.16);
    color: #94a3b8;
}

.front-search input,
.front-search-mobile input {
    width: 100%;
    min-width: 0;
    border: 0;
    background: transparent;
    padding: 0;
    color: #64748b;
    font-size: 0.8125rem;
}

.front-search input:focus,
.front-search-mobile input:focus {
    outline: none;
}

.front-search-icon-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: inherit;
    background: transparent;
    border: 0;
    padding: 0;
    cursor: pointer;
}

.front-user-trigger {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    width: 2.45rem;
    height: 2.45rem;
    padding: 0.12rem;
    border-radius: 999px;
    border: 1px solid rgba(148, 163, 184, 0.16);
    background: rgba(255, 255, 255, 0.92);
    color: #475569;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.07);
    transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.front-user-trigger:hover {
    transform: translateY(-1px);
    border-color: rgba(59, 130, 246, 0.24);
    box-shadow: 0 6px 16px rgba(37, 99, 235, 0.14);
}

.front-user-trigger:focus-visible {
    outline: 2px solid rgba(59, 130, 246, 0.32);
    outline-offset: 2px;
}

.front-user-avatar {
    width: 100%;
    height: 100%;
    border-radius: 999px;
    object-fit: cover;
}

.front-user-avatar-fallback {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
    color: #1d4ed8;
    font-size: 0.8rem;
    font-weight: 800;
}

.front-login-entry {
    display: inline-flex;
    align-items: center;
    gap: 0.3rem;
    padding: 0.45rem 0.8rem;
    border-radius: 999px;
    font-size: 0.8125rem;
    font-weight: 700;
    color: #fff;
    background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
    border: 0;
    cursor: pointer;
    box-shadow: 0 6px 16px rgba(59, 130, 246, 0.18);
}

.front-nav-menu {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    margin-left: 1rem;
    flex-shrink: 0;
}

.front-nav-link {
    display: inline-flex;
    align-items: center;
    width: fit-content;
    padding: 0.4rem 0.7rem;
    border-radius: 999px;
    color: #52607a;
    font-size: 0.8125rem;
    font-weight: 700;
    border: 0;
    background: transparent;
    cursor: pointer;
}

.front-nav-link:hover {
    color: #1d4ed8;
    background: rgba(59, 130, 246, 0.08);
}

.front-nav-link.is-active {
    color: #fff;
    background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
    box-shadow: 0 6px 16px rgba(59, 130, 246, 0.2);
}

@media (max-width: 767px) {
    .front-nav {
        padding: 0.6rem 0.85rem;
    }

    .front-brand-name {
        font-size: 1.05rem;
    }

    .front-nav-bar {
        flex-wrap: wrap;
    }

    .front-nav-menu {
        order: 3;
        width: 100%;
        margin-left: 0;
        overflow-x: auto;
    }
}

@media (max-width: 1024px) {
    .front-search,
    .front-search-mobile {
        width: min(14rem, 40vw);
    }
}
</style>