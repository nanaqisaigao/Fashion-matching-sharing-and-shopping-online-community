package com.autoFunctionTest.testcase;

import com.autoFunctionTest.BaseTest;
import com.autoFunctionTest.po.HomePage;
import com.autoFunctionTest.po.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 登录功能测试类
 */
public class LoginTest extends BaseTest {
    
    /**
     * 测试用户成功登录
     */
    @Test
    public void testUserLoginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        String username = config.getProperty("test.user.phone");
        String password = config.getProperty("test.user.password");
        
        boolean result = loginPage.login(username, password);
        Assert.assertTrue(result, "用户登录失败");
        
        // 验证跳转到用户首页
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/user/"), "登录后未跳转到用户首页");
    }
    
    /**
     * 测试管理员成功登录
     */
    @Test
    public void testAdminLoginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        // 切换到管理员登录标签
        loginPage.switchTab();
        
        String username = config.getProperty("test.admin.phone");
        String password = config.getProperty("test.admin.password");
        
        boolean result = loginPage.login(username, password);
        Assert.assertTrue(result, "管理员登录失败");
        
        // 验证跳转到管理员首页
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/admin/"), "登录后未跳转到管理员首页");
    }
    
    /**
     * 测试用户名错误登录失败
     */
    @Test
    public void testLoginFailWithInvalidUsername() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        boolean result = loginPage.login("invaliduser", "123456");
        Assert.assertFalse(result, "使用无效用户名不应该登录成功");
        
        // 验证错误消息
        Assert.assertTrue(loginPage.hasErrorMessage(), "应该显示错误消息");
        
        // 验证依然在登录页面
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/userlogin"), "应该停留在登录页面");
    }
    
    /**
     * 测试密码错误登录失败
     */
    @Test
    public void testLoginFailWithInvalidPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        String username = config.getProperty("test.user.phone");
        
        boolean result = loginPage.login(username, "wrongpassword");
        Assert.assertFalse(result, "使用错误密码不应该登录成功");
        
        // 验证错误消息
        Assert.assertTrue(loginPage.hasErrorMessage(), "应该显示错误消息");
        
        // 验证依然在登录页面
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/userlogin"), "应该停留在登录页面");
    }
    
    /**
     * 测试用户名为空登录失败
     */
    @Test
    public void testLoginFailWithEmptyUsername() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        loginPage.typeUsername("");
        loginPage.typePassword("123456");
        loginPage.clickLogin();
        
        // 验证依然在登录页面
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/userlogin"), "应该停留在登录页面");
    }
    
    /**
     * 测试密码为空登录失败
     */
    @Test
    public void testLoginFailWithEmptyPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        String username = config.getProperty("test.user.phone");
        
        loginPage.typeUsername(username);
        loginPage.typePassword("");
        loginPage.clickLogin();
        
        // 验证依然在登录页面
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/userlogin"), "应该停留在登录页面");
    }
} 