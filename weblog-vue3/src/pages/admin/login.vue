<template>
    <div class="admin-login-page">
        <div class="admin-login-shell">
            <section class="admin-login-hero animate__animated animate__fadeInLeft">
                <div class="admin-login-hero-inner">
                    <span class="admin-kicker admin-login-kicker">Admin Console</span>
                    <h1 class="admin-login-title">WebLog 管理后台</h1>
                    <p class="admin-login-desc">
                        仅供已注册的管理员使用。游客请直接访问首页浏览公开内容，无需登录。
                    </p>
                    <div class="admin-login-metrics">
                        <span class="admin-summary-chip"><strong>个人数据</strong> 本人文章 / 分类 / 标签</span>
                        <span class="admin-summary-chip"><strong>全站</strong> 仅前台公开展示</span>
                    </div>
                    <img src="@/assets/developer.png" class="login-image" alt="admin">
                </div>
            </section>

            <section class="admin-login-form-wrap animate__animated animate__fadeInRight animate__fast">
                <div class="admin-login-card">
                    <div class="admin-login-card-head">
                        <span class="admin-kicker">Welcome Back</span>
                        <h2 class="admin-login-card-title">登录管理端</h2>
                        <p class="admin-login-card-desc">使用管理员账号登录，管理发文与本人专属分类、标签。</p>
                    </div>

                    <el-form ref="formRef" :rules="rules" :model="form" class="admin-login-form">
                        <el-form-item prop="username">
                            <el-input v-model="form.username" :prefix-icon="User" placeholder="请输入用户名" size="large" clearable />
                        </el-form-item>
                        <el-form-item prop="password">
                            <el-input v-model="form.password" type="password" autocomplete="off" :prefix-icon="Lock"
                                placeholder="请输入密码" show-password size="large" clearable />
                        </el-form-item>
                        <el-form-item class="mb-0">
                            <el-button type="primary" @click="onSubmit" :loading="loading" class="admin-login-btn" size="large">
                                登 录
                            </el-button>
                        </el-form-item>
                    </el-form>

                    <div class="switch-link">
                        需注册管理员账号？
                        <a @click="router.push('/register')">去注册</a>
                    </div>
                    <div class="back-to-site">
                        <el-button text type="primary" @click="router.push('/')">返回网站首页</el-button>
                    </div>
                </div>
            </section>
        </div>
    </div>
</template>
  
<script setup>
import { reactive, ref, onMounted, onBeforeUnmount } from 'vue'
import { login } from '@/api/admin/user';
import { showMessage } from '@/composables/util'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { setToken } from '@/composables/auth'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const store = useStore()

const form = reactive({
    username: '',
    password: '',
})

const rules = {
    username: [
        {
            required: true,
            message: '用户名不能为空',
            trigger: 'blur'
        }
    ],
    password: [
        {
            required: true,
            message: '密码不能为空',
            trigger: 'blur',
        },
    ]
}


const formRef = ref(null)
const loading = ref(false)

const onSubmit = () => {
    // 登录表单验证
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('验证不通过')
            return false
        }
        loading.value = true
        login(form.username, form.password)
            .then(res => {
                if (res.success == true) {
                    // 提示成功
                    let message = res.message
                    showMessage('登录成功', 'success')
                    // notification('登录成功')

                    let token = res.data.token
                    // 存储 token
                    setToken(token)

                    // 跳转到后台页面
                    router.push('/admin')
                } else {
                    let message = res.message
                    showMessage(message, 'error')
                }
            }).finally(() => {
                loading.value = false
            })
    })
}

function onKeyUp(e) {
    console.log(e)
    if (e.key == 'Enter') {
        onSubmit()
    }
}

// 添加键盘监听
onMounted(() => {
    console.log('添加键盘监听')
    document.addEventListener('keyup', onKeyUp)
})

// 移除键盘监听
onBeforeUnmount(() => {
    document.removeEventListener('keyup', onKeyUp)
})

</script>

<style scoped>
:deep([type='text']:focus) {
    border-color: transparent !important;
}

.admin-login-page {
    min-height: 100vh;
    padding: 1.4rem;
}

.admin-login-shell {
    min-height: calc(100vh - 2.8rem);
    display: grid;
    grid-template-columns: minmax(0, 1.2fr) minmax(360px, 0.8fr);
    gap: 1.2rem;
}

.admin-login-hero {
    position: relative;
    overflow: hidden;
    border-radius: 32px;
    padding: 2rem;
    color: #fff;
    background:
        radial-gradient(circle at top left, rgba(255, 255, 255, 0.18), transparent 34%),
        radial-gradient(circle at bottom right, rgba(96, 165, 250, 0.22), transparent 28%),
        linear-gradient(145deg, #0f172a 0%, #172554 45%, #2563eb 100%);
    box-shadow: 0 32px 60px rgba(15, 23, 42, 0.22);
}

.admin-login-hero-inner {
    position: relative;
    z-index: 1;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.admin-login-kicker {
    background: rgba(255, 255, 255, 0.12);
    color: #dbeafe;
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.12);
}

.admin-login-title {
    margin-top: 0.35rem;
    font-size: clamp(2.6rem, 5vw, 4.2rem);
    line-height: 1.04;
    font-weight: 800;
    letter-spacing: -0.05em;
}

.admin-login-desc {
    max-width: 560px;
    margin-top: 1rem;
    color: rgba(226, 232, 240, 0.85);
    font-size: 1rem;
    line-height: 1.95;
}

.admin-login-metrics {
    display: flex;
    flex-wrap: wrap;
    gap: 0.8rem;
    margin-top: 1.6rem;
}

.admin-login-metrics :deep(.admin-summary-chip) {
    background: rgba(255, 255, 255, 0.12);
    color: rgba(226, 232, 240, 0.86);
    border-color: rgba(255, 255, 255, 0.12);
}

.admin-login-metrics :deep(.admin-summary-chip strong) {
    color: #fff;
}

.admin-login-form-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
}

.admin-login-card {
    width: 100%;
    max-width: 440px;
    border-radius: 28px;
    border: 1px solid rgba(148, 163, 184, 0.16);
    background: rgba(255, 255, 255, 0.92);
    box-shadow: 0 26px 50px rgba(15, 23, 42, 0.1);
    backdrop-filter: blur(18px);
    padding: 2rem;
}

.admin-login-card-head {
    margin-bottom: 1.6rem;
}

.admin-login-card-title {
    margin-top: 0.35rem;
    color: #172033;
    font-size: 2rem;
    font-weight: 800;
    letter-spacing: -0.04em;
}

.admin-login-card-desc {
    margin-top: 0.75rem;
    color: #64748b;
    line-height: 1.8;
}

.admin-login-form {
    width: 100%;
}

.admin-login-btn {
    width: 100%;
    min-height: 3.25rem;
    border-radius: 16px;
}

.switch-link {
    margin-top: 1rem;
    color: #64748b;
    text-align: center;
}

.switch-link a {
    color: #2563eb;
    font-weight: 700;
}

.back-to-site {
    margin-top: 0.5rem;
    text-align: center;
}

.login-image {
    margin-top: 2rem;
    height: min(46vh, 460px);
    width: auto;
    object-fit: contain;
    align-self: flex-end;
}

@media (max-width: 1024px) {
    .admin-login-shell {
        grid-template-columns: 1fr;
    }

    .admin-login-hero {
        min-height: 420px;
    }

    .login-image {
        align-self: center;
    }
}
</style>
  