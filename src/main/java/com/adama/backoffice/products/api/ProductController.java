package com.adama.backoffice.products.api;

import com.adama.backoffice.products.entity.Product;
import com.adama.backoffice.products.mapper.ProductMapper;
import com.adama.backoffice.products.repository.ProductRepository;
import com.adama.backoffice.products.service.ProductService;
import com.adama.product.api.ProductApi;
import com.adama.product.model.ProductPatchRequest;
import com.adama.product.model.ProductRequest;
import com.adama.product.model.ProductResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController implements ProductApi {

    private final ProductRepository productRepository;

    private final ProductService productService;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Override
    @PostMapping("/products")
    @PreAuthorize("hasAnyRole('ROLE_WAREHOUSE', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(name = "ProductRequest", required = true) @Valid @RequestBody ProductRequest productRequest) {

        Product product = ProductMapper.toEntity(productRequest);
        product = productRepository.save(product);
        ProductResponse response = ProductMapper.toResponse(product);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    @DeleteMapping("products/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            if (productRepository.existsById(uuid)) {
                productRepository.deleteById(uuid);
            }
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String type, @RequestParam(required = false) String brand) {

        type = (type != null) ? type.trim() : null;
        brand = (brand != null) ? brand.trim() : null;

        List<Product> products;

        if (type != null && !type.isEmpty() && brand != null && !brand.isEmpty()) {
            products = productRepository.findByTypeAndBrand(type, brand);
        } else if (type != null && !type.isEmpty()) {
            products = productRepository.findByType(type);
        } else if (brand != null && !brand.isEmpty()) {
            products = productRepository.findByBrand(brand);
        } else {
            products = productRepository.findAll();
        }

        return ResponseEntity.ok(
                products.stream().map(ProductMapper::toResponse).collect(Collectors.toList()));
    }

    @Override
    @GetMapping("products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<Product> optionalProduct = productRepository.findById(uuid);

            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                return ResponseEntity.ok(ProductMapper.toResponse(product));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @PatchMapping("/products/{id}")
    @PreAuthorize("hasAnyRole('ROLE_WAREHOUSE', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id, @Valid @RequestBody ProductPatchRequest productPatchRequest) {

        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Product updatedProduct = productService.updateProduct(id, productPatchRequest);
            ProductResponse response = ProductMapper.toResponse(updatedProduct);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
