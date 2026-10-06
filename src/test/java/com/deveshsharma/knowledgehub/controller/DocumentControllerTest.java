package com.deveshsharma.knowledgehub.controller;

import com.deveshsharma.knowledgehub.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.mock;

class DocumentControllerTest {

    @Test
    void indexReturnsHealthMessage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new DocumentController(mock(DocumentService.class))
        ).build();

        mockMvc.perform(get("/api/v1/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }
}
