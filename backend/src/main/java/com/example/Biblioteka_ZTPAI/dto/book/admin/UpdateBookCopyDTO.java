package com.example.Biblioteka_ZTPAI.dto.book.admin;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateBookCopyDTO {
    @Size(max = 17, message = "ISBN cannot exceed 17 characters")
    private String isbn;
    private Integer publisherId;
    private Integer yearPublished;
}
