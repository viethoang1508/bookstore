package org.example.user_service.dto.response;

import lombok.Data;

@Data
public class AddressResponse {
    private String id;

    private String receiverName;
    private String phone;

    private String province;
    private String district;
    private String ward;
    private String detailAddress;

    private Boolean isDefault;
}
