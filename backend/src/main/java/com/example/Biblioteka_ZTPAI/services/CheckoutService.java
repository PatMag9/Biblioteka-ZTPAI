package com.example.Biblioteka_ZTPAI.services;


import com.example.Biblioteka_ZTPAI.dto.LoanUserBookDTO;
import com.example.Biblioteka_ZTPAI.models.Loan;
import com.example.Biblioteka_ZTPAI.models.Reservation;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.repositories.BookCopyRepository;
import com.example.Biblioteka_ZTPAI.repositories.LoanRepository;
import com.example.Biblioteka_ZTPAI.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CheckoutService {

    public ResponseEntity<Object> addReservation(int bookId){
        //todo
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    private final BookCopyRepository bookCopyRepository;
    private final ReservationRepository reservationRepository;
    private final LoanRepository loanRepository;

    public boolean isBookCopyReserved(Integer bookCopyId) {
        Optional<Reservation> activeReservation = reservationRepository.findActiveReservationByBookCopyId(bookCopyId);
        return activeReservation.isPresent();
    }

    public boolean isBookCopyLoaned(Integer bookCopyId) {
        Optional<Loan> activeLoan = loanRepository.findActiveLoanByBookCopyId(bookCopyId);
        return activeLoan.isPresent();
    }

    public boolean isReservedByUser(Integer bookCopyId, String username) {
        Optional<Reservation> activeReservation = reservationRepository.findActiveReservationByBookCopyId(bookCopyId);

        if (activeReservation.isPresent()) {
            Reservation reservation = activeReservation.get();
            return reservation.getUser().getUsername().equals(username);
        }
        return false;
    }
    public ResponseEntity<Object> cancelReservation(int reservation_id){
        //todo
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    public ResponseEntity<Object> loanBook(int reservation_id){
        //todo
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    public ResponseEntity<Object> returnBook(int loan_id){
        //todo
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
