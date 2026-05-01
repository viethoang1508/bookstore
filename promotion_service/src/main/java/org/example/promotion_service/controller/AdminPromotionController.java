package org.example.promotion_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.promotion_service.dto.BaseResponse;
import org.example.promotion_service.dto.request.AssignBooksRequest;
import org.example.promotion_service.dto.request.CreatePromotionRequest;
import org.example.promotion_service.dto.request.UpdatePromotionRequest;
import org.example.promotion_service.dto.response.PromotionResponse;
import org.example.promotion_service.service.AdminPromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {

    private final AdminPromotionService adminPromotionService;

    // Tạo promotion
    @PostMapping
    public ResponseEntity<BaseResponse<PromotionResponse>> create (@Valid @RequestBody CreatePromotionRequest request){
        return ResponseEntity.ok(new BaseResponse<>(adminPromotionService.create(request), "Create promotion successfully"));
    }

    // Update promotion
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PromotionResponse>> update(@PathVariable String id,
                                                                  @RequestBody UpdatePromotionRequest request){
        return ResponseEntity.ok(new BaseResponse<>(adminPromotionService.update(request, id), "Update promotion successfully"));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>>  delete(@PathVariable String id){
        return ResponseEntity.ok(new BaseResponse<>(adminPromotionService.delete(id), "Delete promotion successfully"));
    }

    // Lấy danh sách
    @GetMapping
    public ResponseEntity<BaseResponse<List<PromotionResponse>>>  getAll(){
        return ResponseEntity.ok(new BaseResponse<>(adminPromotionService.getAll(), "Get all of the promotions successfully"));
    }

    // Assign books
    @PostMapping("/{id}/books")
    public ResponseEntity<BaseResponse<Void>> assignBooks(@PathVariable String id,
                                                          @RequestBody AssignBooksRequest request)
    {
        return ResponseEntity.ok(new BaseResponse<>(adminPromotionService.assignBooks(id, request), "Assign books successfully"));
    }

    // Change status
    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<Void>> changeStatus(@PathVariable String id,
                                                           @RequestParam String status){
        return ResponseEntity.ok(new BaseResponse<>(adminPromotionService.changeStatus(id, status), "Change status successfully"));
    }

}
