import axios from "@/axios"

export function getCommentPageList(data) {
    return axios.post("/admin/comment/list", data)
}

export function reviewComment(data) {
    return axios.post("/admin/comment/review", data)
}

export function deleteComment(data) {
    return axios.post("/admin/comment/delete", data)
}