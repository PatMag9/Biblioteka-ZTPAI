package com.example.Biblioteka_ZTPAI.dto.book.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookDTO {
    @NotBlank(message = "Title cannot be null or empty")
    @Size(max = 255)
    private String title;

    @NotNull(message = "Genre is required")
    private Integer genreId;

    @NotBlank(message = "Description cannot be null or empty")
    private String description;

    @NotNull(message = "At least one author is required")
    private List<Integer> authorIds;
}
