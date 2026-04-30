<template>
    <el-container class="admin-app admin-shell-root">
        <el-aside :width='$store.state.menuWidth' class="admin-shell-aside">
            <AdminMenu></AdminMenu>
        </el-aside>

        <el-container class="admin-shell-main">
            <el-header class="admin-shell-header">
                <AdminHeader></AdminHeader>
            </el-header>

            <el-main class="admin-shell-content">
                <router-view v-slot="{ Component }">
                    <Transition name="fade">
                        <keep-alive :max="10">
                            <component :is="Component"></component>
                        </keep-alive>
                    </Transition>
                </router-view>
            </el-main>

            <el-footer class="admin-shell-footer">
                <AdminFooter></AdminFooter>
            </el-footer>
        </el-container>
    </el-container>
</template>

<script setup>
import AdminHeader from '@/layouts/components/AdminHeader.vue';
import AdminMenu from '@/layouts/components/AdminMenu.vue';
import AdminFooter from '@/layouts/components/AdminFooter.vue';
</script>

<style scoped>
/* 占满 app-shell 分配的高度，由 app-shell 限制为 100vh 且自身不滚动 */
.admin-shell-root {
    height: 100%;
    max-height: 100%;
    min-height: 0;
    overflow: hidden;
}

.el-aside {
    transition: all 0.3s;
}

/* 侧栏过高时仅侧栏滚动，不撑出整页滚动条 */
.admin-shell-aside {
    display: flex;
    flex-direction: column;
    min-height: 0;
    overflow: hidden;
    height: 100%;
    max-height: 100%;
}

.admin-shell-header {
    padding: 0;
    height: auto;
    width: 100%;
    flex: 0 0 auto;
    flex-shrink: 0;
    z-index: 20;
}

.admin-shell-content {
    position: relative;
    padding: 0;
    background: transparent;
    /* 仅此区域纵向滚动，顶栏始终留在主区上方 */
    flex: 1 1 0;
    min-height: 0;
    min-width: 0;
    overflow-y: auto;
    overflow-x: hidden;
    -webkit-overflow-scrolling: touch;
}

.admin-shell-footer {
    padding: 0!important;
    background: transparent;
    flex: 0 0 auto;
    flex-shrink: 0;
}

/* 右侧主列：纵向 flex，吃满高度，避免顶栏随 body 滚走 */
.admin-shell-main {
    flex: 1 1 auto;
    min-width: 0;
    min-height: 0;
    max-height: 100%;
    height: 100%;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.fade-enter-from {
    opacity: 0;
}

.fade-enter-to {
    opacity: 1;
}

.fade-leave-from {
  opacity: 1;
}

.fade-leave-to {
  opacity: 0;
}

.fade-leave-active {
    transition: all 0.3s;
}

.fade-enter-active {
    transition: all 0.3s;
    transition-delay: 0.3s;
}
</style>