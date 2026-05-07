package org.example.promotion_service.mapper;

import org.example.promotion_service.dto.request.CreatePromotionRequest;
import org.example.promotion_service.dto.request.UpdatePromotionRequest;
import org.example.promotion_service.dto.response.PromotionResponse;
import org.example.promotion_service.entity.Promotion;
import org.example.promotion_service.entity.PromotionStatus;
import org.example.promotion_service.entity.PromotionType;
import org.mapstruct.*;

@Mapper( componentModel = "spring")
public interface PromotionMapper {

    // create
    @Mapping(target = "type", expression = "java(mapType(request.getType()))")
//    @Mapping(target = "status", expression = "java(PromotionStatus.ACTIVE)")
    @Mapping(target = "status", constant = "ACTIVE")

    Promotion toPromotion(CreatePromotionRequest request);

    PromotionResponse toPromotionResponse(Promotion promotion);

    // Update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "type", expression = "java(mapType(request.getType()))")
    @Mapping(target = "status", expression = "java(mapStatus(request.getStatus()))")
    void updatePromotion(@MappingTarget Promotion promotion, UpdatePromotionRequest request);


    default PromotionType mapType(String type) {
        try {
            return PromotionType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid promotion type");
        }
    }

    default PromotionStatus mapStatus(String status) {
        if (status == null) return null;
        try {
            return PromotionStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid promotion status");
        }
    }
}
