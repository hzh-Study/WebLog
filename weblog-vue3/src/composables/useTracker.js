import { ref, onUnmounted } from 'vue'
import { submitBehavior } from '@/api/frontend/recommend'

const queue = ref([])
const BATCH_SIZE = 5
const FLUSH_INTERVAL = 10000
let timer = null

function startTimer() {
    if (timer) return
    timer = setInterval(() => {
        flush()
    }, FLUSH_INTERVAL)
}

function stopTimer() {
    if (timer) {
        clearInterval(timer)
        timer = null
    }
}

async function flush() {
    if (queue.value.length === 0) return
    const items = queue.value.splice(0, queue.value.length)
    for (const item of items) {
        sendItem(item)
    }
}

function sendItem(item) {
    const payload = {
        eventType: item.eventType,
        targetId: item.targetId,
        targetType: item.targetType,
        eventData: item.eventData || '',
        duration: item.duration || 0,
        referrer: item.referrer || ''
    }
    if (navigator.sendBeacon) {
        const blob = new Blob([JSON.stringify(payload)], { type: 'application/json' })
        const baseUrl = window.__API_BASE_URL__ || ''
        const sent = navigator.sendBeacon(baseUrl + '/recommend/behavior', blob)
        if (!sent) {
            submitBehavior(payload).catch(() => {})
        }
    } else {
        submitBehavior(payload).catch(() => {})
    }
}

function enqueue(item) {
    queue.value.push(item)
    if (queue.value.length >= BATCH_SIZE) {
        flush()
    }
}

export function useTracker() {
    startTimer()

    function trackPageView(path, referrer) {
        enqueue({
            eventType: 'page_view',
            targetId: 0,
            targetType: 'page',
            eventData: path || window.location.pathname,
            referrer: referrer || document.referrer
        })
    }

    function trackArticleView(articleId, duration) {
        enqueue({
            eventType: 'article_view',
            targetId: articleId,
            targetType: 'article',
            duration: duration || 0
        })
    }

    function trackInteraction(articleId, type) {
        enqueue({
            eventType: type,
            targetId: articleId,
            targetType: 'article'
        })
    }

    function trackSearch(keyword) {
        enqueue({
            eventType: 'search',
            targetId: 0,
            targetType: 'search',
            eventData: keyword
        })
    }

    onUnmounted(() => {
        flush()
        stopTimer()
    })

    return {
        trackPageView,
        trackArticleView,
        trackInteraction,
        trackSearch
    }
}
