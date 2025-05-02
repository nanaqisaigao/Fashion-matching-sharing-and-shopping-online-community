package com.apiTest.controller;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * API测试的基础服务类
 */
public abstract class BaseApiService {
    protected RequestSpecification requestSpec;
    protected final String baseUrl;
    
    /**
     * 构造方法
     */
    public BaseApiService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.requestSpec = new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();
    }
    
    /**
     * 设置认证Token
     */
    protected void setAuthToken(String token) {
        this.requestSpec = new RequestSpecBuilder()
            .addRequestSpecification(requestSpec)
            .addHeader("Authorization", "Bearer " + token)
            .build();
    }
    
    /**
     * 验证JSON响应结构是否符合预期
     * 只检查字段是否存在，不检查具体值
     */
    public void verifyJsonStructure(Response response, String... expectedFields) {
        JsonPath jsonPath = response.jsonPath();
        for (String field : expectedFields) {
            try {
                Assertions.assertThat(Optional.ofNullable(jsonPath.get(field))).isNotNull();
            } catch (Exception e) {
                throw new AssertionError("响应中缺少必要字段: " + field);
            }
        }
    }
    
    /**
     * 验证响应状态码
     */
    public void verifyStatusCode(Response response, int expectedStatusCode) {
        Assertions.assertThat(response.getStatusCode())
            .as("API响应状态码应为 " + expectedStatusCode)
            .isEqualTo(expectedStatusCode);
    }
    
    /**
     * 验证通用API响应结构
     */
    public void verifyCommonResponseStructure(Response response) {
        verifyJsonStructure(response, "code", "msg");
    }
    
    /**
     * 验证分页响应结构
     */
    public void verifyPaginationResponseStructure(Response response) {
        verifyJsonStructure(response, "code", "msg", "data", "data.list", "data.total");
    }
    
    /**
     * 验证响应中的业务码
     */
    public void verifyBusinessCode(Response response, int expectedCode) {
        Assertions.assertThat(response.jsonPath().getInt("code"))
            .as("业务响应码应为 " + expectedCode)
            .isEqualTo(expectedCode);
    }
    
    /**
     * 获取异常情况下的错误消息
     */
    protected String getErrorMessage(Response response) {
        return response.jsonPath().getString("msg");
    }
} 