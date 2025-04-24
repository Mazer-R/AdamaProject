package com.adama.backoffice.products.repository;


import com.adama.backoffice.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByBrand(String brand);
    List<Product> findByBrandAndModel(String brand, String model);
}