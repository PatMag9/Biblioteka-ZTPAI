package com.example.Biblioteka_ZTPAI.dto.book.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateGenreDTO {
    @NotBlank
    private String name;
}
