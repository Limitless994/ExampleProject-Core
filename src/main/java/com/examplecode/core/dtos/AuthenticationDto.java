package com.examplecode.core.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class AuthenticationDto {
    private String username;
    private String password;
}