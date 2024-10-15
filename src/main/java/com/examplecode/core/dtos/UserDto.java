package com.examplecode.core.dtos;

import lombok.*;

import java.util.List;

@Data
public class UserDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}