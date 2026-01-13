package com.example.Biblioteka_ZTPAI.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLoanDTO {
        private Integer userId;
        private Integer bookCopyId;
        private LocalDateTime startDate;
        private LocalDateTime dueDate;
        private Boolean canExtend;
}
