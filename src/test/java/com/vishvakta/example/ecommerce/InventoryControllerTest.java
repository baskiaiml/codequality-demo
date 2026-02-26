package com.vishvakta.example.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService.clearAll();
    }

    @Test
    @DisplayName("POST /api/inventory adds item and returns 201")
    void addItem_returns201AndItem() throws Exception {
        mockMvc.perform(post("/api/inventory")
                        .param("sku", "SKU-001")
                        .param("name", "Widget")
                        .param("quantityInStock", "50")
                        .param("reorderThreshold", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku", is("SKU-001")))
                .andExpect(jsonPath("$.name", is("Widget")))
                .andExpect(jsonPath("$.quantityInStock", is(50)))
                .andExpect(jsonPath("$.reorderThreshold", is(10)))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/inventory/{id} returns 404 for unknown id")
    void getItem_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/inventory/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/inventory/{id} returns item when exists")
    void getItem_returnsItemWhenExists() throws Exception {
        InventoryItem created = inventoryService.addItem("SKU-001", "Widget", 25, 5);
        mockMvc.perform(get("/api/inventory/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(created.getId().intValue())))
                .andExpect(jsonPath("$.sku", is("SKU-001")))
                .andExpect(jsonPath("$.quantityInStock", is(25)));
    }

    @Test
    @DisplayName("GET /api/inventory/sku/{sku} returns 404 for unknown sku")
    void getItemBySku_returns404ForUnknownSku() throws Exception {
        mockMvc.perform(get("/api/inventory/sku/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/inventory/sku/{sku} returns item when exists")
    void getItemBySku_returnsItemWhenExists() throws Exception {
        inventoryService.addItem("SKU-ABC", "Gadget", 10, 2);
        mockMvc.perform(get("/api/inventory/sku/SKU-ABC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku", is("SKU-ABC")))
                .andExpect(jsonPath("$.name", is("Gadget")));
    }

    @Test
    @DisplayName("GET /api/inventory/{id}/needs-reorder returns true when low stock")
    void needsReorder_returnsTrueWhenLowStock() throws Exception {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 3, 10);
        mockMvc.perform(get("/api/inventory/" + item.getId() + "/needs-reorder"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/inventory/{id}/needs-reorder returns false when enough stock")
    void needsReorder_returnsFalseWhenEnoughStock() throws Exception {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 50, 10);
        mockMvc.perform(get("/api/inventory/" + item.getId() + "/needs-reorder"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("POST /api/inventory/{id}/adjust returns new quantity")
    void adjustStock_returnsNewQuantity() throws Exception {
        InventoryItem item = inventoryService.addItem("SKU-001", "Widget", 20, 5);
        mockMvc.perform(post("/api/inventory/" + item.getId() + "/adjust").param("delta", "-5"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    @DisplayName("POST /api/inventory/{id}/adjust returns 404 for unknown id")
    void adjustStock_returns404ForUnknownId() throws Exception {
        mockMvc.perform(post("/api/inventory/999/adjust").param("delta", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/inventory/{id}/description returns description when exists")
    void getDescription_returnsDescriptionWhenExists() throws Exception {
        InventoryItem item = inventoryService.addItem("SKU-X", "Product X", 10, 2);
        mockMvc.perform(get("/api/inventory/" + item.getId() + "/description"))
                .andExpect(status().isOk())
                .andExpect(content().string("SKU-X - Product X"));
    }

    @Test
    @DisplayName("GET /api/inventory/{id}/description returns 404 for unknown id")
    void getDescription_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/inventory/999/description"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/inventory returns empty list when no items")
    void listItems_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/inventory returns all items")
    void listItems_returnsAllItems() throws Exception {
        inventoryService.addItem("SKU-1", "A", 10, 5);
        inventoryService.addItem("SKU-2", "B", 20, 10);
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/inventory/reorder returns only items needing reorder")
    void itemsNeedingReorder_returnsOnlyLowStock() throws Exception {
        inventoryService.addItem("SKU-HIGH", "High", 100, 10);
        inventoryService.addItem("SKU-LOW", "Low", 3, 10);
        mockMvc.perform(get("/api/inventory/reorder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sku", is("SKU-LOW")));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public InventoryService inventoryService() {
            return new InventoryService();
        }
    }
}
