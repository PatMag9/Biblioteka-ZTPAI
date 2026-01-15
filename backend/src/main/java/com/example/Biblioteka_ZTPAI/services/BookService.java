package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.*;
import com.example.Biblioteka_ZTPAI.dto.book.admin.*;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ReservationRepository reservationRepository;
    private final LoanRepository loanRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    private final BookMapper bookMapper;
    private final BookCopyMapper bookCopyMapper;
    private final MyBookMapper myBookMapper;

    private static final long MAX_COVER_SIZE = 2 * 1024 * 1024; // 2 MB

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

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

    public Page<MyBookDTO> getMyBooks(UserDetails userDetails, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String username = userDetails.getUsername();

        Integer userId = userRepository.findUserIdByEmail(username);

        Page<MyBookProjection> pageResult =
                bookRepository.findUserLoansAndReservations(userId, pageable);

        List<MyBookDTO> dtoList = pageResult.getContent().stream()
                .map(myBookMapper::toDTO)
                .toList();

        return new PageImpl<>(
                dtoList,
                pageable,
                pageResult.getTotalElements()
        );
    }

    public Page<AdminBookDTO> getAdminBooks(String search, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Specification<Book> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), like)
            );
        }

        Page<Book> pageResult = bookRepository.findAll(spec, pageable);

        List<AdminBookDTO> dtoList = bookMapper.toAdminDTO(pageResult.getContent());

        for (int i = 0; i < pageResult.getContent().size(); i++) {
            Book book = pageResult.getContent().get(i);
            AdminBookDTO dto = dtoList.get(i);

            List<BookCopy> copies = bookCopyRepository.findAllByBookIdWithPublisher(book.getId());
            List<BookCopyDTO> copyDTOs = bookCopyMapper.toDTO(copies);

            List<Integer> copyIds = copyDTOs.stream()
                    .map(BookCopyDTO::getId)
                    .filter(Objects::nonNull)
                    .toList();

            Map<Integer, Reservation> activeReservationMap = new HashMap<>();
            Map<Integer, Loan> activeLoanMap = new HashMap<>();

            if (!copyIds.isEmpty()) {
                List<Reservation> activeReservations = reservationRepository.findActiveReservationsByBookCopyIds(copyIds);
                activeReservations.forEach(r -> activeReservationMap.putIfAbsent(r.getBookCopy().getId(), r));

                List<Loan> activeLoans = loanRepository.findActiveLoansByBookCopyIds(copyIds);
                activeLoans.forEach(l -> activeLoanMap.putIfAbsent(l.getBookCopy().getId(), l));
            }

            for (BookCopyDTO bcDto : copyDTOs) {
                Integer cid = bcDto.getId();
                bcDto.setLoaned(activeLoanMap.containsKey(cid));
                bcDto.setReserved(activeReservationMap.containsKey(cid));
            }

            dto.setCopies(copyDTOs);
        }

        return new PageImpl<>(dtoList, pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Transactional
    public void createBook(CreateBookDTO dto, MultipartFile coverFile) {
        Genre genre = null;
        if (dto.getGenreId() != null) {
            genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new NoSuchElementException("Genre not found"));
        }

        Set<Author> authors = new HashSet<>();
        if (dto.getAuthorIds() != null && !dto.getAuthorIds().isEmpty()) {
            authors.addAll(authorRepository.findAllById(dto.getAuthorIds()));
        }

        String coverFileName = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            coverFileName = saveCoverFile(coverFile);
        }

        Book book = Book.builder()
                .title(dto.getTitle())
                .genre(genre)
                .description(dto.getDescription())
                .cover(coverFileName)
                .authors(authors)
                .dateAdded(LocalDateTime.now())
                .build();

        bookRepository.save(book);
    }

    private String saveCoverFile(MultipartFile file) {

        validateCoverFile(file);

        try {
            String uploadDir = "uploads/covers/";
            Files.createDirectories(Paths.get(uploadDir));

            String extension = getFileExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "." + extension;

            Path targetPath = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save cover file", e);
        }
    }

    private void validateCoverFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cover file is empty");
        }

        if (file.getSize() > MAX_COVER_SIZE) {
            throw new IllegalArgumentException("Cover file size must not exceed 2MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Cover file must be an image (JPEG, PNG, WEBP)");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private void deleteCoverFile(String fileName) {
        if (fileName == null) return;

        try {
            Path path = Paths.get("uploads/covers/").resolve(fileName);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete old cover file", e);
        }
    }

    @Transactional
    public void updateBook(Integer bookId, UpdateBookDTO dto, MultipartFile cover) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            book.setDescription(dto.getDescription());
        }

        if (dto.getGenreId() != null) {
            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new IllegalArgumentException("Genre not found"));
            book.setGenre(genre);
        }

        if (dto.getAuthorIds() != null) {
            Set<Author> authors = new HashSet<>(authorRepository.findAllById(dto.getAuthorIds()));
            book.setAuthors(authors);
        }

        if (cover != null && !cover.isEmpty()) {

            deleteCoverFile(book.getCover());

            String newCoverFileName = saveCoverFile(cover);
            book.setCover(newCoverFileName);
        }

        bookRepository.save(book);
    }

    @Transactional
    public void addBookCopy(Integer bookId, CreateBookCopyDTO dto) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        Publisher publisher = null;
        if (dto.getPublisherId() != null) {
            publisher = publisherRepository.findById(dto.getPublisherId())
                    .orElseThrow(() -> new IllegalArgumentException("Publisher not found"));
        }

        BookCopy copy = BookCopy.builder()
                .book(book)
                .code(dto.getCode())
                .publisher(publisher)
                .yearPublished(dto.getYearPublished())
                .build();

        bookCopyRepository.save(copy);
    }

    @Transactional
    public void updateBookCopy(Integer copyId, UpdateBookCopyDTO dto) {

        BookCopy copy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new NoSuchElementException("Book copy not found"));

        if (dto.getCode() != null) {
            copy.setCode(dto.getCode());
        }

        if (dto.getYearPublished() != null) {
            copy.setYearPublished(dto.getYearPublished());
        }

        if (dto.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                    .orElseThrow(() -> new IllegalArgumentException("Publisher not found"));
            copy.setPublisher(publisher);
        }

        bookCopyRepository.save(copy);
    }

    @Transactional
    public void deleteBookCopy(Integer copyId) {

        BookCopy copy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new NoSuchElementException("Book copy not found"));

        boolean hasActiveLoan = loanRepository.existsByBookCopyAndEndDateIsNull(copy);
        boolean hasActiveReservation = reservationRepository.existsByBookCopyAndEndDateIsNull(copy);

        if (hasActiveLoan || hasActiveReservation) {
            throw new IllegalStateException("Cannot delete book copy with active loan or reservation");
        }

        bookCopyRepository.delete(copy);
    }



    @Transactional
    public void deleteBook(Integer bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        if (!book.getBookCopies().isEmpty()) {
            throw new IllegalStateException("Cannot delete book with existing copies");
        }

        bookRepository.delete(book);
    }

}