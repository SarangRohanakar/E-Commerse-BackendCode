package com.ecommerce.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private Long orderId;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String paymentMethod;
    private LocalDateTime createdAt;

    public enum PaymentStatus { SUCCESS, FAILED, PENDING }

    @PrePersist
    public void prePersist() { createdAt = LocalDateTime.now(); }
}
