package com.example.Biblioteka_ZTPAI.mappers;

import com.example.Biblioteka_ZTPAI.dto.checkout.CreateReservationDTO;
import com.example.Biblioteka_ZTPAI.dto.checkout.ReservationUserBookDTO;
import com.example.Biblioteka_ZTPAI.models.BookCopy;
import com.example.Biblioteka_ZTPAI.models.Reservation;
import com.example.Biblioteka_ZTPAI.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", expression = "java(mapUser(dto.getUserId()))")
    @Mapping(target = "bookCopy", expression = "java(mapBookCopy(dto.getBookCopyId()))")
    Reservation toEntity(CreateReservationDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "bookTitle", source = "bookCopy.book.title")
    ReservationUserBookDTO toUserBookDto(Reservation reservation);

    default User mapUser(Integer id) {
        return User.builder().id(id).build();
    }

    default BookCopy mapBookCopy(Integer id) {
        return BookCopy.builder().id(id).build();
    }
}

