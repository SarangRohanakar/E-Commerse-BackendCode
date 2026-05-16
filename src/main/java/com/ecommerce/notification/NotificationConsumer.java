package com.ecommerce.notification;

import com.ecommerce.payment.dto.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationConsumer {

    @KafkaListener(topics = "payment-events", groupId = "ecommerce-group")
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("Notification received - Payment event: {}", event);
        if ("SUCCESS".equals(event.getStatus())) {
            log.info("✅ SUCCESS notification → userId: {} orderId: {} amount: {}",
                    event.getUserId(), event.getOrderId(), event.getAmount());
        } else {
            log.info("❌ FAILED notification → userId: {} orderId: {}",
                    event.getUserId(), event.getOrderId());
        }
    }
}
