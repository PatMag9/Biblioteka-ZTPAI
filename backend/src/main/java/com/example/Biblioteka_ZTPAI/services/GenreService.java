package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.dto.book.GenreDTO;
import com.example.Biblioteka_ZTPAI.dto.book.admin.CreateGenreDTO;
import com.example.Biblioteka_ZTPAI.mappers.GenreMapper;
import com.example.Biblioteka_ZTPAI.models.Genre;
import com.example.Biblioteka_ZTPAI.repositories.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

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

    public GenreDTO createGenre(CreateGenreDTO dto) {
        if (genreRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Gatunek o tej nazwie już istnieje");
        }

        Genre genre = new Genre();
        genre.setName(dto.getName().trim());

        return genreMapper.toDTO(genreRepository.save(genre));
    }

    public void deleteGenre(Integer genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new NoSuchElementException("Gatunek nie istnieje"));

        if (genre.getBooks() != null && !genre.getBooks().isEmpty()) {
            throw new IllegalStateException("Nie można usunąć gatunku przypisanego do książek");
        }

        genreRepository.delete(genre);
    }
}
