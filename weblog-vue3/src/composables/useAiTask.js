import { ref, reactive, onUnmounted } from 'vue'
import { getAiQuota } from '@/api/admin/ai'
import { showMessage } from '@/composables/util'

export function useAiTask() {
    const loading = ref(false)
    const error = ref(null)
    const lastTokens = ref(0)

    const quota = reactive({
        dailyArticleGenLimit: 2,
        dailyArticleGenUsed: 0,
        dailyArticleGenRemaining: 2,
        dailyTokenLimit: 100000,
        dailyTokenUsed: 0,
        dailyTokenRemaining: 100000,
        lastInteractionTime: null,
        interactionIntervalSeconds: 60,
        canInteractNow: true,
        remainingCooldownSeconds: 0
    })

    const quotaLoading = ref(false)
    const remainingSeconds = ref(0)
    let intervalTimer = null

    function updateCooldown() {
        if (intervalTimer) clearInterval(intervalTimer)
        if (!quota.canInteractNow && quota.remainingCooldownSeconds > 0) {
            remainingSeconds.value = quota.remainingCooldownSeconds
            intervalTimer = setInterval(() => {
                remainingSeconds.value = Math.max(0, remainingSeconds.value - 1)
                if (remainingSeconds.value <= 0) {
                    clearInterval(intervalTimer)
                    quota.canInteractNow = true
                    refreshQuota()
                }
            }, 1000)
        } else {
            remainingSeconds.value = 0
        }
    }

    async function refreshQuota() {
        quotaLoading.value = true
        try {
            const res = await getAiQuota()
            if (res.success) {
                Object.assign(quota, res.data)
                updateCooldown()
            }
        } catch (e) {
            console.error('获取额度失败', e)
        } finally {
            quotaLoading.value = false
        }
    }

    function extractTokens(result) {
        if (result && result.actualTokens != null) {
            lastTokens.value = result.actualTokens
        }
        return lastTokens.value
    }

    async function execute(fn, options = {}) {
        const {
            timeout = 120000,
            successMsg = '',
            errorMsg = 'AI 请求失败，请稍后重试',
            onSuccess = null,
            onError = null
        } = options

        loading.value = true
        error.value = null

        try {
            const res = await fn({ timeout })
            if (res && res.success) {
                extractTokens(res.data)
                if (successMsg) showMessage(successMsg, 'success')
                if (onSuccess) onSuccess(res.data)
                await refreshQuota()
                return res.data
            } else {
                const msg = (res && res.message) || errorMsg
                error.value = msg
                showMessage(msg, 'error')
                if (onError) onError(msg)
                return null
            }
        } catch (e) {
            console.error('AI 请求异常', e)
            const msg = e && e.message ? e.message : errorMsg
            error.value = msg
            showMessage(msg, 'error')
            if (onError) onError(msg)
            return null
        } finally {
            loading.value = false
        }
    }

    function canInteract() {
        return quota.canInteractNow && quota.dailyTokenRemaining > 0
    }

    function canGenerateArticle() {
        return canInteract() && quota.dailyArticleGenRemaining > 0
    }

    onUnmounted(() => {
        if (intervalTimer) clearInterval(intervalTimer)
    })

    return {
        loading,
        error,
        lastTokens,
        quota,
        quotaLoading,
        remainingSeconds,
        refreshQuota,
        execute,
        extractTokens,
        canInteract,
        canGenerateArticle
    }
}