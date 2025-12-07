package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.book.ReviewCreateDTO;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.services.BookService;
import com.example.Biblioteka_ZTPAI.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<?> getBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String author,
            @RequestParam(required = false, defaultValue = "title") String sort,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        return bookService.getBooks(search, genre, author, sort, order, page, size);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable int bookId) {
        return bookService.getBookById(bookId);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable int bookId) {
        return bookService.deleteBook(bookId);
    }

    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<Object> getReviews(@PathVariable Integer bookId) {
        return ResponseEntity.ok(reviewService.getReviewsForBook(bookId));
    }

    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<Object> addReview(
            @PathVariable Integer bookId,
            @Valid @RequestBody ReviewCreateDTO dto,
            @AuthenticationPrincipal User user
    ) {
        try {
            return ResponseEntity.ok(reviewService.addReview(bookId, user.getId(), dto));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
