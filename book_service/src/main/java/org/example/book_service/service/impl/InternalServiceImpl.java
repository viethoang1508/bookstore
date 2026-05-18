package org.example.book_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.book_service.dto.request.DeductStockRequest;
import org.example.book_service.dto.request.UpdateStockRequest;
import org.example.book_service.dto.response.InternalBookDTO;
import org.example.book_service.entity.Book;
import org.example.book_service.exception.ApplicationException;
import org.example.book_service.kafka.event.*;
import org.example.book_service.kafka.producer.StockEventProducer;
import org.example.book_service.mapper.BookMapper;
import org.example.book_service.repository.BookRepository;
import org.example.book_service.service.InternalService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternalServiceImpl implements InternalService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final RedissonClient redissonClient;
    private final StockEventProducer stockEventProducer;

    @Override
    @Transactional
    public void updateStock(String bookId, UpdateStockRequest request) {
        log.info("Updating stock, bookId={}, delta={}", bookId, request != null ? request.getDelta() : null);
        Book book = getBook(bookId);
        int delta = request.getDelta();
        int current = book.getStock() != null ? book.getStock() : 0;
        int next = current + delta;
        if (next < 0) {
            throw new ApplicationException("Insufficient stock");
        }
        book.setStock(next);
        bookRepository.save(book);
        log.info("Stock updated successfully, bookId={}, currentStock={}", bookId, next);
    }

    private Book getBook(String id) {
        if (id == null || id.isBlank()) {
            throw new ApplicationException("Book id is empty");
        }
        return bookRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Book not found"));
    }

    @Override
    public InternalBookDTO getBookForCart(String bookId) {
        log.info("Fetching internal book info for cart, bookId={}", bookId);

        Book book = getBook(bookId);

        InternalBookDTO bookDTO = bookMapper.toInternalBookDTO(book);

        return bookDTO;
    }

    @Override
    public Set<String> checkIfBooksExist(List<String> bookIds) {
        log.info("Checking existing books, requestedSize={}", bookIds != null ? bookIds.size() : 0);
        if (bookIds == null || bookIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(bookRepository.findExistingBookIds(bookIds));
    }

    @Override
    @Transactional
    public void deductBooks(OrderCreatedEvent orderCreatedEvent) {
        String orderId = orderCreatedEvent.getOrderId();
        List<DeductStockRequest> deductRequests = orderCreatedEvent.getDeductRequests();

        // validate
        if (orderId == null || deductRequests == null || deductRequests.isEmpty()) {
            deductFailed(orderId, StockFailureReason.UNKNOWN, null);
            return;
        }

        // Gộp quantity theo bookId (tránh trùng bookId)
        Map<String, Integer> qtyByBookId = new HashMap<>();
        for (DeductStockRequest request : deductRequests) {
            String bookId = request.getBookId();
            Integer quantity = request.getQuantity();

            if (bookId == null || quantity == null || quantity <= 0) {
                deductFailed(orderId, StockFailureReason.UNKNOWN, null);
                return;
            }

            if (qtyByBookId.containsKey(bookId)) {
                qtyByBookId.put(bookId, qtyByBookId.get(bookId) + quantity);
            } else  {
                qtyByBookId.put(bookId, quantity);
            }
        }

        List<String> productIds = new ArrayList<>(qtyByBookId.keySet());

        // Sort productIds tránh deadlocl
        productIds.sort(String::compareTo);

        // Tạo Distributed Lock (redis)
        String lockKey = "lock:products:" + String.join(",", productIds);
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;
        try {
            // Thử lấy lock trong 10s và giữ lock trong 5s
            acquired = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!acquired) {
                deductFailed(orderId, StockFailureReason.LOCK_FAILED, null);
                return;
            }

            // Load books
            List<Book> books = bookRepository.findAllByIdIn(productIds);

            Map<String, Book> bookMap = books.stream()
                    .collect(Collectors.toMap(Book::getId, book -> book));

            // Validate và trừ stock
            for (String bookId : productIds) {
                Book book = bookMap.get(bookId);
                if (book == null) {
                    deductFailed(orderId, StockFailureReason.BOOK_NOT_FOUND, null);
                    return;
                }

                int needQty = qtyByBookId.get(bookId);

                if (book.getStock() < needQty) {
                    StockFailureItem errorItem = new StockFailureItem();
                    errorItem.setBookId(bookId);
                    errorItem.setRequestedQty(needQty);
                    errorItem.setAvailableStock(book.getStock());

                    List<StockFailureItem> errorItems = new ArrayList<>();
                    errorItems.add(errorItem);

                    deductFailed(orderId, StockFailureReason.NOT_ENOUGH_STOCK, errorItems);
                    return;
                }

                book.setStock(book.getStock() - needQty);
            }

            // save
            bookRepository.saveAll(books);

            // event thành công
            deductSuccess(orderId, deductRequests);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            log.error("Lock interrupted, orderId={}", orderId, e);

            deductFailed(orderId, StockFailureReason.UNKNOWN, null);

            throw new RuntimeException(e);

        } finally {

            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info(
                        "Unlock success, orderId={}, lockKey={}",
                        orderId,
                        lockKey
                );
            }
        }
    }

    private void deductFailed(String orderId, StockFailureReason reason, List<StockFailureItem> failedItems) {
        OrderStockFailedEvent event = new OrderStockFailedEvent();

        event.setOrderId(orderId);
        event.setReason(reason.name());
        event.setFailedItems(failedItems);

        stockEventProducer.publishStockFailed(event);
    }

    private void deductSuccess(String orderId,
                               List<DeductStockRequest> items) {
        OrderStockDeductedEvent event = new OrderStockDeductedEvent();

        event.setItems(items);
        event.setOrderId(orderId);
        event.setStatus("SUCCESS");

        stockEventProducer.publishStockDeducted(event);
    }

    @Override
    public List<InternalBookDTO> getBooksInfo(List<String> bookIds) {
        log.info("Fetching internal books info, requestedSize={}", bookIds != null ? bookIds.size() : 0);
        List<Book> books = bookRepository.findAllByIdIn(bookIds);

        return books.stream()
                .map(bookMapper::toInternalBookDTO)
                .toList();
    }
}
