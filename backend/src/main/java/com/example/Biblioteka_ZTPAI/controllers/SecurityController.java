package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {
    private final SecurityService securityService;

    @Autowired
    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(){
        return securityService.login();
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(){
        return securityService.register();
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(){
        return securityService.logout();
    }
}
