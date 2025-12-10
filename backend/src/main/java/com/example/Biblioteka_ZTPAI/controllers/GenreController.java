package com.example.Biblioteka_ZTPAI.controllers;

import com.example.Biblioteka_ZTPAI.services.GenreService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getGenres();
    }
}
