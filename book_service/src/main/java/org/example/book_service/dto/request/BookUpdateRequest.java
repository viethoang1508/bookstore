package org.example.book_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookUpdateRequest {
    private String isbn;
    private String title;
    private String slug;
    private String description;
    private String authorName;
    private String publisherName;
    private Integer publishYear;
    private BigDecimal price;
    private Integer stock;
    private String thumbnailUrl;
}
