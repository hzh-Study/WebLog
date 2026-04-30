import axios from "@/axios"

export function getPublicUserDetail(username) {
    return axios.post("/user/public/detail", { username })
}

export function searchPublicUsers(keyword) {
    return axios.post("/user/public/search", { keyword })
}
