package com.examplecode.core.dtos;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
}