package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.book.admin.CreateGenreDTO;
import com.example.Biblioteka_ZTPAI.models.Genre;
import com.example.Biblioteka_ZTPAI.models.LoginRequest;
import com.example.Biblioteka_ZTPAI.models.Role;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.repositories.GenreRepository;
import com.example.Biblioteka_ZTPAI.repositories.RoleRepository;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GenreControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private String jwtToken;
    private Genre testGenre;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        testGenre = new Genre();
        testGenre.setName("Fantasy");
        testGenre = genreRepository.save(testGenre);

        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        User user = new User();
        user.setUsername("user");
        user.setEmail("user@user.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@user.com");
        loginRequest.setPassword("password");

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
    void getGenres_success() throws Exception {
        mockMvc.perform(get("/api/v1/genres")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void getGenres_withNameFilter_success() throws Exception {
        mockMvc.perform(get("/api/v1/genres")
                        .param("name", "Fantasy")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void createGenre_success() throws Exception {
        CreateGenreDTO dto = new CreateGenreDTO();
        dto.setName("Science Fiction");

        mockMvc.perform(post("/api/v1/genres")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteGenre_success() throws Exception {
        mockMvc.perform(delete("/api/v1/genres/{id}", testGenre.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }
}
