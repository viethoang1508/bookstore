package org.example.auth_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.auth_service.dto.BaseResponse;
import org.example.auth_service.dto.request.CreateAdminRequest;
import org.example.auth_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AuthService authService;

    @PostMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<BaseResponse<String>> createAdmin(@RequestBody @Valid CreateAdminRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(authService.createAdmin(request), "Admin created successfully"));
    }
}