package com.adama.backoffice.orders.repository;

import com.adama.backoffice.orders.entity.Order;
import com.adama.backoffice.orders.entity.Order.Status;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByStatus(Status status);

    List<Order> findByUserId(String userId);
}
