package com.shake_art.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType;
    private long expiresAt;
    private String role;
}
