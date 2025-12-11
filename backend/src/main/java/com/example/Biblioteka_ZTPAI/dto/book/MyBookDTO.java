package com.example.Biblioteka_ZTPAI.dto.book;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyBookDTO {
    private Integer itemId;
    private Integer bookId;
    private Integer bookCopyId;
    private String cover;
    private String title;
    private String type;
    private LocalDateTime dueDate;
    private LocalDateTime startDate;
    private Boolean canExtend;
}
