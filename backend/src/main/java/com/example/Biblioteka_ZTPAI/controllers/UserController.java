package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/profile")
public class UserController {
    private final UserService userService;

}
