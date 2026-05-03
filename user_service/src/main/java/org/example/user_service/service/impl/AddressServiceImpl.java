package org.example.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.user_service.dto.request.CreateAddressRequest;
import org.example.user_service.dto.request.UpdateAddressRequest;
import org.example.user_service.dto.response.AddressResponse;
import org.example.user_service.entity.Address;
import org.example.user_service.exception.ApplicationException;
import org.example.user_service.mapper.AddressMapper;
import org.example.user_service.repository.AddressRepository;
import org.example.user_service.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressService addressService;
    private final AddressMapper addressMapper;

    @Override
    public List<AddressResponse> getByUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("userId cannot be null or blank");
        }

        List<Address> addressList = addressRepository.findByUserIdAndDeletedFalse(userId);

        List<AddressResponse> responseList = addressList.stream()
                .map(addressMapper::toResponse)
                .toList();

        return responseList;
    }

    @Override
    public AddressResponse create(String userId, CreateAddressRequest request) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("userId cannot be null or blank");
        }

        if (request == null) {
            throw new ApplicationException("request cannot be null");
        }

        Address address = new  Address();

        // Nếu là default -> clear default cũ
        if(request.getIsDefault().equals(true)) {
            clearDefaultSAddress(userId);
            address.setIsDefault(true);
        } else {
            //Nếu user chưa có default address -> set default cho address này luôn
            boolean isDefault = addressRepository
                    .findByUserIdAndIsDefaultTrueAndDeletedFalse(userId)
                    .size() > 0;
            if(!isDefault) {
                address.setIsDefault(true);
            }
        }

        address = addressMapper.toEntity(request);

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    public AddressResponse updateAddress(String userId, String id, UpdateAddressRequest request) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("userId cannot be null or blank");
        }

        if (request == null) {
            throw new ApplicationException("request cannot be null");
        }

        if (id == null || id.isBlank()) {
            throw new ApplicationException("id cannot be null or blank");
        }

        Address address = getOwnedAddress(userId, id);
        if(request.getIsDefault().equals(true)) {
            clearDefaultSAddress(userId);
            address.setIsDefault(true);
        }

        address = addressMapper.updateAddress(request);

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    public void delete(String userId, String id) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("userId cannot be null or blank");
        }

        if (id == null || id.isBlank()) {
            throw new ApplicationException("id cannot be null or blank");
        }

        Address address = getOwnedAddress(userId, id);
        if(address.getIsDefault().equals(true)) {
            List<Address> addressList = addressRepository.findByUserIdAndDeletedFalse(userId);

            addressList.stream()
                    .filter(another -> !another.getId().equals(id))
                    .findFirst()
                    .ifPresent(another -> another.setIsDefault(true));
        }

        address.setIsDeleted(true);
    }

    @Override
    public void setDefault(String userId, String id) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("userId cannot be null or blank");
        }

        if (id == null || id.isBlank()) {
            throw new ApplicationException("id cannot be null or blank");
        }

        Address address = getOwnedAddress(userId, id);

        addressRepository.clearDefaultByUserId(userId);

        address.setIsDefault(true);
    }

    // Hàm private
    private void clearDefaultSAddress(String userId) {
        List<Address> addressList = addressRepository.findByUserIdAndDeletedFalse(userId);

        addressList.forEach(address -> {
            address.setIsDefault(false);
        })
    }

    private Address getOwnedAddress(String userId, String id) {
        Address address = addressRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApplicationException("Address not found"));

        if (!address.getUserId().equals(userId)) {
            throw new ApplicationException("Wrong owner id");
        }

        return address;
    }
}
