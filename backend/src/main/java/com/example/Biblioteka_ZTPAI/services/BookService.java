package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.models.Book;
import com.example.Biblioteka_ZTPAI.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
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
            throw new NoSuchElementException("Book not found");
        }
    }

    public ResponseEntity<Object> addBook(Book book) {
        bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    public ResponseEntity<Object> updateBook(int bookId, Book updatedBook) {
        Optional<Book> existingBookOpt = bookRepository.findById(bookId);

        if (existingBookOpt.isPresent()) {
            Book existingBook = existingBookOpt.get();
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setGenre(updatedBook.getGenre());
            existingBook.setCover(updatedBook.getCover());
            existingBook.setDescription(updatedBook.getDescription());

            bookRepository.save(existingBook);
            return ResponseEntity.ok(existingBook);
        } else {
            throw new NoSuchElementException("Book not found");
        }
    }

    public ResponseEntity<Object> deleteBook(int bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);

        if (bookOpt.isPresent()) {
            bookRepository.deleteById(bookId);
            return ResponseEntity.noContent().build();
        } else {
            throw new NoSuchElementException("Book not found");
        }
    }

}