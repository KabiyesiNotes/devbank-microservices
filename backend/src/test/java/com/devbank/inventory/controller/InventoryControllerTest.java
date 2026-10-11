package com.devbank.inventory.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the InventoryController endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootEndpoint_shouldReturn200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("running"));
    }

    @Test
    void healthEndpoint_shouldReturn200WithHealthyStatus() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("healthy"));
    }

    @Test
    void inventoryEndpoint_shouldReturn200WithItems() throws Exception {
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Desktop Computer"));
    }

    @Test
    void inventoryEndpoint_shouldReturnItemsWithRequiredFields() throws Exception {
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].category").exists())
                .andExpect(jsonPath("$[0].quantity").exists())
                .andExpect(jsonPath("$[0].status").exists());
    }
}
