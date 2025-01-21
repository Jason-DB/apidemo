package com.example.apidemo.model;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private final String jwt;
}
