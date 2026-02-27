package com.vishvakta.example.ecommerce;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * REST controller for payments. Contains deliberate triggers for quality metrics demo.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> create(
            @RequestParam String orderId,
            @RequestParam BigDecimal amount) {
        try {
            Payment p = paymentService.createPayment(orderId, amount);
            return ResponseEntity.status(HttpStatus.CREATED).body(p);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        Payment p = paymentService.getPayment(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping(params = "orderId")
    public ResponseEntity<Payment> getByOrderId(@RequestParam String orderId) {
        Payment p = paymentService.getByOrderId(orderId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(p);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        boolean ok = paymentService.updateStatus(id, status);
        if (!ok) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Deliberate: possible NPE when payment missing - getPaymentStatusName returns string
     * but we use it without null check on the payment path in some edge case.
     */
    @GetMapping("/{id}/status-name")
    public ResponseEntity<String> getStatusName(@PathVariable Long id) {
        String name = paymentService.getPaymentStatusName(id);
        return ResponseEntity.ok(name);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> listAll() {
        return ResponseEntity.ok(paymentService.listAll());
    }
}
