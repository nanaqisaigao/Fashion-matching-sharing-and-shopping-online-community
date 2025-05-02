package com.apiTest.controller.services;

import com.alibaba.fastjson.JSONObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.apiTest.controller.BaseApiService;

/**
 * 用户相关API服务类
 */
public class UserApiService extends BaseApiService {
    
    // API端点
    private static final String LOGIN_ENDPOINT = "/api/login";
    private static final String REGISTER_ENDPOINT = "/api/register";
    private static final String USER_PROFILE_ENDPOINT = "/api/user/profile";
    private static final String USER_EDIT_ENDPOINT = "/api/user/edit";
    private static final String USER_LIST_ENDPOINT = "/api/user/selectPage";
    
    /**
     * 构造方法
     */
    public UserApiService(String baseUrl) {
        super(baseUrl);
    }
    
    /**
     * 登录接口
     */
    public Response login(String phone, String password) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("phone", phone);
        requestBody.put("password", password);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(LOGIN_ENDPOINT);
    }
    
    /**
     * 注册接口
     */
    public Response register(String phone, String password, String realname, String sex, int age, String address) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("phone", phone);
        requestBody.put("password", password);
        requestBody.put("realname", realname);
        requestBody.put("sex", sex);
        requestBody.put("age", age);
        requestBody.put("address", address);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(REGISTER_ENDPOINT);
    }
    
    /**
     * 获取用户信息接口
     */
    public Response getUserProfile(String token) {
        setAuthToken(token);
        return RestAssured.given(requestSpec)
            .get(USER_PROFILE_ENDPOINT);
    }
    
    /**
     * 修改用户信息接口
     */
    public Response editUserProfile(String token, int id, String realname, String sex, int age, String address) {
        setAuthToken(token);
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("id", id);
        requestBody.put("realname", realname);
        requestBody.put("sex", sex);
        requestBody.put("age", age);
        requestBody.put("address", address);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(USER_EDIT_ENDPOINT);
    }
    
    /**
     * 查询用户列表接口
     */
    public Response getUserList(String token, String phone, String realname, int currentPage, int pagesize) {
        setAuthToken(token);
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("phone", phone);
        requestBody.put("realname", realname);
        requestBody.put("currentPage", currentPage);
        requestBody.put("pagesize", pagesize);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(USER_LIST_ENDPOINT);
    }
} 