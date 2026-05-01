package org.example.promotion_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.promotion_service.client.BookClient;
import org.example.promotion_service.dto.request.AssignBooksRequest;
import org.example.promotion_service.dto.request.CreatePromotionRequest;
import org.example.promotion_service.dto.request.UpdatePromotionRequest;
import org.example.promotion_service.dto.response.PromotionResponse;
import org.example.promotion_service.entity.Promotion;
import org.example.promotion_service.entity.PromotionBook;
import org.example.promotion_service.entity.PromotionStatus;
import org.example.promotion_service.exception.ApplicationException;
import org.example.promotion_service.mapper.PromotionMapper;
import org.example.promotion_service.repository.PromotionBookRepository;
import org.example.promotion_service.repository.PromotionRepository;
import org.example.promotion_service.service.AdminPromotionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPromotionServiceImpl implements AdminPromotionService {
    private static final String SCOPE_BOOK = "BOOK";
    private static final String SCOPE_CATEGORY = "CATEGORY";
    private static final Set<String> SUPPORTED_SCOPES = Set.of(SCOPE_BOOK, SCOPE_CATEGORY);

    private final PromotionMapper promotionMapper;
    private final PromotionBookRepository promotionBookRepository;
    private final PromotionRepository promotionRepository;
    private final BookClient bookClient;

    @Override
    public PromotionResponse create(CreatePromotionRequest createPromotionRequest) {
        if (createPromotionRequest == null) {
            throw new ApplicationException("Invalid request");
        }

        if (createPromotionRequest.getStartTime().isAfter(createPromotionRequest.getEndTime())) {
            throw new ApplicationException("Start time must be before end time");
        }

        createPromotionRequest.setScope(normalizeAndValidateScope(createPromotionRequest.getScope()));

        Promotion promotion = promotionMapper.toPromotion(createPromotionRequest);

        Promotion saved = promotionRepository.save(promotion);

        return promotionMapper.toPromotionResponse(saved);
    }

    @Override
    public PromotionResponse update(UpdatePromotionRequest updatePromotionRequest, String id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Promotion with id " + id + " not found"));

        // Validate ngày
        if (updatePromotionRequest.getStartTime() != null
            && updatePromotionRequest.getEndTime() != null
            && !updatePromotionRequest.getEndTime().isAfter(updatePromotionRequest.getStartTime())) {
            throw new ApplicationException("Start time must be before end time");
        }

        if (updatePromotionRequest.getScope() != null && !updatePromotionRequest.getScope().isBlank()) {
            updatePromotionRequest.setScope(normalizeAndValidateScope(updatePromotionRequest.getScope()));
        }
        promotionMapper.updatePromotion(promotion, updatePromotionRequest);

        Promotion saved = promotionRepository.save(promotion);

        return promotionMapper.toPromotionResponse(saved);
    }

    @Override
    public Void delete(String id) {
        if (id == null || id.isBlank()) {
            throw new ApplicationException("Invalid request");
        }

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Promotion not found"));

        promotion.setDeleted(true);

        promotionRepository.save(promotion);
        return null;
    }

    @Override
    public List<PromotionResponse> getAll() {
        return promotionRepository.findAll()
                .stream()
                //.map(p -> promotionMapper.toPromotionResponse(p))
                .map(promotionMapper::toPromotionResponse)
                .toList();
    }

    @Override
    public Void assignBooks(String promotionId, AssignBooksRequest assignBooksRequest) {

        // Validate input
        if (promotionId == null || promotionId.isBlank()) {
            throw new ApplicationException("Invalid request");
        }

        if (assignBooksRequest == null) {
            throw new ApplicationException("Invalid request");
        }

        // Kiểm tra promotion
        Promotion promotion =  promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ApplicationException("Promotion not found"));

        if (!SCOPE_BOOK.equalsIgnoreCase(promotion.getScope())) {
            throw new ApplicationException("This promotion scope does not allow book assignment");
        }

        // Kiểm tra duplicate input
        Set<String> inputBookIds = new HashSet<>(assignBooksRequest.getBookIds());

        // Gửi bookIds sang Book Service kiểm tra
        Set<String> existedBookIds = bookClient.checkIfBooksExist(assignBooksRequest.getBookIds());

        // Validate book tồn tại
        if (existedBookIds.size() != inputBookIds.size()) {
            inputBookIds.removeAll(existedBookIds); // -> còn lại là invalid
            throw new ApplicationException("Books not found: " + inputBookIds);
        }

        // Lấy những mapping đã có
        List<String> existedPromotionBookIdsList =
                promotionBookRepository.findByPromotionIdAndBookIdIn(promotionId,new ArrayList<>(inputBookIds))
                        .stream()
                        .map(PromotionBook::getBookId)
                        .toList();

        Set<String> existedPromotionBookIds = new HashSet<>(existedPromotionBookIdsList);

        // Filter những id cần insert
        inputBookIds.removeAll(existedPromotionBookIds);

        if (inputBookIds.isEmpty()) {
            return null;
        }
        // Vừa tạo promotion-book vừa check tồn tại
        List<PromotionBook> newPromotionBooks = new ArrayList<>();

        for (String bookId : inputBookIds) {
            PromotionBook promotionBook = new PromotionBook();
            promotionBook.setBookId(bookId);
            promotionBook.setPromotionId(promotionId);

            newPromotionBooks.add(promotionBook);
        }

        // Lưu
        promotionBookRepository.saveAll(newPromotionBooks);

        return null;
    }

    @Override
    public Void changeStatus(String promotionId, String status) {

        if (promotionId == null || promotionId.isBlank() || status == null || status.isBlank()) {
            throw new ApplicationException("Invalid request");
        }

        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ApplicationException("Promotion not found"));

        PromotionStatus newStatus;
        try {
            newStatus = PromotionStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new ApplicationException("Invalid status: " + status);
        }

        // Nếu status giống nhau thì không cần update
        if (promotion.getStatus() == newStatus) {
            return null;
        }

        promotion.setStatus(newStatus);
        promotionRepository.save(promotion);

        return null;
    }

    private String normalizeAndValidateScope(String scope) {
        if (scope == null || scope.isBlank()) {
            throw new ApplicationException("Scope is required");
        }

        String normalizedScope = scope.trim().toUpperCase(Locale.ROOT);
        if (!SUPPORTED_SCOPES.contains(normalizedScope)) {
            throw new ApplicationException("Invalid scope. Supported scopes: " + SUPPORTED_SCOPES);
        }

        return normalizedScope;
    }
}
