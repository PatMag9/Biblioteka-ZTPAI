package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.book.admin.*;
import com.example.Biblioteka_ZTPAI.models.*;
import com.example.Biblioteka_ZTPAI.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminBookControllerTest {

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
    private GenreRepository genreRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookCopyRepository bookCopyRepository;

    private MockMvc mockMvc;
    private String adminJwt;
    private String userJwt;

    private Book testBook;
    private BookCopy testCopy;
    private Publisher publisher;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        User user = new User();
        user.setUsername("user");
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("password");

        MvcResult loginResult = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andReturn();

        adminJwt = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();

        loginRequest.setEmail("user@test.com");

        MvcResult loginResult2 = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andReturn();

        userJwt = objectMapper.readTree(loginResult2.getResponse().getContentAsString())
                .get("token").asText();

        Genre genre = new Genre();
        genre.setName("Fantasy");
        genreRepository.save(genre);

        Author author = new Author();
        author.setName("Andrzej");
        author.setSurname("Sapkowski");
        authorRepository.save(author);

        publisher = new Publisher();
        publisher.setName("SuperNowa");
        publisherRepository.save(publisher);

        testBook = Book.builder()
                .title("Wiedźmin")
                .genre(genre)
                .authors(new HashSet<>(Set.of(author)))
                .description("Opis")
                .bookCopies(new HashSet<>())
                .build();
        testBook = bookRepository.save(testBook);

        testCopy = BookCopy.builder()
                .book(testBook)
                .code("COPY-001")
                .publisher(publisher)
                .yearPublished(2020)
                .build();
        testCopy = bookCopyRepository.save(testCopy);
    }

    @Test
    void getAdminBooks_success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/books")
                        .header("Authorization", "Bearer " + adminJwt))
                .andExpect(status().isOk());
    }

    @Test
    void updateBook_success() throws Exception {

        UpdateBookDTO dto = new UpdateBookDTO();
        dto.setTitle("Zmieniony tytuł");

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        mockMvc.perform(
                        multipart("/api/v1/admin/books/{id}", testBook.getId())
                                .file(data)
                                .with(req -> {
                                    req.setMethod("PUT");
                                    return req;
                                })
                                .header("Authorization", "Bearer " + adminJwt)
                )
                .andExpect(status().isOk());
    }

    @Test
    void createBook_success() throws Exception {

        CreateBookDTO dto = new CreateBookDTO();
        dto.setTitle("Nowa książka");
        dto.setDescription("Opis");
        dto.setGenreId(testBook.getGenre().getId());
        dto.setAuthorIds(new ArrayList<>(List.of(
                testBook.getAuthors().iterator().next().getId()
        )));

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        MockMultipartFile cover = new MockMultipartFile(
                "cover",
                "cover.jpg",
                "image/jpeg",
                "fake-image".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/v1/admin/books")
                                .file(data)
                                .file(cover)
                                .header("Authorization", "Bearer " + adminJwt)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void addBookCopy_success() throws Exception {

        CreateBookCopyDTO dto = new CreateBookCopyDTO();
        dto.setCode("COPY-002");
        dto.setPublisherId(publisher.getId());
        dto.setYearPublished(2022);

        mockMvc.perform(post("/api/v1/admin/books/{id}/copies", testBook.getId())
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateBookCopy_success() throws Exception {

        UpdateBookCopyDTO dto = new UpdateBookCopyDTO();
        dto.setCode("UPDATED-CODE");

        mockMvc.perform(put("/api/v1/admin/books/copies/{id}", testCopy.getId())
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBookCopy_success() throws Exception {

        mockMvc.perform(delete("/api/v1/admin/books/copies/{id}", testCopy.getId())
                        .header("Authorization", "Bearer " + adminJwt))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBook_success() throws Exception {

        bookCopyRepository.delete(testCopy);

        mockMvc.perform(delete("/api/v1/admin/books/{id}", testBook.getId())
                        .header("Authorization", "Bearer " + adminJwt))
                .andExpect(status().isOk());
    }

    @Test
    void getAdminBooks_forbiddenForUser() throws Exception {

        mockMvc.perform(get("/api/v1/admin/books")
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateBook_forbiddenForUser() throws Exception {

        UpdateBookDTO dto = new UpdateBookDTO();
        dto.setTitle("Zmieniony tytuł");

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        mockMvc.perform(
                        multipart("/api/v1/admin/books/{id}", testBook.getId())
                                .file(data)
                                .with(req -> { req.setMethod("PUT"); return req; })
                                .header("Authorization", "Bearer " + userJwt)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void createBook_forbiddenForUser() throws Exception {

        CreateBookDTO dto = new CreateBookDTO();
        dto.setTitle("Nowa książka");
        dto.setDescription("Opis");
        dto.setGenreId(testBook.getGenre().getId());
        dto.setAuthorIds(new ArrayList<>(List.of(
                testBook.getAuthors().iterator().next().getId()
        )));

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        MockMultipartFile cover = new MockMultipartFile(
                "cover",
                "cover.jpg",
                "image/jpeg",
                "fake-image".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/v1/admin/books")
                                .file(data)
                                .file(cover)
                                .header("Authorization", "Bearer " + userJwt)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void addBookCopy_forbiddenForUser() throws Exception {

        CreateBookCopyDTO dto = new CreateBookCopyDTO();
        dto.setCode("COPY-002");
        dto.setPublisherId(publisher.getId());
        dto.setYearPublished(2022);

        mockMvc.perform(post("/api/v1/admin/books/{id}/copies", testBook.getId())
                        .header("Authorization", "Bearer " + userJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateBookCopy_forbiddenForUser() throws Exception {

        UpdateBookCopyDTO dto = new UpdateBookCopyDTO();
        dto.setCode("UPDATED-CODE");

        mockMvc.perform(put("/api/v1/admin/books/copies/{id}", testCopy.getId())
                        .header("Authorization", "Bearer " + userJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBookCopy_forbiddenForUser() throws Exception {

        mockMvc.perform(delete("/api/v1/admin/books/copies/{id}", testCopy.getId())
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBook_forbiddenForUser() throws Exception {

        bookCopyRepository.delete(testCopy);

        mockMvc.perform(delete("/api/v1/admin/books/{id}", testBook.getId())
                        .header("Authorization", "Bearer " + userJwt))
                .andExpect(status().isForbidden());
    }

}
