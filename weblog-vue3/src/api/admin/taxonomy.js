import axios from "@/axios"

export function getCategoryTree() {
    return axios.post("/admin/taxonomy/category/tree")
}

export function getTagsByCategory(data) {
    return axios.post("/admin/taxonomy/tag/list-by-category", data)
}

export function searchTaxonomyCategories(data) {
    return axios.post('/admin/taxonomy/category/search', data)
}

export function searchTaxonomyTags(data) {
    return axios.post("/admin/taxonomy/tag/search", data)
}

export function validateTags(data) {
    return axios.post("/admin/taxonomy/tag/validate", data)
}
