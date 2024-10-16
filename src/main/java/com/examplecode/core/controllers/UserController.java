package com.examplecode.core.controllers;

import com.examplecode.core.dtos.AuthenticationDto;
import com.examplecode.core.dtos.AuthenticationResponseDto;
import com.examplecode.core.dtos.UserDto;
import com.examplecode.core.dtos.UserRegistrationDto;
import com.examplecode.core.services.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class UserController {

    private final KeycloakService keycloakService;

    @Autowired
    public UserController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/public/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationDto authenticationDto) {
        AuthenticationResponseDto response = keycloakService.authenticate(authenticationDto);

            return ResponseEntity.ok(response);

    }

    @PostMapping("/public/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto userDto) {
        boolean isRegistered = keycloakService.registerUser(userDto);

        if (isRegistered) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed.");
        }
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = keycloakService.getUsers();

        if (users != null && !users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        boolean isDeleted = keycloakService.deleteUser(userId);
        if (isDeleted) {
            return new ResponseEntity<>("Utente eliminato con successo.", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Errore durante l'eliminazione dell'utente.", HttpStatus.NOT_FOUND);
        }
    }
}
