package com.autoFunctionTest.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * 登录页面对象类
 */
public class LoginPage extends BasePage {
    // 登录表单元素定位器 - 根据配置文件获取
    private By usernameInput;
    private By passwordInput;
    private By loginButton;
    private By errorMessage;
    private By switchTabButton;
    
    public LoginPage(WebDriver driver) {
        super(driver);
        
        // 从配置文件中获取选择器
        usernameInput = By.cssSelector(config.getProperty("login.username.selector", "input[placeholder='请输入账号']"));
        passwordInput = By.cssSelector(config.getProperty("login.password.selector", "input[type='password']"));
        loginButton = By.cssSelector(config.getProperty("login.button.selector", "button.el-button--primary"));
        errorMessage = By.cssSelector(config.getProperty("login.error.selector", ".el-message--error"));
        switchTabButton = By.cssSelector(config.getProperty("login.tab.selector", ".login-type span:nth-child(2)"));
    }
    
    /**
     * 打开登录页面
     * @return LoginPage实例
     */
    public LoginPage open() {
        navigateTo("/userlogin");
        return this;
    }
    
    /**
     * 输入用户名
     * @param username 用户名
     * @return LoginPage实例
     */
    public LoginPage typeUsername(String username) {
        type(usernameInput, username);
        return this;
    }
    
    /**
     * 输入密码
     * @param password 密码
     * @return LoginPage实例
     */
    public LoginPage typePassword(String password) {
        type(passwordInput, password);
        return this;
    }
    
    /**
     * 点击登录按钮
     */
    public void clickLogin() {
        click(loginButton);
    }
    
    /**
     * 切换标签（用户/管理员）
     */
    public void switchTab() {
        click(switchTabButton);
    }
    
    /**
     * 执行登录操作
     * @param username 用户名
     * @param password 密码
     * @return 如果URL包含/user/或/admin/则返回true，表示登录成功
     */
    public boolean login(String username, String password) {
        typeUsername(username);
        typePassword(password);
        clickLogin();
        
        // 等待页面跳转 (最多等待10秒)
        try {
            waitForUrlContains("/user/", 10);
            return driver.getCurrentUrl().contains("/user/") || driver.getCurrentUrl().contains("/admin/");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 执行用户登录
     * @return 登录成功则返回HomePage实例
     */
    public Object loginAsUser() {
        String username = config.getProperty("test.user.phone");
        String password = config.getProperty("test.user.password");
        
        boolean success = login(username, password);
        Assert.assertTrue(success, "用户登录失败");
        
        // 根据登录后的URL判断用户类型并返回对应页面
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.contains("/user/")) {
            return new HomePage(driver);
        } else {
            throw new RuntimeException("登录成功但未跳转到预期页面");
        }
    }
    
    /**
     * 执行管理员登录
     * @return 登录成功则返回AdminHomePage实例
     */
    public Object loginAsAdmin() {
        String username = config.getProperty("test.admin.phone");
        String password = config.getProperty("test.admin.password");
        
        // 确保选择管理员标签
        if (isElementPresent(switchTabButton)) {
            switchTab();
        }
        
        boolean success = login(username, password);
        Assert.assertTrue(success, "管理员登录失败");
        
        // 检查URL是否包含/admin/
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/admin/"), "登录成功但未跳转到管理员页面");
        
        return null; // 这里应该返回AdminHomePage实例，但现在未创建该类
    }
    
    /**
     * 检查是否显示错误消息
     * @return 是否显示错误消息
     */
    public boolean hasErrorMessage() {
        return isElementPresent(errorMessage);
    }
    
    /**
     * 获取错误消息文本
     * @return 错误消息文本
     */
    public String getErrorMessage() {
        if (hasErrorMessage()) {
            return getText(errorMessage);
        }
        return "";
    }
} 