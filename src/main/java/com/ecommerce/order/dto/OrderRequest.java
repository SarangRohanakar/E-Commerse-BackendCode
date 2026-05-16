package com.ecommerce.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderRequest {
    private Long productId;
    private Integer quantity;
    private Long userId;
}
