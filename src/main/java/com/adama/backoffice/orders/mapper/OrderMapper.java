package com.adama.backoffice.orders.mapper;

import static java.time.LocalTime.now;

import com.adama.backoffice.orders.entity.Order;
import com.adama.order.model.OrderPatchRequest;
import com.adama.order.model.OrderRequest;
import com.adama.order.model.OrderResponse;

public class OrderMapper {

    public static Order toEntity(OrderRequest request) {
        Order order = new Order();
        order.setProductId(request.getProductId());
        order.setUserId(request.getUserId());
        order.setManagerUsername(request.getManagerUsername());
        order.setStatus(Order.Status.ORDERED);
        order.setOrderDate(now().toString());
        if (request.getValidationDate() != null) order.setValidationDate(request.getValidationDate());
        if (request.getFulfillmentDate() != null) order.setFulfillmentDate(request.getFulfillmentDate());
        order.setDetails(request.getDetails());
        return order;
    }

    public static void applyPatch(Order order, OrderPatchRequest patch) {
        if (patch.getProductId() != null) order.setProductId(patch.getProductId());
        if (patch.getManagerUsername() != null) order.setManagerUsername(patch.getManagerUsername());
        if (patch.getStatus() != null)
            order.setStatus(Order.Status.valueOf(patch.getStatus().name()));
        if (patch.getValidationDate() != null) order.setValidationDate(patch.getValidationDate());
        if (patch.getFulfillmentDate() != null) order.setFulfillmentDate(patch.getFulfillmentDate());
        if (patch.getDetails() != null) order.setDetails(patch.getDetails());
    }

    public static OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId().toString());
        response.setProductId(order.getProductId());
        response.setUserId(order.getUserId());
        response.setManagerUsername(order.getManagerUsername());
        response.setStatus(OrderResponse.StatusEnum.valueOf(order.getStatus().name()));
        response.setOrderDate(order.getOrderDate());
        if (order.getValidationDate() != null)
            response.setValidationDate(order.getValidationDate().toString());
        if (order.getFulfillmentDate() != null)
            response.setFulfillmentDate(order.getFulfillmentDate().toString());
        response.setDetails(order.getDetails());
        return response;
    }
}
