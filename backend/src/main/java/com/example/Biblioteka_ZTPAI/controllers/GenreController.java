package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.dto.book.GenreDTO;
import com.example.Biblioteka_ZTPAI.dto.book.admin.CreateGenreDTO;
import com.example.Biblioteka_ZTPAI.services.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<?> getGenres(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GenreDTO> genres = genreService.getGenres(name, page, size);
        if (genres.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(genres);
    }

    @PostMapping
    public ResponseEntity<?> createGenre(
            @Valid @RequestBody CreateGenreDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(genreService.createGenre(dto));
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<?> deleteGenre(@PathVariable Integer genreId) {
        genreService.deleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }

}
