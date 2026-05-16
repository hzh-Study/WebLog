import { SYSTEM_TAGS, getCategoryById } from '@/constants/taxonomy'

const tagMap = new Map()

for (const [categoryId, tags] of Object.entries(SYSTEM_TAGS)) {
    tags.forEach((tag) => {
        tagMap.set(Number(tag.id), {
            ...tag,
            categoryId: Number(categoryId),
        })
    })
}

function normalizeCategory(rawArticle = {}) {
    if (rawArticle.category && typeof rawArticle.category === 'object') {
        const resolvedId = rawArticle.category.id ?? rawArticle.categoryId ?? null
        const resolvedName = rawArticle.category.name || rawArticle.categoryName || getCategoryById(Number(resolvedId))?.name || ''
        return resolvedName ? { ...rawArticle.category, id: resolvedId, name: resolvedName } : null
    }

    const categoryId = rawArticle.categoryId ?? rawArticle.category ?? null
    if (categoryId == null || categoryId === '') {
        return rawArticle.categoryName ? { id: null, name: rawArticle.categoryName } : null
    }

    const taxonomyCategory = getCategoryById(Number(categoryId))
    const categoryName = rawArticle.categoryName || taxonomyCategory?.name || ''

    return categoryName ? { id: Number(categoryId), name: categoryName } : null
}

function normalizeTag(tag) {
    if (tag == null || tag === '') {
        return null
    }

    if (typeof tag === 'object') {
        if (tag.name) {
            return {
                ...tag,
                id: tag.id ?? tag.tagId ?? null,
                name: tag.name || tag.tagName,
            }
        }

        if (tag.tagName) {
            return {
                ...tag,
                id: tag.tagId ?? tag.id ?? null,
                name: tag.tagName,
            }
        }
    }

    const numericId = Number(tag)
    if (!Number.isNaN(numericId) && tagMap.has(numericId)) {
        const taxonomyTag = tagMap.get(numericId)
        return { id: taxonomyTag.id, name: taxonomyTag.name }
    }

    return typeof tag === 'string' ? { id: null, name: tag } : null
}

export function normalizeArticleSummary(rawArticle = {}) {
    return {
        ...rawArticle,
        category: normalizeCategory(rawArticle),
        tags: Array.isArray(rawArticle.tags) ? rawArticle.tags.map(normalizeTag).filter(Boolean) : [],
        likeNum: Number(rawArticle.likeNum || 0),
        favoriteNum: Number(rawArticle.favoriteNum || 0),
        readNum: Number(rawArticle.readNum || 0),
        author: rawArticle.author
            ? (typeof rawArticle.author === 'string'
                ? { nickname: rawArticle.author, username: rawArticle.author }
                : rawArticle.author)
            : null,
    }
}

export function normalizeArticleSummaryList(list) {
    return Array.isArray(list) ? list.map(normalizeArticleSummary) : []
}
