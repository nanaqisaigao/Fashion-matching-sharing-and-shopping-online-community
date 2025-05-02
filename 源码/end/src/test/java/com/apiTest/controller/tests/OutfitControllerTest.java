package com.apiTest.controller.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class OutfitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private String token;
    private int userId;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        // 如果token为空，需要先登录
        if (token == null) {
            login();
        }
    }

    /**
     * 登录获取token
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

    @Test
    @DisplayName("测试获取穿搭列表")
    public void testGetOutfitList() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", 1);
        params.put("pagesize", 10);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/outfit/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.total").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试根据ID获取穿搭详情")
    public void testGetOutfitById() throws Exception {
        // 先获取穿搭列表，找到一个穿搭ID
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", 1);
        params.put("pagesize", 1);
        
        MvcResult listResult = mockMvc.perform(MockMvcRequestBuilders.post("/outfit/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();
        
        String listResponse = listResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(listResponse, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        
        if (data == null || ((java.util.List)data.get("list")).isEmpty()) {
            // 如果没有穿搭数据，则跳过此测试
            System.out.println("没有可用的穿搭数据，跳过测试");
            return;
        }
        
        // 获取第一个穿搭的ID
        Map<String, Object> outfit = (Map<String, Object>) ((java.util.List)data.get("list")).get(0);
        int outfitId = (int) outfit.get("id");
        
        // 测试获取穿搭详情
        Map<String, Object> detailParams = new HashMap<>();
        detailParams.put("id", outfitId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/outfit/selectById")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detailParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(outfitId))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试创建穿搭")
    public void testCreateOutfit() throws Exception {
        // 创建穿搭数据
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("title", "测试穿搭标题" + System.currentTimeMillis());
        params.put("description", "这是一个测试穿搭描述");
        params.put("image", "test_outfit_image.jpg");  // 假设的图片路径
        
        mockMvc.perform(MockMvcRequestBuilders.post("/outfit/add")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试穿搭点赞")
    public void testLikeOutfit() throws Exception {
        // 先获取穿搭列表，找到一个穿搭ID
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", 1);
        params.put("pagesize", 1);
        
        MvcResult listResult = mockMvc.perform(MockMvcRequestBuilders.post("/outfit/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andReturn();
        
        String listResponse = listResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(listResponse, Map.class);
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        
        if (data == null || ((java.util.List)data.get("list")).isEmpty()) {
            // 如果没有穿搭数据，则跳过此测试
            System.out.println("没有可用的穿搭数据，跳过测试");
            return;
        }
        
        // 获取第一个穿搭的ID
        Map<String, Object> outfit = (Map<String, Object>) ((java.util.List)data.get("list")).get(0);
        int outfitId = (int) outfit.get("id");
        
        // 测试点赞
        Map<String, Object> likeParams = new HashMap<>();
        likeParams.put("outfitId", outfitId);
        likeParams.put("userId", userId);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/outfit/like")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(likeParams)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("测试删除穿搭")
    public void testDeleteOutfit() throws Exception {
        // 先创建一个穿搭
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("userId", userId);
        createParams.put("title", "要删除的测试穿搭" + System.currentTimeMillis());
        createParams.put("description", "这是一个要删除的测试穿搭");
        createParams.put("image", "delete_test_outfit.jpg");
        
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/outfit/add")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createParams)))
                .andReturn();
        
        String createResponse = createResult.getResponse().getContentAsString();
        Map<String, Object> createResponseMap = objectMapper.readValue(createResponse, Map.class);
        
        if (createResponseMap.get("code").equals(200)) {
            // 获取创建的穿搭ID
            int outfitId = (int) createResponseMap.get("data");
            
            // 删除穿搭
            Map<String, Object> deleteParams = new HashMap<>();
            deleteParams.put("id", outfitId);
            
            mockMvc.perform(MockMvcRequestBuilders.post("/outfit/delete")
                    .header("token", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deleteParams)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                    .andDo(MockMvcResultHandlers.print());
        } else {
            System.out.println("创建穿搭失败，跳过删除测试");
        }
    }

    @Test
    @DisplayName("测试获取我的穿搭列表")
    public void testGetMyOutfits() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("currentPage", 1);
        params.put("pagesize", 10);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/outfit/selectPageByUserId")
                .header("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.total").exists())
                .andDo(MockMvcResultHandlers.print());
    }
} 