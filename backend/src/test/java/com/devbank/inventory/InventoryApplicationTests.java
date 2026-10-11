package com.devbank.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the DevBank Inventory backend.
 *
 * Spins up the full application context and uses MockMvc
 * to verify all key endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InventoryApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    // ── Root endpoint ─────────────────────────────────────────────────────────

    @Test
    void rootEndpointReturns200() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.status").value("running"));
    }

    // ── Health endpoint ───────────────────────────────────────────────────────

    @Test
    void healthEndpointReturns200() throws Exception {
        mockMvc.perform(get("/health"))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.status").value("healthy"));
    }

    // ── Inventory endpoint ────────────────────────────────────────────────────

    @Test
    void inventoryEndpointReturns200() throws Exception {
        mockMvc.perform(get("/api/inventory"))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void inventoryEndpointReturnsNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/inventory"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void inventoryItemHasExpectedFields() throws Exception {
        mockMvc.perform(get("/api/inventory"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").exists())
               .andExpect(jsonPath("$[0].name").exists())
               .andExpect(jsonPath("$[0].category").exists())
               .andExpect(jsonPath("$[0].quantity").exists())
               .andExpect(jsonPath("$[0].price").exists());
    }

    @Test
    void inventoryContainsLaptop() throws Exception {
        mockMvc.perform(get("/api/inventory"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("Laptop"));
    }
}
