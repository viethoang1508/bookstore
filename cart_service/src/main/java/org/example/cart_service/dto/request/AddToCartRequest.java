package org.example.cart_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class AddToCartRequest {
    @NotBlank(message = "bookId không được để trống")
    private String bookId;

    @NotNull(message = "quantity không được để trống")
    @Positive(message = "quantity phải lớn hơn 0")
    private Integer quantity;

//    private String bookName;
//    private String image;
//    private Double price;
}
