// bus.js 是一个基于 Vue 的 全局事件总线，用于实现 跨组件通信。它的主要作用是：
// 解耦组件：允许非父子关系的组件（如 Sidebar.vue 和 Header.vue）直接通信，无需通过复杂的父子组件传值。
// 状态同步：例如，管理后台的侧边栏折叠状态可以通过 bus.js 同步到其他组件。
import Vue from 'vue';

// 使用 Event Bus
const bus = new Vue();

export default bus;