<template>
    <div class="admin-login-page">
        <div class="admin-login-shell">
            <section class="admin-login-hero animate__animated animate__fadeInLeft">
                <div class="admin-login-hero-inner">
                    <span class="admin-kicker admin-login-kicker">Admin Only</span>
                    <h1 class="admin-login-title">注册管理员账号</h1>
                    <p class="admin-login-desc">
                        本站点仅开放管理员注册。游客无需账号即可浏览全站公开文章、分类与标签。管理员登录后可发文，并管理本人专属分类与标签。
                    </p>
                    <div class="admin-login-metrics">
                        <span class="admin-summary-chip"><strong>公开前台</strong> 游客免登录浏览</span>
                        <span class="admin-summary-chip"><strong>后台</strong> 发文与本人数据</span>
                    </div>
                    <img src="@/assets/developer.png" class="login-image" alt="register">
                </div>
            </section>

            <section class="admin-login-form-wrap animate__animated animate__fadeInRight animate__fast">
                <div class="admin-login-card">
                    <div class="admin-login-card-head">
                        <span class="admin-kicker">Create Account</span>
                        <h2 class="admin-login-card-title">管理员注册</h2>
                        <p class="admin-login-card-desc">供内容管理者使用。填写用户名、昵称和密码，注册为管理员后即可登录后台。</p>
                    </div>

                    <el-form ref="formRef" :rules="rules" :model="form" class="admin-login-form">
                        <el-form-item prop="username">
                            <el-input v-model="form.username" :prefix-icon="User" placeholder="请输入用户名" size="large" clearable />
                        </el-form-item>
                        <el-form-item prop="nickname">
                            <el-input v-model="form.nickname" :prefix-icon="EditPen" placeholder="请输入昵称" size="large" clearable />
                        </el-form-item>
                        <el-form-item prop="password">
                            <el-input v-model="form.password" type="password" autocomplete="off" :prefix-icon="Lock"
                                placeholder="请输入密码" show-password size="large" clearable />
                        </el-form-item>
                        <el-form-item prop="confirmPassword">
                            <el-input v-model="form.confirmPassword" type="password" autocomplete="off" :prefix-icon="Lock"
                                placeholder="请再次输入密码" show-password size="large" clearable />
                        </el-form-item>
                        <el-form-item class="mb-0">
                            <el-button type="primary" @click="onSubmit" :loading="loading" class="admin-login-btn" size="large">
                                注 册
                            </el-button>
                        </el-form-item>
                    </el-form>

                    <div class="switch-link">
                        已有账号？
                        <a @click="router.push('/login')">去登录</a>
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
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/admin/user'
import { showMessage } from '@/composables/util'
import { User, Lock, EditPen } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
    username: '',
    nickname: '',
    password: '',
    confirmPassword: '',
})

const rules = {
    username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
    nickname: [{ required: true, message: '昵称不能为空', trigger: 'blur' }],
    password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
    confirmPassword: [{ required: true, message: '请再次输入密码', trigger: 'blur' }],
}

const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) return false
        if (form.password !== form.confirmPassword) {
            showMessage('两次输入的密码不一致', 'warning')
            return
        }
        loading.value = true
        register({
            username: form.username,
            nickname: form.nickname,
            password: form.password,
        }).then((res) => {
            if (!res.success) {
                showMessage(res.message || '注册失败', 'error')
                return
            }
            showMessage('注册成功，请登录', 'success')
            router.push('/login')
        }).finally(() => {
            loading.value = false
        })
    })
}
</script>

<style scoped>
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

.login-image {
    margin-top: 2rem;
    height: min(46vh, 460px);
    width: auto;
    object-fit: contain;
    align-self: flex-end;
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
