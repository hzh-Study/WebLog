<template>
    <div class="admin-menu fixed transition-all duration-300" :style="{ width: $store.state.menuWidth }">
        <div class="admin-menu-brand">
            <div class="admin-menu-brand-badge">H</div>
            <div v-if="$store.state.menuWidth == '152px'" class="admin-menu-brand-copy">
                <strong>WebLog Admin</strong>
                <span>Content Console</span>
            </div>
        </div>

        <el-menu :collapse="isCollapse"  class="border-0 admin-el-menu"
        :default-active="defaultActive"
        :collapse-transition="false"
        unique-opened
         @select="handleSelect">
            <template v-for="(item, index) in menus" :index="index">
                <el-menu-item :index="item.path" class="admin-el-menu-item">
                    <el-icon>
                        <component :is="item.icon"></component>
                    </el-icon>
                    <span>{{ item.name }}</span>
                </el-menu-item>
            </template>

        </el-menu>
    </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router';
import { computed, ref } from 'vue';
import { useStore } from 'vuex';

const router = useRouter()
const route = useRoute()
const store = useStore()

const defaultActive = computed(() => route.path)

// 是否折叠
const isCollapse = computed(() =>  !(store.state.menuWidth == '152px'))

const menus = [{
    'name': '仪表盘',
    'icon': 'Monitor',
    'path': '/admin',
    'child': []
},
{
    'name': '文章管理',
    'icon': 'Document',
    'path': '/admin/article/list',
    'child': []
},
{
    'name': '分类管理',
    'icon': 'FolderOpened',
    'path': '/admin/category/list',
    'child': []
},
{
    'name': '标签管理',
    'icon': 'PriceTag',
    'path': '/admin/tag/list',
    'child': []
},
{
    'name': '评论管理',
    'icon': 'ChatDotRound',
    'path': '/admin/comment/list',
    'child': []
},
{
    'name': 'AI 工作台',
    'icon': 'MagicStick',
    'path': '/admin/ai/workbench',
    'child': []
},
{
    'name': '推荐管理',
    'icon': 'TrendCharts',
    'path': '/admin/recommend/setting',
    'child': []
},
{
    'name': '我的资料',
    'icon': 'Setting',
    'path': '/admin/profile',
    'child': []
}
]

const handleSelect = (e) => {
    console.log(defaultActive)
    console.log(route.path)

    router.push(e)
}
</script>

<style scoped>
.admin-menu {
    transition: all 0.3s;
    width: 152px;
    top: 0;
    bottom: 0;
    left: 0;
    overflow-y: auto;
    overflow-x: hidden;
    padding: 0.75rem 0.4rem 0.75rem;
    background:
        radial-gradient(circle at top left, rgba(59, 130, 246, 0.16), transparent 28%),
        linear-gradient(180deg, #0f172a 0%, #111c36 100%) !important;
    box-shadow: 22px 0 44px rgba(15, 23, 42, 0.12);
}

.admin-menu-brand {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    min-height: 3.25rem;
    margin-bottom: 0.75rem;
    padding: 0.5rem 0.45rem;
    border-radius: 0.9rem;
    background: rgba(255, 255, 255, 0.06);
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.06);
}

.admin-menu-brand-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 2.2rem;
    height: 2.2rem;
    flex-shrink: 0;
    border-radius: 0.65rem;
    background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
    color: #fff;
    font-size: 0.9rem;
    font-weight: 800;
    box-shadow: 0 8px 16px rgba(37, 99, 235, 0.24);
}

.admin-menu-brand-copy {
    display: flex;
    flex-direction: column;
    min-width: 0;
    overflow: hidden;
}

.admin-menu-brand-copy strong {
    color: #fff;
    font-size: 0.78rem;
    font-weight: 800;
    letter-spacing: -0.02em;
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.admin-menu-brand-copy span {
    color: rgba(191, 219, 254, 0.72);
    font-size: 0.58rem;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.admin-el-menu {
    background: transparent !important;
    border-right: 0;
}

:deep(.admin-el-menu-item) {
    height: 2.65rem;
    margin-bottom: 0.35rem;
    border-radius: 0.75rem;
    color: rgba(226, 232, 240, 0.76) !important;
    font-weight: 700;
    padding-left: 0.5rem !important;
    padding-right: 0.45rem !important;
    font-size: 0.8rem;
}

:deep(.admin-el-menu .el-menu-item span) {
    white-space: nowrap;
}

:deep(.admin-el-menu .el-menu-item .el-icon) {
    font-size: 1rem;
    color: inherit;
}

:deep(.admin-el-menu .el-menu-item.is-active) {
    color: #fff !important;
    background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%) !important;
    box-shadow: 0 14px 24px rgba(37, 99, 235, 0.22);
}

:deep(.admin-el-menu .el-menu-item.is-active::before) {
    content: "";
    position: absolute;
    left: 0.45rem;
    width: 6px;
    height: 6px;
    border-radius: 999px;
    background: #fff;
}

:deep(.admin-el-menu .el-menu-item:hover) {
    background: rgba(255, 255, 255, 0.08);
    color: #fff !important;
}

.admin-menu::-webkit-scrollbar {
    width: 0;
}
</style>