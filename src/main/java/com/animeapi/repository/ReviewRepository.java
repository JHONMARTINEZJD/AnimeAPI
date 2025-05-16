package com.animeapi.repository;

import com.animeapi.model.Anime;
import com.animeapi.model.Review;
import com.animeapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByAnime(Anime anime);
    List<Review> findByUser(User user);
    Optional<Review> findByUserAndAnime(User user, Anime anime);
}
