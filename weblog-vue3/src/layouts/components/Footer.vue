<template>
    <footer class="px-2 pb-4">
        <div class="front-container">
            <div class="front-card footer-shell">
                <div class="footer-grid">
                    <div>
                        <div class="footer-brand">WebLog</div>
                        <p class="footer-copy">记录代码、设计、灵感与日常思考的数字笔记空间。</p>
                    </div>
                    <div class="footer-meta">
                        <span class="footer-badge">浅色高级风</span>
                        <span class="footer-badge">@2026 by hzh</span>
                    </div>
                </div>
            </div>
        </div>
    </footer>
</template>

<style scoped>
.footer-shell {
    /* 原约 1.35rem / 1.6rem，整体高度约减半 */
    padding: 0.65rem 0.8rem;
}

.footer-grid {
    position: relative;
    z-index: 1;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-start;
    justify-content: space-between;
}

.footer-brand {
    font-size: 0.85rem;
    font-weight: 800;
    color: #172033;
    letter-spacing: -0.03em;
    line-height: 1.2;
}

.footer-copy {
    margin-top: 0.2rem;
    color: #64748b;
    font-size: 0.75rem;
    line-height: 1.4;
}

.footer-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 0.35rem;
}

.footer-badge {
    display: inline-flex;
    align-items: center;
    padding: 0.3rem 0.5rem;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.76);
    color: #52607a;
    font-size: 0.7rem;
    font-weight: 700;
    border: 1px solid rgba(148, 163, 184, 0.16);
    line-height: 1.2;
}

@media (min-width: 768px) {
    .footer-grid {
        flex-direction: row;
        align-items: center;
    }
}
</style>