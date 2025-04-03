package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.models.Book;
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

    @PostMapping
    public ResponseEntity<Object> addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

}
