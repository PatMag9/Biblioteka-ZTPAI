package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.book.BookAuthorDTO;
import com.example.Biblioteka_ZTPAI.services.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<?> getAuthors(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BookAuthorDTO> authors = authorService.getAuthors(query, page, size);

        if (authors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(authors);
    }
}
