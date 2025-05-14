package com.adama.backoffice.products.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "product")
@Entity
public class Product {
    public enum Status {
        STOCK,
        ASSIGNED,
        ON_REPAIR,
        PENDING,
        INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;
    private String type;
    private String brand;
    private String model;
    private Status status;
    private String userId;
    private String created;
    private String lastModified;
}
