package com.example.Biblioteka_ZTPAI.dto.book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookAuthorDTO {
    private Integer id;
    private String name;
    private String surname;
}
