import router from '@/router/index'
import { getToken } from '@/composables/auth'
import { notification, showMessage } from '@/composables/util'
import store from '@/store'
import { showPageLoading, hidePageLoading } from '@/composables/util'


// 全局前置守卫
router.beforeEach(async (to, from, next) => {
    console.log('全局前置守卫 >>>>')
    showPageLoading()
    // if (to.name !== 'Login' && !isAuthenticated) next({ name: 'Login' })
    // else next()

    

    const token = getToken()

    try {
        // 如果用户已登录，则自动获取用户信息，并使用全局状态管理
        if (token) {
            console.log('获取登录用户信息。。。。')
            await store.dispatch('getAdminInfo')
        }

        // 前台请求逻辑处理
        if (!to.path.startsWith('/admin')) {
            await store.dispatch('getBlogSetting')
            next()
            return
        }

        // 后台请求逻辑处理
        // 未登录，强制跳转登录页
        if (!token && to.path.startsWith('/admin')) {
            showMessage('请先登录', 'warning')
            next({ path: '/login'})
            return
        }

        // 防止重复登录
        // if (token && to.path == '/login') {
        //     notification('请勿重复登录', 'error')
        //     next({ path: from.path ? from.path : '/' })
        // }

        next()
    } catch (error) {
        console.error('路由守卫执行失败:', error)

        // 前台页面即使博客设置请求失败，也不能阻塞路由渲染
        if (!to.path.startsWith('/admin')) {
            next()
            return
        }

        // 后台页面出错时优先回到登录页，避免界面一直处于加载中
        showMessage('页面初始化失败，请重新登录后再试', 'error')
        next({ path: '/login' })
    }
})

router.afterEach((to, from) => {
    // 设置页面标题
    let title = (to.meta.title ? to.meta.title : '') + ' - WebLog'
    document.title = title

    hidePageLoading()
})
