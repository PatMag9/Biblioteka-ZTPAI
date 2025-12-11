package com.example.Biblioteka_ZTPAI.mappers;

import com.example.Biblioteka_ZTPAI.dto.book.MyBookDTO;
import com.example.Biblioteka_ZTPAI.projections.MyBookProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MyBookMapper {
    MyBookDTO toDTO(MyBookProjection projection);
}
