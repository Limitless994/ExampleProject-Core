package com.examplecode.core.controllers;

import com.examplecode.core.dtos.ProductDto;
import com.examplecode.core.services.ProductService;
import com.examplecode.core.services.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductService productService;
    private final KeycloakService keycloakService;

    @Autowired
    public ProductController(ProductService productService, KeycloakService keycloakService) {
        this.productService = productService;
        this.keycloakService = keycloakService;
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        String userId = keycloakService.getCurrentUserId();
        ProductDto createdProduct = productService.createProduct(userId, productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        String userId = keycloakService.getCurrentUserId();
        ProductDto updatedProduct = productService.updateProduct(userId, id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        String userId = keycloakService.getCurrentUserId();
        ProductDto product = productService.getProductById(userId, id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/products")
    public List<ProductDto> getAllProductsForUser() {
        String userId = keycloakService.getCurrentUserId();
        return productService.getAllProductsForUser(userId);
    }

    @GetMapping("/admin/products")
    public ResponseEntity<List<ProductDto>> getAllProductsForAdmin() {
        if (keycloakService.isAdmin()) {
            return ResponseEntity.ok(productService.getAllProducts());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
