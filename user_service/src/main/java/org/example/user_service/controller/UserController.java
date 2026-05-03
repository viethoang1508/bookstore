package org.example.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user_service.dto.request.UpdateProfileRequest;
import org.example.user_service.dto.response.BaseResponse;
import org.example.user_service.dto.response.UserResponse;
import org.example.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserResponse>> getMyProfile(
            JwtAuthenticationToken token
    ) {
        String userId = token.getName();
        return ResponseEntity.ok(new BaseResponse<>(userService.getMyProfile(userId), "Get personal profile successfully"));
    }

    @PutMapping("/me")
    public ResponseEntity<BaseResponse<UserResponse>> updateProfile(
            JwtAuthenticationToken token,
            @RequestBody UpdateProfileRequest request
    ) {
        String userId = token.getName();
        return ResponseEntity.ok(new BaseResponse<>(userService.updateProfile(userId, request), "Update personal profile successfully"));
    }
}
