package com.adama.backoffice.products.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
@Entity
public class Product {
    public enum Status{
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