package com.adama.backoffice.products.api;

import static java.time.LocalTime.now;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adama.backoffice.products.entity.Product;
import com.adama.backoffice.products.repository.ProductRepository;
import com.adama.product.model.ProductPatchRequest;
import com.adama.product.model.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @MockitoBean
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(productRepository);
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("GET /products when no products exist – should return empty list")
    void getAllProducts_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /products with valid data – should return created product with correct fields")
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setType("Test Type");
        request.setBrand("Test Brand");

        Product savedProduct = new Product();
        savedProduct.setId(UUID.randomUUID());
        savedProduct.setName("Test Product");
        savedProduct.setType("Test Type");
        savedProduct.setBrand("Test Brand");
        savedProduct.setStatus(Product.Status.STOCK);
        savedProduct.setCreated(now().toString());
        savedProduct.setLastModified(now().toString());

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.type", is("Test Type")))
                .andExpect(jsonPath("$.brand", is("Test Brand")))
                .andExpect(jsonPath("$.model", is("Test Model")));
    }

    @Test
    @DisplayName("PATCH /products/{id} – should update product name and keep other fields unchanged")
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        UUID testId = UUID.randomUUID();
        String testIdString = testId.toString();

        ProductPatchRequest request = new ProductPatchRequest();
        request.setName("Updated Product");
        request.setStatus(ProductPatchRequest.StatusEnum.valueOf(Product.Status.INACTIVE.name()));

        Product existingProduct = new Product();
        existingProduct.setId(testId);
        existingProduct.setName("Original Product");
        existingProduct.setDescription("Original Description");
        existingProduct.setType("Original Type");
        existingProduct.setBrand("Original Brand");
        existingProduct.setStatus(Product.Status.STOCK);
        existingProduct.setUserId("original-user");

        Product updatedProduct = new Product();
        updatedProduct.setId(testId);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Original Description");
        updatedProduct.setType("Original Type");
        updatedProduct.setBrand("Original Brand");
        updatedProduct.setStatus(Product.Status.INACTIVE);
        updatedProduct.setUserId("original-user");

        when(productRepository.findById(testId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(patch("/products/{id}", testIdString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Original Description")))
                .andExpect(jsonPath("$.status", is(Product.Status.INACTIVE.name())));
    }

    @Test
    @DisplayName("DELETE /products/{id} with valid ID – should return 204 No Content")
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        // Arrange
        UUID testId = UUID.randomUUID();
        String testIdString = testId.toString();

        when(productRepository.existsById(testId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/products/{id}", testIdString)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /products/{id} with invalid UUID – should return 400 Bad Request")
    void deleteProduct_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/products/{id}", "invalid-uuid")).andExpect(status().isBadRequest());
    }
}
