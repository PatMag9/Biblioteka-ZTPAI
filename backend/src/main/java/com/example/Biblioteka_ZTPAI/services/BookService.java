package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.models.Author;
import com.example.Biblioteka_ZTPAI.models.Book;
import com.example.Biblioteka_ZTPAI.models.BookCopy;
import com.example.Biblioteka_ZTPAI.models.Publisher;
import com.example.Biblioteka_ZTPAI.repositories.BookCopyRepository;
import com.example.Biblioteka_ZTPAI.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

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

    public ResponseEntity<Object> getBookCopiesByBookId(int bookId) {
        List<BookCopy> copies = bookCopyRepository.findAllByBook_IdBook(bookId);
        return ResponseEntity.ok(copies);
    }

    public ResponseEntity<Object> getPublisherByBookCopyId(int bookCopyId) {
        Optional<BookCopy> bookCopy = bookCopyRepository.findById(bookCopyId);
        if (bookCopy.isPresent()) {
            Publisher publisher = bookCopy.get().getPublisher();
            if (publisher != null) {
                return ResponseEntity.ok(publisher);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Object> getAuthorsByBookId(int bookId) {
        Set<Author> authors = bookRepository.findAuthorsByBookId(bookId);
        if (authors == null || authors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(authors);
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