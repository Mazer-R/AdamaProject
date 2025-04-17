package com.adama.backoffice.products.api;

import com.adama.backoffice.products.entity.Product;
import com.adama.backoffice.products.repository.ProductRepository;
import com.adama.product.model.ProductPatchRequest;
import com.adama.product.model.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductApiFullIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Create a valid product and retrieve it via GET – should return correct product")
    void createAndGetProduct_ShouldWorkEndToEnd() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Integration Test Product");
        request.setDescription("Integration Test Description");
        request.setType("Integration Test Type");
        request.setBrand("Integration Test Brand");
        request.setStatus("ACTIVE");
        request.setUserId("integration-user");

        String responseJson = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Integration Test Product")))
                .andReturn().getResponse().getContentAsString();

        // Extract the ID from the response
        String productId = objectMapper.readTree(responseJson).get("id").asText();

        // Verify the product was saved in the repository
        assertEquals(1, productRepository.count());

        // Get all products
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Integration Test Product")));
    }

    @Test
    @DisplayName("Update a product via PATCH – should modify fields and preserve unchanged ones")
    void updateProduct_ShouldUpdateExistingProduct() throws Exception {
        Product product = new Product();
        product.setName("Original Name");
        product.setDescription("Original Description");
        product.setType("Original Type");
        product.setBrand("Original Brand");
        product.setStatus("INACTIVE");
        product.setUserId("original-user");
        product = productRepository.save(product);
        String productId = product.getId().toString();

        // Update the product via API
        ProductPatchRequest patchRequest = new ProductPatchRequest();
        patchRequest.setName("Updated Name");
        patchRequest.setStatus("ACTIVE");

        mockMvc.perform(patch("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        Product updatedProduct = productRepository.findById(UUID.fromString(productId)).orElseThrow();
        assertEquals("Updated Name", updatedProduct.getName());
        assertEquals("ACTIVE", updatedProduct.getStatus());
        assertEquals("Original Description", updatedProduct.getDescription()); // Unchanged fields should remain the same
    }

    @Test
    @DisplayName("Delete a product via DELETE – should remove it from the repository")
    void deleteProduct_ShouldRemoveProductFromRepository() throws Exception {
        Product product = new Product();
        product.setName("Product To Delete");
        product.setDescription("Delete Me");
        product.setType("Test Type");
        product.setBrand("Test Brand");
        product.setStatus("ACTIVE");
        product.setUserId("test-user");
        product = productRepository.save(product);
        String productId = product.getId().toString();

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());

        assertTrue(productRepository.findById(UUID.fromString(productId)).isEmpty());
    }
}