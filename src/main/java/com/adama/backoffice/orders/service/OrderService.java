package com.adama.backoffice.orders.service;

import com.adama.backoffice.orders.entity.Order;
import com.adama.backoffice.orders.repository.OrderRepository;
import com.adama.backoffice.products.service.ProductService;
import com.adama.product.model.ProductPatchRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public Optional<Order> fulfillOrder(UUID orderId) {
        Order order =
                orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));

        String productId = order.getProductId();
        String userId = order.getUserId();

        ProductPatchRequest patchRequest = new ProductPatchRequest();
        patchRequest.setUserId(userId);

        productService.updateProduct(productId, patchRequest);

        order.setStatus(Order.Status.FULFILLED);
        return Optional.of(orderRepository.save(order));
    }

    public Optional<Order> validateOrder(UUID id) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(Order.Status.VALIDATED);
            order.setValidationDate(LocalDateTime.now().toString());
            return orderRepository.save(order);
        });
    }

    public Optional<Order> denyOrder(UUID id) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(Order.Status.DENIED);
            order.setValidationDate(LocalDateTime.now().toString());
            return orderRepository.save(order);
        });
    }
}
