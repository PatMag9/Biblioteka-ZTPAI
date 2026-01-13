package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.models.LoginRequest;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BookControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    private String jwtToken;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@user.com");
        loginRequest.setPassword("user");

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andReturn();

        jwtToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    void getBooks_authenticated_success() throws Exception {
        mockMvc.perform(get("/api/v1/books")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void getBookById_authenticated_success() throws Exception {
        int testBookId = 1; // upewnij się, że w bazie testowej istnieje książka o ID=1
        mockMvc.perform(get("/api/v1/books/{bookId}", testBookId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="user@user.com")
    void getMyBooks_authenticated_success() throws Exception {
        mockMvc.perform(get("/api/v1/books/my")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void getReviews_authenticated_success() throws Exception {
        int testBookId = 1;
        mockMvc.perform(get("/api/v1/books/{bookId}/reviews", testBookId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}
