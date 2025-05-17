package com.adama.backoffice.products.mapper;

import com.adama.backoffice.products.entity.Product;
import com.adama.product.model.ProductRequest;
import com.adama.product.model.ProductResponse;
import java.time.LocalDateTime;

public class ProductMapper {

    public static Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setType(request.getType());
        product.setBrand(request.getBrand());
        product.setStatus(Product.Status.STOCK);
        product.setUserId(request.getUserId());
        product.setCreated(LocalDateTime.now().toString());
        product.setLastModified(LocalDateTime.now().toString());
        return product;
    }

    public static ProductResponse toResponse(Product entity) {
        ProductResponse response = new ProductResponse();
        response.setId(entity.getId().toString());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setType(entity.getType());
        response.setBrand(entity.getBrand());
        response.setStatus(ProductResponse.StatusEnum.valueOf(entity.getStatus().name()));
        response.setUserId(entity.getUserId());
        response.setCreated(entity.getCreated());
        response.setLastModified(entity.getLastModified());
        return response;
    }
}
