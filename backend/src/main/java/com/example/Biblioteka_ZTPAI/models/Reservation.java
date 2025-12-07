package com.example.Biblioteka_ZTPAI.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_copy_id", nullable = false)
    @JsonManagedReference
    private BookCopy bookCopy;

    @Column(name = "start_date")
    private LocalDateTime start_date;
    @Column(name = "end_date")
    private LocalDateTime endDate;
}
