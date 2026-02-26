package com.vishvakta.example.ecommerce;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for orders. Contains deliberate SpotBugs triggers for demo.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam String customerId,
            @RequestParam String productId,
            @RequestParam int quantity,
            @RequestParam BigDecimal unitPrice) {
        Order order = orderService.createOrder(customerId, productId, quantity, unitPrice);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    /**
     * Deliberate: possible NPE - SpotBugs NP_NULL_ON_SOME_PATH when order is missing.
     */
    @GetMapping("/{id}/total")
    public ResponseEntity<BigDecimal> getOrderTotal(@PathVariable Long id) {
        BigDecimal total = orderService.getOrderTotal(id);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Boolean> cancelOrder(@PathVariable Long id) {
        boolean cancelled = orderService.safeCancelOrder(id);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping
    public ResponseEntity<List<Order>> listOrders() {
        List<Order> orders = orderService.findAllOrders();
        return ResponseEntity.ok(orders);
    }
}
