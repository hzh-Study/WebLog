<template>
    <footer class="footer">
        <div class="footer-inner">
            <span class="footer-brand">WebLog Admin</span>
            <span class="footer-sep"></span>
            <span>@2026 by WebLog</span>
        </div>
    </footer>
</template>

<style scoped>
.footer {
    @apply text-sm justify-center flex;
    padding: 1rem 1.5rem 1.5rem;
    background: transparent;
    margin-top: 2rem;
}

.footer-inner {
    display: inline-flex;
    align-items: center;
    gap: 0.7rem;
    padding: 0.9rem 1.15rem;
    border-radius: 999px;
    color: #64748b;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(148, 163, 184, 0.14);
    box-shadow: 0 14px 28px rgba(15, 23, 42, 0.06);
}

.footer-brand {
    font-weight: 800;
    color: #172033;
}

.footer-sep {
    width: 0.3rem;
    height: 0.3rem;
    border-radius: 999px;
    background: #cbd5e1;
}
</style>