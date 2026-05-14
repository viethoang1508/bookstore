package org.example.book_service.mapper;

import org.example.book_service.dto.response.InternalBookDTO;
import org.example.book_service.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "bookId", source = "id")
    @Mapping(target = "bookName", source = "title")
    @Mapping(target = "image", source = "thumbnailUrl")
    InternalBookDTO toInternalBookDTO(Book book);
}
