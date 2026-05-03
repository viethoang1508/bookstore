package org.example.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.example.user_service.dto.response.BaseResponse;
import org.example.user_service.dto.response.UserResponse;
import org.example.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("internal/users")
public class InternalUserController {

    private final UserService userService;

}
