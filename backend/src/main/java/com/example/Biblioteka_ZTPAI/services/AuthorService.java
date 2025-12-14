package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.BookAuthorDTO;
import com.example.Biblioteka_ZTPAI.dto.book.admin.CreateAuthorDTO;
import com.example.Biblioteka_ZTPAI.mappers.AuthorMapper;
import com.example.Biblioteka_ZTPAI.models.Author;
import com.example.Biblioteka_ZTPAI.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

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

    public BookAuthorDTO createAuthor(CreateAuthorDTO dto) {
        if (authorRepository.existsByNameIgnoreCaseAndSurnameIgnoreCase(
                dto.getName(), dto.getSurname())) {
            throw new IllegalArgumentException("Autor już istnieje");
        }

        Author author = Author.builder()
                .name(dto.getName().trim())
                .surname(dto.getSurname())
                .build();

        return authorMapper.toDTO(authorRepository.save(author));
    }

    public void deleteAuthor(Integer authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Autor nie istnieje"));

        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            throw new IllegalStateException("Nie można usunąć autora przypisanego do książek");
        }

        authorRepository.delete(author);
    }
}
