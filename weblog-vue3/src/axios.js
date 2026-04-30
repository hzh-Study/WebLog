import axios from "axios";
import { notification, showMessage } from '@/composables/util'
import { getToken } from '@/composables/auth'
import store from "@/store";
import { Console } from "windicss/utils";

const instance = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API,
    // baseURL: '/',
    // 后台统计/多表查询可能略慢，过短易致请求失败而顶栏保持 0、子组件图表却后续请求成功
    timeout: 20000
});

// 添加请求拦截器
instance.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    const token = getToken()
    console.log('统一添加 token: ' + token)

    // 统一添加请求头 Token
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
    }

    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    return response.data;
}, function (error) {
    // 对响应错误做点什么
    const response = error.response
    if (!response) {
        showMessage('网络异常，请检查服务是否可用', 'error')
        return Promise.reject(error);
    }

    let status = response.status
    console.log('错误响应==========》' + status)
    if (status == 401 || status == 402) {
        console.log('401-------------')
        store.dispatch('logout').finally(() => location.reload())
    }

    let isSuccess = response.data.success
    console.log('错误响应==========》' + isSuccess)
    if (!isSuccess) {
        console.log('error: ' + response.data.message)
        let message = response.data.message || '请求失败'

        // todo 失效的情况
        // notification(message, 'error')
        showMessage(message, 'error')
    }

    return Promise.reject(error);
});

// 暴露
export default instance