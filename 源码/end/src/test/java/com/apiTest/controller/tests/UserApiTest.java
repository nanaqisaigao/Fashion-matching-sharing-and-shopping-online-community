package com.apiTest.controller.tests;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.apiTest.controller.BaseApiTest;
import com.apiTest.controller.services.UserApiService;

/**
 * 用户API测试类
 */
public class UserApiTest extends BaseApiTest {
    
    private UserApiService userApiService;
    private String testUserPhone;
    private String testUserPassword;
    private String testUserName;
    private String authToken;
    private int userId;
    
    /**
     * 初始化测试环境
     */
    @BeforeClass
    public void setup() {
        userApiService = new UserApiService(API_BASE_URL);
        testUserPhone = generateRandomPhone();
        testUserPassword = "Password123";
        testUserName = "测试用户" + generateRandomString(5);
    }
    
    /**
     * 测试用户注册
     */
    @Test(priority = 1)
    public void testRegister() {
        // 执行注册
        Response response = userApiService.register(
            testUserPhone, 
            testUserPassword, 
            testUserName, 
            "男", 
            25, 
            "测试地址"
        );
        
        // 验证响应状态码
        userApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        userApiService.verifyCommonResponseStructure(response);
        
        // 验证业务状态码
        userApiService.verifyBusinessCode(response, 200);
    }
    
    /**
     * 测试用户登录
     */
    @Test(priority = 2, dependsOnMethods = {"testRegister"})
    public void testLogin() {
        // 执行登录
        Response response = userApiService.login(testUserPhone, testUserPassword);
        
        // 验证响应状态码
        userApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        userApiService.verifyJsonStructure(response, 
            "code", "msg", "data", "data.token", "data.userInfo");
        
        // 验证业务状态码
        userApiService.verifyBusinessCode(response, 200);
        
        // 保存token和用户ID，用于后续测试
        authToken = response.jsonPath().getString("data.token");
        userId = response.jsonPath().getInt("data.userInfo.id");
        
        // 验证token不为空
        Assertions.assertThat(authToken).isNotEmpty();
        
        // 验证用户ID大于0
        Assertions.assertThat(userId).isGreaterThan(0);
    }
    
    /**
     * 测试获取用户信息
     */
    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void testGetUserProfile() {
        // 执行获取用户信息
        Response response = userApiService.getUserProfile(authToken);
        
        // 验证响应状态码
        userApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        userApiService.verifyJsonStructure(response, 
            "code", "msg", "data", "data.id", "data.phone", "data.realname");
        
        // 验证业务状态码
        userApiService.verifyBusinessCode(response, 200);
        
        // 验证用户信息正确
        Assertions.assertThat(response.jsonPath().getString("data.phone"))
            .isEqualTo(testUserPhone);
        
        Assertions.assertThat(response.jsonPath().getString("data.realname"))
            .isEqualTo(testUserName);
    }
    
    /**
     * 测试修改用户信息
     */
    @Test(priority = 4, dependsOnMethods = {"testGetUserProfile"})
    public void testEditUserProfile() {
        // 新的用户名
        String newUserName = "更新用户" + generateRandomString(5);
        
        // 执行修改用户信息
        Response response = userApiService.editUserProfile(
            authToken, 
            userId, 
            newUserName, 
            "女", 
            30, 
            "更新后的地址"
        );
        
        // 验证响应状态码
        userApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        userApiService.verifyCommonResponseStructure(response);
        
        // 验证业务状态码
        userApiService.verifyBusinessCode(response, 200);
        
        // 验证更新成功
        Response profileResponse = userApiService.getUserProfile(authToken);
        Assertions.assertThat(profileResponse.jsonPath().getString("data.realname"))
            .isEqualTo(newUserName);
    }
    
    /**
     * 测试登录失败 - 错误的密码
     */
    @Test
    public void testLoginWithWrongPassword() {
        // 使用错误的密码登录
        Response response = userApiService.login(testUserPhone, "wrongpassword");
        
        // 验证响应状态码
        userApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        userApiService.verifyCommonResponseStructure(response);
        
        // 验证业务状态码不是200（登录失败）
        Assertions.assertThat(response.jsonPath().getInt("code")).isNotEqualTo(200);
    }
    
    /**
     * 测试登录失败 - 用户不存在
     */
    @Test
    public void testLoginWithNonExistentUser() {
        // 使用不存在的用户登录
        Response response = userApiService.login("19999999999", "anypassword");
        
        // 验证响应状态码
        userApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        userApiService.verifyCommonResponseStructure(response);
        
        // 验证业务状态码不是200（登录失败）
        Assertions.assertThat(response.jsonPath().getInt("code")).isNotEqualTo(200);
    }
} 