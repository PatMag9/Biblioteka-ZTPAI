package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.BookDTO;
import com.example.Biblioteka_ZTPAI.dto.book.BookListDTO;
import com.example.Biblioteka_ZTPAI.mappers.BookMapper;
import com.example.Biblioteka_ZTPAI.models.Review;
import com.example.Biblioteka_ZTPAI.models.Book;
import com.example.Biblioteka_ZTPAI.repositories.BookCopyRepository;
import com.example.Biblioteka_ZTPAI.repositories.BookRepository;
import com.example.Biblioteka_ZTPAI.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
    private final BookMapper bookMapper;

    private String safe(String s) {
        return (s == null || s.isBlank()) ? null : s.toLowerCase();
    }

    public ResponseEntity<Object> getBooks(
            String search,
            String genre,
            String author,
            String sort,
            String order,
            int page,
            int size
    ) {

        Page<Book> books;

        boolean sortByAuthor = "authors".equalsIgnoreCase(sort);

        Pageable pageable;

        if (sortByAuthor) {
            pageable = PageRequest.of(page, size);

            books = bookRepository.findAllSortedByAuthorName(
                    safe(search),
                    safe(genre),
                    safe(author),
                    pageable
            );
        } else {
            Sort.Direction direction =
                    order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            pageable = PageRequest.of(page, size, Sort.by(direction, sort));

            books = bookRepository.findAll(
                    BookSpecification.filterBooks(search, genre, author),
                    pageable
            );
        }

        List<BookListDTO> dtos = books.stream()
                .map(book -> enrichBookListDTO(bookMapper.toListDTO(book), book))
                .toList();

        return ResponseEntity.ok(
                new PageImpl<>(dtos, pageable, books.getTotalElements())
        );
    }

    public ResponseEntity<Object> getBookById(int bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        BookDTO dto = enrichBookDTO(bookMapper.toDTO(book), book);

        return ResponseEntity.ok(dto);
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

    public BookListDTO enrichBookListDTO(BookListDTO dto, Book book) {
        Double avgRating = book.getReviews().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        dto.setAverageRating(avgRating > 0 ? avgRating : null);
        dto.setReviewCount(book.getReviews().size() > 0 ? book.getReviews().size() : null);
        return dto;
    }

    public BookDTO enrichBookDTO(BookDTO dto, Book book) {
        Double avgRating = book.getReviews().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        dto.setAverageRating(avgRating > 0 ? avgRating : null);
        dto.setReviewCount(book.getReviews().size() > 0 ? book.getReviews().size() : null);
        return dto;
    }

}