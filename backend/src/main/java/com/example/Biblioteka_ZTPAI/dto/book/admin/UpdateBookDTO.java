package com.example.Biblioteka_ZTPAI.dto.book.admin;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateBookDTO {
    private String title;
    private Integer genreId;
    private String description;
    private List<Integer> authorIds;
}
