package com.adama.backoffice.products.api;

import com.adama.backoffice.products.entity.Product;
import com.adama.backoffice.products.mapper.ProductMapper;
import com.adama.backoffice.products.repository.ProductRepository;
import com.adama.product.api.ProductApi;
import com.adama.product.model.ProductPatchRequest;
import com.adama.product.model.ProductRequest;
import com.adama.product.model.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ProductResponse> createProduct(ProductRequest productRequest) {
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
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        List<Product> products = productRepository.findAll();
        List<ProductResponse> responses = products.stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
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
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<ProductResponse> updateProduct(String id, ProductPatchRequest productPatchRequest) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<Product> optionalProduct = productRepository.findById(uuid);

            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();

                // Update only the fields that are present in the request
                if (productPatchRequest.getName() != null) {
                    product.setName(productPatchRequest.getName());
                }
                if (productPatchRequest.getDescription() != null) {
                    product.setDescription(productPatchRequest.getDescription());
                }
                if (productPatchRequest.getType() != null) {
                    product.setType(productPatchRequest.getType());
                }
                if (productPatchRequest.getBrand() != null) {
                    product.setBrand(productPatchRequest.getBrand());
                }
                if (productPatchRequest.getModel() != null) {
                    product.setModel(productPatchRequest.getModel());
                }
                if (productPatchRequest.getStatus() != null) {
                    product.setStatus(productPatchRequest.getStatus());
                }

                product.setLastModified(LocalDateTime.now());
                product = productRepository.save(product);

                return ResponseEntity.ok(ProductMapper.toResponse(product));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model
    ) {
        List<Product> products;

        if (brand != null && model != null) {
            products = productRepository.findByBrandAndModel(brand, model);
        } else if (brand != null) {
            products = productRepository.findByBrand(brand);
        } else {
            products = productRepository.findAll();
        }

        List<ProductResponse> responses = products.stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}
