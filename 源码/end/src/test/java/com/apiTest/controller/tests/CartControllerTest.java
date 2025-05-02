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
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private String token;
    private int userId;
    private Integer cartItemId; // 用于测试中保存创建的购物车项ID

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        // 如果token为空，需要先登录
        if (token == null) {
            login();
        }
    }

    /**
     * 用户登录获取token
     */
    public void login() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("phone", "15527752855");  // 测试账号
            params.put("password", "123456789");    // 测试密码

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params)))
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
            token = (String) data.get("token");
            Map<String, Object> userInfo = (Map<String, Object>) data.get("userInfo");
            userId = (int) userInfo.get("id");
            
            assertNotNull(token, "登录成功后应返回token");
        } catch (Exception e) {
            throw new RuntimeException("登录失败", e);
        }
    }

    /**
     * 获取商品ID用于购物车测试
     */
    private int getProductId() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", 1);
        params.put("pagesize", 1);
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/goods/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        
        if (data == null || ((java.util.List)data.get("list")).isEmpty()) {
            throw new RuntimeException("没有可用的商品数据，无法进行购物车测试");
        }
        
        Map<String, Object> product = (Map<String, Object>) ((java.util.List)data.get("list")).get(0);
        return (int) product.get("id");
    }

    @Test
    @DisplayName("测试添加商品到购物车")
    public void testAddToCart() throws Exception {
        // 获取商品ID
        int productId = getProductId();
        
        // 添加商品到购物车
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("productId", productId);
        params.put("count", 2);
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cart/add")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        
        // 保存购物车项ID用于后续测试
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        if (responseMap.get("code").equals(200)) {
            cartItemId = (Integer) responseMap.get("data");
            assertNotNull(cartItemId, "添加购物车成功后应返回购物车项ID");
        }
    }

    @Test
    @DisplayName("测试获取我的购物车列表")
    public void testGetMyCart() throws Exception {
        // 确保先添加商品到购物车
        if (cartItemId == null) {
            testAddToCart();
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/selectByUserId")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试更新购物车商品数量")
    public void testUpdateCartItemCount() throws Exception {
        // 确保先添加商品到购物车
        if (cartItemId == null) {
            testAddToCart();
        }
        
        // 更新购物车商品数量
        Map<String, Object> params = new HashMap<>();
        params.put("id", cartItemId);
        params.put("count", 5);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/edit")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
        
        // 验证数量已更新
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("userId", userId);
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cart/selectByUserId")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkParams)))
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        java.util.List<Map<String, Object>> cartItems = (java.util.List<Map<String, Object>>) responseMap.get("data");
        
        boolean found = false;
        for (Map<String, Object> item : cartItems) {
            if (item.get("id").equals(cartItemId)) {
                assertEquals(5, item.get("count"), "购物车商品数量应该已更新为5");
                found = true;
                break;
            }
        }
        
        assertTrue(found, "应该能找到更新后的购物车项");
    }

    @Test
    @DisplayName("测试从购物车中删除商品")
    public void testRemoveFromCart() throws Exception {
        // 先添加一个新商品到购物车用于删除测试
        int productId = getProductId();
        
        Map<String, Object> addParams = new HashMap<>();
        addParams.put("userId", userId);
        addParams.put("productId", productId);
        addParams.put("count", 1);
        
        MvcResult addResult = mockMvc.perform(MockMvcRequestBuilders.post("/cart/add")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addParams)))
                .andReturn();
        
        String addResponse = addResult.getResponse().getContentAsString();
        Map<String, Object> addResponseMap = objectMapper.readValue(addResponse, Map.class);
        Integer itemIdToDelete = (Integer) addResponseMap.get("data");
        
        // 删除购物车商品
        Map<String, Object> deleteParams = new HashMap<>();
        deleteParams.put("id", itemIdToDelete);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/delete")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
        
        // 验证商品已从购物车中删除
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("userId", userId);
        
        MvcResult checkResult = mockMvc.perform(MockMvcRequestBuilders.post("/cart/selectByUserId")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkParams)))
                .andReturn();
        
        String checkResponse = checkResult.getResponse().getContentAsString();
        Map<String, Object> checkResponseMap = objectMapper.readValue(checkResponse, Map.class);
        java.util.List<Map<String, Object>> cartItems = (java.util.List<Map<String, Object>>) checkResponseMap.get("data");
        
        boolean found = false;
        for (Map<String, Object> item : cartItems) {
            if (item.get("id").equals(itemIdToDelete)) {
                found = true;
                break;
            }
        }
        
        assertFalse(found, "删除的购物车项不应该出现在购物车列表中");
    }

    @Test
    @DisplayName("测试清空购物车")
    public void testClearCart() throws Exception {
        // 先添加几个商品到购物车
        int productId = getProductId();
        
        Map<String, Object> addParams = new HashMap<>();
        addParams.put("userId", userId);
        addParams.put("productId", productId);
        addParams.put("count", 1);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/add")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addParams)));
        
        // 清空购物车 (假设有提供此API)
        // 如果没有直接清空购物车的API，可以遍历购物车列表并逐个删除
        Map<String, Object> listParams = new HashMap<>();
        listParams.put("userId", userId);
        
        MvcResult listResult = mockMvc.perform(MockMvcRequestBuilders.post("/cart/selectByUserId")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(listParams)))
                .andReturn();
        
        String listResponse = listResult.getResponse().getContentAsString();
        Map<String, Object> listResponseMap = objectMapper.readValue(listResponse, Map.class);
        java.util.List<Map<String, Object>> cartItems = (java.util.List<Map<String, Object>>) listResponseMap.get("data");
        
        // 逐个删除购物车项
        for (Map<String, Object> item : cartItems) {
            Map<String, Object> deleteParams = new HashMap<>();
            deleteParams.put("id", item.get("id"));
            
            mockMvc.perform(MockMvcRequestBuilders.post("/cart/delete")
                    .header("token", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deleteParams)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
        }
        
        // 验证购物车已清空
        MvcResult checkResult = mockMvc.perform(MockMvcRequestBuilders.post("/cart/selectByUserId")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(listParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        
        String checkResponse = checkResult.getResponse().getContentAsString();
        Map<String, Object> checkResponseMap = objectMapper.readValue(checkResponse, Map.class);
        java.util.List<Map<String, Object>> remainingItems = (java.util.List<Map<String, Object>>) checkResponseMap.get("data");
        
        assertTrue(remainingItems.isEmpty(), "购物车应该已清空");
    }
} 