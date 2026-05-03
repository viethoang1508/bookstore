package org.example.auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.example.auth_service.dto.BaseResponse;
import org.example.auth_service.dto.request.LoginRequest;
import org.example.auth_service.dto.request.RegisterRequest;
import org.example.auth_service.dto.response.TokenResponse;
import org.example.auth_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<String>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(authService.register(request), "Register successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(authService.login(request), "Login successfully"));
    }
}
