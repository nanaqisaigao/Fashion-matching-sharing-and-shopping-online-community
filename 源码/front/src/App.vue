<template>
    <div id="app">
        <router-view></router-view>
    </div>
</template>

<style>
    @import "./assets/css/main.css";
    @import "./assets/css/color-dark.css"; /*深色主题*/
    /*@import "./assets/css/theme-green/color-green.css";   !*浅绿色主题*!*/
    
    /* 全局禁用按钮文本选择和点击后出现的竖杠 */
    .el-button, 
    button,
    [role="button"],
    a,
    .el-menu-item,
    .el-submenu__title,
    .nav-item,
    .el-tabs__item,
    .el-pagination-button,
    .el-select,
    .el-radio,
    .el-checkbox,
    .el-switch,
    .el-card__header,
    [class^="el-icon-"] {
        user-select: none !important;
        -webkit-user-select: none !important;
        -moz-user-select: none !important;
        -ms-user-select: none !important;
        -webkit-tap-highlight-color: transparent !important;
        outline: none !important;
    }
    
    /* 下拉菜单修复 */
    .el-dropdown {
        cursor: pointer !important;
        pointer-events: auto !important;
    }
    
    .el-dropdown-menu__item {
        cursor: pointer !important;
        pointer-events: auto !important;
    }
    
    /* 阻止点击后出现的焦点指示器 */
    *:focus {
        outline: none !important;
    }
    
    /* 阻止所有可交互元素出现光标 */
    [role="button"],
    a,
    button,
    .el-button,
    .el-menu-item,
    .el-submenu__title,
    .el-select-dropdown__item {
        caret-color: transparent !important;
    }
    
    /* 对Element UI组件应用不可选择文本样式 */
    .el-main,
    .el-header,
    .el-footer,
    .el-aside,
    .el-menu,
    .el-dialog__header,
    .el-tabs__header,
    .el-form-item__label,
    .el-card__header {
        user-select: none !important;
        -webkit-user-select: none !important;
        -moz-user-select: none !important;
        -ms-user-select: none !important;
    }
</style>