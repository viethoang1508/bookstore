package org.example.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public List<AddressResponse> getByUser(String userId) {
        log.info("Fetching addresses by user, userId={}", userId);
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("userId cannot be null or blank");
        }

        List<Address> addressList = addressRepository.findByUserIdAndDeletedFalse(userId);

        List<AddressResponse> responseList = addressList.stream()
                .map(addressMapper::toResponse)
                .toList();

        log.info("Fetched addresses successfully, userId={}, count={}", userId, responseList.size());
        return responseList;
    }

    @Override
    public AddressResponse create(String userId, CreateAddressRequest request) {
        log.info("Creating address, userId={}", userId);
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("userId cannot be null or blank");
        }

        if (request == null) {
            throw new ApplicationException("request cannot be null");
        }

        Address address = new Address();

        // Nếu là default -> clear default cũ
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultSAddress(userId);
            address.setIsDefault(true);
        } else {
            //Nếu user chưa có default address -> set default cho address này luôn
            boolean isDefault = addressRepository
                    .findByUserIdAndIsDefaultTrueAndDeletedFalse(userId)
                    .size() > 0;
            if (!isDefault) {
                address.setIsDefault(true);
            }
        }

        Address createdAddress = addressMapper.toEntity(request);
        createdAddress.setUserId(userId);
        createdAddress.setIsDefault(address.getIsDefault());

        AddressResponse response = addressMapper.toResponse(addressRepository.save(createdAddress));

        log.info("Address created successfully, userId={}", userId);

        return response;
    }

    @Override
    public AddressResponse updateAddress(String userId, String id, UpdateAddressRequest request) {
        log.info("Updating address, userId={}, addressId={}", userId, id);
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
        if(Boolean.TRUE.equals(address.getIsDefault())) {
            clearDefaultSAddress(userId);
            address.setIsDefault(true);
        }

        addressMapper.updateAddress(request, address);

        AddressResponse response = addressMapper.toResponse(addressRepository.save(address));

        log.info("Address updated successfully, userId={}, addressId={}", userId, id);

        return response;
    }

    @Override
    public Void delete(String userId, String id) {
        log.info("Deleting address, userId={}, addressId={}", userId, id);

        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("userId cannot be null or blank");
        }

        if (id == null || id.isBlank()) {
            throw new ApplicationException("id cannot be null or blank");
        }

        Address address = getOwnedAddress(userId, id);
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<Address> addressList = addressRepository.findByUserIdAndDeletedFalse(userId);

            addressList.stream()
                    .filter(another -> !another.getId().equals(id))
                    .findFirst()
                    .ifPresent(another -> another.setIsDefault(true));
        }

        address.setIsDeleted(true);

        addressRepository.save(address);

        log.info("Address marked deleted successfully, userId={}, addressId={}", userId, id);

        return  null;
    }

    @Override
    public Void setDefault(String userId, String id) {
        log.info("Setting default address, userId={}, addressId={}", userId, id);
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("userId cannot be null or blank");
        }

        if (id == null || id.isBlank()) {
            throw new ApplicationException("id cannot be null or blank");
        }

        Address address = getOwnedAddress(userId, id);

        addressRepository.clearDefaultByUserId(userId);

        address.setIsDefault(true);

        addressRepository.save(address);

        log.info("Set default address successfully, userId={}, addressId={}", userId, id);

        return null;
    }

    // Hàm private
    private void clearDefaultSAddress(String userId) {
        List<Address> addressList = addressRepository.findByUserIdAndDeletedFalse(userId);

        addressList.forEach(address -> {
            address.setIsDefault(false);
        });
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
