package com.animeapi.repository;

import com.animeapi.model.Anime;
import com.animeapi.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    List<Character> findByAnime(Anime anime);
}
