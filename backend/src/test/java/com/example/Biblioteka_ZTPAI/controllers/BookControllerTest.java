package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.services.JwtService;
import com.example.Biblioteka_ZTPAI.models.Book;
import com.example.Biblioteka_ZTPAI.models.Genre;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
import com.example.Biblioteka_ZTPAI.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setup() {
        this.testUser = createTestUser();
        this.jwtToken = generateTokenForUser(testUser);
    }

    private User createTestUser() {
        User user = new User();
        user.setId_user(1);
        user.setUsername("user");
        user.setEmail("user@user.com");
        user.setPassword("user");

        when(userRepository.findByEmail("user@user.com")).thenReturn(Optional.of(user));

        return user;
    }

    private String generateTokenForUser(com.example.Biblioteka_ZTPAI.models.User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();

        return jwtService.generateToken(userDetails);
    }

    private Genre getSampleGenre() {
        return new Genre(1, "Fantasy");
    }

    private Book getSampleBook() {
        return new Book(1, "Test Book", getSampleGenre(), "http://example.com/cover.jpg", "Sample description");
    }

    @Test
    void getBooks_shouldReturnListOfBooks() throws Exception {
        Mockito.when(bookService.getBooks())
                .thenReturn(ResponseEntity.ok(Collections.singletonList(getSampleBook())));

        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].genre.genre_name").value("Fantasy"));
    }

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        Book book = getSampleBook();
        Mockito.when(bookService.getBookById(1))
                .thenReturn(ResponseEntity.ok(book));

        mockMvc.perform(get("/api/books/{bookId}", 1)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_book").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.genre.genre_name").value("Fantasy"));
    }

    @Test
    void addBook_shouldReturnAddedBook() throws Exception {
        Book newBook = new Book(null, "New Book", getSampleGenre(), "cover.png", "Nowa książka");
        Book savedBook = new Book(2, "New Book", getSampleGenre(), "cover.png", "Nowa książka");

        Mockito.when(bookService.addBook(any(Book.class)))
                .thenReturn(ResponseEntity.ok(savedBook));

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_book").value(2))
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.genre.genre_name").value("Fantasy"));
    }

    @Test
    void getBookById_shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        Mockito.when(bookService.getBookById(99999))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/api/books/{bookId}", 99999)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void addBook_shouldReturnBadRequestWhenInvalidData() throws Exception {
        Book invalidBook = new Book(null, "", null, "", "");

        Mockito.when(bookService.addBook(any(Book.class)))
                .thenReturn(ResponseEntity.badRequest().body("Invalid book data"));

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid book data"));
    }

    @Test
    void updateBook_shouldReturnUpdatedBook() throws Exception {
        Book updated = new Book(1, "Updated Title", getSampleGenre(), "new-cover.jpg", "Updated description");

        Mockito.when(bookService.updateBook(eq(1), any(Book.class)))
                .thenReturn(ResponseEntity.ok(updated));

        mockMvc.perform(put("/api/books/{bookId}", 1)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.cover").value("new-cover.jpg"));
    }

    @Test
    void updateBook_shouldReturnNotFoundIfBookDoesNotExist() throws Exception {
        Book updated = new Book(99, "Ghost Book", getSampleGenre(), "", "");

        Mockito.when(bookService.updateBook(eq(99), any(Book.class)))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(put("/api/books/{bookId}", 99)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_shouldReturnNoContentOnSuccess() throws Exception {
        Mockito.when(bookService.deleteBook(1))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/api/books/{bookId}", 1)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_shouldReturnNotFoundIfBookDoesNotExist() throws Exception {
        Mockito.when(bookService.deleteBook(999))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(delete("/api/books/{bookId}", 999)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}
