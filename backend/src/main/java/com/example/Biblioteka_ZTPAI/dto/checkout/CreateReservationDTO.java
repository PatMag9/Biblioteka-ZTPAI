package com.example.Biblioteka_ZTPAI.dto.checkout;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateReservationDTO {
    private Integer userId;
    private Integer bookCopyId;
    private LocalDateTime startDate;
}