package org.example.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.user_service.dto.request.CreateAddressRequest;
import org.example.user_service.dto.request.UpdateAddressRequest;
import org.example.user_service.dto.response.AddressResponse;
import org.example.user_service.dto.response.BaseResponse;
import org.example.user_service.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<AddressResponse>>> getMyAddresses(
            JwtAuthenticationToken token
    ) {
        String userId = token.getName();
        return ResponseEntity.ok(new BaseResponse<>(addressService.getByUser(userId), "Get address successfully"));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<AddressResponse>> createAddress(
            JwtAuthenticationToken token,
            @RequestBody @Valid CreateAddressRequest request
    ) {
        String userId = token.getName();
        return ResponseEntity.ok(new BaseResponse<>(addressService.create(userId, request), "Create address successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<AddressResponse>> updateAddress(
            JwtAuthenticationToken token,
            @PathVariable String id,
            @RequestBody @Valid UpdateAddressRequest request
    ) {
        String userId = token.getName();
        return ResponseEntity.ok(new BaseResponse<>(addressService.updateAddress(userId, id, request), "Update address successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteAddress(
            JwtAuthenticationToken token,
            @PathVariable String id
    ) {
        String userId = token.getName();
        return ResponseEntity.ok(new BaseResponse<>(addressService.delete(userId, id), "Delete address successfully"));
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<BaseResponse<Void>> setDefaultAddress(
            JwtAuthenticationToken token,
            @PathVariable String id
    ) {
        String userId = token.getName();
        return ResponseEntity.ok(new BaseResponse<>(addressService.setDefault(userId, id);, "Set default successfully"));
    }
}

