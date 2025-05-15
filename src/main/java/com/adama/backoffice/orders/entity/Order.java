package com.adama.backoffice.orders.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String managerUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private String orderDate;

    private String validationDate;
    private String fulfillmentDate;
    private String details;

    public enum Status {
        ORDERED,
        VALIDATED,
        FULFILLED,
        DENIED
    }
}
