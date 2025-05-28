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
}
