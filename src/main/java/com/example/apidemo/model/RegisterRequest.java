package com.example.apidemo.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}
