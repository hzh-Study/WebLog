import axios from "@/axios"

/** 全站有公开文章关联的分类 TOP10（按篇数降序） */
export function getCategories() {
    return axios.post("/category/list")
}

/** 有公开文章关联的全部分类（按篇数降序，用于总览页） */
export function getCategoriesAll() {
    return axios.post("/category/list/all")
}

export function getCategoryArticles(data) {
    return axios.post("/category/article/list", data)
}


