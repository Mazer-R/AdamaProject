package com.adama.backoffice.products.repository;


import com.adama.backoffice.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByType(String type);
    List<Product> findByTypeAndBrand(String type, String brand);

}