package com.vishvakta.example.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        orderService.clearAll();
    }

    @Test
    @DisplayName("POST /api/orders creates order and returns 201")
    void createOrder_returns201AndOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .param("customerId", "C001")
                        .param("productId", "P001")
                        .param("quantity", "2")
                        .param("unitPrice", "19.99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId", is("C001")))
                .andExpect(jsonPath("$.productId", is("P001")))
                .andExpect(jsonPath("$.quantity", is(2)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns 404 for unknown id")
    void getOrder_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns order when exists")
    void getOrder_returnsOrderWhenExists() throws Exception {
        Order created = orderService.createOrder("C001", "P001", 1, BigDecimal.TEN);
        mockMvc.perform(get("/api/orders/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(created.getId().intValue())))
                .andExpect(jsonPath("$.customerId", is("C001")))
                .andExpect(jsonPath("$.quantity", is(1)));
    }

    @Test
    @DisplayName("GET /api/orders returns empty list when no orders")
    void listOrders_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/orders returns all orders")
    void listOrders_returnsAllOrders() throws Exception {
        orderService.createOrder("C001", "P001", 1, BigDecimal.ONE);
        orderService.createOrder("C002", "P002", 2, BigDecimal.TEN);
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/cancel returns true when order cancelled")
    void cancelOrder_returnsTrueWhenCancelled() throws Exception {
        Order created = orderService.createOrder("C001", "P001", 1, BigDecimal.ONE);
        mockMvc.perform(post("/api/orders/" + created.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/cancel returns false for unknown order")
    void cancelOrder_returnsFalseForUnknownOrder() throws Exception {
        mockMvc.perform(post("/api/orders/999/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("GET /api/orders/{id}/total returns total for existing order")
    void getOrderTotal_returnsTotalForExistingOrder() throws Exception {
        Order created = orderService.createOrder("C001", "P001", 3, new BigDecimal("5.00"));
        mockMvc.perform(get("/api/orders/" + created.getId() + "/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrderService orderService() {
            return new OrderService();
        }
    }
}
