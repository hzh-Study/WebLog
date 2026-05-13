import axios from "@/axios"

export function getArticleDetail(articleId) {
    return axios.post("/admin/article/detail", {articleId})
}

export function publishArticle(data) {
    return axios.post("/admin/article/publish", data)
}

export function getArticlePageList(data) {
    return axios.post("/admin/article/list", data)
}

export function deleteArticle(articleId) {
    return axios.post("/admin/article/delete", {articleId})
}

export function updateArticle(data) {
    return axios.post("/admin/article/update", data)
}

export function getVersionList(articleId) {
    return axios.post("/admin/article/version/list", {articleId})
}

export function getVersionDetail(versionId) {
    return axios.post("/admin/article/version/detail", {versionId})
}

export function getVersionDiff(versionIdA, versionIdB) {
    return axios.post("/admin/article/version/diff", {versionIdA, versionIdB})
}

export function rollbackVersion(versionId) {
    return axios.post("/admin/article/version/rollback", {versionId})
}
