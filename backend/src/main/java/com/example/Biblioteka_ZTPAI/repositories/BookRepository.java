package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.models.Author;
import com.example.Biblioteka_ZTPAI.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("SELECT b.authors FROM Book b WHERE b.idBook = :bookId")
    Set<Author> findAuthorsByBookId(Integer bookId);
}
