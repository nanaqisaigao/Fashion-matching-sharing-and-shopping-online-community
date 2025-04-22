import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            redirect: '/user/helloHome'
        },
        {
            path: '/admin/',
            component: () => import('../components/common/admin/Home.vue'),
            meta: {title: '管理员首页'},
            children: [
                {
                    path: '/admin/AdminList',
                    component: () => import('../components/page/admin/AdminList.vue'),
                    meta: {title: '管理员管理'}
                },
                {
                    path: '/admin/CartList',
                    component: () => import('../components/page/admin/CartList.vue'),
                    meta: {title: '购物管理'}
                },
                {
                    path: '/admin/CategoryList',
                    component: () => import('../components/page/admin/CategoryList.vue'),
                    meta: {title: '分类管理'}
                },
                {
                    path: '/admin/CommentList',
                    component: () => import('../components/page/admin/CommentList.vue'),
                    meta: {title: '帖子评论管理'}
                },
                {
                    path: '/admin/DiscussList',
                    component: () => import('../components/page/admin/DiscussList.vue'),
                    meta: {title: '穿搭评论管理'}
                },
                {
                    path: '/admin/ForumList',
                    component: () => import('../components/page/admin/ForumList.vue'),
                    meta: {title: '帖子管理'}
                },
                {
                    path: '/admin/GoodsList',
                    component: () => import('../components/page/admin/GoodsList.vue'),
                    meta: {title: '商品管理'}
                },
                {
                    path: '/admin/OrdersList',
                    component: () => import('../components/page/admin/OrdersList.vue'),
                    meta: {title: '订单管理'}
                },
                {
                    path: '/admin/OrderItemList',
                    component: () => import('../components/page/admin/OrderItemList.vue'),
                    meta: {title: '订单项管理'}
                },
                {
                    path: '/admin/OutfitList',
                    component: () => import('../components/page/admin/OutfitList.vue'),
                    meta: {title: '穿搭信息管理'}
                },
                {
                    path: '/admin/StarsList',
                    component: () => import('../components/page/admin/StarsList.vue'),
                    meta: {title: '收藏管理'}
                },
                {
                    path: '/admin/UserList',
                    component: () => import('../components/page/admin/UserList.vue'),
                    meta: {title: '用户管理'}
                },
                {
                    path: '/admin/ReviewList',
                    component: () => import('../components/page/admin/ReviewList.vue'),
                    meta: {title: '商品评论管理'}
                },
                {
                    path: '/admin/*',
                    redirect: '/404'
                }
            ]
        }, {
            path: '/user/',
            component: () => import('../components/common/user/Home.vue'),
            meta: {title: '首页'},
            children: [
                {
                    path: '/user/helloHome',
                    component: () => import('../components/page/user/Home.vue'),
                    meta: {title: '首页'}
                },
                {
                    path: '/user/outfit/:id',
                    component: () => import('../components/page/user/OutfitDetail.vue'),
                    meta: {title: '穿搭详情'},
                    props: true,
                    key: route => route.fullPath
                },
                {
                    path: '/user/outfit',
                    component: () => import('../components/page/user/OutfitSharing.vue'),
                    meta: {title: '穿搭分享'}
                },
                {
                    path: '/user/shop',
                    component: () => import('../components/page/user/Shop.vue'),
                    meta: {title: '商城'}
                },
                {
                    path: '/user/shop/product/:id',
                    component: () => import('../components/page/user/ProductDetail.vue'),
                    meta: {title: '商品详情'},
                    props: true,
                    key: route => route.fullPath
                },
                {
                    path: '/user/forum',
                    component: () => import('../components/page/user/Forum.vue'),
                    meta: {title: '社区'}
                },
                {
                    path: '/user/forum/post/:id',
                    component: () => import('../components/page/user/PostDetail.vue'),
                    meta: {title: '帖子详情'}
                },
                {
                    path: '/user/cart',
                    component: () => import('../components/page/user/Cart.vue'),
                    meta: {title: '购物车'}
                },
                {
                    path: '/user/404',
                    component: () => import('../components/page/user/404.vue'),
                    meta: {title: '404'}
                },
            ]
        },
        {
            path: '/userlogin',
            component: () => import('../components/page/admin/login.vue'),
            meta: {title: '登陆'}
        },{
            path: '/userregister',
            component: () => import('../components/page/admin/register.vue'),
            meta: {title: '注册'}
        },
        {
            path: '*',
            redirect: '/user/404'
        }
    ]
});
