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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<?> getBooks(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer authorId,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(
                bookService.getBooks(sort, authorId, genreId, search, page, size)
        );
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable int bookId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return bookService.getBookById(bookId, username);
    }

    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<Object> getReviews(@PathVariable Integer bookId) {
        return ResponseEntity.ok(reviewService.getReviewsForBook(bookId));
    }

    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<Object> addReview(
            @PathVariable Integer bookId,
            @Valid @RequestBody ReviewCreateDTO dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        try {
            return new ResponseEntity<>(reviewService.addReview(bookId, user.getUsername(), dto), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBooks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(
                bookService.getMyBooks(userDetails, page, size)
        );
    }

}
