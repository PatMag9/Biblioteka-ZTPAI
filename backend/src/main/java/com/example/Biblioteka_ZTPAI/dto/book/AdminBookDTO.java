package com.example.Biblioteka_ZTPAI.dto.book;

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
