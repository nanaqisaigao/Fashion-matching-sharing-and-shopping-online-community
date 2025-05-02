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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
    private void login() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("phone", "13800138000");  // 测试账号
            params.put("password", "123456");    // 测试密码

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
     * 获取订单相关的商品ID
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
            throw new RuntimeException("没有可用的商品数据，无法进行订单测试");
        }
        
        Map<String, Object> product = (Map<String, Object>) ((java.util.List)data.get("list")).get(0);
        return (int) product.get("id");
    }

    @Test
    @DisplayName("测试创建订单")
    public void testCreateOrder() throws Exception {
        // 获取商品ID
        int productId = getProductId();
        
        // 准备订单明细
        List<Map<String, Object>> orderItems = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productId", productId);
        item.put("count", 2);
        item.put("amount", 199.98); // 单价 * 数量
        orderItems.add(item);
        
        // 创建订单
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("address", "测试地址");
        params.put("amount", 199.98);
        params.put("status", 0); // 0表示待支付
        params.put("orderItems", orderItems);
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/order/add")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        
        // 保存订单ID用于后续测试
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        if (responseMap.get("code").equals(200)) {
            orderId = (Integer) responseMap.get("data");
            assertNotNull(orderId, "创建订单成功后应返回订单ID");
        }
    }

    @Test
    @DisplayName("测试获取我的订单列表")
    public void testGetMyOrders() throws Exception {
        // 确保先创建一个订单
        if (orderId == null) {
            testCreateOrder();
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("currentPage", 1);
        params.put("pagesize", 10);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/order/selectPage")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.total").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试获取订单详情")
    public void testGetOrderDetail() throws Exception {
        // 确保先创建一个订单
        if (orderId == null) {
            testCreateOrder();
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/order/selectById")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(orderId))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试更新订单状态-支付")
    public void testUpdateOrderStatusToPaid() throws Exception {
        // 确保先创建一个订单
        if (orderId == null) {
            testCreateOrder();
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderId);
        params.put("status", 1); // 1表示已支付
        
        mockMvc.perform(MockMvcRequestBuilders.post("/order/edit")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
        
        // 验证订单状态已更新
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("id", orderId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/order/selectById")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value(1))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试更新订单状态-发货")
    public void testUpdateOrderStatusToShipped() throws Exception {
        // 确保订单已支付
        testUpdateOrderStatusToPaid();
        
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderId);
        params.put("status", 2); // 2表示已发货
        
        mockMvc.perform(MockMvcRequestBuilders.post("/order/edit")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
        
        // 验证订单状态已更新
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("id", orderId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/order/selectById")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value(2))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试取消订单")
    public void testCancelOrder() throws Exception {
        // 先创建一个新订单用于取消测试
        int productId = getProductId();
        
        List<Map<String, Object>> orderItems = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productId", productId);
        item.put("count", 1);
        item.put("amount", 99.99);
        orderItems.add(item);
        
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("userId", userId);
        createParams.put("address", "取消订单测试地址");
        createParams.put("amount", 99.99);
        createParams.put("status", 0);
        createParams.put("orderItems", orderItems);
        
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/order/add")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createParams)))
                .andReturn();
        
        String createResponse = createResult.getResponse().getContentAsString();
        Map<String, Object> createResponseMap = objectMapper.readValue(createResponse, Map.class);
        Integer cancelOrderId = (Integer) createResponseMap.get("data");
        
        // 取消订单
        Map<String, Object> cancelParams = new HashMap<>();
        cancelParams.put("id", cancelOrderId);
        cancelParams.put("status", 4); // 4表示已取消
        
        mockMvc.perform(MockMvcRequestBuilders.post("/order/edit")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
        
        // 验证订单状态为已取消
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("id", cancelOrderId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/order/selectById")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value(4))
                .andDo(MockMvcResultHandlers.print());
    }
} 