package com.vishvakta.example.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    @DisplayName("createOrder returns order with generated id and PENDING status")
    void createOrder_returnsOrderWithIdAndPendingStatus() {
        Order order = orderService.createOrder("C001", "P001", 2, new BigDecimal("19.99"));
        assertNotNull(order.getId());
        assertEquals("C001", order.getCustomerId());
        assertEquals("P001", order.getProductId());
        assertEquals(2, order.getQuantity());
        assertEquals(new BigDecimal("19.99"), order.getUnitPrice());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(new BigDecimal("39.98"), order.getTotalAmount());
    }

    @Test
    @DisplayName("createOrder rejects null customerId")
    void createOrder_throwsWhenCustomerIdNull() {
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(null, "P001", 1, BigDecimal.ONE));
    }

    @Test
    @DisplayName("createOrder rejects null productId")
    void createOrder_throwsWhenProductIdNull() {
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder("C001", null, 1, BigDecimal.ONE));
    }

    @Test
    @DisplayName("getOrder returns null for unknown id")
    void getOrder_returnsNullForUnknownId() {
        assertNull(orderService.getOrder(999L));
    }

    @Test
    @DisplayName("getOrder returns saved order")
    void getOrder_returnsSavedOrder() {
        Order created = orderService.createOrder("C001", "P001", 1, BigDecimal.TEN);
        Order found = orderService.getOrder(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("getOrderTotal returns total for existing order")
    void getOrderTotal_returnsTotalForExistingOrder() {
        Order created = orderService.createOrder("C001", "P001", 3, new BigDecimal("5.00"));
        BigDecimal total = orderService.getOrderTotal(created.getId());
        assertEquals(new BigDecimal("15.00"), total);
    }

    @Test
    @DisplayName("safeCancelOrder returns true for PENDING order")
    void safeCancelOrder_returnsTrueForPendingOrder() {
        Order created = orderService.createOrder("C001", "P001", 1, BigDecimal.ONE);
        assertTrue(orderService.safeCancelOrder(created.getId()));
        assertEquals(OrderStatus.CANCELLED, orderService.getOrder(created.getId()).getStatus());
    }

    @Test
    @DisplayName("safeCancelOrder returns false for non-existent order")
    void safeCancelOrder_returnsFalseForUnknownOrder() {
        assertFalse(orderService.safeCancelOrder(999L));
    }

    @Test
    @DisplayName("getOrderStatusName returns UNKNOWN for null order")
    void getOrderStatusName_returnsUnknownForMissingOrder() {
        assertEquals("UNKNOWN", orderService.getOrderStatusName(999L));
    }

    @Test
    @DisplayName("getOrderStatusName returns status name for existing order")
    void getOrderStatusName_returnsStatusForExistingOrder() {
        Order created = orderService.createOrder("C001", "P001", 1, BigDecimal.ONE);
        assertEquals("PENDING", orderService.getOrderStatusName(created.getId()));
    }

    @Test
    @DisplayName("isOrderForCustomer returns true when customer matches")
    void isOrderForCustomer_returnsTrueWhenMatch() {
        Order created = orderService.createOrder("C001", "P001", 1, BigDecimal.ONE);
        assertTrue(orderService.isOrderForCustomer(created.getId(), "C001"));
    }

    @Test
    @DisplayName("isOrderForCustomer returns false for wrong customer")
    void isOrderForCustomer_returnsFalseWhenDifferentCustomer() {
        Order created = orderService.createOrder("C001", "P001", 1, BigDecimal.ONE);
        assertFalse(orderService.isOrderForCustomer(created.getId(), "C002"));
    }

    @Test
    @DisplayName("findAllOrders returns empty list when no orders")
    void findAllOrders_returnsEmptyWhenNoOrders() {
        List<Order> orders = orderService.findAllOrders();
        assertTrue(orders.isEmpty());
    }

    @Test
    @DisplayName("findAllOrders returns all created orders")
    void findAllOrders_returnsAllOrders() {
        orderService.createOrder("C001", "P001", 1, BigDecimal.ONE);
        orderService.createOrder("C002", "P002", 2, BigDecimal.TEN);
        List<Order> orders = orderService.findAllOrders();
        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("clearAll removes all orders")
    void clearAll_removesAllOrders() {
        orderService.createOrder("C001", "P001", 1, BigDecimal.ONE);
        orderService.clearAll();
        assertTrue(orderService.findAllOrders().isEmpty());
        assertNull(orderService.getOrder(1L));
    }
}
