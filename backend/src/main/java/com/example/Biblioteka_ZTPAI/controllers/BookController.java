package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.models.Book;
import com.example.Biblioteka_ZTPAI.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Object> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Object> getBookById(@PathVariable int bookId) {
        return bookService.getBookById(bookId);
    }

    @GetMapping("/{bookId}/copies")
    public ResponseEntity<Object> getBookCopies(@PathVariable int bookId) {
        return bookService.getBookCopiesByBookId(bookId);
    }

    @GetMapping("/copies/{copyId}/publisher")
    public ResponseEntity<Object> getPublisherByBookCopyId(@PathVariable int copyId) {
        return bookService.getPublisherByBookCopyId(copyId);
    }

    @GetMapping("/{bookId}/authors")
    public ResponseEntity<Object> getAuthorsByBookId(@PathVariable int bookId) {
        return bookService.getAuthorsByBookId(bookId);
    }

    @PostMapping
    public ResponseEntity<Object> addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Object> updateBook(@PathVariable int bookId, @RequestBody Book updatedBook) {
        return bookService.updateBook(bookId, updatedBook);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable int bookId) {
        return bookService.deleteBook(bookId);
    }

}
