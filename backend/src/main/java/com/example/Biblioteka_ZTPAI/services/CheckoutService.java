package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.checkout.*;
import com.example.Biblioteka_ZTPAI.mappers.LoanMapper;
import com.example.Biblioteka_ZTPAI.models.BookCopy;
import com.example.Biblioteka_ZTPAI.models.Loan;
import com.example.Biblioteka_ZTPAI.models.Reservation;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.repositories.BookCopyRepository;
import com.example.Biblioteka_ZTPAI.repositories.LoanRepository;
import com.example.Biblioteka_ZTPAI.repositories.ReservationRepository;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
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
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;


    public ResponseEntity<?> reserveBookCopy(CreateReservationDTO dto, String username) {
        BookCopy bookCopy = bookCopyRepository.findById(dto.getBookCopyId())
                .orElse(null);

        if (bookCopy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book copy not found.");
        }

        if (isBookCopyReserved(bookCopy.getId()) || isBookCopyLoaned(bookCopy.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Book copy is already reserved or borrowed.");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Reservation reservation = Reservation.builder()
                .bookCopy(bookCopy)
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(null)
                .build();

        reservationRepository.save(reservation);

        return ResponseEntity.ok("Reservation created successfully");
    }

    public ResponseEntity<?> cancelReservation(FinishReservationDTO dto, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Reservation> reservationOpt =
                reservationRepository.findActiveReservationByBookCopyAndUser(dto.getBookCopyId(), user.getId());

        if (reservationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Active reservation not found or does not belong to current user.");
        }

        Reservation reservation = reservationOpt.get();

        reservation.setEndDate(LocalDateTime.now());

        reservationRepository.save(reservation);

        return ResponseEntity.ok("Reservation cancelled successfully.");
    }

    public List<ReservationUserBookDTO> getActiveReservations() {
        return reservationRepository.findActiveReservationsWithUserAndBook();
    }

    public ResponseEntity<?> completeReservation(Integer reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        reservation.setEndDate(LocalDateTime.now());
        reservationRepository.save(reservation);

        CreateLoanDTO dto = new CreateLoanDTO(
                reservation.getUser().getId(),
                reservation.getBookCopy().getId(),
                reservation.getStartDate(),
                LocalDateTime.now().plusWeeks(2)
        );

        Loan loan = loanMapper.toEntity(dto);
        loanRepository.save(loan);

        return ResponseEntity.ok("Reservation converted to loan.");
    }

    public List<LoanUserBookDTO> getActiveLoans() {
        return loanRepository.findActiveLoansWithUserAndBook();
    }

    public ResponseEntity<?> returnLoan(FinishLoanDTO dto) {
        Optional<Loan> loanOpt = loanRepository.findById(dto.getLoanId());

        if (loanOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Loan not found.");
        }

        Loan loan = loanOpt.get();

        if (loan.getEndDate() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Loan already returned.");
        }

        loan.setEndDate(dto.getEndDate());
        loanRepository.save(loan);

        return ResponseEntity.ok("Loan returned successfully.");
    }

    public boolean isBookCopyReserved(Integer bookCopyId) {
        return reservationRepository.findActiveReservationByBookCopyId(bookCopyId).isPresent();
    }

    public boolean isBookCopyLoaned(Integer bookCopyId) {
        return loanRepository.findActiveLoanByBookCopyId(bookCopyId).isPresent();
    }
}
