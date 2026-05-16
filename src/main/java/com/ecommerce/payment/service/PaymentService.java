package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.*;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    private static final String PAYMENT_TOPIC = "payment-events";

    public PaymentResponse processPayment(PaymentRequest request) {
        boolean success = Math.random() > 0.1; // 90% success simulation

        Payment payment = Payment.builder()
                .orderId(request.getOrderId()).userId(request.getUserId())
                .amount(request.getAmount()).paymentMethod(request.getPaymentMethod())
                .status(success ? Payment.PaymentStatus.SUCCESS : Payment.PaymentStatus.FAILED)
                .build();

        Payment saved = paymentRepository.save(payment);

        PaymentEvent event = PaymentEvent.builder()
                .orderId(request.getOrderId()).userId(request.getUserId())
                .amount(request.getAmount())
                .status(success ? "SUCCESS" : "FAILED")
                .paymentMethod(request.getPaymentMethod())
                .build();

        kafkaTemplate.send(PAYMENT_TOPIC, event);
        log.info("Payment event published: {}", event);

        return mapToResponse(saved);
    }

    public List<PaymentResponse> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment p) {
        PaymentResponse res = new PaymentResponse();
        res.setId(p.getId()); res.setOrderId(p.getOrderId());
        res.setUserId(p.getUserId()); res.setAmount(p.getAmount());
        res.setStatus(p.getStatus().name()); res.setPaymentMethod(p.getPaymentMethod());
        res.setCreatedAt(p.getCreatedAt());
        return res;
    }
}
