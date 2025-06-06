import axios from 'axios';
import router from '@/router/index.js';
import common from './common';
import Vue from 'vue'; // 引入vue
var vm = new Vue(); // 实例化

const service = axios.create({
    timeout: 5000
});

service.interceptors.request.use(
    config => {
        return config;
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
);

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
service.interceptors.request.use(config => {
    // 优先从内存缓存中获取token
    let token = common.cache.get('token') || common.get('token');
    // 如果从localStorage获取到了token，同时更新到内存缓存中
    if (token && !common.cache.get('token')) {
        common.cache.set('token', token);
    }
    if (token) {
        config.headers['token'] = token;  // 设置请求头
    }
    return config
}, error => {
    return Promise.reject(error)
});



service.interceptors.response.use(
    response => {
        if (response.data && response.data.code === -1) { // token有问题
            vm.$message.error(response.data.msg);
            localStorage.clear();
            common.cache.clearAll(); // 同时清除内存缓存
            router.push('/login');
            return response;
        }
        if (response.status === 200) {
            return response;
        } else {
            Promise.reject();//返回一个带有拒绝原因的Promise对象。
        }
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
);

export default service;

