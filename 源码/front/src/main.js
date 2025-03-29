import Vue from 'vue';
import App from './App.vue';
import router from './router';
import ElementUI from 'element-ui';
import VueI18n from 'vue-i18n';
import {messages} from './components/common/admin/i18n';
import 'element-ui/lib/theme-chalk/index.css'; // 默认主题
// import './assets/css/theme-green/index.css'; // 浅绿色主题
import './assets/css/icon.css';
import './components/common/admin/directives';
import 'babel-polyfill';
// import axios from '/axios';
import 'default-passive-events'
import common from './utils/common.js';
//引入过滤器
import filter from './utils/filter.js'
for (const key in filter) {
    Vue.filter(key,filter[key]);
}
//引入 echarts
import * as echarts from 'echarts';
import axios from './utils/request.js'
//注册组件
Vue.prototype.$echarts = echarts
// 导入共用组件
import global from './utils/global.js'
Vue.prototype.global = global
import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)

// 修改 el-dialog 默认点击遮照为不关闭
ElementUI.Dialog.props.closeOnClickModal.default = false
Vue.config.productionTip = false;
Vue.prototype.$axios = axios
Vue.prototype.common=common;
Vue.use(VueI18n);
Vue.use(ElementUI, {
    size: 'small'
});
const i18n = new VueI18n({
    locale: 'zh',
    messages
});

//守卫路由代码
//使用钩子函数对路由进行权限跳转
router.beforeEach((to, from, next) => {
    if (to.fullPath.indexOf('/admin/') >= 0) {
        document.title = `${to.meta.title}`;
        const userInfo = common.getUserInfo('userInfo');
        const type = common.get("type");
        const token = common.get("token");
        if (token!=null && userInfo!=null && type!=null) {
            next();
        } else {
            localStorage.clear;
            next('/403');
        }
    }else {
        //适用前台
        document.title = `${to.meta.title}`;
        next();
    }
});

// 在其他 import 后添加
import './assets/css/global.css'

new Vue({
    router,
    i18n,
    render: h => h(App)
}).$mount('#app');
