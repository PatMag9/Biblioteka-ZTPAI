package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.BookCopyDTO;
import com.example.Biblioteka_ZTPAI.dto.book.BookDTO;
import com.example.Biblioteka_ZTPAI.dto.book.BookListDTO;
import com.example.Biblioteka_ZTPAI.mappers.BookCopyMapper;
import com.example.Biblioteka_ZTPAI.mappers.BookMapper;
import com.example.Biblioteka_ZTPAI.mappers.ReservationMapper;
import com.example.Biblioteka_ZTPAI.models.*;
import com.example.Biblioteka_ZTPAI.repositories.*;
import com.example.Biblioteka_ZTPAI.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ReservationRepository reservationRepository;
    private final LoanRepository loanRepository;
    private final BookMapper bookMapper;
    private final BookCopyMapper bookCopyMapper;
    private final ReviewRepository reviewRepository;

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

    public ResponseEntity<Object> getBookById(int bookId, @Nullable String currentUsername) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        List<BookCopy> copies = bookCopyRepository.findAllByBookIdWithPublisher(bookId);

        List<BookCopyDTO> copyDTOs = bookCopyMapper.toDTO(copies);

        List<Integer> copyIds = copyDTOs.stream()
                .map(BookCopyDTO::getId)
                .filter(Objects::nonNull)
                .toList();

        Map<Integer, Reservation> activeReservationMap = new HashMap<>();
        Map<Integer, Loan> activeLoanMap = new HashMap<>();

        if (!copyIds.isEmpty()) {
            List<Reservation> activeReservations = reservationRepository.findActiveReservationsByBookCopyIds(copyIds);
            activeReservations.forEach(r -> {
                Integer cid = r.getBookCopy().getId();
                activeReservationMap.putIfAbsent(cid, r);
            });

            List<Loan> activeLoans = loanRepository.findActiveLoansByBookCopyIds(copyIds);
            activeLoans.forEach(l -> {
                Integer cid = l.getBookCopy().getId();
                activeLoanMap.putIfAbsent(cid, l);
            });
        }

        for (BookCopyDTO bcDto : copyDTOs) {
            Integer cid = bcDto.getId();
            boolean loaned = activeLoanMap.containsKey(cid);
            boolean reserved = activeReservationMap.containsKey(cid);
            boolean reservedByCurrentUser = false;

            if (reserved && currentUsername != null) {
                Reservation r = activeReservationMap.get(cid);
                reservedByCurrentUser = r.getUser() != null && currentUsername.equals(r.getUser().getUsername());
            }

            bcDto.setLoaned(loaned);
            bcDto.setReserved(reserved);
            bcDto.setReservedByCurrentUser(reservedByCurrentUser);
        }

        BookDTO dto = bookMapper.toDTO(book);
        dto.setCopies(copyDTOs);

        Double avgRating = null;
        Integer reviewCount = null;
        try {
            Double avg = reviewRepository.getAverageRating(bookId);
            Integer cnt = reviewRepository.getReviewCount(bookId);
            if (cnt != null && cnt > 0) {
                avgRating = avg;
                reviewCount = cnt;
            }
        } catch (Exception ignored) {
            if (book.getReviews() != null && !book.getReviews().isEmpty()) {
                double avg = book.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0.0);
                int cnt = book.getReviews().size();
                if (cnt > 0) {
                    avgRating = avg;
                    reviewCount = cnt;
                }
            }
        }

        dto.setAverageRating(avgRating);
        dto.setReviewCount(reviewCount);

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