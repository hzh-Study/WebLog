import axios from "@/axios"

export function getCommentList(data) {
    return axios.post("/comment/list", data)
}

export function publishComment(data) {
    return axios.post("/comment/publish", data)
}