package com.examplecode.core.utilities;

import com.examplecode.core.dtos.ProductDto;
import com.examplecode.core.dtos.UserRegistrationDto;
import com.examplecode.core.services.KeycloakService;
import com.examplecode.core.services.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class StartupServiceUtility {

    private final ProductService productService;
    private final KeycloakService keycloakService;
    private final ObjectMapper objectMapper;

    @Autowired
    public StartupServiceUtility(ProductService productService, KeycloakService keycloakService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.keycloakService = keycloakService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initializeData() {
        initializeUsers();
        initializeProducts();
    }

    private void initializeUsers() {
        // Dati fittizi per la registrazione degli utenti
        List<UserRegistrationDto> usersToRegister = List.of(
                new UserRegistrationDto("user1","test", "first", "User", "user1@example.com" ),
                new UserRegistrationDto("user2","test", "second", "User", "user2@example.com" ),
                new UserRegistrationDto("user3","test", "third", "User", "user3@example.com" ),
                new UserRegistrationDto("user4","test", "fourth", "User", "user4@example.com" )
        );

        for (UserRegistrationDto userDto : usersToRegister) {
            boolean success = keycloakService.registerUser(userDto);
            if (success) {
                System.out.println("User registered successfully: " + userDto.getUsername());
            } else {
                System.err.println("Failed to register user: " + userDto.getUsername());
            }
        }
    }

    private void initializeProducts() {
        try (InputStream inputStream = getClass().getResourceAsStream("/productsMock.json")) {
            if (inputStream != null) {
                List<ProductDto> products = objectMapper.readValue(inputStream, new TypeReference<List<ProductDto>>() {});
                for (ProductDto product : products) {
                    // Imposta un ID utente predefinito (ad esempio, "user1") per il mock
                    product.setUserId("user1"); // Puoi personalizzare l'ID utente come necessario
                    productService.createProduct(product.getUserId(), product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}