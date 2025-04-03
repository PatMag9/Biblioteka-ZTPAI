package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.models.Book;
import com.example.Biblioteka_ZTPAI.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final Map<Integer, Map<String, String>> books = new HashMap<>();
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public ResponseEntity<Object> getBooks() {
        return ResponseEntity.ok(this.bookRepository.findAll());
    }

    public ResponseEntity<Object> getBookById(int bookId) {
        Optional<Book> book = bookRepository.findById(bookId);

        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Book not found"));
        }
    }

    public ResponseEntity<Object> addBook(Book book) {
        bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

}