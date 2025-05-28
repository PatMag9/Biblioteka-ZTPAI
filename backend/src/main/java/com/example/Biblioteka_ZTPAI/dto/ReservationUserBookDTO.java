package com.example.Biblioteka_ZTPAI.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationUserBookDTO {
    private Integer idReservation;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String username;
    private String bookTitle;

    public ReservationUserBookDTO(Integer idReservation, LocalDateTime startDate, LocalDateTime endDate,
                                  String username, String bookTitle) {
        this.idReservation = idReservation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.username = username;
        this.bookTitle = bookTitle;
    }

}
