package com.ecommerce.order_service.service.impl;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.order_service.client.InventoryClient;
import com.ecommerce.order_service.event.OrderCreatedEvent;
import com.ecommerce.order_service.event.OrderEventPublisher;
import com.ecommerce.order_service.model.dto.request.ReserveInventoryRequest;
import com.ecommerce.order_service.model.dto.response.OrderItemResponse;
import com.ecommerce.order_service.model.dto.response.OrderResponse;
import com.ecommerce.order_service.model.entity.CartItem;
import com.ecommerce.order_service.model.entity.Order;
import com.ecommerce.order_service.model.entity.OrderItem;
import com.ecommerce.order_service.model.enums.OrderStatus;
import com.ecommerce.order_service.repository.CartItemRepository;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal STUB_UNIT_PRICE = new BigDecimal("9.99");

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderEventPublisher orderEventPublisher;

    @Override
    @Transactional
    public OrderResponse checkout(String userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("Cart is empty", HttpStatus.BAD_REQUEST);
        }

        for (CartItem cartItem : cartItems) {
            inventoryClient.reserve(
                    cartItem.getProductId(),
                    ReserveInventoryRequest.builder().quantity(cartItem.getQuantity()).build()
            );
        }

        Order order = Order.builder()
                .id(Order.newId())
                .userId(userId)
                .status(OrderStatus.PENDING)
                .createdAt(Instant.now())
                .items(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(STUB_UNIT_PRICE)
                    .build();
            order.getItems().add(orderItem);
            total = total.add(STUB_UNIT_PRICE.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);

        orderEventPublisher.publishOrderCreated(toEvent(saved));

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(String userId, String orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));
        return toResponse(order);
    }

    private OrderCreatedEvent toEvent(Order order) {
        List<OrderCreatedEvent.OrderLine> lines = order.getItems().stream()
                .map(item -> OrderCreatedEvent.OrderLine.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .toList();

        return OrderCreatedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(lines)
                .build();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
