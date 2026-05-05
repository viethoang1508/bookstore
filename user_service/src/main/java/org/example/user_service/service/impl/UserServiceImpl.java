package org.example.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user_service.dto.request.CreateUserRequest;
import org.example.user_service.dto.request.UpdateProfileRequest;
import org.example.user_service.dto.response.UserResponse;
import org.example.user_service.entity.User;
import org.example.user_service.exception.ApplicationException;
import org.example.user_service.mapper.UserMapper;
import org.example.user_service.repository.UserRepository;
import org.example.user_service.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getMyProfile(String userId) {
        log.info("Fetching user profile, userId={}", userId);
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("Invalid user id");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException("Cannot find user with id " + userId));

        log.info("Fetched user profile successfully, userId={}", userId);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateProfile(String userId, UpdateProfileRequest request) {
        log.info("Updating user profile, userId={}", userId);

        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("Invalid user id");
        }

        if (request == null) {
            throw new ApplicationException("Invalid request");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException("Cannot find user with id " + userId));

        userMapper.updateUser(request);

        user = userRepository.save(user);

        log.info("Updated user profile successfully, userId={}", userId);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse createProfile(CreateUserRequest request) {
        log.info("Creating user profile");
        if (request == null) {
            throw new ApplicationException("Invalid request");
        }

        User user = userMapper.toEntity(request);

        user = userRepository.save(user);

        log.info("Created user profile successfully");
        
        return userMapper.toResponse(user);
    }
}
