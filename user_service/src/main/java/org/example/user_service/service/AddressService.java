package org.example.user_service.service;

import org.example.user_service.dto.request.CreateAddressRequest;
import org.example.user_service.dto.request.UpdateAddressRequest;
import org.example.user_service.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getByUser(String userId);
    AddressResponse create(String userId, CreateAddressRequest request);
    AddressResponse updateAddress(String userId, String id, UpdateAddressRequest request);
    Void delete(String userId, String id);
    Void setDefault(String userId, String id);
}
