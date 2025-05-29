package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.services.CheckoutService;
import com.example.Biblioteka_ZTPAI.services.JwtService;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckoutService checkoutService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId_user(1);
        testUser.setUsername("user");
        testUser.setEmail("user@user.com");
        testUser.setPassword("user");

        when(userRepository.findByEmail("user@user.com")).thenReturn(Optional.of(testUser));
        jwtToken = generateTokenForUser(testUser);
    }

    private String generateTokenForUser(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();

        return jwtService.generateToken(userDetails);
    }

    @Test
    void reserveBookCopy_shouldReturnOk() throws Exception {
        when(checkoutService.isBookCopyReserved(1)).thenReturn(false);
        when(checkoutService.isBookCopyLoaned(1)).thenReturn(false);
        when(checkoutService.reserveBookCopy(eq(1), any(User.class)))
                .thenReturn(ResponseEntity.ok("Rezerwacja została utworzona."));

        mockMvc.perform(post("/api/v1/checkout/reserve/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Rezerwacja została utworzona."));
    }

    @Test
    void reserveBookCopy_shouldReturnConflict() throws Exception {
        when(checkoutService.isBookCopyReserved(1)).thenReturn(true);

        mockMvc.perform(post("/api/v1/checkout/reserve/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isConflict())
                .andExpect(content().string("Book copy is already reserved or borrowed."));
    }

    @Test
    void getBookCopyStatus_shouldReturnStatus() throws Exception {
        when(checkoutService.isBookCopyReserved(1)).thenReturn(true);
        when(checkoutService.isReservedByUser(1, testUser.getUsername())).thenReturn(true);
        when(checkoutService.isBookCopyLoaned(1)).thenReturn(false);

        mockMvc.perform(get("/api/v1/checkout/bookCopyStatus/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reserved").value(true))
                .andExpect(jsonPath("$.reservedByCurrentUser").value(true))
                .andExpect(jsonPath("$.loaned").value(false));
    }

    @Test
    void cancelReservation_shouldReturnOk() throws Exception {
        when(checkoutService.cancelReservation(1, testUser)).thenReturn(true);

        mockMvc.perform(put("/api/v1/checkout/cancelReservation/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Rezerwacja została anulowana."));
    }

    @Test
    void cancelReservation_shouldReturnNotFound() throws Exception {
        when(checkoutService.cancelReservation(1, testUser)).thenReturn(false);

        mockMvc.perform(put("/api/v1/checkout/cancelReservation/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Nie znaleziono aktywnej rezerwacji lub brak uprawnień."));
    }

    @Test
    void completeReservation_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(checkoutService).completeReservation(1);

        mockMvc.perform(put("/api/v1/checkout/reservations/1/confirm")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Rezerwacja została zakończona i przekształcona w wypożyczenie."));
    }

    @Test
    void completeReservation_shouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Rezerwacja nie została znaleziona"))
                .when(checkoutService).completeReservation(1);

        mockMvc.perform(put("/api/v1/checkout/reservations/1/confirm")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Rezerwacja nie została znaleziona"));
    }

    @Test
    void returnLoan_shouldReturnOk() throws Exception {
        when(checkoutService.returnLoan(1)).thenReturn(ResponseEntity.ok("Zwrócono"));

        mockMvc.perform(put("/api/v1/checkout/loans/1/return")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Zwrócono"));
    }

    @Test
    void getActiveReservations_shouldReturnOk() throws Exception {
        when(checkoutService.getActiveReservations())
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/api/v1/checkout/reservations")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void getActiveLoans_shouldReturnOk() throws Exception {
        when(checkoutService.getActiveLoans()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/checkout/loans")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void reserveBookCopy_shouldReturnConflict_whenBookIsAlreadyReservedOrLoaned() throws Exception {
        when(checkoutService.isBookCopyReserved(1)).thenReturn(true);
        when(checkoutService.isBookCopyLoaned(1)).thenReturn(false);

        mockMvc.perform(post("/api/v1/checkout/reserve/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isConflict())
                .andExpect(content().string("Book copy is already reserved or borrowed."));
    }

    @Test
    void cancelReservation_shouldReturnNotFound_whenNoActiveReservation() throws Exception {
        when(checkoutService.cancelReservation(eq(1), any(User.class))).thenReturn(false);

        mockMvc.perform(put("/api/v1/checkout/cancelReservation/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Nie znaleziono aktywnej rezerwacji lub brak uprawnień."));
    }

    @Test
    void completeReservation_shouldReturnNotFound_whenReservationNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Rezerwacja nie została znaleziona"))
                .when(checkoutService).completeReservation(1);

        mockMvc.perform(put("/api/v1/checkout/reservations/1/confirm")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Rezerwacja nie została znaleziona"));
    }

}
