package org.example.user_service.mapper;

import org.example.user_service.dto.request.CreateUserRequest;
import org.example.user_service.dto.request.UpdateProfileRequest;
import org.example.user_service.dto.response.UserResponse;
import org.example.user_service.entity.User;
import org.example.user_service.entity.UserStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Response
    @Mapping(target = "status", expression = "java(mapStatus(user.getStatus()))")
    UserResponse toResponse(User user);

    // Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);

    // Update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UpdateProfileRequest request, @MappingTarget User user);

    default String mapStatus(UserStatus status) {
        return status != null ? status.name() : null;
    }
}
