package com.lessonring.api.payment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessonring.api.common.error.GlobalExceptionHandler;
import com.lessonring.api.common.security.JwtAuthenticationFilter;
import com.lessonring.api.payment.api.request.PaymentApproveRequest;
import com.lessonring.api.payment.api.response.PaymentApproveResponse;
import com.lessonring.api.payment.application.PaymentPgService;
import com.lessonring.api.payment.application.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, PaymentControllerTest.MockConfig.class})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentPgService paymentPgService;

    static class MockConfig {

        @org.springframework.context.annotation.Bean
        PaymentService paymentService() {
            return mock(PaymentService.class);
        }

        @org.springframework.context.annotation.Bean
        PaymentPgService paymentPgService() {
            return mock(PaymentPgService.class);
        }

        @org.springframework.context.annotation.Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            return mock(JwtAuthenticationFilter.class);
        }
    }

    @Test
    @DisplayName("결제 승인 요청이 성공하면 200 응답을 반환한다")
    void approve_success() throws Exception {
        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "paymentKey", "paymentKey_123");
        setField(request, "orderId", "ORDER_123");
        setField(request, "amount", 100_000L);

        given(paymentPgService.approve(eq(1L), any(PaymentApproveRequest.class)))
                .willReturn(
                        PaymentApproveResponse.builder()
                                .paymentId(1L)
                                .status("COMPLETED")
                                .paymentKey("paymentKey_123")
                                .amount(100_000L)
                                .build()
                );

        mockMvc.perform(post("/api/v1/payments/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentId").value(1))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.paymentKey").value("paymentKey_123"))
                .andExpect(jsonPath("$.data.amount").value(100000));
    }

    @Test
    @DisplayName("결제 승인 요청 시 paymentKey가 없으면 validation 에러가 발생한다")
    void approve_validation_fail_paymentKey_blank() throws Exception {
        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "orderId", "ORDER_123");
        setField(request, "amount", 100_000L);

        mockMvc.perform(post("/api/v1/payments/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("결제 승인 요청 시 amount가 없으면 validation 에러가 발생한다")
    void approve_validation_fail_amount_null() throws Exception {
        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "paymentKey", "paymentKey_123");
        setField(request, "orderId", "ORDER_123");

        mockMvc.perform(post("/api/v1/payments/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}