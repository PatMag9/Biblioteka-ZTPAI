package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.MyBookDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.AccessControlException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
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
                .expirationDate(LocalDateTime.now().plusDays(3))
                .build();

        reservationRepository.save(reservation);

        return new ResponseEntity<>("Reservation created successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<?> cancelReservation(Integer reservationId, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (!reservation.getUser().getId().equals(user.getId()) || reservation.getEndDate() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Active reservation not found or does not belong to current user.");
        }

        reservation.setEndDate(LocalDateTime.now());

        reservationRepository.save(reservation);

        return ResponseEntity.ok("Reservation cancelled successfully.");
    }

    public Page<ReservationUserBookDTO> getActiveReservations(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size);

        String search = username != null ? username : "";

        return reservationRepository.findActiveReservationsWithUserAndBook(search, pageable);
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

        return new ResponseEntity<>("Reservation converted to loan.", HttpStatus.CREATED);
    }

    public Page<LoanUserBookDTO> getActiveLoans(int page, int size, String username) {

        Pageable pageable = PageRequest.of(page, size);

        String search = username != null ? username : "";

        return loanRepository.findActiveLoansWithUserAndBook(search, pageable);
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

    @Transactional
    public MyBookDTO extendLoan(Integer loanId, String username) {

        Loan loan = loanRepository.findActiveLoanById(loanId)
                .orElseThrow(() -> new NoSuchElementException("Loan not found or already ended"));

        if (!loan.getUser().getUsername().equals(username)) {
            throw new AccessControlException("You cannot extend a loan that is not yours");
        }

        if (Boolean.FALSE.equals(loan.getCanExtend())) {
            throw new IllegalStateException("This loan cannot be extended");
        }

        LocalDateTime newDueDate = loan.getDueDate().plusDays(7);

        loan.setDueDate(newDueDate);
        loan.setCanExtend(false);

        loanRepository.save(loan);

        return MyBookDTO.builder()
                .itemId(loan.getId())
                .bookId(loan.getBookCopy().getBook().getId())
                .bookCopyId(loan.getBookCopy().getId())
                .cover(loan.getBookCopy().getBook().getCover())
                .title(loan.getBookCopy().getBook().getTitle())
                .type("loan")
                .startDate(loan.getStartDate())
                .dueDate(loan.getDueDate())
                .canExtend(false)
                .build();
    }
}
