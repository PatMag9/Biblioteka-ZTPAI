package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.models.Author;
import com.example.Biblioteka_ZTPAI.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    @Query("SELECT b.authors FROM Book b WHERE b.idBook = :bookId")
    Set<Author> findAuthorsByBookId(Integer bookId);

    @Query("""
SELECT b FROM Book b
LEFT JOIN b.authors a
WHERE (:search IS NULL OR LOWER(b.title) LIKE %:search%)
  AND (:genre IS NULL OR LOWER(b.genre.genreName) = LOWER(:genre))
  AND (:author IS NULL OR EXISTS (SELECT 1 FROM b.authors au WHERE LOWER(au.name) LIKE %:author%))
GROUP BY b
ORDER BY MIN(a.name) ASC
""")
    Page<Book> findAllSortedByAuthorName(
            @Param("search") String search,
            @Param("genre") String genre,
            @Param("author") String author,
            Pageable pageable
    );
}
