package com.example.Biblioteka_ZTPAI.mappers;

import com.example.Biblioteka_ZTPAI.dto.book.BookCopyDTO;
import com.example.Biblioteka_ZTPAI.models.BookCopy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { PublisherMapper.class })
public interface BookCopyMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "publisher", target = "publisher")
    @Mapping(source = "yearPublished", target = "yearPublished")
    BookCopyDTO toDTO(BookCopy copy);

    List<BookCopyDTO> toDTO(List<BookCopy> copies);
}