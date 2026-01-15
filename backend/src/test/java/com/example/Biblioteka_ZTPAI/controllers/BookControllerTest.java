package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.book.ReviewCreateDTO;
import com.example.Biblioteka_ZTPAI.models.*;
import com.example.Biblioteka_ZTPAI.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private MockMvc mockMvc;

    private String jwtToken;

    private Book testBook;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Genre testGenre = new Genre();
        testGenre.setName("Test Genre");
        genreRepository.save(testGenre);

        Author testAuthor = new Author();
        testAuthor.setName("Jan");
        testAuthor.setSurname("Kowalski");
        authorRepository.save(testAuthor);

        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setGenre(testGenre);
        testBook.setAuthors(Set.of(testAuthor));
        bookRepository.save(testBook);

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
    void getBooks_success() throws Exception {
        mockMvc.perform(get("/api/v1/books")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void getBookById_success() throws Exception {
        mockMvc.perform(get("/api/v1/books/{bookId}", testBook.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void getBookById_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/books/{bookId}", 9999)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="user@user.com")
    void getMyBooks_success() throws Exception {
        mockMvc.perform(get("/api/v1/books/my")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void getMyBooks_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/books/my"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getReviews_success() throws Exception {
        mockMvc.perform(get("/api/v1/books/{bookId}/reviews", testBook.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void getReviews_bookNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/books/{bookId}/reviews", 9999)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="user@user.com")
    void addReview_success() throws Exception {

        ReviewCreateDTO reviewDTO = new ReviewCreateDTO();
        reviewDTO.setRating(5);
        reviewDTO.setReviewText("Świetna książka!");

        mockMvc.perform(post("/api/v1/books/{bookId}/reviews", testBook.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user@user.com")
    void addReview_bookNotFound() throws Exception {
        ReviewCreateDTO reviewDTO = new ReviewCreateDTO();
        reviewDTO.setRating(4);
        reviewDTO.setReviewText("Test");

        mockMvc.perform(post("/api/v1/books/{bookId}/reviews", 9999)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@user.com")
    void addReview_invalidRating() throws Exception {
        ReviewCreateDTO reviewDTO = new ReviewCreateDTO();
        reviewDTO.setRating(10);
        reviewDTO.setReviewText("Niepoprawna ocena");

        mockMvc.perform(post("/api/v1/books/{bookId}/reviews", testBook.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@user.com")
    void addReview_duplicateReview() throws Exception {
        ReviewCreateDTO reviewDTO = new ReviewCreateDTO();
        reviewDTO.setRating(5);
        reviewDTO.setReviewText("Pierwsza recenzja");

        mockMvc.perform(post("/api/v1/books/{bookId}/reviews", testBook.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/books/{bookId}/reviews", testBook.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isConflict());
    }

}
