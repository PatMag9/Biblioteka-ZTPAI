package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.models.LoginRequest;
import com.example.Biblioteka_ZTPAI.models.RegisterRequest;
import com.example.Biblioteka_ZTPAI.models.Role;
import com.example.Biblioteka_ZTPAI.repositories.RoleRepository;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);
    }

    @Test
    void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@test.com");
        request.setPassword("password123");

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void login_success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("loginuser");
        registerRequest.setEmail("login@test.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login@test.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    @Test
    void logout_success() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }
}
