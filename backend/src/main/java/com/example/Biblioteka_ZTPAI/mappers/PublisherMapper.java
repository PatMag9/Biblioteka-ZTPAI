package com.example.Biblioteka_ZTPAI.mappers;

import com.example.Biblioteka_ZTPAI.dto.book.PublisherDTO;
import com.example.Biblioteka_ZTPAI.models.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    @Mapping(source = "idPublishers", target = "id")
    @Mapping(source = "publishersName", target = "name")
    PublisherDTO toDTO(Publisher publisher);
}
