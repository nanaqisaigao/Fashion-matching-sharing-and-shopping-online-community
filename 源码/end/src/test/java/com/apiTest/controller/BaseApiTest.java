package com.apiTest.controller;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

/**
 * 所有API测试的基类
 */
public class BaseApiTest {
    
    protected static final String API_BASE_URL = "http://localhost:8000";
    
    /**
     * 在所有测试套件前的准备工作
     */
    @BeforeSuite
    public void setupSuite() {
        // 配置RestAssured
        RestAssured.baseURI = API_BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    /**
     * 在所有测试类前的准备工作
     */
    @BeforeClass
    public void setupClass() {
        // 可以在这里添加每个测试类需要的初始化代码
    }
    
    /**
     * 生成随机字符串，用于测试数据
     */
    protected String generateRandomString(int length) {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * allowedChars.length());
            sb.append(allowedChars.charAt(randomIndex));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成随机手机号，用于测试数据
     */
    protected String generateRandomPhone() {
        StringBuilder sb = new StringBuilder("1");
        
        // 第二位数字
        int secondDigit = 3 + (int)(Math.random() * 7); // 3-9
        sb.append(secondDigit);
        
        // 剩余9位数字
        for (int i = 0; i < 9; i++) {
            int digit = (int)(Math.random() * 10); // 0-9
            sb.append(digit);
        }
        
        return sb.toString();
    }
    
    /**
     * 生成随机邮箱，用于测试数据
     */
    protected String generateRandomEmail() {
        String username = generateRandomString(8);
        String[] domains = {"example.com", "test.com", "sample.org", "demo.cn"};
        String domain = domains[(int)(Math.random() * domains.length)];
        
        return username + "@" + domain;
    }
} 