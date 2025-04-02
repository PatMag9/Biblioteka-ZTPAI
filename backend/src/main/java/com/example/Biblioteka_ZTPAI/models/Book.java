package com.example.Biblioteka_ZTPAI.models;

import jakarta.persistence.*;

@Entity
@Table(name="books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id_book;

    private String title;
    private Integer id_genre;
    private String cover;
    @Column(columnDefinition = "TEXT")
    private String description;

    public Book() {
    }
    public Book(Integer id_book, String title, Integer id_genre, String cover, String description) {
        this.id_book = id_book;
        this.title = title;
        this.id_genre = id_genre;
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

    public Integer getId_genre() {
        return id_genre;
    }

    public void setId_genre(Integer id_genre) {
        this.id_genre = id_genre;
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
