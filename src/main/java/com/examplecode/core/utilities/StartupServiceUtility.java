package com.examplecode.core.utilities;

import com.examplecode.core.dtos.ProductDto;
import com.examplecode.core.dtos.UserDto;
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
import java.util.Random;

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
    private void initializeUsers() {
        try (InputStream inputStream = getClass().getResourceAsStream("/MocksJson/usersMock.json")) {
            if (inputStream != null) {
                List<UserRegistrationDto> usersToRegister = objectMapper.readValue(inputStream, new TypeReference<List<UserRegistrationDto>>() {});

                for (UserRegistrationDto userDto : usersToRegister) {
                    boolean success = keycloakService.registerUser(userDto);
                    if (success) {
                        System.out.println("User registered successfully: " + userDto.getUsername());
                    } else {
                        System.err.println("Failed to register user: " + userDto.getUsername());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void initializeProducts() {
        try (InputStream inputStream = getClass().getResourceAsStream("/MocksJson/productsMock.json")) {
            if (inputStream != null) {
                List<ProductDto> products = objectMapper.readValue(inputStream, new TypeReference<List<ProductDto>>() {});
                List<String> userIds = keycloakService.getUsers().stream()
                        .map(UserDto::getId)
                        .toList();

                Random random = new Random();
                for (ProductDto product : products) {
                    String randomId = userIds.get(random.nextInt(userIds.size()));
                    productService.createProduct(randomId, product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
