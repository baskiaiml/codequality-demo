package com.vishvakta.example.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
    }

    @Test
    @DisplayName("createCart returns cart with generated id")
    void createCart_returnsCartWithId() {
        Cart cart = cartService.createCart("C001");
        assertNotNull(cart.getId());
        assertEquals("C001", cart.getCustomerId());
        assertTrue(cart.getProductQuantities().isEmpty());
        assertEquals(0, cart.getTotalItemCount());
    }

    @Test
    @DisplayName("createCart rejects null customerId")
    void createCart_throwsWhenCustomerIdNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.createCart(null));
    }

    @Test
    @DisplayName("getCart returns null for unknown id")
    void getCart_returnsNullForUnknownId() {
        assertNull(cartService.getCart(999L));
    }

    @Test
    @DisplayName("getCart returns saved cart")
    void getCart_returnsSavedCart() {
        Cart created = cartService.createCart("C001");
        Cart found = cartService.getCart(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("getTotalItemCount returns total for cart with items")
    void getTotalItemCount_returnsTotalWhenHasItems() {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 2);
        cartService.addItem(cart.getId(), "P002", 3);
        assertEquals(5, cartService.getTotalItemCount(cart.getId()));
    }

    @Test
    @DisplayName("addItem returns true and updates cart")
    void addItem_returnsTrueAndUpdatesCart() {
        Cart cart = cartService.createCart("C001");
        assertTrue(cartService.addItem(cart.getId(), "P001", 2));
        assertEquals(2, cartService.getCart(cart.getId()).getQuantity("P001"));
    }

    @Test
    @DisplayName("addItem merges quantity for same product")
    void addItem_mergesQuantityForSameProduct() {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 2);
        cartService.addItem(cart.getId(), "P001", 3);
        assertEquals(5, cartService.getCart(cart.getId()).getQuantity("P001"));
    }

    @Test
    @DisplayName("addItem returns false for null cart")
    void addItem_returnsFalseForUnknownCart() {
        assertFalse(cartService.addItem(999L, "P001", 1));
    }

    @Test
    @DisplayName("removeItem removes product from cart")
    void removeItem_removesProduct() {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 2);
        assertTrue(cartService.removeItem(cart.getId(), "P001"));
        assertEquals(0, cartService.getCart(cart.getId()).getQuantity("P001"));
    }

    @Test
    @DisplayName("removeItem returns false for unknown cart")
    void removeItem_returnsFalseForUnknownCart() {
        assertFalse(cartService.removeItem(999L, "P001"));
    }

    @Test
    @DisplayName("getLineItemCount returns number of distinct products")
    void getLineItemCount_returnsDistinctProductCount() {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 2);
        cartService.addItem(cart.getId(), "P002", 1);
        assertEquals(2, cartService.getLineItemCount(cart.getId()));
    }

    @Test
    @DisplayName("getLineItemCount returns -1 for unknown cart")
    void getLineItemCount_returnsMinusOneForUnknownCart() {
        assertEquals(-1, cartService.getLineItemCount(999L));
    }

    @Test
    @DisplayName("isCartForCustomer returns true when customer matches")
    void isCartForCustomer_returnsTrueWhenMatch() {
        Cart cart = cartService.createCart("C001");
        assertTrue(cartService.isCartForCustomer(cart.getId(), "C001"));
    }

    @Test
    @DisplayName("isCartForCustomer returns false when customer differs")
    void isCartForCustomer_returnsFalseWhenDifferent() {
        Cart cart = cartService.createCart("C001");
        assertFalse(cartService.isCartForCustomer(cart.getId(), "C002"));
    }

    @Test
    @DisplayName("containsProduct returns true when product in cart")
    void containsProduct_returnsTrueWhenInCart() {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 1);
        assertTrue(cartService.containsProduct(cart.getId(), "P001"));
    }

    @Test
    @DisplayName("containsProduct returns false when product not in cart")
    void containsProduct_returnsFalseWhenNotInCart() {
        Cart cart = cartService.createCart("C001");
        assertFalse(cartService.containsProduct(cart.getId(), "P999"));
    }

    @Test
    @DisplayName("clearCart empties cart items")
    void clearCart_emptiesItems() {
        Cart cart = cartService.createCart("C001");
        cartService.addItem(cart.getId(), "P001", 2);
        cartService.clearCart(cart.getId());
        assertEquals(0, cartService.getCart(cart.getId()).getTotalItemCount());
    }

    @Test
    @DisplayName("findAllCarts returns empty list when no carts")
    void findAllCarts_returnsEmptyWhenNone() {
        List<Cart> carts = cartService.findAllCarts();
        assertTrue(carts.isEmpty());
    }

    @Test
    @DisplayName("findAllCarts returns all carts")
    void findAllCarts_returnsAllCarts() {
        cartService.createCart("C001");
        cartService.createCart("C002");
        assertEquals(2, cartService.findAllCarts().size());
    }

    @Test
    @DisplayName("deleteCart removes cart")
    void deleteCart_removesCart() {
        Cart cart = cartService.createCart("C001");
        cartService.deleteCart(cart.getId());
        assertNull(cartService.getCart(cart.getId()));
    }

    @Test
    @DisplayName("clearAll removes all carts")
    void clearAll_removesAllCarts() {
        cartService.createCart("C001");
        cartService.clearAll();
        assertTrue(cartService.findAllCarts().isEmpty());
    }
}
