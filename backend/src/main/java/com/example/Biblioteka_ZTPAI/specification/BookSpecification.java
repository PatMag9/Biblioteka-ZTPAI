package com.example.Biblioteka_ZTPAI.specification;

import com.example.Biblioteka_ZTPAI.models.Book;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> filterBooks(String search, String genre, String author) {
        return Specification.where(titleContains(search))
                .and(genreEquals(genre))
                .and(authorEquals(author));
    }

    private static Specification<Book> titleContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%");
        };
    }

    private static Specification<Book> genreEquals(String genre) {
        return (root, query, cb) -> {
            if (genre == null || genre.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.join("genre").get("genre_name")), genre.toLowerCase());
        };
    }

    private static Specification<Book> authorEquals(String author) {
        return (root, query, cb) -> {
            if (author == null || author.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.join("authors", JoinType.LEFT).get("surname")),
                    "%" + author.toLowerCase() + "%"
            );
        };
    }
}