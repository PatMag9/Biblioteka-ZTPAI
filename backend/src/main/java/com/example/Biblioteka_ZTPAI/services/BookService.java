package com.example.Biblioteka_ZTPAI.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookService {
    private static final Map<Integer, Map<String, String>> books = new
            HashMap<>() {{
                put(1, Map.of("id", "1", "title", "Pan Tadeusz", "id_genre","1", "id_publisher","11"));
                put(2, Map.of("id", "2", "title", "Krzyżacy", "id_genre","2", "id_publisher","22"));
            }};

    public ResponseEntity<Object> getBooks() {
        return ResponseEntity.ok(books.values());
    }

    public ResponseEntity<Object> getBookById(@PathVariable int bookId) {
        if (!books.containsKey(bookId)) {
            return ResponseEntity.status(404).body(Map.of("error", "Book not found"));
        }
        return ResponseEntity.ok(books.get(bookId));
    }

}
