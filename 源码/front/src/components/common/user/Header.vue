<template>
  <div class="header">
    <div class="header-content">
      <!-- Logo部分 -->
      <div class="logo">
        <router-link to="/user/helloHome">
          <img src="https://img.freepik.com/free-vector/elegant-fashion-logo_23-2148452998.jpg" alt="Fashion Store">
          <span class="logo-text">Fashion Store</span>
        </router-link>
      </div>

      <!-- 导航菜单 -->
      <nav class="nav-menu">
        <el-menu 
          :default-active="activeIndex" 
          mode="horizontal" 
          @select="handleSelect"
          background-color="#ffffff"
          text-color="#303133"
          active-text-color="#f3c3cc">
          
          <el-menu-item index="/user/helloHome">
            <i class="el-icon-s-home"></i>首页
          </el-menu-item>

          <el-menu-item index="/user/outfit">
            <i class="el-icon-picture-outline"></i>穿搭分享
          </el-menu-item>

          <el-menu-item index="/user/shop">
            <i class="el-icon-shopping-bag-1"></i>商城
          </el-menu-item>

          <el-menu-item index="/user/forum">
            <i class="el-icon-chat-dot-round"></i>社区
          </el-menu-item>
        </el-menu>
      </nav>

      <!-- 右侧功能区 -->
      <div class="right-section">

        <!-- 购物车 -->
        <div class="cart-icon" @click="goToCart" v-if="isLoggedIn && type=='02'">
            <i class="el-icon-shopping-cart-2"></i>
        </div>

        <!-- 用户信息/登录按钮 -->
        <div class="user-section">
          <template v-if="isLoggedIn">
            <el-dropdown trigger="hover" @command="handleCommand">
              <div class="user-info">
                <img :src="userInfo.image==null?userAvatar:userInfo.image" class="user-avatar" alt="用户头像">
                <span class="username">{{ type==='01'?userInfo.username:userInfo.realname }}</span>
                <i class="el-icon-caret-bottom"></i>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="orders" v-if="type==='02'">
                  <i class="el-icon-shopping-bag-1"></i> 我的订单
                </el-dropdown-item>
                <el-dropdown-item command="logout">
                  <i class="el-icon-switch-button"></i> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="text" @click="login">登录</el-button>
            <el-divider direction="vertical"></el-divider>
            <el-button type="text" @click="register">注册</el-button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Header',
  data() {
    return {
      activeIndex: '/user/helloHome',
      searchText: '',
      isLoggedIn: false,
      username: '用户名',
      userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      type:'',
      userInfo:{},
    }
  },
  created() {
    let token = this.common.get('token');
    if(token==null || token==''){
        this.isLoggedIn = false;
    }else{
        this.isLoggedIn = true;
        this.type = this.common.get('type');
        this.userInfo = this.common.getUserInfo('userInfo');
        this.activeIndex = this.$route.path
    }
  },
  methods: {
    handleSelect(key) {
      this.$router.push(key)
    },
    handleSearch() {
      // 实现搜索功能
      console.log('搜索:', this.searchText)
    },
    goToCart() {
      this.$router.push('/user/cart')
    },
    handleCommand(command) {
      switch (command) {
        case 'profile':
          if(this.type=='01'){
            this.$router.push('/admin/UserList')
          }else{
            this.$router.push('/admin/OrdersList')
          }
          break
        case 'orders':
          this.$router.push('/admin/OrdersList')
          break
        case 'logout':
          this.logout()
          break
      }
    },
    login() {
      this.$router.push('/userlogin')
    },
    register() {
      this.$router.push('/userregister')
    },
    logout() {
      this.$confirm('确认退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.clear();
        this.common.cache.clearAll();
        this.isLoggedIn = false
        this.$message.success('已退出登录')
        this.$router.push('/')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #ffffff;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  width: auto;
  display: flex;
  align-items: center;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  gap: 10px;
}

.logo img {
  height: 40px;
  width: 40px;
  object-fit: cover;
  border-radius: 8px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  font-family: 'Playfair Display', serif;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
}

.right-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-box {
  width: 200px;
}

.cart-icon {
  font-size: 24px;
  cursor: pointer;
  color: #606266;
}

.cart-icon:hover {
  color: #f3c3cc;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 8px;
  object-fit: cover;
}

.username {
  color: #606266;
  font-size: 14px;
  margin-right: 4px;
}

/* Element UI 样式覆盖 */
:deep(.el-menu) {
  border-bottom: none;
}

:deep(.el-menu-item) {
  font-size: 15px;
}

:deep(.el-menu-item i) {
  margin-right: 4px;
}

/* 下拉菜单样式 */
:deep(.el-dropdown-menu) {
  padding: 5px 0;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  padding: 8px 20px;
}

:deep(.el-dropdown-menu__item i) {
  margin-right: 10px;
  font-size: 16px;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #f5f7fa;
  color: #f3c3cc;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  color: #C0C4CC;
}

/* 分割线样式 */
:deep(.el-dropdown-menu__item.divided) {
  border-top: 1px solid #ebeef5;
  margin: 5px 0;
  padding-top: 12px;
}

/* 禁用按钮文本选择 */
button,
.el-button,
.el-button--text,
.el-dropdown {
  user-select: none !important;
  -webkit-user-select: none !important;
  -moz-user-select: none !important;
  -ms-user-select: none !important;
  -webkit-tap-highlight-color: transparent !important;
}

/* 禁用用户信息部分的文本选择 */
.user-info,
.username,
.user-section {
  user-select: none !important;
  -webkit-user-select: none !important;
  -moz-user-select: none !important;
  -ms-user-select: none !important;
}
</style> 