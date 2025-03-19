package com.example.Biblioteka_ZTPAI.services;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {

    public ResponseEntity<Object> addReservation(int bookId){
        //todo
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
