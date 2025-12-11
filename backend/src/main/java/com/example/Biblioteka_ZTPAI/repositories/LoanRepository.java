package com.example.Biblioteka_ZTPAI.repositories;

import com.example.Biblioteka_ZTPAI.dto.checkout.LoanUserBookDTO;
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
    SELECT new com.example.Biblioteka_ZTPAI.dto.checkout.LoanUserBookDTO(
        l.id, l.startDate, l.dueDate, l.endDate,
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

    @Query("SELECT l FROM Loan l WHERE l.bookCopy.id = :bookCopyId AND l.endDate IS NULL")
    Optional<Loan> findActiveLoanByBookCopyId(@Param("bookCopyId") Integer bookCopyId);

    @Query("SELECT l FROM Loan l WHERE l.bookCopy.id IN :copyIds AND l.endDate IS NULL")
    List<Loan> findActiveLoansByBookCopyIds(@Param("copyIds") List<Integer> copyIds);

    @Query("SELECT l FROM Loan l WHERE l.id = :loanId AND l.endDate IS NULL")
    Optional<Loan> findActiveLoanById(@Param("loanId") Integer loanId);
}
