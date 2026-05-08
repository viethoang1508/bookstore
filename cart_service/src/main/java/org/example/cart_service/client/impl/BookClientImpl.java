package org.example.cart_service.client.impl;

import lombok.RequiredArgsConstructor;
import org.example.cart_service.client.BookClient;
import org.example.cart_service.dto.response.BaseResponse;
import org.example.cart_service.dto.response.BookResponseDTO;
import org.example.cart_service.exception.ApplicationException;
import org.example.cart_service.security.SecurityUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class BookClientImpl implements BookClient {

    private final WebClient.Builder webClientBuilder;

    @Override
    public BookResponseDTO getBook(String bookId) {
        String token = SecurityUtils.getCurrentToken();
        BaseResponse<BookResponseDTO> response =
                webClientBuilder.build()
                        .post()
                        .uri("http://api-gateway:8282/internal/books/{bookId}", bookId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(bookId)
                        .retrieve()
                        .bodyToMono(
                                new ParameterizedTypeReference<BaseResponse<BookResponseDTO>>() {}
                        )
                        .block();
        if (response == null ||  response.getData() == null) {
            throw new ApplicationException("Cannot get book with id " + bookId);
        }
        return response.getData();
    }
}
