package com.example.Biblioteka_ZTPAI.dto.book;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewDTO {
    private Integer id;
    private Integer rating;
    private String reviewText;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

