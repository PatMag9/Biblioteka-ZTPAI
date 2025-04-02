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

//        books.put(1, new HashMap<>(Map.of("id", "1", "title", "Pan Tadeusz", "id_genre","1", "id_publisher","11", "status","dostępne")));
//        books.put(2, new HashMap<>(Map.of("id", "2", "title", "Krzyżacy", "id_genre","2", "id_publisher","22", "status","zarezerwowane")));
//        books.put(3, new HashMap<>(Map.of("id", "3", "title", "Romeo i Julia", "id_genre","3", "id_publisher","33", "status","wypożyczone")));
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

    public ResponseEntity<Object> addBook(String title, Integer idGenre, String cover, String description) {
        Book newBook = new Book(null, title, idGenre, cover, description);
        bookRepository.save(newBook);

        return ResponseEntity.status(HttpStatus.CREATED).body(newBook);
    }

    public ResponseEntity<Object> updateBook(int bookId, String title, String idGenre, String idPublisher, String status) {
        if (!books.containsKey(bookId)) {
            return ResponseEntity.status(404).body(Map.of("error", "Book not found"));
        }
        Map<String, String> updatedBook = books.get(bookId);
        updatedBook.put("title", title);
        updatedBook.put("id_genre", idGenre);
        updatedBook.put("id_publisher", idPublisher);
        updatedBook.put("status", status);
        return ResponseEntity.ok(Map.of("success", "Book updated"));
    }

    public ResponseEntity<Object> deleteBook(int bookId) {
        if (!books.containsKey(bookId)) {
            return ResponseEntity.status(404).body(Map.of("error", "Book not found"));
        }
        books.remove(bookId);
        return ResponseEntity.ok(Map.of("success", "Book removed"));
    }

}