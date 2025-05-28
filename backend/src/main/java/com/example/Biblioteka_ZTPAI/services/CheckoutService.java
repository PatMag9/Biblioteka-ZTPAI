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

    private final BookCopyRepository bookCopyRepository;
    private final ReservationRepository reservationRepository;
    private final LoanRepository loanRepository;

    public ResponseEntity<?> reserveBookCopy(Integer bookCopyId, User user) {
        return bookCopyRepository.findById(bookCopyId)
                .map(bookCopy -> {
                    Reservation reservation = Reservation.builder()
                            .bookCopy(bookCopy)
                            .user(user)
                            .start_date(LocalDateTime.now())
                            .endDate(null)
                            .build();
                    reservationRepository.save(reservation);
                    return ResponseEntity.ok("Rezerwacja została utworzona.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public boolean isBookCopyReserved(Integer bookCopyId) {
        Optional<Reservation> activeReservation = reservationRepository.findActiveReservationByBookCopyId(bookCopyId);
        return activeReservation.isPresent();
    }

    public boolean isBookCopyLoaned(Integer bookCopyId) {
        Optional<Loan> activeLoan = loanRepository.findActiveLoanByBookCopyId(bookCopyId);
        return activeLoan.isPresent();
    }

    public boolean cancelReservation(Integer bookCopyId, User user) {
        Optional<Reservation> reservationOpt = reservationRepository
                .findActiveReservationByBookCopyAndUser(bookCopyId, user.getId_user());

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setEndDate(LocalDateTime.now());
            reservationRepository.save(reservation);
            return true;
        }

        return false;
    }

    public boolean isReservedByUser(Integer bookCopyId, String username) {
        Optional<Reservation> activeReservation = reservationRepository.findActiveReservationByBookCopyId(bookCopyId);

        if (activeReservation.isPresent()) {
            Reservation reservation = activeReservation.get();
            return reservation.getUser().getUsername().equals(username);
        }
        return false;
    }

    public ResponseEntity<Object> getActiveReservations() {
        return ResponseEntity.ok(reservationRepository.findActiveReservationsWithUserAndBook());
    }

    public void completeReservation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Rezerwacja nie została znaleziona"));

        reservation.setEndDate(LocalDateTime.now());
        reservationRepository.save(reservation);

        Loan loan = Loan.builder()
                .user(reservation.getUser())
                .bookCopy(reservation.getBookCopy())
                .start_date(reservation.getStart_date())
                .due_date(LocalDateTime.now().plusWeeks(2))
                .endDate(null)
                .build();

        loanRepository.save(loan);
    }

    public List<LoanUserBookDTO> getActiveLoans() {
        return loanRepository.findActiveLoansWithUserAndBook();
    }

    public ResponseEntity<Object> returnLoan(Integer loanId) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);

        if (loanOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Wypożyczenie o ID " + loanId + " nie istnieje.");
        }

        Loan loan = loanOpt.get();

        if (loan.getEndDate() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("To wypożyczenie zostało już zakończone.");
        }

        loan.setEndDate(LocalDateTime.now());
        loanRepository.save(loan);

        return ResponseEntity.ok("Wypożyczenie zakończone pomyślnie.");
    }
}
