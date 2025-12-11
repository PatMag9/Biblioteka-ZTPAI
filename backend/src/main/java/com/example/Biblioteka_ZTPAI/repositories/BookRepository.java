package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.models.Book;
import com.example.Biblioteka_ZTPAI.projections.MyBookProjection;
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

    @Query(value =
            "SELECT " +
                    "   b.id AS bookId, " +
                    "   bc.id AS bookCopyId, " +
                    "   b.cover AS cover, " +
                    "   b.title AS title, " +
                    "   'loan' AS type, " +
                    "   l.due_date AS dueDate, " +
                    "   l.start_date AS startDate, " +
                    "   l.can_extend AS canExtend " +
                    "FROM loans l " +
                    "JOIN book_copies bc ON bc.id = l.book_copy_id " +
                    "JOIN books b ON b.id = bc.book_id " +
                    "WHERE l.user_id = :userId " +
                    "  AND l.end_date IS NULL " +

                    "UNION ALL " +

                    "SELECT " +
                    "   b.id AS bookId, " +
                    "   bc.id AS bookCopyId, " +
                    "   b.cover AS cover, " +
                    "   b.title AS title, " +
                    "   'reservation' AS type, " +
                    "   r.expiration_date AS dueDate, " +
                    "   r.start_date AS startDate, " +
                    "   FALSE AS canExtend " +
                    "FROM reservations r " +
                    "JOIN book_copies bc ON bc.id = r.book_copy_id " +
                    "JOIN books b ON b.id = bc.book_id " +
                    "WHERE r.user_id = :userId " +
                    "  AND r.end_date IS NULL ",

            countQuery =
                    "SELECT COUNT(*) FROM (" +
                            "   SELECT l.id FROM loans l WHERE l.user_id = :userId AND l.end_date IS NULL " +
                            "   UNION ALL " +
                            "   SELECT r.id FROM reservations r WHERE r.user_id = :userId AND r.end_date IS NULL " +
                            ") AS t",
            nativeQuery = true
    )
    Page<MyBookProjection> findUserLoansAndReservations(
            @Param("userId") Integer userId,
            Pageable pageable
    );

}
