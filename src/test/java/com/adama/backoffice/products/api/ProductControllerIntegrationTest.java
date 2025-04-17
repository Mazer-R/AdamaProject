package com.adama.backoffice.products.api;

import com.adama.backoffice.products.entity.Product;
import com.adama.backoffice.products.repository.ProductRepository;
import com.adama.product.model.ProductPatchRequest;
import com.adama.product.model.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the ProductController.
 * These tests verify that the controller correctly handles HTTP requests and responses.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(productRepository);
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("GET /api/products when no products exist – should return empty list")
    void getAllProducts_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/products with valid data – should return created product with correct fields")
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setType("Test Type");
        request.setBrand("Test Brand");
        request.setStatus("ACTIVE");
        request.setUserId("user123");

        Product savedProduct = new Product();
        savedProduct.setId(UUID.randomUUID());
        savedProduct.setName("Test Product");
        savedProduct.setDescription("Test Description");
        savedProduct.setType("Test Type");
        savedProduct.setBrand("Test Brand");
        savedProduct.setStatus("ACTIVE");
        savedProduct.setUserId("user123");

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.type", is("Test Type")))
                .andExpect(jsonPath("$.brand", is("Test Brand")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.userId", is("user123")));
    }

    @Test
    @DisplayName("PATCH /api/products/{id} – should update product name and keep other fields unchanged")
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        UUID testId = UUID.randomUUID();
        String testIdString = testId.toString();

        ProductPatchRequest request = new ProductPatchRequest();
        request.setName("Updated Product");

        Product existingProduct = new Product();
        existingProduct.setId(testId);
        existingProduct.setName("Original Product");
        existingProduct.setDescription("Original Description");
        existingProduct.setType("Original Type");
        existingProduct.setBrand("Original Brand");
        existingProduct.setStatus("INACTIVE");
        existingProduct.setUserId("original-user");

        Product updatedProduct = new Product();
        updatedProduct.setId(testId);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Original Description");
        updatedProduct.setType("Original Type");
        updatedProduct.setBrand("Original Brand");
        updatedProduct.setStatus("INACTIVE");
        updatedProduct.setUserId("original-user");

        when(productRepository.findById(testId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(patch("/api/products/{id}", testIdString)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Original Description")));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} with valid ID – should return 204 No Content")
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        // Arrange
        UUID testId = UUID.randomUUID();
        String testIdString = testId.toString();

        when(productRepository.existsById(testId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/products/{id}", testIdString))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} with invalid UUID – should return 404 Not Found")
    void deleteProduct_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/products/{id}", "invalid-uuid"))
                .andExpect(status().isNotFound());
    }
}
