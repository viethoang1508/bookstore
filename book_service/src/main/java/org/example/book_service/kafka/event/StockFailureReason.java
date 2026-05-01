package org.example.book_service.kafka.event;

public enum StockFailureReason {
    BOOK_NOT_FOUND,
    NOT_ENOUGH_STOCK,
    LOCK_FAILED,
    UNKNOWN
}
