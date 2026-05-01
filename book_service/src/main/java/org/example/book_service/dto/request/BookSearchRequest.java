package org.example.book_service.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookSearchRequest extends PaginationRequest {
    private String keyword;
    private String categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
