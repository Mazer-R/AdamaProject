package com.adama.backoffice.products.repository;

import com.adama.backoffice.products.entity.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByType(String type);

    List<Product> findByBrand(String brand);

    List<Product> findByTypeAndBrand(String type, String brand);
}
