package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.checkout.CreateReservationDTO;
import com.example.Biblioteka_ZTPAI.dto.checkout.FinishLoanDTO;
import com.example.Biblioteka_ZTPAI.models.*;
import com.example.Biblioteka_ZTPAI.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CheckoutControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookCopyRepository bookCopyRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private LoanRepository loanRepository;

    private MockMvc mockMvc;
    private String adminJwt;
    private String userJwt;

    private User user;
    private User admin;
    private Book testBook;
    private BookCopy testCopy;
    private Reservation testReservation;
    private Loan testLoan;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Role adminRole = new Role(); adminRole.setName("ADMIN"); roleRepository.save(adminRole);
        Role userRole = new Role(); userRole.setName("USER"); roleRepository.save(userRole);

        admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        user = new User();
        user.setUsername("user");
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        MvcResult loginResultAdmin = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin@test.com", "password"))))
                .andExpect(status().isOk())
                .andReturn();
        adminJwt = objectMapper.readTree(loginResultAdmin.getResponse().getContentAsString())
                .get("token").asText();

        MvcResult loginResultUser = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("user@test.com", "password"))))
                .andExpect(status().isOk())
                .andReturn();
        userJwt = objectMapper.readTree(loginResultUser.getResponse().getContentAsString())
                .get("token").asText();

        testBook = Book.builder().title("Wiedźmin").description("Opis").bookCopies(new HashSet<>()).build();
        testBook = bookRepository.save(testBook);

        testCopy = BookCopy.builder().book(testBook).code("COPY-001").yearPublished(2020).build();
        testCopy = bookCopyRepository.save(testCopy);

        testReservation = Reservation.builder()
                .bookCopy(testCopy)
                .user(user)
                .startDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusDays(3))
                .build();
        testReservation = reservationRepository.save(testReservation);

        testLoan = Loan.builder()
                .bookCopy(testCopy)
                .user(user)
                .startDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(7))
                .canExtend(true)
                .build();
        testLoan = loanRepository.save(testLoan);
    }

    @Test
    void reserveBook_success() throws Exception {
        loanRepository.delete(testLoan);
        reservationRepository.delete(testReservation);

        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setUserId(user.getId());
        dto.setBookCopyId(testCopy.getId());

        mockMvc.perform(post("/api/v1/checkout/reserve")
                        .header("Authorization", "Bearer " + userJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getActiveReservations_success() throws Exception {
        mockMvc.perform(get("/api/v1/checkout/reservations")
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isOk());
    }

    @Test
    void cancelReservation_success() throws Exception {
        mockMvc.perform(put("/api/v1/checkout/reservations/{id}/cancel", testReservation.getId())
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isOk());
    }

    @Test
    void getActiveLoans_success() throws Exception {
        mockMvc.perform(get("/api/v1/checkout/loans")
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isOk());
    }

    @Test
    void extendLoan_success() throws Exception {
        mockMvc.perform(put("/api/v1/checkout/loans/{id}/extend", testLoan.getId())
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isOk());
    }

    @Test
    void completeReservation_adminSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/checkout/reservations/{id}/confirm", testReservation.getId())
                        .header("Authorization", "Bearer " + adminJwt))
                .andExpect(status().isCreated());
    }

    @Test
    void returnLoan_adminSuccess() throws Exception {
        FinishLoanDTO dto = new FinishLoanDTO();
        dto.setLoanId(testLoan.getId());
        dto.setEndDate(LocalDateTime.now());

        mockMvc.perform(put("/api/v1/checkout/loans/return")
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void completeReservation_forbiddenForUser() throws Exception {
        mockMvc.perform(put("/api/v1/checkout/reservations/{id}/confirm", testReservation.getId())
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnLoan_forbiddenForUser() throws Exception {
        FinishLoanDTO dto = new FinishLoanDTO();
        dto.setLoanId(testLoan.getId());
        dto.setEndDate(LocalDateTime.now());

        mockMvc.perform(put("/api/v1/checkout/loans/return")
                        .header("Authorization", "Bearer " + userJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void reserveBook_bookCopyNotFound() throws Exception {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setUserId(user.getId());
        dto.setBookCopyId(9999);

        mockMvc.perform(post("/api/v1/checkout/reserve")
                        .header("Authorization", "Bearer " + userJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void reserveBook_copyAlreadyReserved_conflict() throws Exception {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setUserId(user.getId());
        dto.setBookCopyId(testCopy.getId());

        mockMvc.perform(post("/api/v1/checkout/reserve")
                        .header("Authorization", "Bearer " + userJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelReservation_notOwnedByUser() throws Exception {
        mockMvc.perform(put("/api/v1/checkout/reservations/{id}/cancel", testReservation.getId())
                        .header("Authorization", "Bearer " + adminJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelReservation_notFound() throws Exception {
        mockMvc.perform(put("/api/v1/checkout/reservations/{id}/cancel", 9999)
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void extendLoan_notOwnedByUser_forbidden() throws Exception {
        mockMvc.perform(put("/api/v1/checkout/loans/{id}/extend", testLoan.getId())
                        .header("Authorization", "Bearer " + adminJwt))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void extendLoan_alreadyExtended_illegalState() throws Exception {
        testLoan.setCanExtend(false);
        loanRepository.save(testLoan);

        mockMvc.perform(put("/api/v1/checkout/loans/{id}/extend", testLoan.getId())
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void extendLoan_notFound() throws Exception {
        mockMvc.perform(put("/api/v1/checkout/loans/{id}/extend", 9999)
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void completeReservation_notFound() throws Exception {
        mockMvc.perform(put("/api/v1/checkout/reservations/{id}/confirm", 9999)
                        .header("Authorization", "Bearer " + adminJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnLoan_notFound() throws Exception {
        FinishLoanDTO dto = new FinishLoanDTO();
        dto.setLoanId(9999);
        dto.setEndDate(LocalDateTime.now());

        mockMvc.perform(put("/api/v1/checkout/loans/return")
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnLoan_alreadyReturned_badRequest() throws Exception {
        testLoan.setEndDate(LocalDateTime.now());
        loanRepository.save(testLoan);

        FinishLoanDTO dto = new FinishLoanDTO();
        dto.setLoanId(testLoan.getId());
        dto.setEndDate(LocalDateTime.now());

        mockMvc.perform(put("/api/v1/checkout/loans/return")
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}

