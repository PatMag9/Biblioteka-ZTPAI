package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.models.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Page<Author> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(
            String nameQuery, String surnameQuery, Pageable pageable);
    @Query("SELECT a FROM Author a " +
            "WHERE LOWER(CONCAT(a.name, ' ', a.surname)) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(a.surname) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Author> searchByFullNameIgnoreCase(@Param("query") String query, Pageable pageable);
}
