package com.mty.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mty.entity.Goods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class GoodsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSelectPage() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("currentPage", "1");
        params.put("pagesize", "10");

        mockMvc.perform(MockMvcRequestBuilders.post("/goods/selectPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSelectOne() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/goods/selectOne")
                .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAdd() throws Exception {
        Goods goods = new Goods();
        goods.setName("Test Product");
        goods.setMoney("100");

        mockMvc.perform(MockMvcRequestBuilders.post("/goods/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goods)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testEdit() throws Exception {
        Goods goods = new Goods();
        goods.setId(1);
        goods.setName("Updated Product");

        mockMvc.perform(MockMvcRequestBuilders.post("/goods/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goods)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/goods/deleteById")
                .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFrontPage() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("currentPage", "1");
        params.put("pagesize", "10");

        mockMvc.perform(MockMvcRequestBuilders.post("/goods/frontPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFrontOne() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/goods/frontOne")
                .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFrontAll() throws Exception {
        Goods goods = new Goods();
        goods.setName("Test Product");

        mockMvc.perform(MockMvcRequestBuilders.post("/goods/frontAll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goods)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFrontBySales() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/goods/frontBySales"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testListByIds() throws Exception {
        Map<String, List<Integer>> request = new HashMap<>();
        request.put("ids", List.of(1, 2, 3));

        mockMvc.perform(MockMvcRequestBuilders.post("/goods/listByIds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}