package org.example.user_service.service.impl;

import lombok.RequiredArgsConstructor;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getMyProfile(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("Invalid user id");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {throw new ApplicationException("Cannot find user with id " + {userId});});

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateProfile(String userId, UpdateProfileRequest request) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("Invalid user id");
        }

        if (request == null) {
            throw new ApplicationException("Invalid request");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {throw new ApplicationException("Cannot find user with id " + {userId});});

        user = userMapper.updateUser(request);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse createProfile(UpdateProfileRequest request) {
        if (request == null) {
            throw new ApplicationException("Invalid request");
        }

        User user = userMapper.toEntity(request);

        return userMapper.toResponse(user);
    }
}
