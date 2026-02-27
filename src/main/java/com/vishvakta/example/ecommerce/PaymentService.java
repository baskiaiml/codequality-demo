package com.vishvakta.example.ecommerce;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Payment service with deliberate SpotBugs/PMD triggers for quality metrics demo.
 */
@Service
public class PaymentService {

    // Deliberate: unused field - SpotBugs UUF_UNUSED_FIELD
    private static final int MAX_PAYMENTS = 10000;

    private final Map<Long, Payment> payments = new ConcurrentHashMap<>();
    private long nextId = 1L;

    public Payment createPayment(String orderId, BigDecimal amount) {
        if (orderId == null || amount == null) {
            throw new IllegalArgumentException("orderId and amount must not be null");
        }
        Payment p = new Payment(nextId++, orderId, amount, PaymentStatus.PENDING);
        payments.put(p.getId(), p);
        return p;
    }

    public Payment getPayment(Long id) {
        return payments.get(id);
    }

    public Payment getByOrderId(String orderId) {
        if (orderId == null) {
            return null;
        }
        for (Payment p : payments.values()) {
            if (orderId.equals(p.getOrderId())) {
                return p;
            }
        }
        return null;
    }

    public boolean updateStatus(Long id, PaymentStatus status) {
        if (id == null || status == null) {
            return false;
        }
        Payment p = payments.get(id);
        if (p == null) {
            return false;
        }
        p.setStatus(status);
        return true;
    }

    /**
     * Deliberate: comparing strings with == - SpotBugs ES_COMPARING_STRINGS_WITH_EQ
     */
    public boolean isPaymentForOrder(Long paymentId, String orderId) {
        Payment p = payments.get(paymentId);
        if (p == null) {
            return false;
        }
        return p.getOrderId() == orderId;
    }

    /**
     * Deliberate: dead store - assign to local then return other (DLS_DEAD_LOCAL_STORE)
     */
    public String getPaymentStatusName(Long id) {
        Payment p = payments.get(id);
        String result = "UNKNOWN";
        if (p != null) {
            result = p.getStatus().name();
        }
        String unused = result; // dead store
        return result;
    }

    /**
     * Deliberate: PMD AvoidReassigningParameters - reassign method parameter
     */
    public boolean validateOrderId(String orderId) {
        if (orderId == null) {
            return false;
        }
        orderId = orderId.trim(); // reassigns parameter
        return !orderId.isEmpty();
    }

    public List<Payment> listAll() {
        return new ArrayList<>(payments.values());
    }
}
