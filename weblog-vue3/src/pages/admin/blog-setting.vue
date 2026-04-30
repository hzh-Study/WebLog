<template>
    <div class="admin-page">
    <section class="admin-page-header">
        <div>
            <span class="admin-kicker">Profile</span>
            <h1 class="admin-page-title">我的资料</h1>
            <p class="admin-page-desc">维护你的公开主页资料。昵称、头像、简介和外链会展示在个人主页与文章作者信息里。</p>
        </div>
    </section>

    <el-card shadow="never" class="admin-form-card">
        <el-form :model="form" label-width="160px" :rules="rules" class="admin-form-layout">
            <el-form-item label="用户名">
                <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
                <el-input v-model="form.nickname" clearable />
            </el-form-item>
            <el-form-item label="头像" prop="avatar">
                <el-upload class="avatar-uploader admin-upload-box" action="#" :on-change="handleTitleImageChange" :auto-upload="false"
                    :show-file-list="false" :on-success="handleAvatarSuccess">
                    <img v-if="form.avatar" :src="form.avatar" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </el-form-item>
            <el-form-item label="个人简介">
                <el-input v-model="form.bio" type="textarea" :rows="4" />
            </el-form-item>
            <el-form-item label="个人网站">
                <el-input v-model="form.website" clearable placeholder="请输入个人网站 URL" />
            </el-form-item>
            <el-form-item label="开启 GitHub 访问">
                <el-switch v-model="isGithubCheck" inline-prompt :active-icon="Check" :inactive-icon="Close" @change="githubSwitchChange"/>
            </el-form-item>
            <el-form-item label="GitHub 主页访问地址" v-if="isGithubCheck">
                <el-input v-model="form.githubHome" clearable placeholder="请输入 GitHub 主页访问的 URL" />
            </el-form-item>
            <el-form-item label="开启 CSDN 访问">
                <el-switch v-model="isCSDNCheck" inline-prompt :active-icon="Check" :inactive-icon="Close" @change="csdnSwitchChange"/>
            </el-form-item>
            <el-form-item label="CSDN 主页访问地址" v-if="isCSDNCheck">
                <el-input v-model="form.csdnHome" clearable placeholder="请输入 CSDN 主页访问的 URL" />
            </el-form-item>
            <el-form-item label="开启 Gitee 访问">
                <el-switch v-model="isGiteeCheck" inline-prompt :active-icon="Check" :inactive-icon="Close" @change="giteeSwitchChange"/>
            </el-form-item>
            <el-form-item label="Gitee 主页访问地址" v-if="isGiteeCheck">
                <el-input v-model="form.giteeHome" clearable placeholder="请输入 Gitee 主页访问的 URL" />
            </el-form-item>
            <el-form-item label="开启知乎访问">
                <el-switch v-model="isZhihuCheck" inline-prompt :active-icon="Check" :inactive-icon="Close" @change="zhihuSwitchChange"/>
            </el-form-item>
            <el-form-item label="知乎主页访问地址" v-if="isZhihuCheck">
                <el-input v-model="form.zhihuHome" clearable placeholder="请输入知乎主页访问的 URL" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSubmit">保存</el-button>
            </el-form-item>
        </el-form>
    </el-card>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Check, Close } from '@element-plus/icons-vue'
import { uploadFile } from '@/api/admin/file'
import { showMessage } from '@/composables/util'
import { getBlogSettingDetail, updateBlogSetting } from '@/api/admin/blogsetting'

const isGithubCheck = ref(false)
const isCSDNCheck = ref(false)
const isGiteeCheck = ref(false)
const isZhihuCheck = ref(false)


const form = reactive({
    username: '',
    nickname: '',
    avatar: '',
    bio: '',
    website: '',
    githubHome: '',
    giteeHome: '',
    csdnHome: '',
    zhihuHome: '',
})

const rules = {
    nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
    avatar: [{ required: true, message: '请选择头像', trigger: 'blur' }],
}

const githubSwitchChange = (e) => {
    if (e == false) {
        form.githubHome = ''
    }
}

const csdnSwitchChange = (e) => {
    if (e == false) {
        form.csdnHome = ''
    }
}

const giteeSwitchChange = (e) => {
    if (e == false) {
        form.giteeHome = ''
    }
}

const zhihuSwitchChange = (e) => {
    if (e == false) {
        form.zhihuHome = ''
    }
}

const handleTitleImageChange = (file) => {
    console.log('开始上传文件')
    console.log(file)
    let formData = new FormData()
    formData.append("file", file.raw);
    uploadFile(formData).then((e) => {
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error', 'message')
            return
        }
        form.avatar = e.data.url
        showMessage('头像上传成功', 'success', 'message')
    })
}

function initBlogSetting() {
    getBlogSettingDetail().then((e) => {
        if (e.success == true) {
            form.username = e.data.username
            form.nickname = e.data.nickname
            form.avatar = e.data.avatar
            form.bio = e.data.bio
            form.website = e.data.website
            if (e.data.githubHome) {
                isGithubCheck.value = true
                form.githubHome = e.data.githubHome
            }
            if (e.data.giteeHome) {
                isGiteeCheck.value = true
                form.giteeHome = e.data.giteeHome
            }
            if (e.data.csdnHome) {
                isCSDNCheck.value = true
                form.csdnHome = e.data.csdnHome
            }
            if (e.data.zhihuHome) {
                isZhihuCheck.value = true
                form.zhihuHome = e.data.zhihuHome
            }
        }
})
}
initBlogSetting()


const onSubmit = () => {
    console.log('提交内容' + form.content)
    updateBlogSetting(form).then((e) => {
        console.log(e)
        if (e.success == false) {
            var message = e.message
            showMessage(message, 'warning', 'message')
            return
        }

        showMessage('更新成功', 'success', 'message')
        initBlogSetting()
    })
}
</script>

<style scoped>
.avatar {
    width: 100%;
    max-width: 260px;
    border-radius: 20px;
    display: block;
}
</style>