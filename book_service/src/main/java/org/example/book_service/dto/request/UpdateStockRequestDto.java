package org.example.book_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStockRequestDto {

    /** Dương: nhập thêm; âm: trừ tồn (ví dụ sau khi đặt hàng). */
    @NotNull(message = "Delta is required")
    private Integer delta;
}
