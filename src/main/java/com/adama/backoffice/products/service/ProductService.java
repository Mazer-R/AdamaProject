package com.adama.backoffice.products.service;

import com.adama.backoffice.products.entity.Product;
import com.adama.backoffice.products.repository.ProductRepository;
import com.adama.product.model.ProductPatchRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product updateProduct(String id, ProductPatchRequest request) {
        UUID uuid;

        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + id);
        }

        Product product = productRepository
                .findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getType() != null) product.setType(request.getType());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getStatus() != null)
            product.setStatus(Product.Status.valueOf(request.getStatus().name()));
        if (request.getUserId() != null) product.setUserId(request.getUserId());

        product.setLastModified(LocalDateTime.now().toString());

        return productRepository.save(product);
    }
}
