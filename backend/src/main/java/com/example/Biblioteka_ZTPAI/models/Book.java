package com.example.Biblioteka_ZTPAI.models;

import jakarta.persistence.*;

@Entity
@Table(name="books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id_book;

    private String title;
    @ManyToOne
    @JoinColumn(name = "id_genre", referencedColumnName = "id_genre")
    private Genre genre;
    private String cover;
    @Column(columnDefinition = "TEXT")
    private String description;

    public Book() {
    }
    public Book(Integer id_book, String title, Genre genre, String cover, String description) {
        this.id_book = id_book;
        this.title = title;
        this.genre = genre;
        this.cover = cover;
        this.description = description;
    }

    public Integer getId_book() {
        return id_book;
    }

    public void setId_book(Integer id_book) {
        this.id_book = id_book;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
