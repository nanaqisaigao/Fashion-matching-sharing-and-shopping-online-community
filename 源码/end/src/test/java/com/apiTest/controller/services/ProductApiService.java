package com.apiTest.controller.services;

import com.alibaba.fastjson.JSONObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.apiTest.controller.BaseApiService;

/**
 * 商品相关API服务类
 */
public class ProductApiService extends BaseApiService {
    
    // API端点
    private static final String PRODUCT_LIST_ENDPOINT = "/api/goods/selectPage";
    private static final String PRODUCT_DETAIL_ENDPOINT = "/api/goods/selectById";
    private static final String PRODUCT_ADD_ENDPOINT = "/api/goods/add";
    private static final String PRODUCT_EDIT_ENDPOINT = "/api/goods/edit";
    private static final String PRODUCT_DELETE_ENDPOINT = "/api/goods/delete";
    private static final String REVIEW_ADD_ENDPOINT = "/api/review/add";
    private static final String REVIEW_LIST_ENDPOINT = "/api/review/selectPage";
    
    /**
     * 构造方法
     */
    public ProductApiService(String baseUrl) {
        super(baseUrl);
    }
    
    /**
     * 获取商品列表
     */
    public Response getProductList(String name, int categoryId, int currentPage, int pagesize) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("name", name);
        requestBody.put("categoryId", categoryId);
        requestBody.put("currentPage", currentPage);
        requestBody.put("pagesize", pagesize);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(PRODUCT_LIST_ENDPOINT);
    }
    
    /**
     * 获取商品详情
     */
    public Response getProductDetail(int productId) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("id", productId);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(PRODUCT_DETAIL_ENDPOINT);
    }
    
    /**
     * 添加商品
     */
    public Response addProduct(String token, String name, String image, double price, int stock, 
                               int categoryId, String description) {
        setAuthToken(token);
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("name", name);
        requestBody.put("image", image);
        requestBody.put("price", price);
        requestBody.put("stock", stock);
        requestBody.put("categoryId", categoryId);
        requestBody.put("description", description);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(PRODUCT_ADD_ENDPOINT);
    }
    
    /**
     * 编辑商品
     */
    public Response editProduct(String token, int id, String name, String image, double price, 
                                int stock, int categoryId, String description) {
        setAuthToken(token);
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("id", id);
        requestBody.put("name", name);
        requestBody.put("image", image);
        requestBody.put("price", price);
        requestBody.put("stock", stock);
        requestBody.put("categoryId", categoryId);
        requestBody.put("description", description);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(PRODUCT_EDIT_ENDPOINT);
    }
    
    /**
     * 删除商品
     */
    public Response deleteProduct(String token, int productId) {
        setAuthToken(token);
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("id", productId);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(PRODUCT_DELETE_ENDPOINT);
    }
    
    /**
     * 添加商品评论
     */
    public Response addProductReview(String token, int productId, int userId, int rating, String content) {
        setAuthToken(token);
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("productId", productId);
        requestBody.put("userId", userId);
        requestBody.put("rating", rating);
        requestBody.put("content", content);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(REVIEW_ADD_ENDPOINT);
    }
    
    /**
     * 获取商品评论列表
     */
    public Response getProductReviews(int productId, int currentPage, int pagesize) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("productId", productId);
        requestBody.put("currentPage", currentPage);
        requestBody.put("pagesize", pagesize);
        
        return RestAssured.given(requestSpec)
            .body(requestBody.toString())
            .post(REVIEW_LIST_ENDPOINT);
    }
} 