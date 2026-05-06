package org.example.promotion_service.client.impl;

import lombok.RequiredArgsConstructor;
import org.example.promotion_service.client.BookClient;
import org.example.promotion_service.dto.response.BaseResponse;
import org.example.promotion_service.security.SecurityUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class BookClientImpl implements BookClient {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Set<String> checkIfBooksExist(List<String> bookIds) {
        String token = SecurityUtils.getCurrentToken();

        BaseResponse<Set<String>> response =
                webClientBuilder.build()
                        .post()
                        .uri("http://api-gateway:8282/internal/books/existing")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(bookIds)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<BaseResponse<Set<String>>>() {})
                        .block();

        if (response == null || response.getData() == null) {
            throw new RuntimeException("response is null");
        }
        return response.getData();
    }
}
