package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.models.Genre;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
import com.example.Biblioteka_ZTPAI.services.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @MockBean
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        this.testUser = createTestUser();
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("ccc");
        user.setEmail("user@user.com");
        user.setPassword("user");

        when(userRepository.findByUsername("ccc")).thenReturn(Optional.of(user));

        return user;
    }
    @Test
    @DisplayName("GET /api/genres - should return list of genres")
    void getGenres_ShouldReturnGenresList() throws Exception {
        Genre genre1 = new Genre(1, "Fantasy");
        Genre genre2 = new Genre(2, "Science Fiction");
        List<Genre> genres = Arrays.asList(genre1, genre2);

        when(genreService.getGenres()).thenReturn(genres);

        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzQ2NzExOTY3LCJleHAiOjE3NDY3MTM0MDd9.VVf1G8irREsr9t-jbQXUTgteFtvOZDze_XVeGvTh8ss";

        // Act & Assert
        mockMvc.perform(get("/api/genres")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id_genre", is(1)))
                .andExpect(jsonPath("$[0].genre_name", is("Fantasy")))
                .andExpect(jsonPath("$[1].id_genre", is(2)))
                .andExpect(jsonPath("$[1].genre_name", is("Science Fiction")));
    }
}

