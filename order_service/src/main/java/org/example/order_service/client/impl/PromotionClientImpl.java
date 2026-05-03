package org.example.order_service.client.impl;

import org.example.order_service.client.PromotionClient;
import org.example.order_service.dto.request.ApplyPromotionRequest;
import org.example.order_service.dto.response.ApplyPromotionResponse;
import org.example.order_service.dto.response.BaseResponse;
import org.example.order_service.security.SecurityUtils;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class PromotionClientImpl implements PromotionClient {

    private final WebClient.Builder webClientBuilder;

    @Override
    public ApplyPromotionResponse applyPromotion(ApplyPromotionRequest request) {
        String token = SecurityUtils.getCurrentToken();
        BaseResponse<ApplyPromotionResponse> response =
                webClientBuilder.build()
                        .post()
                        .uri("http://api-gateway:8282/internal/promotions/apply")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(
                                new ParameterizedTypeReference<BaseResponse<ApplyPromotionResponse>(){}
                        )
                        .block();
        if (response == null || response.getData() == null) {
            throw new RuntimeException("Response is null");
        }
        return response.getData();
    }
}
