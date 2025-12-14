package com.example.Biblioteka_ZTPAI.dto.book.admin;

import com.example.Biblioteka_ZTPAI.dto.book.BookAuthorDTO;
import com.example.Biblioteka_ZTPAI.dto.book.BookCopyDTO;
import com.example.Biblioteka_ZTPAI.dto.book.GenreDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminBookDTO {
    private Integer id;
    private String title;
    private GenreDTO genre;
    private String cover;
    private String description;
    private List<BookAuthorDTO> authors;
    private List<BookCopyDTO> copies;
}
