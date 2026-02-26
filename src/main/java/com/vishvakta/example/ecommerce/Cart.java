package com.vishvakta.example.ecommerce;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Cart entity for ecommerce demo. Holds productId -> quantity.
 */
public class Cart {

    private Long id;
    private String customerId;
    private Map<String, Integer> productQuantities;

    public Cart() {
        this.productQuantities = new HashMap<>();
    }

    public Cart(Long id, String customerId) {
        this.id = id;
        this.customerId = customerId;
        this.productQuantities = new HashMap<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Map<String, Integer> getProductQuantities() {
        return Collections.unmodifiableMap(productQuantities);
    }

    public void setProductQuantities(Map<String, Integer> productQuantities) {
        this.productQuantities = productQuantities != null ? new HashMap<>(productQuantities) : new HashMap<>();
    }

    public int getQuantity(String productId) {
        return productQuantities.getOrDefault(productId, 0);
    }

    public int getTotalItemCount() {
        return productQuantities.values().stream().mapToInt(Integer::intValue).sum();
    }

    /** Internal use: add or update quantity for a product. */
    void addProduct(String productId, int quantity) {
        if (productId == null || quantity <= 0) return;
        productQuantities.merge(productId, quantity, Integer::sum);
    }

    /** Internal use: remove a product from the cart. */
    void removeProduct(String productId) {
        if (productId != null) {
            productQuantities.remove(productId);
        }
    }

    /** Internal use: clear all items. */
    void clearItems() {
        productQuantities.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id)
                && Objects.equals(customerId, cart.customerId)
                && Objects.equals(productQuantities, cart.productQuantities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, productQuantities);
    }
}
