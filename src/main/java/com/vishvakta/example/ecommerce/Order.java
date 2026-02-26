package com.vishvakta.example.ecommerce;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Order entity for ecommerce demo.
 */
public class Order {

    private Long id;
    private String customerId;
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;
    private OrderStatus status;

    public Order() {
    }

    public Order(Long id, String customerId, String productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.status = OrderStatus.PENDING;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return quantity == order.quantity
                && Objects.equals(id, order.id)
                && Objects.equals(customerId, order.customerId)
                && Objects.equals(productId, order.productId)
                && Objects.equals(unitPrice, order.unitPrice)
                && status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, productId, quantity, unitPrice, status);
    }
}
