package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.BookCopyDTO;
import com.example.Biblioteka_ZTPAI.dto.book.BookDTO;
import com.example.Biblioteka_ZTPAI.dto.book.BookListDTO;
import com.example.Biblioteka_ZTPAI.dto.book.MyBookDTO;
import com.example.Biblioteka_ZTPAI.mappers.BookCopyMapper;
import com.example.Biblioteka_ZTPAI.mappers.BookMapper;
import com.example.Biblioteka_ZTPAI.mappers.MyBookMapper;
import com.example.Biblioteka_ZTPAI.models.*;
import com.example.Biblioteka_ZTPAI.projections.MyBookProjection;
import com.example.Biblioteka_ZTPAI.repositories.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private final UserRepository userRepository;
    private final MyBookMapper myBookMapper;

    private String safe(String s) {
        return (s == null || s.isBlank()) ? null : s.toLowerCase();
    }

    public Page<BookListDTO> getBooks(
            String sort,
            Integer authorId,
            Integer genreId,
            String search,
            int page,
            int size
    ) {
        Pageable pageable;

        if ("popular".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort sortObj;
            if ("newest".equalsIgnoreCase(sort)) {
                sortObj = Sort.by("dateAdded");
                sortObj = sortObj.descending();
            } else {
                sortObj = Sort.by("title");
                sortObj = sortObj.ascending();
            }

            pageable = PageRequest.of(page, size, sortObj);
        }

        Page<Book> pageResult;

        boolean hasSearch = search != null && !search.isBlank();

        if ("popular".equalsIgnoreCase(sort)) {
            pageResult = bookRepository.findAllOrderByReviewCountDesc(
                    hasSearch ? search : null,
                    genreId,
                    authorId,
                    pageable
            );
        } else {
            Specification<Book> spec = Specification.where(null);

            if (authorId != null) {
                spec = spec.and((root, query, cb) -> {
                    query.distinct(true);
                    Join<Book, Author> authorJoin = root.join("authors", JoinType.INNER);
                    return cb.equal(authorJoin.get("id"), authorId);
                });
            }

            if (genreId != null) {
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("genre").get("id"), genreId)
                );
            }

            if (hasSearch) {
                String like = "%" + search.toLowerCase() + "%";
                spec = spec.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("title")), like)
                );
            }

            pageResult = bookRepository.findAll(spec, pageable);
        }

        List<Book> books = pageResult.getContent();
        List<BookListDTO> dtoList = bookMapper.toListDTO(books);

        List<Integer> bookIds = books.stream().map(Book::getId).collect(Collectors.toList());
        Map<Integer, ReviewStats> statsMap = new HashMap<>();
        if (!bookIds.isEmpty()) {
            List<Object[]> stats = bookRepository.findReviewStatsByBookIds(bookIds);
            for (Object[] row : stats) {
                Integer bId = ((Number)row[0]).intValue();
                Integer cnt = ((Number)row[1]).intValue();
                Double avg = row[2] == null ? null : ((Number)row[2]).doubleValue();
                statsMap.put(bId, new ReviewStats(cnt, avg));
            }
        }

        for (BookListDTO dto : dtoList) {
            ReviewStats st = statsMap.get(dto.getId());
            if (st != null) {
                dto.setReviewCount(st.count);
                dto.setAverageRating(st.average);
            } else {
                dto.setReviewCount(0);
                dto.setAverageRating(null);
            }
        }

        return new PageImpl<>(
                dtoList,
                pageResult.getPageable(),
                pageResult.getTotalElements()
        );
    }

    private static class ReviewStats {
        final int count;
        final Double average;
        ReviewStats(int c, Double a) { count = c; average = a; }
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

    public Page<MyBookDTO> getMyBooks(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Integer userId = userRepository.findUserIdByEmail(username);

        Page<MyBookProjection> pageResult =
                bookRepository.findUserLoansAndReservations(userId, pageable);

        // mapowanie na DTO
        List<MyBookDTO> dtoList = pageResult.getContent().stream()
                .map(myBookMapper::toDTO)
                .toList();

        return new PageImpl<>(
                dtoList,
                pageable,
                pageResult.getTotalElements()
        );
    }

}