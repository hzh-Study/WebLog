import axios from "@/axios"

export function getDashboardArticleStatisticsInfo() {
    return axios.post("/admin/dashboard/article/statistics")
}

export function getDashboardPublishArticleStatisticsInfo() {
    return axios.post("/admin/dashboard/publishArticle/statistics")
}

export function getDashboardPVStatisticsInfo() {
    return axios.post("/admin/dashboard/pv/statistics")
}

export function getStatisticsOverview() {
    return axios.post("/admin/dashboard/statistics/overview")
}

export function getPvUvTrend(data) {
    return axios.post("/admin/dashboard/statistics/pvuv/trend", data)
}

export function getArticleRank() {
    return axios.post("/admin/dashboard/statistics/article/rank")
}

export function getVisitorRegion() {
    return axios.post("/admin/dashboard/statistics/visitor/region")
}

export function getPublishHeatmap() {
    return axios.post("/admin/dashboard/statistics/publish/heatmap")
}

export function getCategoryRatio() {
    return axios.post("/admin/dashboard/statistics/category/ratio")
}

export function getTagRatio() {
    return axios.post("/admin/dashboard/statistics/tag/ratio")
}


