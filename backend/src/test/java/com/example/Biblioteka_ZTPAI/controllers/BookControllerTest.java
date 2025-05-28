package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.models.*;
import com.example.Biblioteka_ZTPAI.services.JwtService;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
import com.example.Biblioteka_ZTPAI.services.BookService;
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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

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
        return Book.builder()
                .idBook(1)
                .title("Test Book")
                .genre(getSampleGenre())
                .cover("http://example.com/cover.jpg")
                .description("Sample description")
                .build();
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
                .andExpect(jsonPath("$.idBook").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
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

    @Test
    void getBookCopies_shouldReturnCopies() throws Exception {
        BookCopy copy = new BookCopy();
        copy.setId_book_copy(1);
        copy.setIsbn("123-456-789");

        Mockito.when(bookService.getBookCopiesByBookId(1))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(copy)));

        mockMvc.perform(get("/api/books/{bookId}/copies", 1)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id_book_copy").value(1))
                .andExpect(jsonPath("$[0].isbn").value("123-456-789"));
    }

    @Test
    void getPublisherByBookCopyId_shouldReturnPublisher() throws Exception {
        Publisher publisher = new Publisher();
        publisher.setId_publishers(1);
        publisher.setPublishers_name("PublisherName");

        Mockito.when(bookService.getPublisherByBookCopyId(1))
                .thenReturn(ResponseEntity.ok(publisher));

        mockMvc.perform(get("/api/books/copies/{copyId}/publisher", 1)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_publishers").value(1))
                .andExpect(jsonPath("$.publishers_name").value("PublisherName"));
    }

    @Test
    void getAuthorsByBookId_shouldReturnAuthors() throws Exception {
        Author author = new Author(1, "John", "Doe", null);

        Mockito.when(bookService.getAuthorsByBookId(1))
                .thenReturn(ResponseEntity.ok(Set.of(author)));

        mockMvc.perform(get("/api/books/{bookId}/authors", 1)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].surname").value("Doe"));
    }

}
