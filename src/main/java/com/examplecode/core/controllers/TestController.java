package com.examplecode.core.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint accessible by anyone.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userEndpoint() {
        return "This is a user endpoint, accessible only to users with the 'USER' role.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "This is an admin endpoint, accessible only to users with the 'ADMIN' role.";
    }
}
