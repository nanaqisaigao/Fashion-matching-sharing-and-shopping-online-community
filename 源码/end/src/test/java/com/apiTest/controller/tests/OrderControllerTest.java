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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private String token;
    private int userId;
    private Integer orderId; // 用于测试中保存创建的订单ID

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
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            
            if (responseMap.get("code").equals(200)) {
                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                token = (String) data.get("token");
                Map<String, Object> userInfo = (Map<String, Object>) data.get("userInfo");
                userId = (int) userInfo.get("id");
                
                assertNotNull(token, "登录成功后应返回token");
            } else {
                throw new RuntimeException("登录失败: " + responseMap.get("msg"));
            }
        } catch (Exception e) {
            throw new RuntimeException("登录操作异常", e);
        }
    }
    
    /**
     * 获取用户购物车商品作为订单商品
     */
    private List<Map<String, Object>> getCartItems() throws Exception {
        // 确保购物车中有商品
        ensureCartHasItems();
        
        // 获取购物车商品
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cart/selectByUserId")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        
        if (responseMap.get("code").equals(200) && responseMap.get("data") != null) {
            return (List<Map<String, Object>>) responseMap.get("data");
        } else {
            throw new RuntimeException("获取购物车商品失败");
        }
    }
    
    /**
     * 确保购物车中有商品
     */
    private void ensureCartHasItems() throws Exception {
        // 获取商品
        Map<String, Object> goodsParams = new HashMap<>();
        goodsParams.put("currentPage", 1);
        goodsParams.put("pagesize", 2);
        
        MvcResult goodsResult = mockMvc.perform(MockMvcRequestBuilders.post("/goods/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goodsParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        
        String goodsResponse = goodsResult.getResponse().getContentAsString();
        Map<String, Object> goodsResponseMap = objectMapper.readValue(goodsResponse, Map.class);
        Map<String, Object> goodsData = (Map<String, Object>) goodsResponseMap.get("data");
        List<Map<String, Object>> goodsList = (List<Map<String, Object>>) goodsData.get("list");
        
        if (goodsList == null || goodsList.isEmpty()) {
            throw new RuntimeException("没有可用的商品数据");
        }
        
        // 先清空购物车
        Map<String, Object> clearParams = new HashMap<>();
        clearParams.put("userId", userId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/deleteByUserId")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clearParams)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        
        // 添加商品到购物车
        for (Map<String, Object> goods : goodsList) {
            Map<String, Object> addParams = new HashMap<>();
            addParams.put("userId", userId);
            addParams.put("productId", goods.get("id"));
            addParams.put("count", 1);
            
            mockMvc.perform(MockMvcRequestBuilders.post("/cart/add")
                    .header("token", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addParams)))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }

    @Test
    @DisplayName("测试创建订单")
    public void testCreateOrder() throws Exception {
        // 获取购物车商品
        List<Map<String, Object>> cartItems = getCartItems();
        assertFalse(cartItems.isEmpty(), "购物车不应为空");
        
        // 准备订单数据
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("address", "测试地址");
        orderData.put("name", "测试用户");
        orderData.put("phone", "13800138000");
        
        // 计算总金额
        double total = 0;
        List<Map<String, Object>> orderItems = new ArrayList<>();
        
        for (Map<String, Object> item : cartItems) {
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("productId", item.get("productId"));
            orderItem.put("count", item.get("count"));
            orderItem.put("productPrice", item.get("price"));
            
            int count = (int) item.get("count");
            double price = Double.parseDouble(item.get("price").toString());
            total += count * price;
            
            orderItems.add(orderItem);
        }
        
        orderData.put("total", total);
        orderData.put("orderItems", orderItems);
        
        // 创建订单
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/orders/create")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        
        // 获取订单ID
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        
        if (responseMap.get("code").equals(200)) {
            orderId = (Integer) responseMap.get("data");
            assertNotNull(orderId, "创建订单成功后应返回订单ID");
        }
    }

    @Test
    @DisplayName("测试获取订单详情")
    public void testGetOrderDetail() throws Exception {
        // 确保有订单
        if (orderId == null) {
            testCreateOrder();
        }
        
        // 获取订单详情
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/getById/" + orderId)
                .header("token", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(orderId))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试获取我的订单列表")
    public void testGetMyOrders() throws Exception {
        // 确保有订单
        if (orderId == null) {
            testCreateOrder();
        }
        
        // 获取我的订单列表
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("currentPage", 1);
        params.put("pagesize", 10);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/selectPage")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试更新订单状态")
    public void testUpdateOrderStatus() throws Exception {
        // 确保有订单
        if (orderId == null) {
            testCreateOrder();
        }
        
        // 更新订单状态为已支付
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderId);
        params.put("status", "已支付"); // 更新为已支付状态
        
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/update")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
        
        // 验证订单状态已更新
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/orders/getById/" + orderId)
                .header("token", token))
                .andReturn();
                
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        
        assertEquals("已支付", data.get("status"), "订单状态应该已更新为已支付");
    }

    @Test
    @DisplayName("测试取消订单")
    public void testCancelOrder() throws Exception {
        // 先创建一个新订单用于取消测试
        testCreateOrder();
        int orderIdToCancel = orderId;
        
        // 取消订单
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderIdToCancel);
        params.put("status", "已取消"); // 设置状态为已取消
        
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/update")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
        
        // 验证订单状态已更新为取消
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/orders/getById/" + orderIdToCancel)
                .header("token", token))
                .andReturn();
                
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        
        assertEquals("已取消", data.get("status"), "订单状态应该已更新为已取消");
    }
} 