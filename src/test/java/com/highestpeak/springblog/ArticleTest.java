package com.highestpeak.springblog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void exampleMockMvcUse() throws Exception {
        String title = "java learning";
        String expect = "{\"title\":\"java learning\",\"author\":\"dax\",\"price\":78.56,\"releaseTime\":\"2018-03-22\"}";
        mockMvc.perform(
                MockMvcRequestBuilders.get("/book/get")
                .param("title", title)
        ).andExpect(
                MockMvcResultMatchers.content()
                        .json(expect)
        ).andDo(
                MockMvcResultHandlers.print()
        );
    }

    @Test
    void articleListTest() throws Exception{

    }
}
