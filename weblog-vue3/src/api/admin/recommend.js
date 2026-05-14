import axios from "@/axios"

export function getRecommendConfigList() {
    return axios.post("/admin/recommend/config/list")
}

export function updateRecommendConfig(data) {
    return axios.post("/admin/recommend/config/update", data)
}

export function getRecommendDashboard() {
    return axios.post("/admin/recommend/dashboard")
}
