import axios from "@/axios"

export function getAiQuota() {
    return axios.post("/admin/ai/quota")
}

export function recommendTags(data, config) {
    return axios.post("/admin/ai/tag/recommend", data || {}, config)
}

export function recommendCategories(data, config) {
    return axios.post("/admin/ai/category/recommend", data || {}, config)
}

export function generateArticle(data, config) {
    return axios.post("/admin/ai/article/generate", data, config)
}

export function estimateTokens(data) {
    return axios.post("/admin/ai/token/estimate", data)
}

export function continueArticle(data, config) {
    return axios.post("/admin/ai/article/continue", data, config)
}

export function rewriteText(data, config) {
    return axios.post("/admin/ai/article/rewrite", data, config)
}

export function polishText(data, config) {
    return axios.post("/admin/ai/article/polish", data, config)
}

export function aiSeoOptimize(data, config) {
    return axios.post("/admin/ai/seo/optimize", data, config)
}
