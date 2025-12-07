package com.example.Biblioteka_ZTPAI.mappers;

import com.example.Biblioteka_ZTPAI.dto.book.BookAuthorDTO;
import com.example.Biblioteka_ZTPAI.models.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(source = "id", target = "id")
    BookAuthorDTO toDTO(Author author);

    List<BookAuthorDTO> toDTO(List<Author> authors);
}
