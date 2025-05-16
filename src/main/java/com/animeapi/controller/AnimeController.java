package com.animeapi.controller;

import com.animeapi.dto.request.AnimeRequest;
import com.animeapi.model.Anime;
import com.animeapi.model.AnimeStatus;
import com.animeapi.model.Genre;
import com.animeapi.model.Studio;
import com.animeapi.repository.AnimeRepository;
import com.animeapi.repository.GenreRepository;
import com.animeapi.repository.StudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/animes")
public class AnimeController {
    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private StudioRepository studioRepository;

    @GetMapping
    public ResponseEntity<Page<Anime>> getAllAnimes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long studioId,
            @RequestParam(required = false) AnimeStatus status) {

        Pageable paging = PageRequest.of(page, size, Sort.by("title"));
        Page<Anime> animes;

        if (genreId != null) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
            animes = animeRepository.findByGenresContaining(genre, paging);
        } else if (studioId != null) {
            Studio studio = studioRepository.findById(studioId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Studio not found"));
            animes = animeRepository.findByStudio(studio, paging);
        } else if (status != null) {
            animes = animeRepository.findByStatus(status, paging);
        } else {
            animes = animeRepository.findAll(paging);
        }

        return ResponseEntity.ok(animes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Anime> getAnimeById(@PathVariable Long id) {
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
        return ResponseEntity.ok(anime);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Anime> createAnime(@Valid @RequestBody AnimeRequest animeRequest) {
        Studio studio = null;
        if (animeRequest.getStudioId() != null) {
            studio = studioRepository.findById(animeRequest.getStudioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Studio not found"));
        }

        Set<Genre> genres = new HashSet<>();
        if (animeRequest.getGenreIds() != null) {
            for (Long genreId : animeRequest.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found with id: " + genreId));
                genres.add(genre);
            }
        }

        Anime anime = new Anime();
        anime.setTitle(animeRequest.getTitle());
        anime.setDescription(animeRequest.getDescription());
        anime.setReleaseDate(animeRequest.getReleaseDate());
        anime.setEpisodes(animeRequest.getEpisodes());
        anime.setRating(animeRequest.getRating());
        anime.setStatus(animeRequest.getStatus());
        anime.setStudio(studio);
        anime.setGenres(genres);

        Anime savedAnime = animeRepository.save(anime);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAnime);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Anime> updateAnime(@PathVariable Long id, @Valid @RequestBody AnimeRequest animeRequest) {
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));

        Studio studio = null;
        if (animeRequest.getStudioId() != null) {
            studio = studioRepository.findById(animeRequest.getStudioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Studio not found"));
        }

        Set<Genre> genres = new HashSet<>();
        if (animeRequest.getGenreIds() != null) {
            for (Long genreId : animeRequest.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found with id: " + genreId));
                genres.add(genre);
            }
        }

        anime.setTitle(animeRequest.getTitle());
        anime.setDescription(animeRequest.getDescription());
        anime.setReleaseDate(animeRequest.getReleaseDate());
        anime.setEpisodes(animeRequest.getEpisodes());
        anime.setRating(animeRequest.getRating());
        anime.setStatus(animeRequest.getStatus());
        anime.setStudio(studio);
        anime.setGenres(genres);

        Anime updatedAnime = animeRepository.save(anime);
        return ResponseEntity.ok(updatedAnime);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAnime(@PathVariable Long id) {
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));

        animeRepository.delete(anime);
        return ResponseEntity.noContent().build();
    }
}
