package org.example.book_service.dto.request;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

@Data
public class PaginationRequest {
    private int page = 0;
    private int size = 10;
    private String sort = "createdAt";
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
