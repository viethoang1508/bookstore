package org.example.promotion_service.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AssignBooksRequest {
    private List<String> bookIds;
}
