package com.adama.backoffice.orders.api;

import com.adama.backoffice.orders.entity.Order;
import com.adama.backoffice.orders.mapper.OrderMapper;
import com.adama.backoffice.orders.repository.OrderRepository;
import com.adama.backoffice.orders.service.OrderService;
import com.adama.order.api.OrderApi;
import com.adama.order.model.OrderPatchRequest;
import com.adama.order.model.OrderRequest;
import com.adama.order.model.OrderResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController implements OrderApi {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    private final OrderService orderService;

    @Override
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).collect(Collectors.toList()));
    }

    @Override
    @PostMapping("/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        Order order = OrderMapper.toEntity(orderRequest);
        order = orderRepository.save(order);
        return ResponseEntity.status(201).body(OrderMapper.toResponse(order));
    }

    @Override
    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return orderRepository
                    .findById(uuid)
                    .map(OrderMapper::toResponse)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @PatchMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable String id, @Valid @RequestBody OrderPatchRequest patchRequest) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<Order> optionalOrder = orderRepository.findById(uuid);
            if (optionalOrder.isEmpty()) return ResponseEntity.notFound().build();

            Order order = optionalOrder.get();
            OrderMapper.applyPatch(order, patchRequest);

            orderRepository.save(order);
            return ResponseEntity.ok(OrderMapper.toResponse(order));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @DeleteMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            if (orderRepository.existsById(uuid)) orderRepository.deleteById(uuid);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @GetMapping("/orders/status/ordered")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<List<OrderResponse>> getOrderedOrders() {
        List<Order> orders = orderRepository.findByStatus(Order.Status.ORDERED);
        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).collect(Collectors.toList()));
    }

    @Override
    @GetMapping("/orders/status/validated")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER','ROLE_WAREHOUSE')")
    public ResponseEntity<List<OrderResponse>> getValidatedOrders() {
        List<Order> orders = orderRepository.findByStatus(Order.Status.VALIDATED);
        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).collect(Collectors.toList()));
    }

    @Override
    @GetMapping("/orders/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return ResponseEntity.ok(orders.stream().map(OrderMapper::toResponse).collect(Collectors.toList()));
    }

    @Override
    @PostMapping("/orders/{id}/validate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<OrderResponse> validateOrder(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return orderService
                    .validateOrder(uuid)
                    .map(order -> ResponseEntity.ok(OrderMapper.toResponse(order)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @PostMapping("/orders/{id}/deny")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
    public ResponseEntity<OrderResponse> denyOrder(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return orderService
                    .denyOrder(uuid)
                    .map(order -> ResponseEntity.ok(OrderMapper.toResponse(order)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @PostMapping("/orders/{id}/fulfill")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN','ROLE_WAREHOUSE')")
    public ResponseEntity<OrderResponse> fulfillOrder(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return orderService
                    .fulfillOrder(uuid)
                    .map(order -> ResponseEntity.ok(OrderMapper.toResponse(order)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
