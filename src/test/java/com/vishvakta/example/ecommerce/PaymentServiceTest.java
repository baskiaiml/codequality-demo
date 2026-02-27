package com.vishvakta.example.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
    }

    @Test
    @DisplayName("createPayment returns payment with PENDING status")
    void createPayment_returnsPaymentWithPendingStatus() {
        Payment p = paymentService.createPayment("O1", new BigDecimal("99.99"));
        assertNotNull(p.getId());
        assertEquals("O1", p.getOrderId());
        assertEquals(new BigDecimal("99.99"), p.getAmount());
        assertEquals(PaymentStatus.PENDING, p.getStatus());
    }

    @Test
    @DisplayName("createPayment throws when orderId null")
    void createPayment_throwsWhenOrderIdNull() {
        assertThrows(IllegalArgumentException.class, () ->
                paymentService.createPayment(null, BigDecimal.TEN));
    }

    @Test
    @DisplayName("createPayment throws when amount null")
    void createPayment_throwsWhenAmountNull() {
        assertThrows(IllegalArgumentException.class, () ->
                paymentService.createPayment("O1", null));
    }

    @Test
    @DisplayName("getPayment returns null for unknown id")
    void getPayment_returnsNullForUnknownId() {
        assertNull(paymentService.getPayment(999L));
    }

    @Test
    @DisplayName("getPayment returns saved payment")
    void getPayment_returnsSavedPayment() {
        Payment created = paymentService.createPayment("O1", BigDecimal.TEN);
        Payment found = paymentService.getPayment(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("getByOrderId returns payment when exists")
    void getByOrderId_returnsPaymentWhenExists() {
        paymentService.createPayment("O1", BigDecimal.ONE);
        Payment found = paymentService.getByOrderId("O1");
        assertNotNull(found);
        assertEquals("O1", found.getOrderId());
    }

    @Test
    @DisplayName("getByOrderId returns null for unknown orderId")
    void getByOrderId_returnsNullForUnknown() {
        assertNull(paymentService.getByOrderId("UNKNOWN"));
    }

    @Test
    @DisplayName("updateStatus updates payment status")
    void updateStatus_updatesStatus() {
        Payment p = paymentService.createPayment("O1", BigDecimal.TEN);
        assertTrue(paymentService.updateStatus(p.getId(), PaymentStatus.COMPLETED));
        assertEquals(PaymentStatus.COMPLETED, paymentService.getPayment(p.getId()).getStatus());
    }

    @Test
    @DisplayName("updateStatus returns false for null id")
    void updateStatus_returnsFalseForNullId() {
        assertFalse(paymentService.updateStatus(null, PaymentStatus.COMPLETED));
    }

    // Intentionally omit test for updateStatus(unknownId, status) -> false to leave branch uncovered for JaCoCo

    @Test
    @DisplayName("getPaymentStatusName returns UNKNOWN for missing payment")
    void getPaymentStatusName_returnsUnknownForMissing() {
        assertEquals("UNKNOWN", paymentService.getPaymentStatusName(999L));
    }

    @Test
    @DisplayName("getPaymentStatusName returns status name for existing payment")
    void getPaymentStatusName_returnsStatusForExisting() {
        Payment p = paymentService.createPayment("O1", BigDecimal.ONE);
        assertEquals("PENDING", paymentService.getPaymentStatusName(p.getId()));
    }

    @Test
    @DisplayName("validateOrderId returns true for non-empty orderId")
    void validateOrderId_returnsTrueForNonEmpty() {
        assertTrue(paymentService.validateOrderId("O1"));
    }

    @Test
    @DisplayName("validateOrderId returns false for null")
    void validateOrderId_returnsFalseForNull() {
        assertFalse(paymentService.validateOrderId(null));
    }

    @Test
    @DisplayName("listAll returns empty list when no payments")
    void listAll_returnsEmptyWhenNone() {
        List<Payment> list = paymentService.listAll();
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("listAll returns all payments")
    void listAll_returnsAllPayments() {
        paymentService.createPayment("O1", BigDecimal.ONE);
        paymentService.createPayment("O2", BigDecimal.TEN);
        assertEquals(2, paymentService.listAll().size());
    }
}
