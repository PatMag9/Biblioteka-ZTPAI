package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.book.admin.CreateBookCopyDTO;
import com.example.Biblioteka_ZTPAI.dto.book.admin.CreateBookDTO;
import com.example.Biblioteka_ZTPAI.dto.book.admin.UpdateBookCopyDTO;
import com.example.Biblioteka_ZTPAI.dto.book.admin.UpdateBookDTO;
import com.example.Biblioteka_ZTPAI.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/books")
public class AdminBookController {

    private final BookService bookService;


    @GetMapping
    public ResponseEntity<?> getAdminBooks(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(
                bookService.getAdminBooks(search, page, size)
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBook(
            @RequestPart("data") @Valid CreateBookDTO dto,
            @RequestPart(value = "cover", required = false) MultipartFile cover
    ) {
        bookService.createBook(dto, cover);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBook(
            @PathVariable Integer bookId,
            @RequestPart("data") @Valid UpdateBookDTO dto,
            @RequestPart(value = "cover", required = false) MultipartFile cover
    ) {
        bookService.updateBook(bookId, dto, cover);
        return ResponseEntity.ok("Book updated successfully");
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Integer bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok("Book deleted");
    }

    @PostMapping("/{bookId}/copies")
    public ResponseEntity<?> addBookCopy(
            @PathVariable Integer bookId,
            @Valid @RequestBody CreateBookCopyDTO dto
    ) {
        bookService.addBookCopy(bookId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Book copy added successfully");
    }

    @PutMapping("/copies/{copyId}")
    public ResponseEntity<?> updateBookCopy(
            @PathVariable Integer copyId,
            @Valid @RequestBody UpdateBookCopyDTO dto
    ) {
        bookService.updateBookCopy(copyId, dto);
        return ResponseEntity.ok("Book copy updated successfully");
    }

    @DeleteMapping("/copies/{copyId}")
    public ResponseEntity<?> deleteBookCopy(@PathVariable Integer copyId) {
        bookService.deleteBookCopy(copyId);
        return ResponseEntity.ok("Book copy deleted");
    }
}
