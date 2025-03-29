<template>
  <div class="app-container">
    <Header />
    <div class="page-wrapper">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import Header from './Header.vue'

export default {
  name: 'Home',
  components: {
    Header
  }
}
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

body {
  background-color: #f5f7fa;
}

.app-container {
  min-height: 100vh;
  padding-top: 60px; /* Header 高度 */
}

/* Header 已经在自己的组件中设置了 fixed 定位 */

.page-wrapper {
  height: calc(100vh - 60px);
  overflow-y: auto;
  position: relative;
}
</style> 