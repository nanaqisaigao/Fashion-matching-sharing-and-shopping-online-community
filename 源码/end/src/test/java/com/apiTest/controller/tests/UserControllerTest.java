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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private String token;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("测试用户登录-成功")
    public void testLoginSuccess() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("phone", "13800138000");  // 使用测试账号
        params.put("password", "123456");  // 使用测试密码

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        // 提取token供后续测试使用
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        token = (String) data.get("token");
        assertNotNull(token, "登录成功后应返回token");
    }

    @Test
    @DisplayName("测试用户登录-失败(密码错误)")
    public void testLoginFailWithWrongPassword() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("phone", "13800138000");
        params.put("password", "wrongpassword");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(500))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试用户注册")
    public void testRegister() throws Exception {
        // 生成随机手机号以确保测试不重复
        String randomPhone = "138" + String.format("%08d", System.currentTimeMillis() % 100000000);
        
        Map<String, Object> params = new HashMap<>();
        params.put("phone", randomPhone);
        params.put("password", "123456");
        params.put("realname", "测试用户");
        params.put("sex", "男");
        params.put("age", 25);
        params.put("address", "测试地址");

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试获取用户信息")
    public void testGetUserInfo() throws Exception {
        // 先登录获取token
        if (token == null) {
            testLoginSuccess();
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/user/userInfo")
                .header("token", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phone").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试修改用户信息")
    public void testEditUser() throws Exception {
        // 先登录获取token
        if (token == null) {
            testLoginSuccess();
        }

        // 先获取用户信息
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/userInfo")
                .header("token", token))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        int userId = (int) data.get("id");

        // 更新用户信息
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("id", userId);
        updateParams.put("realname", "更新的用户名" + System.currentTimeMillis() % 1000);
        updateParams.put("sex", "女");
        updateParams.put("age", 30);
        updateParams.put("address", "更新的地址");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/edit")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
    }
} 