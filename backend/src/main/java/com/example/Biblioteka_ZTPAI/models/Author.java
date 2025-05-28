package com.example.Biblioteka_ZTPAI.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_author;

    @Column(nullable = false)
    private String name;

    private String surname;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;
}