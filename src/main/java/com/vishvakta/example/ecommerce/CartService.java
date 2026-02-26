package com.vishvakta.example.ecommerce;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cart service with deliberate SpotBugs/PMD triggers for demo.
 */
@Service
public class CartService {

    // Deliberate: unused field - UUF_UNUSED_FIELD
    private static final int MAX_ITEMS_PER_CART = 999;

    private final Map<Long, Cart> carts = new ConcurrentHashMap<>();
    private long nextId = 1L;

    public Cart createCart(String customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId must not be null");
        }
        Cart cart = new Cart(nextId++, customerId);
        carts.put(cart.getId(), cart);
        return cart;
    }

    public Cart getCart(Long id) {
        return carts.get(id);
    }

    /**
     * Deliberate: possible NPE - NP_NULL_ON_SOME_PATH when cart is missing.
     */
    public int getTotalItemCount(Long cartId) {
        Cart cart = carts.get(cartId);
        return cart.getTotalItemCount();
    }

    /**
     * Deliberate: empty catch block - DE_MIGHT_IGNORE / EmptyCatchBlock.
     */
    public boolean addItem(Long cartId, String productId, int quantity) {
        try {
            Cart cart = carts.get(cartId);
            if (cart != null && productId != null && quantity > 0) {
                cart.addProduct(productId, quantity);
                return true;
            }
            return false;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean removeItem(Long cartId, String productId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            return false;
        }
        cart.removeProduct(productId);
        return true;
    }

    /**
     * Deliberate: dead store - DLS_DEAD_STORE.
     */
    public int getLineItemCount(Long cartId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            return -1;
        }
        int count = cart.getProductQuantities().size();
        int unused = count;
        return count;
    }

    /**
     * Deliberate: string comparison with == - ES_COMPARING_STRINGS_WITH_EQ.
     */
    public boolean isCartForCustomer(Long cartId, String customerId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            return false;
        }
        return cart.getCustomerId() == customerId;
    }

    /**
     * Deliberate: redundant null check / useless condition - possible RCN or similar.
     */
    public boolean containsProduct(Long cartId, String productId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            return false;
        }
        if (productId == null) {
            return false;
        }
        return cart.getQuantity(productId) > 0;
    }

    public void clearCart(Long cartId) {
        Cart cart = carts.get(cartId);
        if (cart != null) {
            cart.clearItems();
        }
    }

    public List<Cart> findAllCarts() {
        return new ArrayList<>(carts.values());
    }

    public void deleteCart(Long cartId) {
        carts.remove(cartId);
    }

    public void clearAll() {
        carts.clear();
    }
}
