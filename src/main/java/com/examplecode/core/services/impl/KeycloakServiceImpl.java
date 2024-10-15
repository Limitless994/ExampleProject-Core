package com.examplecode.core.services.impl;

import com.examplecode.core.dtos.AuthenticationDto;
import com.examplecode.core.dtos.AuthenticationResponseDto;
import com.examplecode.core.dtos.UserDto;
import com.examplecode.core.dtos.UserRegistrationDto;
import com.examplecode.core.services.KeycloakService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final RestTemplate restTemplate;

    @Autowired
    public KeycloakServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AuthenticationResponseDto authenticate(AuthenticationDto authDto) {
        // Crea i parametri per la richiesta al token endpoint di Keycloak
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("username", authDto.getUsername());
        requestParams.add("password", authDto.getPassword());
        requestParams.add("grant_type", "password");
        requestParams.add("client_id", System.getenv("KEYCLOAK_CLIENT_ID"));
        requestParams.add("client_secret", System.getenv("KEYCLOAK_CLIENT_SECRET"));

        try {
            // Effettua la richiesta al server di Keycloak
            ResponseEntity<String> response = restTemplate.postForEntity(System.getenv("KEYCLOAK_BASE_URL") + System.getenv("KEYCLOAK_AUTH_URL"), requestParams, String.class);

            // Se la risposta è 2xx, deserializza il corpo in AuthenticationResponseDto
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getBody(), AuthenticationResponseDto.class);
            } else {
                // Se non è un successo 2xx, ritorna un oggetto con messaggio di fallimento
                return buildErrorResponse("Invalid credentials", response.getStatusCodeValue());
            }
        } catch (Exception e) {
            // Log dell'errore e ritorno di un messaggio di errore personalizzato
            System.err.println("Authentication failed: " + e.getMessage());
            return buildErrorResponse("Authentication error", 500);
        }
    }

    // Costruisce una AuthenticationResponseDto con un messaggio di errore
    private AuthenticationResponseDto buildErrorResponse(String message, int status) {
        AuthenticationResponseDto errorResponse = new AuthenticationResponseDto();
        errorResponse.setAccessToken(message);  // Inserisci il messaggio nel campo accessToken (o un altro campo)
        errorResponse.setExpiresIn(0L);         // Indica che il token non è valido impostando expiresIn a 0
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
            headers.setBearerAuth(authenticate(new AuthenticationDto(
                    System.getenv("KEYCLOAK_USERS_MANAGER_EMAIL"),
                    System.getenv("KEYCLOAK_USERS_MANAGER_PASSWORD")
            )).getAccessToken());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            restTemplate.postForEntity(
                    System.getenv("KEYCLOAK_BASE_URL") +
                    System.getenv("KEYCLOAK_USERS_URL"),
                    entity, Void.class);
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
            headers.set("Authorization", "Bearer " + authenticate(new AuthenticationDto(
                    System.getenv("KEYCLOAK_USERS_MANAGER_EMAIL"),
                    System.getenv("KEYCLOAK_USERS_MANAGER_PASSWORD")
            )).getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<UserDto[]> response = restTemplate.exchange(
                    System.getenv("KEYCLOAK_BASE_URL") + System.getenv("KEYCLOAK_USERS_URL"),
                    HttpMethod.GET, entity, UserDto[].class);

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
            headers.set("Authorization", "Bearer " + authenticate(new AuthenticationDto(
                    System.getenv("KEYCLOAK_USERS_MANAGER_EMAIL"),
                    System.getenv("KEYCLOAK_USERS_MANAGER_PASSWORD")
            )).getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String deleteUserUrl = System.getenv("KEYCLOAK_BASE_URL") + System.getenv("KEYCLOAK_USERS_URL") + "/" + userId;

            ResponseEntity<Void> response = restTemplate.exchange(
                    deleteUserUrl,
                    HttpMethod.DELETE,
                    entity,
                    Void.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }
}

