package com.example.Biblioteka_ZTPAI.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_loan;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_book_copy", nullable = false)
    @JsonManagedReference
    private BookCopy bookCopy;

    private LocalDateTime start_date;
    private LocalDateTime due_date;
    @Column(name = "end_date")
    private LocalDateTime endDate;
}