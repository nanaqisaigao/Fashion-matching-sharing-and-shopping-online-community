package com.apiTest.controller.tests;

import com.apiTest.controller.services.ProductApiService;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.apiTest.controller.BaseApiTest;
import com.apiTest.controller.services.UserApiService;

/**
 * 商品API测试类
 */
public class ProductApiTest extends BaseApiTest {
    
    private ProductApiService productApiService;
    private UserApiService userApiService;
    private String adminToken;
    private int testProductId;
    
    /**
     * 初始化测试环境
     */
    @BeforeClass
    public void setup() {
        productApiService = new ProductApiService(API_BASE_URL);
        userApiService = new UserApiService(API_BASE_URL);
        
        // 管理员登录获取token
        Response loginResponse = userApiService.login("admin", "admin123");
        adminToken = loginResponse.jsonPath().getString("data.token");
    }
    
    /**
     * 测试添加商品
     */
    @Test(priority = 1)
    public void testAddProduct() {
        // 商品名称
        String productName = "测试商品" + generateRandomString(5);
        
        // 执行添加商品
        Response response = productApiService.addProduct(
            adminToken,
            productName,
            "https://example.com/image.jpg",
            99.99,
            100,
            1, // 假设分类ID为1
            "这是一个测试商品描述"
        );
        
        // 验证响应状态码
        productApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        productApiService.verifyCommonResponseStructure(response);
        
        // 验证业务状态码
        productApiService.verifyBusinessCode(response, 200);
        
        // 保存测试商品ID
        testProductId = response.jsonPath().getInt("data");
        
        // 验证商品ID大于0
        Assertions.assertThat(testProductId).isGreaterThan(0);
    }
    
    /**
     * 测试获取商品列表
     */
    @Test(priority = 2)
    public void testGetProductList() {
        // 执行获取商品列表
        Response response = productApiService.getProductList("", 0, 1, 10);
        
        // 验证响应状态码
        productApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        productApiService.verifyPaginationResponseStructure(response);
        
        // 验证业务状态码
        productApiService.verifyBusinessCode(response, 200);
        
        // 验证列表不为空
        int total = response.jsonPath().getInt("data.total");
        Assertions.assertThat(total).isGreaterThan(0);
    }
    
    /**
     * 测试获取商品详情
     */
    @Test(priority = 3, dependsOnMethods = {"testAddProduct"})
    public void testGetProductDetail() {
        // 执行获取商品详情
        Response response = productApiService.getProductDetail(testProductId);
        
        // 验证响应状态码
        productApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        productApiService.verifyJsonStructure(response, 
            "code", "msg", "data", "data.id", "data.name", "data.price", "data.image");
        
        // 验证业务状态码
        productApiService.verifyBusinessCode(response, 200);
        
        // 验证商品ID正确
        int productId = response.jsonPath().getInt("data.id");
        Assertions.assertThat(productId).isEqualTo(testProductId);
    }
    
    /**
     * 测试修改商品
     */
    @Test(priority = 4, dependsOnMethods = {"testGetProductDetail"})
    public void testEditProduct() {
        // 获取商品详情
        Response detailResponse = productApiService.getProductDetail(testProductId);
        
        // 修改后的商品名称
        String newProductName = "更新商品" + generateRandomString(5);
        
        // 执行修改商品
        Response response = productApiService.editProduct(
            adminToken,
            testProductId,
            newProductName,
            detailResponse.jsonPath().getString("data.image"),
            129.99,
            200,
            1, // 假设分类ID为1
            "这是更新后的商品描述"
        );
        
        // 验证响应状态码
        productApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        productApiService.verifyCommonResponseStructure(response);
        
        // 验证业务状态码
        productApiService.verifyBusinessCode(response, 200);
        
        // 验证更新成功
        Response updatedDetailResponse = productApiService.getProductDetail(testProductId);
        Assertions.assertThat(updatedDetailResponse.jsonPath().getString("data.name"))
            .isEqualTo(newProductName);
        
        Assertions.assertThat(updatedDetailResponse.jsonPath().getDouble("data.price"))
            .isEqualTo(129.99);
    }
    
    /**
     * 测试添加商品评论
     */
    @Test(priority = 5, dependsOnMethods = {"testEditProduct"})
    public void testAddProductReview() {
        // 假设用户ID为1
        int userId = 1;
        
        // 执行添加商品评论
        Response response = productApiService.addProductReview(
            adminToken,
            testProductId,
            userId,
            5, // 5星评价
            "这是一条测试评论"
        );
        
        // 验证响应状态码
        productApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        productApiService.verifyCommonResponseStructure(response);
        
        // 验证业务状态码
        productApiService.verifyBusinessCode(response, 200);
    }
    
    /**
     * 测试获取商品评论列表
     */
    @Test(priority = 6, dependsOnMethods = {"testAddProductReview"})
    public void testGetProductReviews() {
        // 执行获取商品评论列表
        Response response = productApiService.getProductReviews(testProductId, 1, 10);
        
        // 验证响应状态码
        productApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        productApiService.verifyPaginationResponseStructure(response);
        
        // 验证业务状态码
        productApiService.verifyBusinessCode(response, 200);
        
        // 验证评论列表不为空
        int total = response.jsonPath().getInt("data.total");
        Assertions.assertThat(total).isGreaterThan(0);
    }
    
    /**
     * 测试删除商品
     */
    @Test(priority = 7, dependsOnMethods = {"testGetProductReviews"})
    public void testDeleteProduct() {
        // 执行删除商品
        Response response = productApiService.deleteProduct(adminToken, testProductId);
        
        // 验证响应状态码
        productApiService.verifyStatusCode(response, 200);
        
        // 验证响应结构
        productApiService.verifyCommonResponseStructure(response);
        
        // 验证业务状态码
        productApiService.verifyBusinessCode(response, 200);
    }
} 