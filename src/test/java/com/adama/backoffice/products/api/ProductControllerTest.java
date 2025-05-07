package com.adama.backoffice.products.api;

import com.adama.backoffice.BackofficeApplicationTests;
import com.adama.backoffice.products.entity.Product;
import com.adama.backoffice.products.repository.ProductRepository;
import com.adama.product.model.ProductPatchRequest;
import com.adama.product.model.ProductRequest;
import com.adama.product.model.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController productController;

    private UUID testId;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testProduct = new Product();
        testProduct.setId(testId);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setType("Test Type");
        testProduct.setBrand("Test Brand");
        testProduct.setModel("Test Model");
        testProduct.setStatus("ACTIVE");
        testProduct.setUserId("user123");
    }

    @Test
    @DisplayName("POST createProduct – should return 201 Created and response body with saved product")
    void createProduct_ShouldReturnCreatedStatus() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setType("Test Type");
        request.setBrand("Test Brand");
        request.setModel("Test Model");
        request.setStatus("ACTIVE");
        request.setUserId("user123");

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ResponseEntity<ProductResponse> response = productController.createProduct(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Product", response.getBody().getName());
        assertEquals("Test Model", response.getBody().getModel());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("DELETE deleteProduct with valid ID – should return 204 No Content")
    void deleteProduct_ShouldReturnNoContent() {
        // Arrange
        String id = testId.toString();
        when(productRepository.existsById(testId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(testId);

        // Act
        ResponseEntity<Void> response = productController.deleteProduct(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productRepository).existsById(testId);
        verify(productRepository).deleteById(testId);
    }

    @Test
    @DisplayName("DELETE deleteProduct with invalid ID format – should return 400 Bad Request")
    void deleteProduct_WithInvalidId_ShouldReturnBadRequest() {
        ResponseEntity<Void> response = productController.deleteProduct("invalid-id");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("GET getAllProducts – should return list with all products and 200 OK")
    void getAllProducts_ShouldReturnProductList() {
        // Arrange
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        ResponseEntity<List<ProductResponse>> response = productController.getAllProducts(null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Product", response.getBody().getFirst().getName());
        verify(productRepository).findAll();
    }
    @Test
    @DisplayName("GET getProducts – with only type returns filtered products")
    void getProducts_WithOnlyType_ReturnsFilteredByType() {
        String type = "Test Type";
        List<Product> products = List.of(testProduct);
        when(productRepository.findByType(type)).thenReturn(products);

        ResponseEntity<List<ProductResponse>> response = productController.getAllProducts(type, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Type", response.getBody().getFirst().getType());
        verify(productRepository).findByType(type);
    }


    @Test
    @DisplayName("PATCH updateProduct with valid ID – should return updated product and 200 OK")
    void updateProduct_ShouldReturnUpdatedProduct() {
        // Arrange
        String id = testId.toString();
        ProductPatchRequest request = new ProductPatchRequest();
        request.setName("Updated Product");
        request.setModel("Updated Model");

        Product updatedProduct = new Product();
        updatedProduct.setId(testId);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Test Description");
        updatedProduct.setType("Test Type");
        updatedProduct.setBrand("Test Brand");
        updatedProduct.setModel("Updated Model");
        updatedProduct.setStatus("ACTIVE");
        updatedProduct.setUserId("user123");

        when(productRepository.findById(testId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        ResponseEntity<ProductResponse> response = productController.updateProduct(id, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Product", response.getBody().getName());
        assertEquals("Updated Model", response.getBody().getModel());
        verify(productRepository).findById(testId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("PATCH updateProduct with non-existent ID – should return 404 Not Found")
    void updateProduct_WithNonExistentId_ShouldReturnNotFound() {
        // Arrange
        String id = testId.toString();
        ProductPatchRequest request = new ProductPatchRequest();
        request.setName("Updated Product");

        when(productRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ProductResponse> response = productController.updateProduct(id, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productRepository).findById(testId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("PATCH updateProduct with invalid ID format – should return 400 Bad Request")
    void updateProduct_WithInvalidId_ShouldReturnNotFound() {
        // Arrange
        ProductPatchRequest request = new ProductPatchRequest();
        request.setName("Updated Product");

        // Act
        ResponseEntity<ProductResponse> response = productController.updateProduct("invalid-id", request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(productRepository);
    }
    @Test
    @DisplayName("GET getProductById with valid ID-should return  200 OK")
    void getProductById_ValidExistingId_ReturnsProduct() {
        when(productRepository.findById(testId)).thenReturn(Optional.of(testProduct));

        ResponseEntity<ProductResponse> response = productController.getProductById(testId.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testProduct.getName(), response.getBody().getName());
        assertEquals(testProduct.getModel(), response.getBody().getModel());
    }
    @Test
    @DisplayName("GET getProductById with invalid ID – return 404 Not Found")
    void getProductById_InvalidId_ReturnsNotFound() {
        ResponseEntity<ProductResponse> response = productController.getProductById("invalid-id");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(productRepository);
    }

   }
