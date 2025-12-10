package com.example.Biblioteka_ZTPAI.services;

import com.example.Biblioteka_ZTPAI.models.Genre;
import com.example.Biblioteka_ZTPAI.repositories.GenreRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public List<Genre> getGenres() {
        return genreRepository.findAll();
    }
}
