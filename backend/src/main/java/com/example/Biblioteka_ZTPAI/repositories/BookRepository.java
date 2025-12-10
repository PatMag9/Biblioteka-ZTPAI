package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    Page<Book> findByAuthors_Id(Integer authorId, Pageable pageable);
    Page<Book> findByGenre_Id(Integer genreId, Pageable pageable);
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Book> searchByTitle(@Param("search") String search, Pageable pageable);

    @Query(
            value = """
        SELECT b.* FROM books b
        LEFT JOIN reviews r ON r.book_id = b.id
        WHERE (:search IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:genreId IS NULL OR b.genre_id = :genreId)
          AND (:authorId IS NULL OR EXISTS (
                SELECT 1 FROM books_authors ba
                WHERE ba.book_id = b.id AND ba.author_id = :authorId
              ))
        GROUP BY b.id
        ORDER BY COUNT(r.id) DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM (
          SELECT b.id FROM books b
          LEFT JOIN reviews r ON r.book_id = b.id
          WHERE (:search IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:genreId IS NULL OR b.genre_id = :genreId)
            AND (:authorId IS NULL OR EXISTS (
                  SELECT 1 FROM books_authors ba
                  WHERE ba.book_id = b.id AND ba.author_id = :authorId
                ))
          GROUP BY b.id
        ) AS sub
        """,
            nativeQuery = true
    )
    Page<Book> findAllOrderByReviewCountDesc(
            @Param("search") String search,
            @Param("genreId") Integer genreId,
            @Param("authorId") Integer authorId,
            Pageable pageable
    );

    @Query("""
      SELECT r.book.id as bookId, COUNT(r) as cnt, AVG(r.rating) as avgRating
      FROM Review r
      WHERE r.book.id IN :bookIds
      GROUP BY r.book.id
    """)
    List<Object[]> findReviewStatsByBookIds(@Param("bookIds") List<Integer> bookIds);


}
