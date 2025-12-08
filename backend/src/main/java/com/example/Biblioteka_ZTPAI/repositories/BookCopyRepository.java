package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.models.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
    @Query("SELECT bc FROM BookCopy bc LEFT JOIN FETCH bc.publisher WHERE bc.book.id = :bookId")
    List<BookCopy> findAllByBookIdWithPublisher(@Param("bookId") Integer bookId);

    List<BookCopy> findAllByBook_Id(Integer bookId);
}
