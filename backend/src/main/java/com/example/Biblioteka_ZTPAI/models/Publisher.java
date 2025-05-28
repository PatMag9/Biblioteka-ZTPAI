package com.example.Biblioteka_ZTPAI.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "publishers")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_publishers;

    private String publishers_name;

    @OneToMany(mappedBy = "publisher")
    @JsonManagedReference
    private Set<BookCopy> bookCopies;
}
