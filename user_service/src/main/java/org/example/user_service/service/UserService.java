package org.example.user_service.service;

import org.example.user_service.dto.request.CreateUserRequest;
import org.example.user_service.dto.request.UpdateProfileRequest;
import org.example.user_service.dto.response.UserResponse;

public interface UserService {
    UserResponse getMyProfile(String userId);
    UserResponse updateProfile(String userId, UpdateProfileRequest request);
    UserResponse createProfile(CreateUserRequest request);
}
