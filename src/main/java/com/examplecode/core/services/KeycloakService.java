package com.examplecode.core.services;

import com.examplecode.core.dtos.AuthenticationDto;
import com.examplecode.core.dtos.AuthenticationResponseDto;
import com.examplecode.core.dtos.UserDto;
import com.examplecode.core.dtos.UserRegistrationDto;

import java.util.List;

public interface KeycloakService {
    AuthenticationResponseDto authenticate(AuthenticationDto authenticationDto);
    boolean registerUser(UserRegistrationDto userDto);
    List<UserDto> getUsers(); // Nuovo metodo per ottenere la lista degli utenti
    boolean deleteUser(String userId);
}