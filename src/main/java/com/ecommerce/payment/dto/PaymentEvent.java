package com.ecommerce.payment.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
}
