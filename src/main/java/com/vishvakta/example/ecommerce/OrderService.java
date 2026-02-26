package com.vishvakta.example.ecommerce;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Order service with deliberate SpotBugs triggers for demo.
 */
@Service
public class OrderService {

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private long nextId = 1L;

    public Order createOrder(String customerId, String productId, int quantity, BigDecimal unitPrice) {
        if (customerId == null || productId == null) {
            throw new IllegalArgumentException("customerId and productId must not be null");
        }
        Order order = new Order(nextId++, customerId, productId, quantity, unitPrice);
        orders.put(order.getId(), order);
        return order;
    }

    public Order getOrder(Long id) {
        return orders.get(id);
    }

    public BigDecimal getOrderTotal(Long id) {
        Order order = orders.get(id);
        if (order == null) {
            return BigDecimal.ZERO;
        }
        return order.getTotalAmount();
    }

    public boolean safeCancelOrder(Long id) {
        try {
            Order order = orders.get(id);
            if (order != null && order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CANCELLED);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Deliberate: dead store - SpotBugs DLS_DEAD_STORE_OF_CLASS_LITERAL or similar
     */
    public String getOrderStatusName(Long id) {
        Order order = orders.get(id);
        String result = "UNKNOWN";
        if (order != null) {
            result = order.getStatus().name();
        }
        String unused = result; // dead store
        return result;
    }

    /**
     * Deliberate: comparing objects with == instead of equals - SpotBugs ES_COMPARING_STRINGS_WITH_EQ
     */
    public boolean isOrderForCustomer(Long orderId, String customerId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }
        return order.getCustomerId() == customerId;
    }

    public List<Order> findAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public void clearAll() {
        orders.clear();
    }
}
