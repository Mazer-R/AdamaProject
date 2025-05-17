package com.adama.backoffice.products.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.adama.backoffice.products.entity.Product;
import com.adama.product.model.ProductRequest;
import com.adama.product.model.ProductResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductMapperTest {

    @Test
    void toEntity_ShouldMapAllFields() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setType("Test Type");
        request.setBrand("Test Brand");
        request.setDescription("Optional Description");

        // Act
        Product product = ProductMapper.toEntity(request);

        // Assert
        assertEquals("Test Product", product.getName());
        assertEquals("Test Type", product.getType());
        assertEquals("Test Brand", product.getBrand());
        assertEquals(Product.Status.STOCK, product.getStatus());
        assertEquals("Optional Description", product.getDescription());
        assertNotNull(product.getCreated());
        assertNotNull(product.getLastModified());
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        // Arrange
        Product product = new Product();
        UUID id = UUID.randomUUID();
        product.setId(id);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setType("Test Type");
        product.setBrand("Test Brand");
        product.setStatus(Product.Status.STOCK);
        LocalDateTime now = LocalDateTime.now();
        product.setCreated(now.toString());
        product.setLastModified(now.toString());

        // Act
        ProductResponse response = ProductMapper.toResponse(product);

        // Assert
        assertEquals(id.toString(), response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals("Test Type", response.getType());
        assertEquals("Test Brand", response.getBrand());
    }
}
