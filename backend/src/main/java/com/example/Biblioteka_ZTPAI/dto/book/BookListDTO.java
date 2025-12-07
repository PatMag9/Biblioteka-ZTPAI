package com.example.Biblioteka_ZTPAI.dto.book;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookListDTO {
    private Integer id;
    private String title;
    private String genre;
    private String cover;
    private List<String> authors;
    private Double averageRating;
    private Integer reviewCount;
}
