package com.ecommerce.order.service;

import com.ecommerce.order.dto.*;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;   // Direct call — no WebClient needed in monolith

    public OrderResponse placeOrder(OrderRequest request) {
        // Get product directly via service call
        Product product = productService.getProductById(request.getProductId());

        if (product == null)
            throw new RuntimeException("Product not found!");

        if (product.getStock() < request.getQuantity())
            throw new RuntimeException("Insufficient stock! Available: " + product.getStock());

        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .status(Order.OrderStatus.PENDING)
                .build();

        Order saved = orderRepository.save(order);

        // Reduce stock directly
        productService.reduceStock(request.getProductId(), request.getQuantity());

        log.info("Order placed: {}", saved.getId());
        return mapToResponse(saved, product.getName());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(o -> mapToResponse(o, "Product #" + o.getProductId()))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(o -> mapToResponse(o, "Product #" + o.getProductId()))
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToResponse(order, "Product #" + order.getProductId());
    }

    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(Order.OrderStatus.valueOf(status));
        return mapToResponse(orderRepository.save(order), "Product #" + order.getProductId());
    }

    private OrderResponse mapToResponse(Order order, String productName) {
        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setUserId(order.getUserId());
        res.setProductId(order.getProductId());
        res.setProductName(productName);
        res.setQuantity(order.getQuantity());
        res.setTotalPrice(order.getTotalPrice());
        res.setStatus(order.getStatus().name());
        res.setCreatedAt(order.getCreatedAt());
        return res;
    }
}
