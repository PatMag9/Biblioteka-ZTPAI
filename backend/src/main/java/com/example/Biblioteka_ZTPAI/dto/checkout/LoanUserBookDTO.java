package com.example.Biblioteka_ZTPAI.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
public class LoanUserBookDTO {
    private Integer id;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime endDate;
    private String username;
    private String bookTitle;
}
