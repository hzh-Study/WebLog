<template>
    <Header></Header>

    <div class="front-container front-shell">
        <section class="front-card about-card">
            <div class="front-card-body about-body">
                <span class="front-page-kicker">About WebLog</span>
                <h1 class="front-page-title">关于这个站点</h1>
                <p class="front-page-description">
                    这里是 WebLog 的内容空间，用来记录学习、实践、项目思考与一些值得留下来的日常片段。
                </p>
                <div class="front-badge-row">
                    <span class="front-badge"><strong>Design</strong> 浅色高级风</span>
                    <span class="front-badge"><strong>Focus</strong> 内容与阅读体验</span>
                </div>
            </div>
        </section>
    </div>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/components/Header.vue'
import Footer from '@/layouts/components/Footer.vue'
</script>

<style scoped>
.about-card {
    min-height: 55vh;
}

.about-body {
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-height: 55vh;
}
</style>