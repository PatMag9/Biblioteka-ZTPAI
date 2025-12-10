package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.BookAuthorDTO;
import com.example.Biblioteka_ZTPAI.mappers.AuthorMapper;
import com.example.Biblioteka_ZTPAI.models.Author;
import com.example.Biblioteka_ZTPAI.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public Page<BookAuthorDTO> getAuthors(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        String search = query != null ? query : "";
        Page<Author> authorPage = authorRepository.searchByFullNameIgnoreCase(search, pageable);

        return authorPage.map(authorMapper::toDTO);
    }
}
