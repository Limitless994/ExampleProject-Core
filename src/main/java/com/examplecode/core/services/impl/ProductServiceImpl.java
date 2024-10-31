package com.examplecode.core.services.impl;

import com.examplecode.core.dtos.ProductDto;
import com.examplecode.core.models.Product;
import com.examplecode.core.repositories.ProductRepository;
import com.examplecode.core.services.ProductService;
import com.examplecode.core.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public ProductDto createProduct(String userId, ProductDto productDto) {
        Product product = ProductMapper.INSTANCE.toEntity(productDto); // Usa il mapper per convertire DTO in entità
        product.setUserId(userId);

        Product savedProduct = productRepository.save(product);
        return ProductMapper.INSTANCE.toDto(savedProduct); // Usa il mapper per convertire entità in DTO
    }

    @Override
    public ProductDto updateProduct(String userId, Long productId, ProductDto productDto) {
        Product product = productRepository.findById(productId)
                .filter(p -> p.getUserId().equals(userId)) // Verifica che l'utente sia proprietario
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Usa il mapper per aggiornare i campi
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());

        Product updatedProduct = productRepository.save(product);
        return ProductMapper.INSTANCE.toDto(updatedProduct); // Usa il mapper per convertire entità in DTO
    }

    @Override
    public boolean deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElse(null);

        if (product != null) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public ProductDto getProductById(String userId, Long productId) {
        return productRepository.findById(productId)
                .filter(p -> p.getUserId().equals(userId))
                .map(ProductMapper.INSTANCE::toDto) // Usa il mapper per convertire entità in DTO
                .orElse(null);
    }

    @Override
    public List<ProductDto> getAllProductsForUser(String userId) {
        return productRepository.findByUserId(userId).stream()
                .map(ProductMapper.INSTANCE::toDto) // Usa il mapper per convertire entità in DTO
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper.INSTANCE::toDto) // Usa il mapper per convertire entità in DTO
                .collect(Collectors.toList());
    }
}
