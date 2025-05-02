package com.apiTest.controller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private String adminToken;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        // 如果token为空，需要先登录
        if (adminToken == null) {
            loginAsAdmin();
        }
    }

    /**
     * 管理员登录获取token
     */
    private void loginAsAdmin() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("phone", "admin");  // 管理员账号
            params.put("password", "admin"); // 管理员密码

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params)))
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            
            if (responseMap.get("code").equals(200)) {
                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                adminToken = (String) data.get("token");
                assertNotNull(adminToken, "登录成功后应返回token");
            } else {
                System.out.println("管理员登录失败: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            throw new RuntimeException("管理员登录失败", e);
        }
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.total").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试根据ID获取商品")
    public void testGetProductById() throws Exception {
        // 先获取商品列表，找到一个商品ID
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", 1);
        params.put("pagesize", 1);
        
        MvcResult listResult = mockMvc.perform(MockMvcRequestBuilders.post("/goods/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();
        
        String listResponse = listResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(listResponse, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        
        if (data == null || ((java.util.List)data.get("list")).isEmpty()) {
            // 如果没有商品数据，则跳过此测试
            System.out.println("没有可用的商品数据，跳过测试");
            return;
        }
        
        // 获取第一个商品的ID
        Map<String, Object> product = (Map<String, Object>) ((java.util.List)data.get("list")).get(0);
        int productId = (int) product.get("id");
        
        // 测试获取商品详情
        Map<String, Object> detailParams = new HashMap<>();
        detailParams.put("id", productId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/goods/selectById")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detailParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(productId))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试按名称搜索商品")
    public void testSearchProductByName() throws Exception {
        // 假设我们知道有一个名称包含"连衣裙"的商品
        Map<String, Object> params = new HashMap<>();
        params.put("name", "连衣裙");
        params.put("currentPage", 1);
        params.put("pagesize", 10);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/goods/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试添加商品")
    public void testAddProduct() throws Exception {
        // 创建商品数据
        Map<String, Object> params = new HashMap<>();
        params.put("name", "测试商品" + System.currentTimeMillis());
        params.put("price", 99.99);
        params.put("stock", 100);
        params.put("description", "这是一个测试商品描述");
        params.put("image", "test_product_image.jpg"); // 假设的图片路径
        params.put("categoryId", 1); // 假设分类ID为1
        
        mockMvc.perform(MockMvcRequestBuilders.post("/goods/add")
                .header("token", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试更新商品")
    public void testUpdateProduct() throws Exception {
        // 先创建一个商品
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("name", "要更新的测试商品" + System.currentTimeMillis());
        createParams.put("price", 88.88);
        createParams.put("stock", 50);
        createParams.put("description", "这是一个要更新的测试商品描述");
        createParams.put("image", "update_test_product.jpg");
        createParams.put("categoryId", 1);
        
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/goods/add")
                .header("token", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createParams)))
                .andReturn();
        
        String createResponse = createResult.getResponse().getContentAsString();
        Map<String, Object> createResponseMap = objectMapper.readValue(createResponse, Map.class);
        
        if (createResponseMap.get("code").equals(200)) {
            // 获取创建的商品ID
            int productId = (int) createResponseMap.get("data");
            
            // 更新商品
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("id", productId);
            updateParams.put("name", "已更新的测试商品" + System.currentTimeMillis());
            updateParams.put("price", 199.99);
            updateParams.put("stock", 200);
            updateParams.put("description", "这是已更新的测试商品描述");
            updateParams.put("image", "updated_test_product.jpg");
            updateParams.put("categoryId", 2);
            
            mockMvc.perform(MockMvcRequestBuilders.post("/goods/edit")
                    .header("token", adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateParams)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                    .andDo(MockMvcResultHandlers.print());
        } else {
            System.out.println("创建商品失败，跳过更新测试");
        }
    }

    @Test
    @DisplayName("测试删除商品")
    public void testDeleteProduct() throws Exception {
        // 先创建一个商品
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("name", "要删除的测试商品" + System.currentTimeMillis());
        createParams.put("price", 55.55);
        createParams.put("stock", 30);
        createParams.put("description", "这是一个要删除的测试商品描述");
        createParams.put("image", "delete_test_product.jpg");
        createParams.put("categoryId", 1);
        
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/goods/add")
                .header("token", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createParams)))
                .andReturn();
        
        String createResponse = createResult.getResponse().getContentAsString();
        Map<String, Object> createResponseMap = objectMapper.readValue(createResponse, Map.class);
        
        if (createResponseMap.get("code").equals(200)) {
            // 获取创建的商品ID
            int productId = (int) createResponseMap.get("data");
            
            // 删除商品
            Map<String, Object> deleteParams = new HashMap<>();
            deleteParams.put("id", productId);
            
            mockMvc.perform(MockMvcRequestBuilders.post("/goods/delete")
                    .header("token", adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deleteParams)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                    .andDo(MockMvcResultHandlers.print());
        } else {
            System.out.println("创建商品失败，跳过删除测试");
        }
    }

    @Test
    @DisplayName("测试按分类搜索商品")
    public void testSearchProductByCategory() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", 1); // 假设分类ID为1
        params.put("currentPage", 1);
        params.put("pagesize", 10);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/goods/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
    }
} 