package com.animeapi.repository;

import com.animeapi.model.Anime;
import com.animeapi.model.AnimeStatus;
import com.animeapi.model.Genre;
import com.animeapi.model.Studio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    Page<Anime> findByGenresContaining(Genre genre, Pageable pageable);
    Page<Anime> findByStudio(Studio studio, Pageable pageable);
    Page<Anime> findByStatus(AnimeStatus status, Pageable pageable);
}
