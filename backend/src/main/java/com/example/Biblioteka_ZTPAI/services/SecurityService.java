package com.example.Biblioteka_ZTPAI.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public ResponseEntity<Object> login(){
        System.out.println("login test");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    public ResponseEntity<Object> register(){
        System.out.println("register test");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    public ResponseEntity<Object> logout(){
        System.out.println("logout test");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
