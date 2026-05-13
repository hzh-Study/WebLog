import axios from "@/axios"

export function getArticleDetail(articleId) {
    return axios.post("/article/detail", {articleId})
}

export function likeArticle(articleId) {
    return axios.post("/article/like", { articleId })
}

export function getLikeStatus(articleId) {
    return axios.post("/article/like/status", { articleId })
}

export function favoriteArticle(articleId) {
    return axios.post("/article/favorite", { articleId })
}

export function getFavoriteList(params) {
    return axios.post("/article/favorite/list", params)
}