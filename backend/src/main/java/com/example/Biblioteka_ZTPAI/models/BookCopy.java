package com.example.Biblioteka_ZTPAI.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_copies")
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_book_copy;

    @ManyToOne
    @JoinColumn(name = "id_book")
    @JsonManagedReference
    private Book book;

    @Column(name = "isbn", length = 17)
    private String isbn;

    @ManyToOne
    @JoinColumn(name = "id_publisher")
    @JsonBackReference
    private Publisher publisher;

    private Integer year_published;

    @OneToMany(mappedBy = "bookCopy")
    @JsonBackReference
    private Set<Reservation> reservations;

    @OneToMany(mappedBy = "bookCopy")
    @JsonBackReference
    private Set<Loan> loans;
}
