package com.example.Biblioteka_ZTPAI.mappers;

import com.example.Biblioteka_ZTPAI.dto.book.BookDTO;
import com.example.Biblioteka_ZTPAI.dto.book.BookListDTO;
import com.example.Biblioteka_ZTPAI.models.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        AuthorMapper.class,
        GenreMapper.class,
        PublisherMapper.class,
        BookCopyMapper.class
})
public interface BookMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "genre.name", target = "genre")
    @Mapping(target = "authors", expression = "java(book.getAuthors().stream().map(a -> a.getName() + \" \" + (a.getSurname() != null ? a.getSurname() : \"\")).toList())")
    BookListDTO toListDTO(Book book);

    List<BookListDTO> toListDTO(List<Book> books);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "genre", target = "genre")
    @Mapping(source = "authors", target = "authors")
    @Mapping(source = "bookCopies", target = "copies")
    BookDTO toDTO(Book book);
}
