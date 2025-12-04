package com.example.Biblioteka_ZTPAI.dto.book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublisherDTO {
    private Integer id;
    private String name;
}
