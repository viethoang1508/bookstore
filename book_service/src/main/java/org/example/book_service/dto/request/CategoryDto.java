package org.example.book_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDto {
    @NotBlank(message = "Category name is required")
    private String name;
    private String slug;
}
