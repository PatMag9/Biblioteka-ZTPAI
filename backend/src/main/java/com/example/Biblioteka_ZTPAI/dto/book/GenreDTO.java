package com.example.Biblioteka_ZTPAI.dto.book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreDTO {
    private Integer id;
    private String name;
}
