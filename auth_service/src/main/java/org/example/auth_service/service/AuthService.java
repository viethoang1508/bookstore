package org.example.auth_service.service;

import org.example.auth_service.dto.request.LoginRequest;
import org.example.auth_service.dto.request.RegisterRequest;
import org.example.auth_service.dto.response.TokenResponse;

public interface AuthService {
    String register (RegisterRequest request);
    TokenResponse login (LoginRequest request);
}
