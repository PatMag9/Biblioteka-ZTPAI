package com.example.Biblioteka_ZTPAI.dto.book.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookDTO {
    private String title;
    private Integer genreId;
    private String description;
    private List<Integer> authorIds;
}
