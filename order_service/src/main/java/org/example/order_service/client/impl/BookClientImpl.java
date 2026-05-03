package org.example.order_service.client.impl;

import lombok.RequiredArgsConstructor;
import org.example.order_service.client.BookClient;
import org.example.order_service.dto.response.BaseResponse;
import org.example.order_service.dto.response.BookDTO;
import org.example.order_service.repository.OrderItemRepository;
import org.example.order_service.security.SecurityUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookClientImpl implements BookClient {

    private final WebClient.Builder webClientBuilder;

    @Override
    public List<BookDTO> getBooksInfo(List<String> bookIds) {
        String token = SecurityUtils.getCurrentToken();

        BaseResponse<List<BookDTO>> response =
                webClientBuilder.build()
                        .post()
                        .uri("http://api-gateway:8282/internal/books/info")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(bookIds)
                        .retrieve()
                        .bodyToMono(
                                new ParameterizedTypeReference<BaseResponse<List<BookDTO>>>() {
                                })
                        .block();

        if (response == null || response.getData() == null) {
            throw new RuntimeException("Response is null");
        }

        return response.getData();
    }
}
