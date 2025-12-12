package com.example.Biblioteka_ZTPAI.dto.checkout;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationUserBookDTO {
    private Integer id;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
    private LocalDateTime endDate;
    private String username;
    private String bookTitle;
}
