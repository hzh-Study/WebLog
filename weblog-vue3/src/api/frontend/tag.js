import axios from "@/axios"

/** 全站有公开文章关联的标签 TOP10（按篇数降序） */
export function getTags() {
    return axios.post("/tag/list")
}

/** 有公开文章关联的全部标签（按篇数降序，用于总览页） */
export function getTagsAll() {
    return axios.post("/tag/list/all")
}

export function getTagArticles(data) {
    return axios.post("/tag/article/list", data)
}
