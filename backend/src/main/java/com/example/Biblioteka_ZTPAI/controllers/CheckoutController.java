package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.services.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/reservation/{bookId}")
    public ResponseEntity<Object> addReservation(@PathVariable int bookId){
        return checkoutService.addReservation(bookId);
    }

    @PutMapping("/reservation/{reservation_id}")
    public ResponseEntity<Object> cancelReservation(@PathVariable int reservation_id){
        return checkoutService.cancelReservation(reservation_id);
    }

    @PostMapping("/loan/{reservation_id}")
    public ResponseEntity<Object> loanBook(@PathVariable int reservation_id){
        return checkoutService.loanBook(reservation_id);
    }

    @PutMapping("/loan/{loan_id}")
    public ResponseEntity<Object> returnBook(@PathVariable int loan_id){
        return checkoutService.returnBook(loan_id);
    }
}
