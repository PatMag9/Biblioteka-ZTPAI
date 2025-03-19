package com.example.Biblioteka_ZTPAI.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookService {
    private final Map<Integer, Map<String, String>> books = new HashMap<>();
    public BookService() {
        books.put(1, new HashMap<>(Map.of("id", "1", "title", "Pan Tadeusz", "id_genre","1", "id_publisher","11", "status","dostępne")));
        books.put(2, new HashMap<>(Map.of("id", "2", "title", "Krzyżacy", "id_genre","2", "id_publisher","22", "status","zarezerwowane")));
        books.put(3, new HashMap<>(Map.of("id", "3", "title", "Romeo i Julia", "id_genre","3", "id_publisher","33", "status","wypożyczone")));
    }

    public ResponseEntity<Object> getBooks() {
        return ResponseEntity.ok(books.values());
    }

    public ResponseEntity<Object> getBookById(int bookId) {
        if (!books.containsKey(bookId)) {
            return ResponseEntity.status(404).body(Map.of("error", "Book not found"));
        }
        return ResponseEntity.ok(books.get(bookId));
    }

    public ResponseEntity<Object> getUserBooks() {
        return ResponseEntity.ok(books.entrySet().stream()
                .filter(entry -> {
                    String status = entry.getValue().get("status");
                    return "zarezerwowane".equals(status) || "wypożyczone".equals(status);
                })
        );
    }

    public ResponseEntity<Object> addBook(int bookId, String title, String idGenre, String idPublisher, String status) {
        books.put(bookId, new HashMap<>(Map.of("id", String.valueOf(bookId), "title", title, "id_genre", idGenre, "id_publisher", idPublisher, "status", status)));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", "Book added"));
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