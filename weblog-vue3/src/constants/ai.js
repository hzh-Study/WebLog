export const DEFAULT_AI_SCENERY_COVER =
    'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1600&q=80'

export function resolveAiCover(url) {
    return url && String(url).trim() ? url : DEFAULT_AI_SCENERY_COVER
}

export function buildAiRecommendRequirement(requirement, content) {
    const req = requirement && String(requirement).trim()
    const body = content && String(content).trim()
    if (!req && !body) return null
    if (!body) return req
    const excerpt = body.length > 2000 ? `${body.slice(0, 2000)}...` : body
    return [req, `当前待分析正文：\n${excerpt}`].filter(Boolean).join('\n\n')
}
