package org.example.book_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSummaryDTO {
    private String id;
    private String title;
    private String slug;
    private BigDecimal price;
    private String thumbnailUrl;
}
