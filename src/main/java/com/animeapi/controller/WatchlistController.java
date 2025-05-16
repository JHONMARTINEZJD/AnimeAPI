package com.animeapi.controller;

import com.animeapi.dto.request.WatchlistRequest;
import com.animeapi.dto.response.MessageResponse;
import com.animeapi.model.Anime;
import com.animeapi.model.User;
import com.animeapi.model.WatchStatus;
import com.animeapi.model.Watchlist;
import com.animeapi.repository.AnimeRepository;
import com.animeapi.repository.UserRepository;
import com.animeapi.repository.WatchlistRepository;
import com.animeapi.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class WatchlistController {
    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnimeRepository animeRepository;

    @GetMapping("/users/watchlist")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Watchlist>> getUserWatchlist(
            @RequestParam(required = false) WatchStatus status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Watchlist> watchlist;
        if (status != null) {
            watchlist = watchlistRepository.findByUserAndStatus(user, status);
        } else {
            watchlist = watchlistRepository.findByUser(user);
        }

        return ResponseEntity.ok(watchlist);
    }

    @PostMapping("/animes/{animeId}/watchlist")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addToWatchlist(
            @PathVariable Long animeId,
            @Valid @RequestBody WatchlistRequest watchlistRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));

        Optional<Watchlist> existingWatchlist = watchlistRepository.findByUserAndAnime(user, anime);
        if (existingWatchlist.isPresent()) {
            Watchlist watchlist = existingWatchlist.get();
            watchlist.setStatus(watchlistRequest.getStatus());
            watchlistRepository.save(watchlist);
            return ResponseEntity.ok(new MessageResponse("Watchlist status updated"));
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setAnime(anime);
        watchlist.setStatus(watchlistRequest.getStatus());

        watchlistRepository.save(watchlist);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Anime added to watchlist"));
    }

    @DeleteMapping("/watchlist/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Watchlist watchlist = watchlistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Watchlist entry not found"));

        if (!watchlist.getUser().getId().equals(userDetails.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own watchlist entries");
        }

        watchlistRepository.delete(watchlist);
        return ResponseEntity.noContent().build();
    }
}
