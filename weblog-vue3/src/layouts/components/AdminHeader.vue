<template>
    <!-- 不用 el-affix：吸顶时 position:fixed 在侧栏+主区 flex 下宽度易被算成「内容宽」，导致左右挤在一起 -->
    <div class="admin-header-wrap">
        <div class="header">
            <div class="header-left">
                <el-icon class="icon-btn" @click="$store.commit('HANDLE_MENU_WIDTH')">
                    <Fold v-if="$store.state.menuWidth == '152px'" />
                    <Expand v-else />
                </el-icon>
                <div class="header-copy">
                    <span class="header-kicker">Dashboard</span>
                    <strong>管理控制台</strong>
                </div>
            </div>
            <div class="header-spacer" aria-hidden="true" />
            <div class="header-right">
                <el-tooltip class="box-item" effect="dark" content="跳转博客前台" placement="bottom">
                    <el-icon class="icon-btn" @click="router.push('/')">
                        <House />
                    </el-icon>
                </el-tooltip>

                <el-tooltip class="box-item" effect="dark" content="刷新" placement="bottom">
                    <el-icon class="icon-btn" @click="refresh">
                        <Refresh />
                    </el-icon>
                </el-tooltip>

                <el-tooltip class="box-item" effect="dark" content="全屏" placement="bottom">
                    <el-icon class="icon-btn" @click="toggle">
                        <FullScreen v-if="!isFullscreen" />
                        <Aim v-else />
                    </el-icon>
                </el-tooltip>

                <el-dropdown class="dropdown" @command="handleCommand">
                    <span class="dropdown-trigger">
                        <el-avatar :size="32" :src="$store.state.user.avatar" class="mr-2" />
                        <span class="dropdown-copy">
                            <strong>{{ $store.state.user.nickname || $store.state.user.username }}</strong>
                            <small>创作者后台</small>
                        </span>
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu class="header-dropdown-menu">
                            <el-dropdown-item command="updatePassword">修改密码</el-dropdown-item>
                            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>
    </div>

    <!-- 修改密码 -->
    <el-dialog v-model="dialogShow" title="修改密码" width="30%" :show-close="false" class="admin-password-dialog">
        <el-form ref="formRef" :rules="rules" :model="form">
            <el-form-item label="用户名" prop="oldPassword" label-width="120px">
                <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword" label-width="120px">
                <el-input v-model="form.newPassword" type="password" autocomplete="off" placeholder="请输入新密码"
                    show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="rePassword" label-width="120px">
                <el-input v-model="form.rePassword" type="password" autocomplete="off" placeholder="请再次确认新密码"
                    show-password />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogShow = false">取消</el-button>
                <el-button type="primary" @click="onSubmit">
                    提交
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>



<script setup>
import { showModel, showMessage } from '@/composables/util'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ref, reactive } from 'vue'
import { useFullscreen } from '@vueuse/core'
import { updateAdminPassword } from '@/api/admin/user'
import { times } from 'lodash'

const { isFullscreen, toggle } = useFullscreen()

const store = useStore()
const router = useRouter()

const dialogShow = ref(false)

const form = reactive({
    username: store.state.user.username,
    newPassword: '',
    rePassword: '',
})

const rules = {
    username: [
        {
            required: true,
            message: '用户名不能为空',
            trigger: 'blur'
        }
    ],
    newPassword: [
        {
            required: true,
            message: '新密码不能为空',
            trigger: 'blur',
        },
    ],
    rePassword: [
        {
            required: true,
            message: '请再次输入密码',
            trigger: 'blur',
        },
    ]
}


const formRef = ref(null)

const onSubmit = () => {
    // 登录表单验证
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('验证不通过')
            return false
        }

        console.log("newPassword:" + form.newPassword)
        console.log("rePassword:" + form.rePassword)

        // 校验两次输入的密码是否一致
        if (form.newPassword != form.rePassword) {
            showMessage("两次输入的密码不一致！", 'warning')
            return
        }

        updateAdminPassword(form).then(res => {
            if (res.success) {
                // 提示
                showMessage('重置密码成功, 请重新登录！', 'success', 'message')
                store.dispatch('logout')
                router.push('/login')
                dialogShow.value = false
            } else {
                let message = res.message
                showMessage(message, 'warning')
            }
        })
    })
}

const handleCommand = (e) => {
    console.log(e)
    switch (e) {
        case 'updatePassword':
            dialogShow.value = true
            break
        case 'logout':
            logout()
            break
    }

}

// 刷新页面
const refresh = () => location.reload()

function logout() {
    showModel('是否确定要退出登录？').then(() => {
        console.log('登出')
        store.dispatch('logout')

        // 跳转回登录页
        router.push('/login')

        // 提示登出成功
        showMessage('退出登录成功', 'success')
    }).catch(() => { })
}
</script>

<style scoped>
.admin-header-wrap {
    display: block;
    width: 100%;
    min-width: 0;
    box-sizing: border-box;
}

.header {
    width: 100%;
    min-width: 0;
    box-sizing: border-box;
    height: 72px;
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    align-items: center;
    justify-content: flex-start;
    gap: 0.5rem;
    padding: 0 1.5rem;
    background: rgba(255, 255, 255, 0.82);
    border-bottom: 1px solid rgba(148, 163, 184, 0.14);
    backdrop-filter: blur(16px);
    z-index: 100;
}

.header-left,
.header-right {
    display: flex;
    align-items: center;
}

.header-left {
    flex: 0 1 auto;
    min-width: 0;
    gap: 0.85rem;
}

/* 吃满中间空白，把右侧始终顶到主区最右（比单纯 space-between 更抗变窄/吸顶算宽错误） */
.header-spacer {
    flex: 1 1 0;
    min-width: 0.5rem;
    height: 1px;
    align-self: center;
}

.header-right {
    flex: 0 0 auto;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    flex-wrap: nowrap;
    gap: 0.3rem;
}

.header-copy {
    display: flex;
    flex-direction: column;
}

.header-copy strong {
    color: #172033;
    font-size: 1rem;
    font-weight: 800;
    letter-spacing: -0.02em;
}

.header-kicker {
    color: #94a3b8;
    font-size: 0.72rem;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    font-weight: 800;
}

.icon-btn {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    width: 2.9rem;
    height: 2.9rem;
    border-radius: 16px;
    cursor: pointer;
    color: #374151;
    background: rgba(248, 250, 252, 0.88);
    box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.12);
}

.icon-btn:hover {
    background: rgba(37, 99, 235, 0.1);
    color: #1d4ed8;
}

.dropdown {
    cursor: pointer;
    color: #374151 !important;
}

.dropdown-trigger {
    display: inline-flex;
    align-items: center;
    gap: 0.65rem;
    padding: 0.45rem 0.7rem 0.45rem 0.5rem;
    border-radius: 18px;
    background: rgba(248, 250, 252, 0.92);
    box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.12);
}

.dropdown-copy {
    display: flex;
    flex-direction: column;
    line-height: 1.2;
}

.dropdown-copy strong {
    color: #172033;
    font-size: 0.92rem;
    font-weight: 800;
}

.dropdown-copy small {
    color: #94a3b8;
    font-size: 0.74rem;
}

:deep(.header-dropdown-menu) {
    border-radius: 18px;
    padding: 0.35rem;
}

:deep(.header-dropdown-menu .el-dropdown-menu__item) {
    border-radius: 12px;
    font-weight: 700;
    min-width: 140px;
}

@media (max-width: 768px) {
    .header {
        padding: 0 1rem;
    }

    .header-copy,
    .dropdown-copy {
        display: none;
    }
}
</style>