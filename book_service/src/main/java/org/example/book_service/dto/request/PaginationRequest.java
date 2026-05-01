package org.example.book_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

@Data
public class PaginationRequest {
    @Min(value = 0, message = "Page must be greater than or equal to 0")
    private int page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size must not exceed 100")
    private int size = 10;

    @Size(max = 100, message = "Sort field must not exceed 100 characters")
    private String sort = "createdAt";

    @Pattern(regexp = "^(?i)(asc|desc)$", message = "Direction must be either 'asc' or 'desc'")
    private String direction = "desc";

    public Pageable toPageable() {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        String safeSort = StringUtils.hasText(sort) ? sort : "createdAt";
        Sort.Direction safeDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(safePage, safeSize, Sort.by(safeDirection, safeSort));
    }
}
