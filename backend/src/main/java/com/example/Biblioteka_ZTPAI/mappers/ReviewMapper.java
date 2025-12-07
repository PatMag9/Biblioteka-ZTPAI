package com.example.Biblioteka_ZTPAI.mappers;

import com.example.Biblioteka_ZTPAI.dto.book.ReviewDTO;
import com.example.Biblioteka_ZTPAI.models.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.username", target = "username")
    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTO(List<Review> reviews);
}

