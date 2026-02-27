package com.vishvakta.example.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService.createPayment("O1", new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("POST /api/payments creates payment and returns 201")
    void create_returns201AndBody() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .param("orderId", "O2")
                        .param("amount", "99.99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", is("O2")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/payments/{id} returns 404 for unknown id")
    void getById_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/payments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/payments/{id} returns payment when exists")
    void getById_returnsPaymentWhenExists() throws Exception {
        Payment p = paymentService.createPayment("O2", new BigDecimal("25.00"));
        mockMvc.perform(get("/api/payments/" + p.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(p.getId().intValue())))
                .andExpect(jsonPath("$.orderId", is("O2")));
    }

    @Test
    @DisplayName("GET /api/payments?orderId= returns payment when exists")
    void getByOrderId_returnsPaymentWhenExists() throws Exception {
        mockMvc.perform(get("/api/payments").param("orderId", "O1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is("O1")));
    }

    @Test
    @DisplayName("GET /api/payments?orderId= returns 404 for unknown orderId")
    void getByOrderId_returns404ForUnknown() throws Exception {
        mockMvc.perform(get("/api/payments").param("orderId", "UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/payments/{id}/status updates status")
    void updateStatus_updatesStatus() throws Exception {
        Payment p = paymentService.getByOrderId("O1");
        mockMvc.perform(patch("/api/payments/" + p.getId() + "/status")
                        .param("status", "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/payments/{id}/status-name returns status name")
    void getStatusName_returnsStatusName() throws Exception {
        Payment p = paymentService.getByOrderId("O1");
        mockMvc.perform(get("/api/payments/" + p.getId() + "/status-name"))
                .andExpect(status().isOk())
                .andExpect(content().string("PENDING"));
    }

    @Test
    @DisplayName("GET /api/payments returns list")
    void listAll_returnsList() throws Exception {
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    // Intentionally omit test for PATCH with unknown id -> 404 to leave one controller path uncovered for JaCoCo

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PaymentService paymentService() {
            return new PaymentService();
        }
    }
}
