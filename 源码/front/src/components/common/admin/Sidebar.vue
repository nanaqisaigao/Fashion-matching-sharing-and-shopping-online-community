<template>
    <div class="sidebar">
        <el-menu
            class="sidebar-el-menu"
            :default-active="onRoutes"
            :collapse="collapse"
            background-color="#f3c3cc"
            text-color="#bfcbd9"
            active-text-color="#20a0ff"
            unique-opened
            router
        >
            <template v-for="item in items">
                <template v-if="item.role==type">
                    <template v-if="item.subs">
                        <el-submenu :index="item.index" :key="item.index">
                            <template slot="title">
                                <i :class="item.icon"></i>
                                <span slot="title">{{ item.title }}</span>
                            </template>
                            <template v-for="subItem in item.subs">
                                <el-submenu
                                        v-if="subItem.subs"
                                        :index="subItem.index"
                                        :key="subItem.index"
                                >
                                    <template slot="title">{{ subItem.title }}</template>
                                    <el-menu-item
                                            v-for="(threeItem,i) in subItem.subs"
                                            :key="i"
                                            :index="threeItem.index"
                                    >{{ threeItem.title }}</el-menu-item>
                                </el-submenu>
                                <el-menu-item
                                        v-else
                                        :index="subItem.index"
                                        :key="subItem.index"
                                >{{ subItem.title }}</el-menu-item>
                            </template>
                        </el-submenu>
                    </template>
                    <template v-else>
                        <el-menu-item :index="item.index" :key="item.index">
                            <i :class="item.icon"></i>
                            <span slot="title">{{ item.title }}</span>
                        </el-menu-item>
                    </template>
                </template>
            </template>
        </el-menu>
    </div>
</template>
<script>
import bus from './bus';
import common from "../../../utils/common";
export default {
    data() {
        return {
            collapse: false,
            type:'',
            items: [
                {
                    icon: 'el-icon-user',
                    index: 'UserList',
                    title: '用户管理',
                    role:'01'
                },
                {
                    icon: 'el-icon-help',
                    index: 'CategoryList',
                    title: '分类管理',
                    role:'01'
                },
                {
                    icon: 'el-icon-box',
                    index: 'GoodsList',
                    title: '商品管理',
                    role:'01'
                },
                {
                    icon: 'el-icon-wallet',
                    index: 'OrdersList',
                    title: '订单管理',
                    role:'01'
                },
                {
                    icon: 'el-icon-chat-line-square',
                    index: 'ReviewList',
                    title: '商品评论管理',
                    role:'01'
                },
                {
                    icon: 'el-icon-present',
                    index: 'OutfitList',
                    title: '穿搭信息管理',
                    role:'01'
                },
                {
                    icon: 'el-icon-chat-dot-round',
                    index: 'DiscussList',
                    title: '穿搭评论管理',
                    role:'01'
                },
                {
                    icon: 'el-icon-connection',
                    index: 'ForumList',
                    title: '帖子管理',
                    role:'01'
                },
                {
                    icon: 'el-icon-chat-round',
                    index: 'CommentList',
                    title: '帖子评论管理',
                    role:'01'
                },
                {
                    icon: 'el-icon-star-off',
                    index: 'StarsList',
                    title: '收藏管理',
                    role:'01'
                },

                {
                    icon: 'el-icon-wallet',
                    index: 'OrdersList',
                    title: '订单管理',
                    role:'02'
                },
                {
                    icon: 'el-icon-chat-line-square',
                    index: 'ReviewList',
                    title: '商品评论管理',
                    role:'02'
                },
                {
                    icon: 'el-icon-present',
                    index: 'OutfitList',
                    title: '穿搭信息管理',
                    role:'02'
                },
                {
                    icon: 'el-icon-chat-dot-round',
                    index: 'DiscussList',
                    title: '穿搭评论管理',
                    role:'02'
                },
                {
                    icon: 'el-icon-connection',
                    index: 'ForumList',
                    title: '帖子管理',
                    role:'02'
                },
                {
                    icon: 'el-icon-chat-round',
                    index: 'CommentList',
                    title: '帖子评论管理',
                    role:'02'
                },
                {
                    icon: 'el-icon-star-off',
                    index: 'StarsList',
                    title: '收藏管理',
                    role:'02'
                },
              
               
            ]
        };
    },
    computed: {
        onRoutes() {
            return this.$route.path.replace('/', '');
        }
    },
    created() {
        // 通过 Event Bus 进行组件间通信，来折叠侧边栏
        bus.$on('collapse', msg => {
            this.collapse = msg;
            bus.$emit('collapse-content', msg);
        });
        this.getInfo();
    },
    methods: {
        getInfo(){
            this.type = this.common.get("type");
        }
    }
};
</script>
<style scoped>
.sidebar {
    display: block;
    position: absolute;
    left: 0;
    top: 45px;
    bottom: 0;
    overflow-y: scroll;
}
.sidebar::-webkit-scrollbar {
    width: 0;
}
.sidebar-el-menu:not(.el-menu--collapse) {
    width: 250px;
}
.sidebar > ul {
    height: 100%;
}
.el-menu-item{
    color: rgb(251, 251, 251) !important;
    background-color: aqua;
}
.el-menu-item i {
    color: #fdfdfd !important;
}
</style>
