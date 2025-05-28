package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.dto.LoanUserBookDTO;
import com.example.Biblioteka_ZTPAI.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {
    @Query("""
    SELECT new com.example.Biblioteka_ZTPAI.dto.LoanUserBookDTO(
        l.id_loan, l.start_date, l.due_date, l.endDate,
        u.username,
        b.title
    )
    FROM Loan l
    JOIN l.user u
    JOIN l.bookCopy bc
    JOIN bc.book b
    WHERE l.endDate IS NULL
    """)
    List<LoanUserBookDTO> findActiveLoansWithUserAndBook();

    @Query("SELECT l FROM Loan l WHERE l.bookCopy.id_book_copy = :bookCopyId AND l.endDate IS NULL")
    Optional<Loan> findActiveLoanByBookCopyId(@Param("bookCopyId") Integer bookCopyId);
}
