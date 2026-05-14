import axios from "@/axios"

export function getRecommendFeed(data) {
    return axios.post("/recommend/feed", data)
}

export function getRelatedArticles(data) {
    return axios.post("/recommend/related", data)
}

export function getHotArticles(data) {
    return axios.post("/recommend/hot", data)
}

export function submitBehavior(data) {
    return axios.post("/recommend/behavior", data)
}

export function submitFeedback(data) {
    return axios.post("/recommend/feedback", data)
}

export function getRecommendSettings() {
    return axios.post("/recommend/settings")
}

export function updateRecommendSettings(data) {
    return axios.post("/recommend/settings/update", data)
}
