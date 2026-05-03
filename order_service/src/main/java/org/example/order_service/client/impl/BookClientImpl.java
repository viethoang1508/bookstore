package org.example.order_service.client.impl;

import org.example.order_service.client.BookClient;
import org.example.order_service.dto.response.BaseResponse;
import org.example.order_service.dto.response.BookDTO;
import org.example.order_service.repository.OrderItemRepository;
import org.example.order_service.security.SecurityUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Set;

public class BookClientImpl implements BookClient {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Set<BookDTO> getBooksInfo(List<String> bookIds) {
        String token = SecurityUtils.getCurrentToken();

        BaseResponse<Set<BookDTO>> response =
                webClientBuilder.build()
                        .post()
                        .uri("http://api-gateway:8282/internal/books/existing")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(List<String> bookIds)
                        .retrieve()
                        .bodyToMono(
                                new ParameterizedTypeReference<BaseResponse<Set<BookDTO>>>() {
                                }
                        )
                        .block();

        if (response == null || response.getData() == null) {
            throw new RuntimeException("response is null");
        }
        return response.getData();
    }
}
