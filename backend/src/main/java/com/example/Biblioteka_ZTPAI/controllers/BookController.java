package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Object> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Object> getBookById(@PathVariable int bookId) {
        return bookService.getBookById(bookId);
    }

    @GetMapping("/mybooks")
    public ResponseEntity<Object> getUserBooks() {
        return bookService.getUserBooks();
    }

    @PostMapping
    public ResponseEntity<Object> addBook(){
        return bookService.addBook(4, "Lalka", "4", "44", "dostępne");
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Object> updateBook(@PathVariable int bookId){
        return bookService.updateBook(bookId, "Faraon", "4", "44", "dostępne");
    }
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable int bookId){
        return bookService.deleteBook(bookId);
    }
}
