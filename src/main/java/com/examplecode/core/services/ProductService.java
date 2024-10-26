package com.examplecode.core.services;

import com.examplecode.core.dtos.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(String userId, ProductDto productDto);
    ProductDto updateProduct(String userId, Long productId, ProductDto productDto);
    boolean deleteProduct(String userId, Long productId);
    ProductDto getProductById(String userId, Long productId);
    List<ProductDto> getAllProductsForUser(String userId);
    List<ProductDto> getAllProducts();
}