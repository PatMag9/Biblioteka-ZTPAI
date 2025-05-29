package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.models.AuthenticationResponse;
import com.example.Biblioteka_ZTPAI.models.LoginRequest;
import com.example.Biblioteka_ZTPAI.models.RegisterRequest;
import com.example.Biblioteka_ZTPAI.services.AuthService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return authService.logout();
    }
}
