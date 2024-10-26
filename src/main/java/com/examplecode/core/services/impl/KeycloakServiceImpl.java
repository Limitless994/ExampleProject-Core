package com.examplecode.core.services.impl;

import com.examplecode.core.dtos.AuthenticationDto;
import com.examplecode.core.dtos.AuthenticationResponseDto;
import com.examplecode.core.dtos.UserDto;
import com.examplecode.core.dtos.UserRegistrationDto;
import com.examplecode.core.services.KeycloakService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final RestTemplate restTemplate;
    private final String keycloakBaseUrl = System.getenv("KEYCLOAK_BASE_URL");
    private final String usersUrl = System.getenv("KEYCLOAK_USERS_URL");
    private final String authUrl = System.getenv("KEYCLOAK_AUTH_URL");

    @Autowired
    public KeycloakServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getCachedToken() {
        return authenticate(new AuthenticationDto(
                System.getenv("KEYCLOAK_USERS_MANAGER_EMAIL"),
                System.getenv("KEYCLOAK_USERS_MANAGER_PASSWORD")
        )).getAccessToken();
    }

    @Override
    public AuthenticationResponseDto authenticate(AuthenticationDto authDto) {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("username", authDto.getUsername());
        requestParams.add("password", authDto.getPassword());
        requestParams.add("grant_type", "password");
        requestParams.add("client_id", System.getenv("KEYCLOAK_CLIENT_ID"));
        requestParams.add("client_secret", System.getenv("KEYCLOAK_CLIENT_SECRET"));

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(keycloakBaseUrl + authUrl, requestParams, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getBody(), AuthenticationResponseDto.class);
            } else {
                return buildErrorResponse("Invalid credentials", response.getStatusCodeValue());
            }
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return buildErrorResponse("Authentication error", 500);
        }
    }

    private AuthenticationResponseDto buildErrorResponse(String message, int status) {
        AuthenticationResponseDto errorResponse = new AuthenticationResponseDto();
        errorResponse.setAccessToken(message);
        errorResponse.setExpiresIn(0L);
        return errorResponse;
    }

    @Override
    public boolean registerUser(UserRegistrationDto userDto) {
        Map<String, Object> request = new HashMap<>();
        request.put("username", userDto.getUsername());
        request.put("email", userDto.getEmail());
        request.put("firstName", userDto.getFirstName());
        request.put("lastName", userDto.getLastName());
        request.put("enabled", true);
        request.put("credentials", Collections.singletonList(
                Map.of("type", "password", "value", userDto.getPassword(), "temporary", false)
        ));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getCachedToken());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            restTemplate.postForEntity(keycloakBaseUrl + usersUrl, entity, Void.class);
            return true;
        } catch (HttpClientErrorException e) {
            System.err.println("Failed to register user: " + e.getResponseBodyAsString());
            return false;
        } catch (RestClientException e) {
            System.err.println("An error occurred during the request: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<UserDto> getUsers() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getCachedToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<UserDto[]> response = restTemplate.exchange(
                    keycloakBaseUrl + usersUrl, HttpMethod.GET, entity, UserDto[].class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Arrays.asList(response.getBody());
            } else {
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean deleteUser(String userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getCachedToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String deleteUserUrl = keycloakBaseUrl + usersUrl + "/" + userId;

            ResponseEntity<Void> response = restTemplate.exchange(
                    deleteUserUrl, HttpMethod.DELETE, entity, Void.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }

    @Override
    public boolean updateUser(String userId, UserDto userDto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getCachedToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> request = new HashMap<>();
            request.put("firstName", userDto.getFirstName());
            request.put("lastName", userDto.getLastName());
            request.put("email", userDto.getEmail());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            String updateUserUrl = keycloakBaseUrl + usersUrl + "/" + userId;

            ResponseEntity<Void> response = restTemplate.exchange(
                    updateUserUrl, HttpMethod.PUT, entity, Void.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            System.err.println("Failed to update user: " + e.getResponseBodyAsString());
            return false;
        } catch (RestClientException e) {
            System.err.println("An error occurred during the update request: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getCurrentUserId() {
        // Ottieni l'autenticazione corrente
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'autenticazione è di tipo JwtAuthenticationToken
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            // Ottieni il token JWT
            Jwt jwt = (Jwt) jwtAuth.getPrincipal(); // Puoi usare jwtAuth.getToken() se lo hai definito

            // Estrai il claim "sub"
            return (String) jwt.getClaims().get("sub"); // Cast al tipo corretto
        }

        return null; // Gestisci il caso in cui non ci sia un'ID utente
    }

    @Override
    public boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

}
