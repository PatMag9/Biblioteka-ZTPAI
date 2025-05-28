package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.models.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
    List<BookCopy> findAllByBook_IdBook(Integer bookId);
}
