package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.ReviewCreateDTO;
import com.example.Biblioteka_ZTPAI.dto.book.ReviewDTO;
import com.example.Biblioteka_ZTPAI.mappers.ReviewMapper;
import com.example.Biblioteka_ZTPAI.models.Book;
import com.example.Biblioteka_ZTPAI.models.Review;
import com.example.Biblioteka_ZTPAI.models.User;
import com.example.Biblioteka_ZTPAI.repositories.BookRepository;
import com.example.Biblioteka_ZTPAI.repositories.ReviewRepository;
import com.example.Biblioteka_ZTPAI.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewDTO> getReviewsForBook(Integer bookId) {
        return reviewMapper.toDTO(reviewRepository.findByBookId(bookId));
    }

    @Transactional
    public ReviewDTO addReview(Integer bookId, String username, ReviewCreateDTO dto) {
        Integer userId = userRepository.findUserIdByEmail(username);
        if (reviewRepository.findByBookIdAndUserId(bookId, userId).isPresent()) {
            throw new IllegalStateException("Użytkownik już dodał recenzję dla tej książki.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Review review = Review.builder()
                .book(book)
                .user(user)
                .rating(dto.getRating())
                .reviewText(dto.getReviewText())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);
        return reviewMapper.toDTO(saved);
    }
}