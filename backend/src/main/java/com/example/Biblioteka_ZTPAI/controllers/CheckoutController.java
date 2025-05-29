package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.LoanUserBookDTO;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.services.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/reserve/{bookCopyId}")
    public ResponseEntity<String> reserveBookCopy(@PathVariable Integer bookCopyId, @AuthenticationPrincipal User user) {
        if (checkoutService.isBookCopyReserved(bookCopyId) || checkoutService.isBookCopyLoaned(bookCopyId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book copy is already reserved or borrowed.");
        }
        return checkoutService.reserveBookCopy(bookCopyId, user);
    }

    @GetMapping("/bookCopyStatus/{bookCopyId}")
    public ResponseEntity<Map<String, Boolean>> getBookCopyStatus(
            @PathVariable Integer bookCopyId,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean isReserved = checkoutService.isBookCopyReserved(bookCopyId);
        boolean isReservedByUser = false;
        boolean isLoaned = checkoutService.isBookCopyLoaned(bookCopyId);

        if (isReserved) {
            isReservedByUser = checkoutService.isReservedByUser(bookCopyId, userDetails.getUsername());
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("reserved", isReserved);
        response.put("reservedByCurrentUser", isReservedByUser);
        response.put("loaned", isLoaned);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/cancelReservation/{bookCopyId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Integer bookCopyId, @AuthenticationPrincipal User user) {
        boolean success = checkoutService.cancelReservation(bookCopyId, user);
        if (success) {
            return ResponseEntity.ok("Rezerwacja została anulowana.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono aktywnej rezerwacji lub brak uprawnień.");
        }
    }

    @GetMapping("/reservations")
    public ResponseEntity<Object> getActiveReservations() {
        return checkoutService.getActiveReservations();
    }

    @PutMapping("/reservations/{reservationId}/confirm")
    public ResponseEntity<?> completeReservation(@PathVariable Integer reservationId) {
        try {
            checkoutService.completeReservation(reservationId);
            return ResponseEntity.ok("Rezerwacja została zakończona i przekształcona w wypożyczenie.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wystąpił błąd podczas przetwarzania.");
        }
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanUserBookDTO>> getActiveLoans() {
        List<LoanUserBookDTO> activeLoans = checkoutService.getActiveLoans();
        return ResponseEntity.ok(activeLoans);
    }

    @PutMapping("/loans/{loanId}/return")
    public ResponseEntity<Object> returnLoan(@PathVariable Integer loanId) {
        return checkoutService.returnLoan(loanId);
    }

}
