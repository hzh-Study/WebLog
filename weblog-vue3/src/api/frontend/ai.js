import axios from '@/axios'

export function summarizeArticle(articleId) {
    return axios.post('/article/ai/summarize', { articleId })
}