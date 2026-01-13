package com.example.Biblioteka_ZTPAI.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationDTO {
    private Integer userId;
    private Integer bookCopyId;
    private LocalDateTime startDate;
}