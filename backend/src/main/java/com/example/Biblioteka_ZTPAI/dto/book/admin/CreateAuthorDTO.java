package com.example.Biblioteka_ZTPAI.dto.book.admin;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuthorDTO {

    @NotBlank
    private String name;

    private String surname;
}
