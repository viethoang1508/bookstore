package org.example.book_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookSearchRequest extends PaginationRequest {
    @Size(max = 255, message = "Keyword must not exceed 255 characters")
    private String keyword;
    @Size(max = 50, message = "Category ID must not exceed 50 characters")
    private String categoryId;
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum price must be greater than or equal to 0")
    private BigDecimal minPrice;
    @DecimalMin(value = "0.0", inclusive = true, message = "Maximum price must be greater than or equal to 0")
    private BigDecimal maxPrice;
}