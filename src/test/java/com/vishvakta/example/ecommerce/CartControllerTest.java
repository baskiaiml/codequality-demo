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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService.clearAll();
    }

    @Test
    @DisplayName("POST /api/carts creates cart and returns 201")
    void createCart_returns201AndCart() throws Exception {
        mockMvc.perform(post("/api/carts")
                        .param("customerId", "C001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId", is("C001")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.productQuantities", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/carts/{id} returns 404 for unknown id")
    void getCart_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/carts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/carts/{id} returns cart when exists")
    void getCart_returnsCartWhenExists() throws Exception {
        Cart created = cartService.createCart("C001");
        mockMvc.perform(get("/api/carts/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(created.getId().intValue())))
                .andExpect(jsonPath("$.customerId", is("C001")));
    }

    @Test
    @DisplayName("GET /api/carts/{id}/total-items returns total for cart with items")
    void getTotalItemCount_returnsTotalWhenHasItems() throws Exception {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 2);
        cartService.addItem(cart.getId(), "P002", 3);
        mockMvc.perform(get("/api/carts/" + cart.getId() + "/total-items"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @DisplayName("POST /api/carts/{id}/items adds item and returns 200")
    void addItem_returns200WhenAdded() throws Exception {
        Cart cart = cartService.createCart("C001");
        mockMvc.perform(post("/api/carts/" + cart.getId() + "/items")
                        .param("productId", "P001")
                        .param("quantity", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("DELETE /api/carts/{id}/items/{productId} removes item")
    void removeItem_removesItem() throws Exception {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 1);
        mockMvc.perform(delete("/api/carts/" + cart.getId() + "/items/P001"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/carts/{id}/line-count returns distinct product count")
    void getLineItemCount_returnsLineCount() throws Exception {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 2);
        cartService.addItem(cart.getId(), "P002", 1);
        mockMvc.perform(get("/api/carts/" + cart.getId() + "/line-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    @DisplayName("GET /api/carts/{id}/line-count returns 404 for unknown cart")
    void getLineItemCount_returns404ForUnknownCart() throws Exception {
        mockMvc.perform(get("/api/carts/999/line-count"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/carts/{id}/clear clears cart")
    void clearCart_clearsCart() throws Exception {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 1);
        mockMvc.perform(post("/api/carts/" + cart.getId() + "/clear"))
                .andExpect(status().isOk());
        assertEquals(0, cartService.getCart(cart.getId()).getTotalItemCount());
    }

    @Test
    @DisplayName("GET /api/carts/{id}/contains returns true when product in cart")
    void containsProduct_returnsTrueWhenInCart() throws Exception {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 1);
        mockMvc.perform(get("/api/carts/" + cart.getId() + "/contains").param("productId", "P001"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/carts returns empty list when no carts")
    void listCarts_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/carts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/carts returns all carts")
    void listCarts_returnsAllCarts() throws Exception {
        cartService.createCart("C001");
        cartService.createCart("C002");
        mockMvc.perform(get("/api/carts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("DELETE /api/carts/{id} deletes cart")
    void deleteCart_deletesCart() throws Exception {
        Cart cart = cartService.createCart("C001");
        mockMvc.perform(delete("/api/carts/" + cart.getId()))
                .andExpect(status().isOk());
        assertNull(cartService.getCart(cart.getId()));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CartService cartService() {
            return new CartService();
        }
    }
}
