package com.example.Biblioteka_ZTPAI.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanUserBookDTO {
    private Integer idLoan;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime endDate;
    private String username;
    private String bookTitle;

    public LoanUserBookDTO(Integer idLoan, LocalDateTime startDate, LocalDateTime dueDate, LocalDateTime endDate,
                           String username, String bookTitle) {
        this.idLoan = idLoan;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.endDate = endDate;
        this.username = username;
        this.bookTitle = bookTitle;
    }
}
