import axios from "@/axios"

export function login(username, password) {
    return axios.post("/login", {username, password})
}

export function register(data) {
    return axios.post("/user/register", data)
}

export function getAdminInfo() {
    return axios.post("/admin/detail")
}

export function updateAdminPassword(data) {
    return axios.post("/admin/password/update", data)
}