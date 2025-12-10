package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.checkout.CreateReservationDTO;
import com.example.Biblioteka_ZTPAI.dto.checkout.FinishLoanDTO;
import com.example.Biblioteka_ZTPAI.dto.checkout.FinishReservationDTO;
import com.example.Biblioteka_ZTPAI.services.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/cancelReservation")
    public ResponseEntity<?> cancelReservation(
            @RequestBody FinishReservationDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return checkoutService.cancelReservation(dto, userDetails.getUsername());
    }

    @GetMapping("/reservations")
    public ResponseEntity<?> getActiveReservations() {
        return ResponseEntity.ok(checkoutService.getActiveReservations());
    }

    @PutMapping("/reservations/{reservationId}/confirm")
    public ResponseEntity<?> completeReservation(@PathVariable Integer reservationId) {
        return checkoutService.completeReservation(reservationId);
    }

    @GetMapping("/loans")
    public ResponseEntity<?> getActiveLoans() {
        return ResponseEntity.ok(checkoutService.getActiveLoans());
    }

    @PutMapping("/loans/return")
    public ResponseEntity<?> returnLoan(@RequestBody FinishLoanDTO dto) {
        return checkoutService.returnLoan(dto);
    }

}
