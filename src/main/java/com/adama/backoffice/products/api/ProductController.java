package com.adama.backoffice.products.api;

import com.adama.backoffice.products.entity.Product;
import com.adama.backoffice.products.mapper.ProductMapper;
import com.adama.backoffice.products.repository.ProductRepository;
import com.adama.product.api.ProductApi;
import com.adama.product.model.ProductPatchRequest;
import com.adama.product.model.ProductRequest;
import com.adama.product.model.ProductResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductController implements ProductApi {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(name = "ProductRequest", description = "", required = true)
            @Valid @RequestBody ProductRequest productRequest) {

        Product product = ProductMapper.toEntity(productRequest);
        product = productRepository.save(product);
        ProductResponse response = ProductMapper.toResponse(product);
        return ResponseEntity.status(201).body(response);
    }


    @Override
    public ResponseEntity<Void> deleteProduct(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            if (productRepository.existsById(uuid)) {
                productRepository.deleteById(uuid);
            }
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();         }
    }

    @Override
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String brand) {

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

        return ResponseEntity.ok(products.stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<ProductResponse> getProductById(String id) {
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
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") String id,
            @Valid @RequestBody ProductPatchRequest productPatchRequest) {

        try {
            // Parse the UUID from the provided ID
            UUID uuid = UUID.fromString(id);

            // Check if the product exists
            Optional<Product> optionalProduct = productRepository.findById(uuid);
            if (optionalProduct.isEmpty()) {
                return ResponseEntity.notFound().build(); // Return 404 if not found
            }

            // Update the product fields
            Product product = optionalProduct.get();
            if (productPatchRequest.getName() != null) product.setName(productPatchRequest.getName());
            if (productPatchRequest.getDescription() != null) product.setDescription(productPatchRequest.getDescription());
            if (productPatchRequest.getType() != null) product.setType(productPatchRequest.getType());
            if (productPatchRequest.getBrand() != null) product.setBrand(productPatchRequest.getBrand());
            if (productPatchRequest.getModel() != null) product.setModel(productPatchRequest.getModel());
            if (productPatchRequest.getStatus() != null) product.setStatus(productPatchRequest.getStatus());

            // Update metadata
            product.setLastModified(LocalDateTime.now());
            product.setModifiedBy("SYSTEM");

            // Save the updated product
            productRepository.save(product);

            // Map the updated product to a response object
            ProductResponse response = ProductMapper.toResponse(product);

            // Return the updated product with a 200 status
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Return 400 if the ID is not a valid UUID
            return ResponseEntity.badRequest().build();
        }
    }


    }