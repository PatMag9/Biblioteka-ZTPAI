package com.example.Biblioteka_ZTPAI.dto.book.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateBookCopyDTO {
    @NotBlank(message = "Code cannot be null")
    @Size(max = 17, message = "Code cannot exceed 17 characters")
    private String code;
    @NotNull(message = "Publisher is required")
    private Integer publisherId;
    @NotNull(message = "Year published is required")
    private Integer yearPublished;
}
