package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.checkout.CreateReservationDTO;
import com.example.Biblioteka_ZTPAI.dto.checkout.FinishLoanDTO;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.services.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveBookCopy(
            @RequestBody CreateReservationDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return checkoutService.reserveBookCopy(dto, userDetails.getUsername());
    }

    @GetMapping("/reservations")
    public ResponseEntity<?> getActiveReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String username
    ) {
        return ResponseEntity.ok(checkoutService.getActiveReservations(page, size, username));
    }

    @PutMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Integer reservationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return checkoutService.cancelReservation(reservationId, userDetails.getUsername());
    }

    @PutMapping("/reservations/{reservationId}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> completeReservation(@PathVariable Integer reservationId) {
        return checkoutService.completeReservation(reservationId);
    }

    @GetMapping("/loans")
    public ResponseEntity<?> getActiveLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String username
    ) {
        return ResponseEntity.ok(checkoutService.getActiveLoans(page, size, username));
    }

    @PutMapping("/loans/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> returnLoan(@RequestBody FinishLoanDTO dto) {
        return checkoutService.returnLoan(dto);
    }

    @PutMapping("/loans/{loanId}/extend")
    public ResponseEntity<?> extendLoan(
            @PathVariable Integer loanId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(checkoutService.extendLoan(loanId, username));
    }

}
