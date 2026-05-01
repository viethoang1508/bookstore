package org.example.promotion_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssignBooksRequest {
    @NotEmpty(message = "Book IDs must not be empty")
    private List<String> bookIds;
}