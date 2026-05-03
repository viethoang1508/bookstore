package org.example.user_service.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAddressRequest {
    @Size(max = 255)
    private String receiverName;

    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "Phone number is invalid"
    )
    private String phone;

    private String province;
    private String district;
    private String ward;
    private String detailAddress;

    private Boolean isDefault;
}
