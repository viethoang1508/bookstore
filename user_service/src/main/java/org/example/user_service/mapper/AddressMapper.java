package org.example.user_service.mapper;

import org.example.user_service.dto.request.CreateAddressRequest;
import org.example.user_service.dto.request.CreateUserRequest;
import org.example.user_service.dto.request.UpdateAddressRequest;
import org.example.user_service.dto.request.UpdateProfileRequest;
import org.example.user_service.dto.response.AddressResponse;
import org.example.user_service.dto.response.UserResponse;
import org.example.user_service.entity.Address;
import org.example.user_service.entity.User;
import org.example.user_service.entity.UserStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    // Response
    AddressResponse toResponse(Address address);

    // Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Address toEntity(CreateAddressRequest request);

    // Update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddress(UpdateAddressRequest request, @MappingTarget Address address);

    default String mapStatus(UserStatus status) {
        return status != null ? status.name() : null;
    }
}
