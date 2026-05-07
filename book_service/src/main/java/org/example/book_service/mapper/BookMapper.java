package org.example.book_service.mapper;

import org.example.book_service.dto.response.InternalBookDTO;
import org.example.book_service.entity.Book;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface BookMapper {
    InternalBookDTO toInternalBookDTO(Book book);
}
