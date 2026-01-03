package com.example.Biblioteka_ZTPAI.dto.book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookCopyDTO {
    private Integer id;
    private String code;
    private PublisherDTO publisher;
    private Integer yearPublished;
    private boolean reserved;
    private boolean reservedByCurrentUser;
    private boolean loaned;
}
