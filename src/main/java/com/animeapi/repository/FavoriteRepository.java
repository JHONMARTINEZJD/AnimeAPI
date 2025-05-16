package com.animeapi.repository;

import com.animeapi.model.Anime;
import com.animeapi.model.Favorite;
import com.animeapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    Optional<Favorite> findByUserAndAnime(User user, Anime anime);
    boolean existsByUserAndAnime(User user, Anime anime);
}
