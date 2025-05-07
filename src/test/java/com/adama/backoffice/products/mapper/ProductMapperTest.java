package com.adama.backoffice.products.mapper;

import com.adama.backoffice.BackofficeApplicationTests;
import com.adama.backoffice.products.entity.Product;
import com.adama.product.model.ProductRequest;
import com.adama.product.model.ProductResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    @Test
    void toEntity_ShouldMapAllFields() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setType("Test Type");
        request.setBrand("Test Brand");
        request.setStatus("ACTIVE");
        request.setUserId("user123");

        // Act
        Product product = ProductMapper.toEntity(request);

        // Assert
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals("Test Type", product.getType());
        assertEquals("Test Brand", product.getBrand());
        assertEquals("ACTIVE", product.getStatus());
        assertEquals("user123", product.getUserId());
        assertNotNull(product.getCreated());
        assertNotNull(product.getLastModified());
        assertEquals("SYSTEM", product.getModifiedBy());
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
        product.setStatus("ACTIVE");
        product.setUserId("user123");
        LocalDateTime now = LocalDateTime.now();
        product.setCreated(now);
        product.setLastModified(now);
        product.setModifiedBy("test-user");

        // Act
        ProductResponse response = ProductMapper.toResponse(product);

        // Assert
        assertEquals(id.toString(), response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals("Test Type", response.getType());
        assertEquals("Test Brand", response.getBrand());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("user123", response.getUserId());
    }
}