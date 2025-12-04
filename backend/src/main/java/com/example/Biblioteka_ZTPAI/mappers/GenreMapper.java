package com.example.Biblioteka_ZTPAI.mappers;

import com.example.Biblioteka_ZTPAI.dto.book.GenreDTO;
import com.example.Biblioteka_ZTPAI.models.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    @Mapping(source = "idGenre", target = "id")
    @Mapping(source = "genreName", target = "name")
    GenreDTO toDTO(Genre genre);
}
