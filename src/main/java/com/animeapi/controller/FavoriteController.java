package com.animeapi.controller;

import com.animeapi.dto.response.MessageResponse;
import com.animeapi.model.Anime;
import com.animeapi.model.Favorite;
import com.animeapi.model.User;
import com.animeapi.repository.AnimeRepository;
import com.animeapi.repository.FavoriteRepository;
import com.animeapi.repository.UserRepository;
import com.animeapi.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class FavoriteController {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnimeRepository animeRepository;

    @GetMapping("/users/favorites")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Favorite>> getUserFavorites() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Favorite> favorites = favoriteRepository.findByUser(user);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/animes/{animeId}/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addToFavorites(@PathVariable Long animeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));

        if (favoriteRepository.existsByUserAndAnime(user, anime)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("This anime is already in your favorites"));
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setAnime(anime);

        favoriteRepository.save(favorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Anime added to favorites"));
    }

    @DeleteMapping("/favorites/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found"));

        if (!favorite.getUser().getId().equals(userDetails.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own favorites");
        }

        favoriteRepository.delete(favorite);
        return ResponseEntity.noContent().build();
    }
}
