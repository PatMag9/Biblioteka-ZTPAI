package com.example.Biblioteka_ZTPAI.mappers;

import com.example.Biblioteka_ZTPAI.dto.checkout.CreateLoanDTO;
import com.example.Biblioteka_ZTPAI.dto.checkout.LoanUserBookDTO;
import com.example.Biblioteka_ZTPAI.models.BookCopy;
import com.example.Biblioteka_ZTPAI.models.Loan;
import com.example.Biblioteka_ZTPAI.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", expression = "java(mapUser(dto.getUserId()))")
    @Mapping(target = "bookCopy", expression = "java(mapBookCopy(dto.getBookCopyId()))")
    Loan toEntity(CreateLoanDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "dueDate", source = "dueDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "bookTitle", source = "bookCopy.book.title")
    LoanUserBookDTO toUserBookDto(Loan loan);

    default User mapUser(Integer id) {
        return User.builder().id(id).build();
    }

    default BookCopy mapBookCopy(Integer id) {
        return BookCopy.builder().id(id).build();
    }
}

