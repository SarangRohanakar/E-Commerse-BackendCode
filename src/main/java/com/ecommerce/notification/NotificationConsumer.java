package com.ecommerce.notification;

import com.ecommerce.payment.dto.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")  // ← only loads if kafka configured
public class NotificationConsumer {

    @KafkaListener(topics = "payment-events", groupId = "ecommerce-group")
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("Notification received: {}", event);
        if ("SUCCESS".equals(event.getStatus())) {
            log.info("✅ SUCCESS → userId: {} orderId: {}", event.getUserId(), event.getOrderId());
        } else {
            log.info("❌ FAILED → userId: {} orderId: {}", event.getUserId(), event.getOrderId());
        }
    }
}