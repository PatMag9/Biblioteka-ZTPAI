package com.example.Biblioteka_ZTPAI.projections;

import java.time.LocalDateTime;

public interface MyBookProjection {
    Integer getBookId();
    Integer getBookCopyId();
    String getCover();
    String getTitle();
    String getType();
    LocalDateTime getDueDate();
    LocalDateTime getStartDate();
    Boolean getCanExtend();
}
