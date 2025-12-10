package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.GenreDTO;
import com.example.Biblioteka_ZTPAI.mappers.GenreMapper;
import com.example.Biblioteka_ZTPAI.models.Genre;
import com.example.Biblioteka_ZTPAI.repositories.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public Page<GenreDTO> getGenres(String nameFilter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Genre> genrePage;
        if (nameFilter != null && !nameFilter.isBlank()) {
            genrePage = genreRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
        } else {
            genrePage = genreRepository.findAll(pageable);
        }

        return genrePage.map(genreMapper::toDTO);
    }
}
