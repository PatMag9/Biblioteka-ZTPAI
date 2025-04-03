package com.example.Biblioteka_ZTPAI.models;

import jakarta.persistence.*;

@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_genre;
    private String genre_name;
    public Genre() {
    }

    public Genre(Integer id_genre, String genre_name) {
        this.id_genre = id_genre;
        this.genre_name = genre_name;
    }

    public Integer getId_genre() {
        return id_genre;
    }

    public void setId_genre(Integer id_genre) {
        this.id_genre = id_genre;
    }

    public String getGenre_name() {
        return genre_name;
    }

    public void setGenre_name(String genre_name) {
        this.genre_name = genre_name;
    }
}
