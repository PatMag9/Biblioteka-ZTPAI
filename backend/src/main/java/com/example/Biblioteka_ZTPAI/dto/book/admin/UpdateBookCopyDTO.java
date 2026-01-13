package com.example.Biblioteka_ZTPAI.dto.book.admin;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookCopyDTO {
    @Size(max = 17, message = "Code cannot exceed 17 characters")
    private String code;
    private Integer publisherId;
    private Integer yearPublished;
}
