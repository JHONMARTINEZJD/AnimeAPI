package com.animeapi.repository;

import com.animeapi.model.Anime;
import com.animeapi.model.User;
import com.animeapi.model.WatchStatus;
import com.animeapi.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUser(User user);
    List<Watchlist> findByUserAndStatus(User user, WatchStatus status);
    Optional<Watchlist> findByUserAndAnime(User user, Anime anime);
    boolean existsByUserAndAnime(User user, Anime anime);
}
