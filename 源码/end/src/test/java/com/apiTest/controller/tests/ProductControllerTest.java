package com.apiTest.controller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private String adminToken;
    private String userToken;
    private int userId;
    private Integer categoryId;
    private Integer productId;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        
        // 如果token为空，需要先获取token
        if (adminToken == null) {
            adminLogin();
        }
        
        if (userToken == null) {
            userLogin();
        }
        
        // 确保有商品分类
        if (categoryId == null) {
            try {
                categoryId = getFirstCategoryId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 管理员登录获取token
     */
    private void adminLogin() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("phone", "admin");  // 管理员账号
            params.put("password", "123456");    // 管理员密码

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            
            if (responseMap.get("code").equals(200)) {
                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                adminToken = (String) data.get("token");
                assertNotNull(adminToken, "管理员登录成功后应返回token");
            } else {
                throw new RuntimeException("管理员登录失败: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            throw new RuntimeException("管理员登录操作异常", e);
        }
    }
    
    /**
     * 普通用户登录获取token
     */
    private void userLogin() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("phone", "15527752855");  // 测试账号
            params.put("password", "123456789");    // 测试密码

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            
            if (responseMap.get("code").equals(200)) {
                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                userToken = (String) data.get("token");
                Map<String, Object> userInfo = (Map<String, Object>) data.get("userInfo");
                userId = (int) userInfo.get("id");
                
                assertNotNull(userToken, "用户登录成功后应返回token");
            } else {
                throw new RuntimeException("用户登录失败: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            throw new RuntimeException("用户登录操作异常", e);
        }
    }
    
    /**
     * 获取第一个分类ID
     */
    private int getFirstCategoryId() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/category/selectAll"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
                
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        
        if (responseMap.get("code").equals(0)) {
            List<Map<String, Object>> categories = (List<Map<String, Object>>) responseMap.get("data");
            
            if (categories == null || categories.isEmpty()) {
                throw new RuntimeException("没有可用的商品分类数据");
            }
            
            return (int) categories.get(0).get("id");
        } else {
            throw new RuntimeException("获取分类失败: " + responseMap.get("msg"));
        }
    }

    @Test
    @DisplayName("测试添加商品")
    public void testAddProduct() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "测试商品" + System.currentTimeMillis());
        params.put("price", "199.99");
        params.put("categoryId", categoryId);
        params.put("inventory", 100);
        params.put("picture", "http://example.com/test.jpg");
        params.put("detail", "这是一个测试商品的详细描述");
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/goods/add")
                .header("token", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        productId = (Integer) responseMap.get("data");
        
        assertNotNull(productId, "添加商品成功后应返回商品ID");
    }

    @Test
    @DisplayName("测试获取商品列表")
    public void testGetProductList() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", 1);
        params.put("pagesize", 10);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/goods/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.total").isNumber())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试获取商品详情")
    public void testGetProductDetail() throws Exception {
        // 确保先添加商品
        if (productId == null) {
            testAddProduct();
        }
        
        mockMvc.perform(MockMvcRequestBuilders.get("/goods/getById/" + productId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(productId))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试按分类查询商品")
    public void testGetProductsByCategory() throws Exception {
        // 确保已获取分类ID
        assertNotNull(categoryId, "应该有可用的分类ID");
        
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", 1);
        params.put("pagesize", 10);
        params.put("categoryId", categoryId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/goods/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试搜索商品")
    public void testSearchProducts() throws Exception {
        // 确保先添加商品
        if (productId == null) {
            testAddProduct();
        }
        
        // 获取商品名称
        MvcResult detailResult = mockMvc.perform(MockMvcRequestBuilders.get("/goods/getById/" + productId))
                .andReturn();
                
        String detailResponse = detailResult.getResponse().getContentAsString();
        Map<String, Object> detailResponseMap = objectMapper.readValue(detailResponse, Map.class);
        Map<String, Object> productData = (Map<String, Object>) detailResponseMap.get("data");
        String productName = (String) productData.get("name");
        
        // 使用商品名称的一部分进行搜索
        String searchTerm = productName.substring(0, 3);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/goods/search/" + searchTerm))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试更新商品")
    public void testUpdateProduct() throws Exception {
        // 确保先添加商品
        if (productId == null) {
            testAddProduct();
        }
        
        // 更新商品信息
        Map<String, Object> params = new HashMap<>();
        params.put("id", productId);
        params.put("name", "更新后的商品名称");
        params.put("price", "299.99");
        params.put("categoryId", categoryId);
        params.put("inventory", 50);
        params.put("picture", "http://example.com/updated.jpg");
        params.put("detail", "这是更新后的商品描述");
        
        mockMvc.perform(MockMvcRequestBuilders.post("/goods/edit")
                .header("token", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(MockMvcResultHandlers.print());
        
        // 验证更新成功
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/goods/getById/" + productId))
                .andReturn();
                
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        
        assertEquals("更新后的商品名称", data.get("name"), "商品名称应该已更新");
        assertEquals("299.99", data.get("price").toString(), "商品价格应该已更新");
    }

    @Test
    @DisplayName("测试删除商品")
    public void testDeleteProduct() throws Exception {
        // 先添加一个新商品用于删除测试
        Map<String, Object> addParams = new HashMap<>();
        addParams.put("name", "待删除商品" + System.currentTimeMillis());
        addParams.put("price", "99.99");
        addParams.put("categoryId", categoryId);
        addParams.put("inventory", 10);
        addParams.put("picture", "http://example.com/delete.jpg");
        addParams.put("detail", "这是一个将被删除的测试商品");
        
        MvcResult addResult = mockMvc.perform(MockMvcRequestBuilders.post("/goods/add")
                .header("token", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addParams)))
                .andReturn();
        
        String addResponse = addResult.getResponse().getContentAsString();
        Map<String, Object> addResponseMap = objectMapper.readValue(addResponse, Map.class);
        Integer productIdToDelete = (Integer) addResponseMap.get("data");
        
        // 删除商品
        Map<String, Object> deleteParams = new HashMap<>();
        deleteParams.put("id", productIdToDelete);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/goods/delete")
                .header("token", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(MockMvcResultHandlers.print());
        
        // 验证商品已被删除（查询应返回错误或空数据）
        MvcResult checkResult = mockMvc.perform(MockMvcRequestBuilders.get("/goods/getById/" + productIdToDelete))
                .andReturn();
                
        String checkResponse = checkResult.getResponse().getContentAsString();
        Map<String, Object> checkResponseMap = objectMapper.readValue(checkResponse, Map.class);
        
        // 根据API设计，删除后查询可能返回空数据或错误码
        if (checkResponseMap.get("data") != null) {
            Map<String, Object> data = (Map<String, Object>) checkResponseMap.get("data");
            assertTrue(data.isEmpty() || data.get("id") == null, "被删除的商品不应该能被查询到");
        } else {
            assertNotEquals(0, checkResponseMap.get("code"), "查询已删除商品应返回错误码");
        }
    }
} 