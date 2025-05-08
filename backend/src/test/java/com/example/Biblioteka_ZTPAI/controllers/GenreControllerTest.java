package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.models.Genre;
import com.example.Biblioteka_ZTPAI.services.GenreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @Test
    @DisplayName("GET /api/genres - should return list of genres")
    void getGenres_ShouldReturnGenresList() throws Exception {
        // Arrange
        Genre genre1 = new Genre(1, "Fantasy");
        Genre genre2 = new Genre(2, "Science Fiction");
        List<Genre> genres = Arrays.asList(genre1, genre2);

        when(genreService.getGenres()).thenReturn(genres);

        // Act & Assert
        mockMvc.perform(get("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id_genre", is(1)))
                .andExpect(jsonPath("$[0].genre_name", is("Fantasy")))
                .andExpect(jsonPath("$[1].id_genre", is(2)))
                .andExpect(jsonPath("$[1].genre_name", is("Science Fiction")));
    }
}

