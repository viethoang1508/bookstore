package org.example.book_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookCreateRequest {
    @NotBlank(message = "ISBN is required")
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be URL-friendly (lowercase, numbers, hyphen)")
    private String slug;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotBlank(message = "Author name is required")
    @Size(max = 255, message = "Author name must not exceed 255 characters")
    private String authorName;

    @NotBlank(message = "Publisher name is required")
    @Size(max = 255, message = "Publisher name must not exceed 255 characters")
    private String publisherName;

    @NotNull(message = "Publish year is required")
    @Min(value = 0, message = "Publish year must be a positive number")
    private Integer publishYear;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Positive(message = "Stock must be greater than 0")
    private Integer stock;

    @Size(max = 500, message = "Thumbnail URL must not exceed 512 characters")
    private String thumbnailUrl;
}
