package com.adama.backoffice.products.api;


import com.adama.product.api.ProductApi;
import com.adama.product.model.ProductPatchRequest;
import com.adama.product.model.ProductRequest;
import com.adama.product.model.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController implements ProductApi {

    @Override
    public ResponseEntity<ProductResponse> createProduct(ProductRequest productRequest) {
        // TODO: guardar en base de datos
        ProductResponse response = new ProductResponse();
        response.setName(productRequest.getName());
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteProduct(String id) {
        // TODO: eliminar de la base de datos
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        // TODO: obtener desde la base de datos
        return ResponseEntity.ok(new ArrayList<>());
    }

    @Override
    public ResponseEntity<ProductResponse> updateProduct(String id, ProductPatchRequest productPatchRequest) {
        // TODO: buscar por ID, modificar campos y guardar
        ProductResponse response = new ProductResponse();
        response.setName(productPatchRequest.getName());
        return ResponseEntity.ok(response);
    }
}